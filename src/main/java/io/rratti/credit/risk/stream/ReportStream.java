package io.rratti.credit.risk.stream;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.rratti.avro.model.Company;
import io.rratti.avro.model.FinancialMetric;
import io.rratti.avro.model.FinancialReport;
import io.rratti.credit.risk.config.CsvConfig;
import io.rratti.credit.risk.config.KafkaCompanyConfig;
import io.rratti.credit.risk.config.KafkaFinancialMetricConfig;
import io.rratti.credit.risk.config.KafkaFinancialReportConfig;
import io.rratti.credit.risk.utils.ReportUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Optional;
import java.util.Properties;

import static java.lang.String.format;

@Slf4j
@Service
@AllArgsConstructor
public class ReportStream {
    private final Environment environment;
    private final CsvConfig csvConfig;
    private final KafkaCompanyConfig kafkaCompanyConfig;
    private final KafkaFinancialMetricConfig kafkaFinancialMetricConfig;
    private final KafkaFinancialReportConfig kafkaFinancialReportConfig;

    public KafkaStreams stream() {
        var financialMetrics = new StreamsBuilder();
        var globalKTable = financialMetrics.globalTable(environment.getProperty("io.rratti.kafka.properties.company-topic"),
                Consumed.with(
                        Serdes.Integer(),
                        getCompanySpecificAvroSerde()
                ));
        financialMetrics.stream(
                environment.getProperty("io.rratti.kafka.properties.financial-metric-topic"),
                Consumed.with(
                        Serdes.Integer(),
                        getFinancialMetricSpecificAvroSerde()
                )
        )
                .leftJoin(
                        globalKTable,
                        (companyId, financialMetric) -> financialMetric.getCompanyId(),
                        (financialMetric, company) -> {
                            log.info(format("New record received: %s for company %s", financialMetric, company));
                            return FinancialReport.newBuilder()
                                .setAppName(financialMetric.getAppName())
                                .setCompanyId(company.getId())
                                .setCompanyName(company.getName())
                                .setRiskRating(null)
                                .setRiskScore(-1)
                                .setDate(financialMetric.getDate().toString())
                                .setPaybackDate(null)
                                .setAggregatedMarketingSpent(Optional.ofNullable(financialMetric.getMarketingSpent()).orElse(0.00))
                                .setAggregatedRevenues(Optional.ofNullable(financialMetric.getRevenues()).orElse(0.00))
                                .build();
                        })
                .groupByKey()
                .aggregate(() -> FinancialReport.newBuilder()
                                .setRiskScore(-1)
                                .setPaybackDate(null)
                                .setAggregatedMarketingSpent(0.00)
                                .setAggregatedRevenues(0.00)
                                .build(),
                        (companyId, financialReport, financialReport2) -> {
                    log.info(format(
                            "New Aggregation for companyId %s:%n A: %s%n B: %s%n",
                            companyId,
                            financialReport,
                            financialReport2
                    ));
                    var aggregatedMarketSpent = Optional.ofNullable(financialReport.getAggregatedMarketingSpent()).orElse(0.00) +
                            Optional.ofNullable(financialReport2.getAggregatedMarketingSpent()).orElse(0.00);
                    var aggregatedRevenues = Optional.ofNullable(financialReport.getAggregatedRevenues()).orElse(0.00) +
                            Optional.ofNullable(financialReport2.getAggregatedRevenues()).orElse(0.00);
                    var mergedReport= FinancialReport.newBuilder()
                            .setCompanyId(financialReport.getCompanyId())
                            .setAppName(financialReport.getAppName())
                            .setCompanyName(financialReport.getCompanyName())
                            .setAggregatedMarketingSpent(aggregatedMarketSpent)
                            .setAggregatedRevenues(aggregatedRevenues)
                            .setRiskScore(-1)
                            .setDate(financialReport.getDate())
                            .build();
                    if (aggregatedRevenues > aggregatedMarketSpent && financialReport2.getPaybackDate()==null) {
                        mergedReport.setPaybackDate(financialReport.getDate());
                    } else {
                        mergedReport.setPaybackDate(financialReport2.getPaybackDate());
                    }

                    return mergedReport;
                })
                .toStream()
                .map((key, value) -> {
                    if (value.getPaybackDate()!=null) {
                        var riskScore = this.calculateIncrementalRiskScore(value);
                        value.setRiskScore(riskScore);
                        value.setRiskRating(ReportUtils.getRiskRating(riskScore).getName());
                    }

                    return new KeyValue<>(key, value);
                })
                .filter((integer, financialReport) -> financialReport.getRiskScore() > 0)
//                .foreach((key, value) -> log.info(format("key: %s, value: %s", key, value)))
                .to(
                        environment.getProperty("io.rratti.kafka.properties.financial-reports"),
                        Produced.with(Serdes.Integer(), getFinancialReportSpecificAvroSerde())
                );


        var props = new Properties();
        props.putAll(kafkaFinancialMetricConfig.getProducerConfigs());

        return new KafkaStreams(financialMetrics.build(), props);
    }

    private int calculateIncrementalRiskScore(FinancialReport financialReport) {
        var ltcCacRatio = (financialReport.getAggregatedMarketingSpent()!=null ||
                financialReport.getAggregatedMarketingSpent()!=0
        ) ? financialReport.getAggregatedRevenues()/financialReport.getAggregatedMarketingSpent() :
                0.00;
        var paybackPeriod = calculateDays(financialReport.getPaybackDate().toString());

        return (int) (.7 * ReportUtils.getLtcCacNormalizedLive(ltcCacRatio) + 0.3 * ReportUtils.getPaybackPeriodNormalized(paybackPeriod));
    }

    private SpecificAvroSerde<FinancialMetric> getFinancialMetricSpecificAvroSerde() {
        var financialMetricSpecificSerde = new SpecificAvroSerde<FinancialMetric>();
        financialMetricSpecificSerde.configure(
                kafkaFinancialMetricConfig.getProducerConfigs(),
                false
        );

        return financialMetricSpecificSerde;
    }

    private SpecificAvroSerde<Company> getCompanySpecificAvroSerde() {
        var financialMetricSpecificSerde = new SpecificAvroSerde<Company>();
        financialMetricSpecificSerde.configure(
                kafkaCompanyConfig.getProducerConfigs(),
                false
        );

        return financialMetricSpecificSerde;
    }

    private SpecificAvroSerde<FinancialReport> getFinancialReportSpecificAvroSerde() {
        var financialReportSpecificAvroSerde = new SpecificAvroSerde<FinancialReport>();
        financialReportSpecificAvroSerde.configure(
                kafkaFinancialReportConfig.getProducerConfigs(),
                false
        );

        return financialReportSpecificAvroSerde;
    }

    private Integer calculateDays(String date) {
        try {
            var initialDate = csvConfig.dateFormat().parse("2021-01-01");
            return Math.toIntExact((csvConfig.dateFormat().parse(date).getTime() - initialDate.getTime()) / 86400000L);
        } catch (ParseException e) {
            log.error(e.getMessage());
            return 0;
        }
    }
}

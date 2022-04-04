package io.sanlo.credit.risk;

import io.sanlo.avro.model.Company;
import io.sanlo.avro.model.FinancialMetric;
import io.sanlo.credit.risk.config.CsvConfig;
import io.sanlo.credit.risk.repository.CompanyReadRepository;
import io.sanlo.credit.risk.repository.FinancialMetricsRepository;
import io.sanlo.credit.risk.stream.ReportStream;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.PostConstruct;

@AllArgsConstructor
@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan("io.sanlo.credit.risk.config.properties")
public class DemoApplication {
    private final ReportStream reportStream;
    private final CompanyReadRepository companyReadRepository;
    private final CsvConfig csvConfig;
    private final FinancialMetricsRepository financialMetricsRepository;
    private final KafkaTemplate<Integer, Company> companyKafkaTemplate;
    private final KafkaTemplate<Integer, FinancialMetric> financialMetricsKafkaTemplate;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @PostConstruct
    public void init() {
//        initTopics();
        startStream();
    }

    private void startStream() {
        reportStream.stream().start();
    }

    private void initTopics() {
        companyReadRepository.read()
                .forEach(company -> companyKafkaTemplate.sendDefault(
                        company.getId(),
                        Company.newBuilder()
                                .setId(company.getId())
                                .setCountry(company.getCountryCode())
                                .setName(company.getCompanyName())
                                .build()
                ));
        financialMetricsRepository.read()
                .forEach(financialMetric -> financialMetricsKafkaTemplate.sendDefault(
                        financialMetric.getCompanyId(),
                        FinancialMetric.newBuilder()
                                .setMarketingSpent(financialMetric.getMarketingSpent())
                                .setRevenues(financialMetric.getRevenue())
                                .setAppName(financialMetric.getAppName())
                                .setDate(csvConfig.dateFormat().format(financialMetric.getDate()))
                                .setCompanyId(financialMetric.getCompanyId())
                                .build()
                ));
    }
}
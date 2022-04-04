package io.rratti.credit.risk;

import io.rratti.avro.model.Company;
import io.rratti.avro.model.FinancialMetric;
import io.rratti.credit.risk.config.CsvConfig;
import io.rratti.credit.risk.repository.CompanyReadRepository;
import io.rratti.credit.risk.repository.FinancialMetricsRepository;
import io.rratti.credit.risk.stream.ReportStream;
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
@ConfigurationPropertiesScan("io.rratti.credit.risk.config.properties")
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
package io.rratti.credit.risk.config;

import io.rratti.avro.model.FinancialReport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Objects;

@Configuration
public class KafkaFinancialReportConfig extends AbstractKafkaConfig<io.rratti.avro.model.FinancialReport> {
    public KafkaFinancialReportConfig(Environment environment) {
        super(environment);
    }

    @Bean
    public KafkaTemplate<Integer, FinancialReport> financialReportKafkaTemplate() {
        return super.getJsonKafkaTemplate(Objects.requireNonNull(
                environment.getProperty(
                        "io.rratti.kafka.properties.financial-reports"
                )
        ));
    }
}

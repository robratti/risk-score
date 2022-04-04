package io.rratti.credit.risk.config;

import io.rratti.avro.model.Company;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Objects;

@Configuration
public class KafkaCompanyConfig extends AbstractKafkaConfig<Company> {
    public KafkaCompanyConfig(Environment environment) {
        super(environment);
    }

    @Bean
    public KafkaTemplate<Integer, Company> companyKafkaTemplate() {
        return super.getKafkaTemplate(Objects.requireNonNull(
                environment.getProperty(
                        "io.rratti.kafka.properties.company-topic"
                )
        ));
    }
}

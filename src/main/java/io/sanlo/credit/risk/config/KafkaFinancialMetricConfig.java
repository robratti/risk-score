package io.sanlo.credit.risk.config;

import io.sanlo.avro.model.FinancialMetric;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Objects;

@Configuration
public class KafkaFinancialMetricConfig extends AbstractKafkaConfig<FinancialMetric> {
    public KafkaFinancialMetricConfig(Environment environment) {
        super(environment);
    }

    @Bean
    public KafkaTemplate<Integer, FinancialMetric> financialMetricsKafkaTemplate() {
        return super.getKafkaTemplate(Objects.requireNonNull(
                environment.getProperty(
                        "io.sanlo.kafka.properties.financial-metric-topic"
                )
        ));
    }
}
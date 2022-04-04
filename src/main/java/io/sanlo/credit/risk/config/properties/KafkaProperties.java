package io.sanlo.credit.risk.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "io.sanlo.kafka")
public class KafkaProperties {
    private Map<String, Object> properties;
}

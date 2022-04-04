package io.rratti.credit.risk.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "io.rratti")
public class CsvProperties {
    private String companyFileName;
    private String metricsFileName;
    private String financialReportFileName;
}

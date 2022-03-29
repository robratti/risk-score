package io.sanlo.credit.risk;

import io.sanlo.credit.risk.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AllArgsConstructor
@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan("io.sanlo.credit.risk.config.properties")
public class DemoApplication implements CommandLineRunner {
    private final ReportService reportService;
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) {
        reportService.run().subscribe();
    }
}

package io.rratti.credit.risk.controller;

import io.rratti.credit.risk.config.CsvConfig;
import io.rratti.credit.risk.model.FinancialMetric;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class FinancialMetricController {
    private final CsvConfig csvConfig;
    private final KafkaTemplate<Integer, io.rratti.avro.model.FinancialMetric> financialMetricKafkaTemplate;

    @PostMapping("/financial-metric")
    public Mono<FinancialMetric> postFinancialMetric(@RequestBody FinancialMetric financialMetric) {
        return Mono.fromFuture(financialMetricKafkaTemplate.sendDefault(
                financialMetric.getCompanyId(),
                io.rratti.avro.model.FinancialMetric.newBuilder()
                        .setCompanyId(financialMetric.getCompanyId())
                        .setDate(csvConfig.dateFormat().format(financialMetric.getDate()))
                        .setAppName(financialMetric.getAppName())
                        .setRevenues(financialMetric.getRevenue())
                        .setMarketingSpent(financialMetric.getMarketingSpent())
                        .setAppName(financialMetric.getAppName())
                .build()).completable())
                .thenReturn(financialMetric);
    }
}

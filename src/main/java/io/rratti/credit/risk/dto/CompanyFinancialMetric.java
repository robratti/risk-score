package io.rratti.credit.risk.dto;

import io.rratti.avro.model.Company;
import io.rratti.avro.model.FinancialMetric;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CompanyFinancialMetric {
    private Company company;
    private FinancialMetric financialMetric;
    private Double sumRevenues;
    private Double sumMarketingSpent;
}

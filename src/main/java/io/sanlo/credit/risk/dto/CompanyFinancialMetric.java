package io.sanlo.credit.risk.dto;

import io.sanlo.avro.model.Company;
import io.sanlo.avro.model.FinancialMetric;
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

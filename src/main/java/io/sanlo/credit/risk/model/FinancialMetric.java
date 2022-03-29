package io.sanlo.credit.risk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class FinancialMetric {
    private Date date;
    private String appName;
    private Integer companyId;
    private Double revenue;
    private Double marketingSpent;
}
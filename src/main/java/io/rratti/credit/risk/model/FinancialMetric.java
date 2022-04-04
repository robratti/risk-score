package io.rratti.credit.risk.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
public class FinancialMetric {
    private Date date;
    private String appName;
    private Integer companyId;
    private Double revenue;
    private Double marketingSpent;

    @Builder
    public FinancialMetric(
            Date date,
            String appName,
            Integer companyId,
            Double revenue,
            Double marketingSpent
    ) {
        this.date = date;
        this.appName = appName;
        this.companyId = companyId;
        this.revenue = revenue;
        this.marketingSpent = marketingSpent;
    }
}
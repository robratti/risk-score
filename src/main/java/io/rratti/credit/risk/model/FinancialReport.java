package io.rratti.credit.risk.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FinancialReport {
    @CsvNumber("#")
    @CsvBindByName(column = "company_id", required = true)
    private Integer companyId;
    @CsvBindByName(column = "company_name", required = true)
    private String companyName;
    @CsvBindByName(column = "app_name", required = true)
    private String appName;
    @CsvNumber("###")
    @CsvBindByName(column = "risk_score", required = true)
    private Integer riskScore;
    @CsvBindByName(column = "risk_rating", required = true)
    private String riskRating;
}
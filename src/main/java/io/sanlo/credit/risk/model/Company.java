package io.sanlo.credit.risk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Company {
    private Integer id;
    private String companyName;
    private String countryCode;
}

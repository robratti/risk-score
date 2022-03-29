package io.sanlo.credit.risk.repository;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import io.sanlo.credit.risk.model.Company;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Repository
@AllArgsConstructor
public class CompanyReadRepository implements CsvReadRepository<Company> {
    private final CSVReader companyReader;
    @Override
    public List<Company> read() {
        ArrayList<Company> companies = new ArrayList<>();
        try {
            String[] recordArray;
            while ((recordArray = companyReader.readNext()) != null) {
                if (recordArray.length==3) {
                    var companyRecord = Company.builder()
                            .id(Integer.valueOf(recordArray[0]))
                            .companyName(recordArray[1])
                            .countryCode(recordArray[2])
                            .build();
                    companies.add(companyRecord);
                }
            }
            companyReader.close();
        } catch (IOException|CsvValidationException e) {
            log.error(format("An Exception was thrown while reading companies: %s", e.getMessage()));
        }

        return companies;
    }
}

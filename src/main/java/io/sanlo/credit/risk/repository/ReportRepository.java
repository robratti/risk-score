package io.sanlo.credit.risk.repository;

import com.opencsv.CSVWriter;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.sanlo.credit.risk.model.FinancialReport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@AllArgsConstructor
public class ReportRepository implements CsvWriteRepository<FinancialReport> {
    private final CSVWriter reportWriter;

    @Override
    public List<FinancialReport> write(
            List<FinancialReport> csvRecord
    ) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        HeaderColumnNameMappingStrategy<FinancialReport> strategy =
                new HeaderColumnNameMappingStrategyBuilder<FinancialReport>().build();
        strategy.setType(FinancialReport.class);
        var beanToCsv = new StatefulBeanToCsvBuilder<FinancialReport>(reportWriter)
                .withMappingStrategy(strategy)

                .build();
        beanToCsv.write(csvRecord);

        return csvRecord;
    }
}
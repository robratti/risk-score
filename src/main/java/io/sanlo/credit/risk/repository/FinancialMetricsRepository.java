package io.sanlo.credit.risk.repository;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import io.sanlo.credit.risk.model.FinancialMetric;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Repository
@AllArgsConstructor
public class FinancialMetricsRepository implements CsvReadRepository<FinancialMetric> {
    private final CSVReader financialMetricsReader;
    private final SimpleDateFormat dateFormat;

    @Override
    public List<FinancialMetric> read() {
        ArrayList<FinancialMetric> metrics = new ArrayList<>();
        try {
            String[] recordArray;
            while ((recordArray = financialMetricsReader.readNext()) != null) {
                if (recordArray.length==5) {
                    var metric = FinancialMetric.builder()
                            .date(dateFormat.parse(recordArray[0]))
                            .appName(recordArray[1])
                            .companyId(Integer.valueOf(recordArray[2]))
                            .revenue((recordArray[3].isBlank() || recordArray[3].isEmpty()) ?
                                    0.00 :
                                    Double.parseDouble(recordArray[3])
                            )
                            .marketingSpent((recordArray[4].isBlank() || recordArray[4].isEmpty()) ?
                                    0.00 :
                                    Double.parseDouble(recordArray[4])
                            ).build();
                    metrics.add(metric);
                }
            }
            financialMetricsReader.close();
        } catch (IOException | CsvValidationException | ParseException e) {
            log.error(format("An Exception was thrown while reading companies: %s", e.getMessage()));
        }

        return metrics;
    }
}

package io.sanlo.credit.risk.repository;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinancialMetricRepositoryTest {
    @Mock
    private CSVReader financialMetricsReader;
    @Mock
    private SimpleDateFormat dateFormat;

    @Test
    void read() throws CsvValidationException, IOException {
        when(financialMetricsReader.readNext()).thenReturn(new String[] {
                "2021-01-01","Kutch Townz","100","2628","40000"
        });
        var financialMetricsRepository = new FinancialMetricsRepository(financialMetricsReader, dateFormat);
        when(financialMetricsReader.readNext()).thenReturn(null);
        assertDoesNotThrow(financialMetricsRepository::read);
    }
}
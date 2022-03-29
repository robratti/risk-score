package io.sanlo.credit.risk.repository;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinancialMetricRepositoryExceptionTest {
    @Mock
    private CSVReader financialMetricsReader;
    @Mock
    private SimpleDateFormat simpleDateFormat;

    @Test
    void readIOException() throws CsvValidationException, IOException {
        when(financialMetricsReader.readNext()).thenThrow(new IOException("Test Exception"));
        var financialMetricsRepository = new FinancialMetricsRepository(financialMetricsReader, simpleDateFormat);

        assertDoesNotThrow(financialMetricsRepository::read);
    }

    @Test
    void readCsvValidationException() throws CsvValidationException, IOException {
        when(financialMetricsReader.readNext()).thenThrow(new CsvValidationException("Test Exception"));
        var financialMetricsRepository = new FinancialMetricsRepository(financialMetricsReader, simpleDateFormat);

        assertDoesNotThrow(financialMetricsRepository::read);
    }

    @Test
    void readCsvParseException() throws ParseException, CsvValidationException, IOException {
        when(financialMetricsReader.readNext()).thenReturn(new String[]{"2021-01-01","Kutch Town","100", "2628","40000"});
        when(simpleDateFormat.parse(anyString())).thenThrow(new ParseException("Test Exception", 0));
        var financialMetricsRepository = new FinancialMetricsRepository(financialMetricsReader, simpleDateFormat);

        assertDoesNotThrow(financialMetricsRepository::read);
    }
}
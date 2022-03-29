package io.sanlo.credit.risk.repository;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyReadRepositoryFailingTest {
    @Mock
    private CSVReader companyReader;

    @Test
    void readIOException() throws CsvValidationException, IOException {
        when(companyReader.readNext()).thenThrow(new IOException("Test Exception"));
        var companyReadRepository = new CompanyReadRepository(companyReader);
        assertDoesNotThrow(companyReadRepository::read);
    }

    @Test
    void readCsvValidation() throws CsvValidationException, IOException {
        when(companyReader.readNext()).thenThrow(new CsvValidationException("Test Exception"));
        var companyReadRepository = new CompanyReadRepository(companyReader);
        assertDoesNotThrow(companyReadRepository::read);
    }
}
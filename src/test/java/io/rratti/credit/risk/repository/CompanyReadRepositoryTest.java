package io.rratti.credit.risk.repository;

import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class CompanyReadRepositoryTest {
    @Autowired
    private CSVReader companyReader;

    @Test
    void read() {
        var companyReadRepository = new CompanyReadRepository(companyReader);
        assertDoesNotThrow(companyReadRepository::read);
    }
}
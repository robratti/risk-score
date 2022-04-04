package io.rratti.credit.risk.service;

import io.rratti.credit.risk.model.Company;
import io.rratti.credit.risk.repository.CompanyReadRepository;
import io.rratti.credit.risk.repository.FinancialMetricsRepository;
import io.rratti.credit.risk.repository.ReportRepository;
import io.rratti.credit.risk.utils.ReportUtilsTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {
    @Mock
    private CompanyReadRepository companyReadRepository;
    @Mock
    private FinancialMetricsRepository financialMetricsRepository;
    @Mock
    private ReportRepository reportRepository;

    @Test
    void run() throws ParseException {
        var companies = new ArrayList<Company>();
        companies.add(Company.builder()
                        .id(1)
                        .countryCode("US")
                        .companyName("test company")
                .build());
        Mockito.when(companyReadRepository.read()).thenReturn(companies);
        Mockito.when(financialMetricsRepository.read()).thenReturn(ReportUtilsTest.getFinancialMetrics());

        var reportService = new ReportServiceImpl(
                companyReadRepository,
                financialMetricsRepository,
                reportRepository
        );

        StepVerifier.create(reportService.run())
                .verifyComplete();
    }

    @Test
    void runEmpty() {
        var companies = new ArrayList<Company>();
        companies.add(Company.builder()
                .id(1)
                .countryCode("US")
                .companyName("test company")
                .build());
        Mockito.when(companyReadRepository.read()).thenReturn(companies);
        Mockito.when(financialMetricsRepository.read()).thenReturn(Collections.emptyList());

        var reportService = new ReportServiceImpl(
                companyReadRepository,
                financialMetricsRepository,
                reportRepository
        );

        StepVerifier.create(reportService.run())
                .verifyComplete();
    }
}
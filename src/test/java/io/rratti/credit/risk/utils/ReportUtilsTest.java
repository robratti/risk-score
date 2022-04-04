package io.rratti.credit.risk.utils;

import io.rratti.credit.risk.enums.Order;
import io.rratti.credit.risk.model.FinancialReport;
import io.rratti.credit.risk.model.FinancialMetric;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static io.rratti.credit.risk.enums.RiskRating.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReportUtilsTest {
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    @Test
    void sortDesc() {
        List<FinancialReport> financialReport = new ArrayList<>();
        financialReport.add(FinancialReport.builder()
                        .riskScore(10)
                .build());
        financialReport.add(FinancialReport.builder()
                .riskScore(50)
                .build());
        financialReport.add(FinancialReport.builder()
                        .riskScore(20)
                .build());

        ReportUtils.sortByRiskScore(financialReport, Order.DESC);

        assertEquals(50, financialReport.get(0).getRiskScore());
        assertEquals(20, financialReport.get(1).getRiskScore());
        assertEquals(10, financialReport.get(2).getRiskScore());
    }

    @Test
    void sortAsc() {
        List<FinancialReport> financialReport = new ArrayList<>();
        financialReport.add(FinancialReport.builder()
                .riskScore(10)
                .build());
        financialReport.add(FinancialReport.builder()
                .riskScore(50)
                .build());
        financialReport.add(FinancialReport.builder()
                .riskScore(20)
                .build());

        ReportUtils.sortByRiskScore(financialReport, Order.ASC);

        assertEquals(50, financialReport.get(2).getRiskScore());
        assertEquals(20, financialReport.get(1).getRiskScore());
        assertEquals(10, financialReport.get(0).getRiskScore());
    }

    @Test
    void sortByDateAsc() throws ParseException {
        List<FinancialMetric> financialMetrics = new ArrayList<>();
        financialMetrics.add(FinancialMetric.builder()
                        .companyId(1)
                        .date(dateFormatter.parse("2010-01-01"))
                .build());
        financialMetrics.add(FinancialMetric.builder()
                        .companyId(2)
                .date(dateFormatter.parse("2009-01-01"))
                .build());

        ReportUtils.sortByDate(financialMetrics, Order.ASC);

        assertEquals(2, financialMetrics.get(0).getCompanyId());
        assertEquals(1, financialMetrics.get(1).getCompanyId());
    }

    @Test
    void sortByDateDesc() throws ParseException {
        var dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        List<FinancialMetric> financialMetrics = new ArrayList<>();
        financialMetrics.add(FinancialMetric.builder()
                .companyId(1)
                .date(dateFormat.parse("2010-01-01"))
                .build());
        financialMetrics.add(FinancialMetric.builder()
                .companyId(2)
                .date(dateFormat.parse("2009-01-01"))
                .build());

        ReportUtils.sortByDate(financialMetrics, Order.DESC);

        assertEquals(1, financialMetrics.get(0).getCompanyId());
        assertEquals(2, financialMetrics.get(1).getCompanyId());
    }

    @Test
    void getPaybackPeriodByAppTest() throws ParseException {
        var report = getFinancialMetrics();
        assertEquals(4, ReportUtils.getPaybackPeriodByApp(report));
    }

    @Test
    void getLtcCacRatioByAppTest() throws ParseException {
        var report = getFinancialMetrics();
        assertEquals(1.2, ReportUtils.getLtcCacRatioByApp(report));
    }

    @Test
    void getPaybackPeriodNormalizedTest() {
        assertEquals(100, ReportUtils.getPaybackPeriodNormalized(6));
        assertEquals(80, ReportUtils.getPaybackPeriodNormalized(10));
        assertEquals(60, ReportUtils.getPaybackPeriodNormalized(19));
        assertEquals(30, ReportUtils.getPaybackPeriodNormalized(25));
        assertEquals(10, ReportUtils.getPaybackPeriodNormalized(28));
    }

    @Test
    void getLtcCacNormalizedTest() {
        assertEquals(100, ReportUtils.getLtcCacNormalized(3.1));
        assertEquals(80, ReportUtils.getLtcCacNormalized(2.8));
        assertEquals(60, ReportUtils.getLtcCacNormalized(2.2));
        assertEquals(30, ReportUtils.getLtcCacNormalized(1.8));
        assertEquals(10, ReportUtils.getLtcCacNormalized(1.2));
    }

    @Test
    void getRiskRatingTest() {
        assertEquals(UNDOUBTED, ReportUtils.getRiskRating(90));
        assertEquals(LOW, ReportUtils.getRiskRating(80));
        assertEquals(MODERATE, ReportUtils.getRiskRating(64));
        assertEquals(UNSATISFACTORY, ReportUtils.getRiskRating(30));
        assertEquals(UNACCEPTABLE, ReportUtils.getRiskRating(10));
    }

    public static List<FinancialMetric> getFinancialMetrics() throws ParseException {
        var report = new ArrayList<FinancialMetric>();
        report.add(FinancialMetric.builder()
                .appName("test-app")
                .companyId(1)
                .date(dateFormatter.parse("2022-01-01"))
                .revenue(1000.00)
                .marketingSpent(0.00)
                .build());
        report.add(FinancialMetric.builder()
                .appName("test-app")
                .companyId(1)
                .date(dateFormatter.parse("2022-01-01"))
                .revenue(2000.00)
                .marketingSpent(5000.00)
                .build());
        report.add(FinancialMetric.builder()
                .appName("test-app")
                .companyId(1)
                .date(dateFormatter.parse("2022-01-01"))
                .revenue(1000.00)
                .marketingSpent(0.00)
                .build());
        report.add(FinancialMetric.builder()
                .appName("test-app")
                .companyId(1)
                .date(dateFormatter.parse("2022-01-01"))
                .revenue(1000.00)
                .marketingSpent(0.00)
                .build());
        report.add(FinancialMetric.builder()
                .appName("test-app")
                .companyId(1)
                .date(dateFormatter.parse("2022-01-01"))
                .revenue(1000.00)
                .marketingSpent(0.00)
                .build());

        return report;
    }
}
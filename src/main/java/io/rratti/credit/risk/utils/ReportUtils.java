package io.rratti.credit.risk.utils;

import io.rratti.credit.risk.enums.RiskRating;
import io.rratti.credit.risk.model.FinancialMetric;
import io.rratti.credit.risk.model.FinancialReport;
import io.rratti.credit.risk.enums.Order;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ReportUtils {
    private ReportUtils() {}

    public static List<FinancialReport> sortByRiskScore(List<FinancialReport> reportList, Order order) {
        reportList.sort((o1, o2) -> {
            if (o1.getRiskScore().equals(o2.getRiskScore())) {
                return 0;
            } else if (o1.getRiskScore() > o2.getRiskScore()) {
                return order.getSign();
            }
            return -1 * order.getSign();
        });

        return reportList;
    }

    public static void sortByDate(List<FinancialMetric> reportList, Order order) {
        reportList.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()) * order.getSign());
    }

    public static Integer getPaybackPeriodByApp(List<FinancialMetric> report) {
        var days = 0;
        var index = 0;
        double marketingSpent = 0.00;
        double revenues = 0.00;
        ReportUtils.sortByDate(report, Order.ASC);

        while (marketingSpent <= revenues || index < report.size()) {
            if (report.get(index).getMarketingSpent() > 0) {
                marketingSpent = report.get(index).getMarketingSpent();
                days = 0;
            }
            days++;
            index++;
        }

        return days;
    }

    public static double getLtcCacRatioByApp(List<FinancialMetric> report) {
        AtomicReference<Double> cac = new AtomicReference<>(0.00);
        AtomicReference<Double> ltv = new AtomicReference<>(0.00);
        report.forEach(financialMetric -> {
            cac.getAndAccumulate(financialMetric.getMarketingSpent(), Double::sum);
            ltv.getAndAccumulate(financialMetric.getRevenue(), Double::sum);
        });

        return ltv.get()/cac.get();
    }

    public static Integer getPaybackPeriodNormalized(Integer days) {
        if (days < 7) {
            return 100;
        } else if (days<13) {
            return 80;
        } else if (days < 20) {
            return 60;
        } else if (days < 27) {
            return 30;
        }

        return 10;
    }

    public static Integer getLtcCacNormalized(double ltcCacRatio) {
        if (ltcCacRatio > 3.0) {
            return 100;
        } else if (ltcCacRatio > 2.5) {
            return 80;
        } else if (ltcCacRatio > 2.0) {
            return 60;
        } else if (ltcCacRatio > 1.5) {
            return 30;
        }

        return 10;
    }

    public static RiskRating getRiskRating(Integer riskScore) {
        if (riskScore>=85) {
            return RiskRating.UNDOUBTED;
        } else if (riskScore >=65) {
            return RiskRating.LOW;
        } else if (riskScore >= 45) {
            return RiskRating.MODERATE;
        } else if (riskScore >= 15) {
            return RiskRating.UNSATISFACTORY;
        }

        return RiskRating.UNACCEPTABLE;
    }
}

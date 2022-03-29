package io.sanlo.credit.risk.service;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.sanlo.credit.risk.enums.Order;
import io.sanlo.credit.risk.model.FinancialMetric;
import io.sanlo.credit.risk.model.FinancialReport;
import io.sanlo.credit.risk.repository.CompanyReadRepository;
import io.sanlo.credit.risk.repository.FinancialMetricsRepository;
import io.sanlo.credit.risk.repository.ReportRepository;
import io.sanlo.credit.risk.utils.ReportUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService, ApplicationContextAware {
    private final CompanyReadRepository companyReadRepository;
    private final FinancialMetricsRepository financialMetricsRepository;
    private final ReportRepository reportRepository;

    private ApplicationContext applicationContext;

    private static final Map<String, List<FinancialMetric>> groupedByAppName = new ConcurrentHashMap<>();
    private static final Map<Integer, String> companyMap = new HashMap<>();

    public ReportServiceImpl(CompanyReadRepository companyReadRepository, FinancialMetricsRepository financialMetricsRepository, ReportRepository reportRepository) {
        this.companyReadRepository = companyReadRepository;
        this.financialMetricsRepository = financialMetricsRepository;
        this.reportRepository = reportRepository;
    }

    @Override
    public Mono<Void> run() {
        init();
        return Flux.fromIterable(groupedByAppName.values())
                .flatMap(financialMetrics -> this.calculateReport(financialMetrics.get(0).getAppName(), financialMetrics))
                .collectList()
                .flatMap(this::writeReport)
                .doFinally(signalType -> shutdownApplication())
                .then();
    }

    private void shutdownApplication() {
        try {
            ((ConfigurableApplicationContext) applicationContext).close();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    protected void init() {
        companyReadRepository.read().forEach(company -> companyMap.put(company.getId(), company.getCompanyName()));
        financialMetricsRepository.read().forEach(financialMetric -> {
            if (groupedByAppName.isEmpty() || !groupedByAppName.containsKey(financialMetric.getAppName())) {
                var financialReportList = new ArrayList<FinancialMetric>();
                financialReportList.add(financialMetric);
                groupedByAppName.put(financialMetric.getAppName(), financialReportList);
            } else {
                groupedByAppName.get(financialMetric.getAppName()).add(financialMetric);
            }
        });

        log.info("App initialized...");
    }

    private Mono<FinancialReport> calculateReport(String appName, List<FinancialMetric> financialMetricList) {
        return Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
            var ltcCacRatio = ReportUtils.getLtcCacRatioByApp(financialMetricList);
            var paybackPeriod = ReportUtils.getPaybackPeriodByApp(financialMetricList);

            int riskScore = (int) (.7 * ReportUtils.getLtcCacNormalized(ltcCacRatio) +
                    0.3 * ReportUtils.getPaybackPeriodNormalized(paybackPeriod));
            var appReport = FinancialReport.builder()
                    .appName(appName)
                    .companyId(financialMetricList.get(0).getCompanyId())
                    .companyName(companyMap.get(financialMetricList.get(0).getCompanyId()))
                    .riskScore(riskScore)
                    .riskRating(ReportUtils.getRiskRating(riskScore).getName())
                    .build();
            log.info(format("calculated report for %s", appReport));

            return appReport;
        }));
    }

    private Mono<List<FinancialReport>> writeReport(List<FinancialReport> reportList) {
        return Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Generating report...");
                return this.reportRepository.write(ReportUtils.sortByRiskScore(reportList, Order.DESC));
            } catch (CsvRequiredFieldEmptyException|CsvDataTypeMismatchException e) {
                log.error(format("It was not possible produce the report for %s: %s", reportList, e.getMessage()));
                return Collections.emptyList();
            }
        }));
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

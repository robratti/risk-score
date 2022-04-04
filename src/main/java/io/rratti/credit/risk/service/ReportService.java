package io.rratti.credit.risk.service;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ReportService {
    Mono<Void> run();
}

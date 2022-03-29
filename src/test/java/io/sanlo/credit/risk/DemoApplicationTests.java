package io.sanlo.credit.risk;

import io.sanlo.credit.risk.service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DemoApplicationTests {
    @Mock
    ReportService reportService;
    @Test
    void contextLoads() {
        when(reportService.run()).thenReturn(Mono.empty());
        var application = new DemoApplication(reportService);

        assertDoesNotThrow(() -> application.run());
    }
}
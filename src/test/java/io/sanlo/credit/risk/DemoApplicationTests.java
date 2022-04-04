package io.sanlo.credit.risk;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class DemoApplicationTests {
    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> DemoApplication.main(new String[]{}));
    }
}
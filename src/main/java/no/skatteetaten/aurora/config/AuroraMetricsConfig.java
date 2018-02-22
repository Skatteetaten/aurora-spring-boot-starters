package no.skatteetaten.aurora.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.spring.autoconfigure.MeterRegistryCustomizer;

@Configuration
public class AuroraMetricsConfig {

    private static final int MIN_MILLIS = 100;
    private static final int MAX_SECONDS = 5;

    @Bean
    MeterRegistryCustomizer<MeterRegistry> auroraConfigurer() {
        return registry -> registry.config()
            .meterFilter(MeterFilter.minExpected("http", Duration.ofMillis(MIN_MILLIS)))
            .meterFilter(MeterFilter.maxExpected("http", Duration.ofSeconds(MAX_SECONDS)))
            .meterFilter(MeterFilter.minExpected("operations", Duration.ofMillis(MIN_MILLIS)))
            .meterFilter(MeterFilter.maxExpected("operations", Duration.ofSeconds(MAX_SECONDS)));
    }

}

package no.skatteetaten.aurora.config;

import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.spring.autoconfigure.MeterRegistryCustomizer;

@Configuration
public class AuroraMetricsConfig {

    private static final int MIN_MILLIS = 100;
    private static final int MAX_SECONDS = 5;

    @ConditionalOnMissingBean
    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer() {
        return registry -> registry.config()
            .meterFilter(MeterFilter.minExpected("http", Duration.ofMillis(MIN_MILLIS)))
            .meterFilter(MeterFilter.maxExpected("http", Duration.ofSeconds(MAX_SECONDS)))
            .meterFilter(MeterFilter.minExpected("operations", Duration.ofMillis(MIN_MILLIS)))
            .meterFilter(MeterFilter.maxExpected("operations", Duration.ofSeconds(MAX_SECONDS)));
    }

    @Bean
    FileDescriptorMetrics fileDescriptorMetrics() {
        return new FileDescriptorMetrics();
    }

    @Bean
    JvmThreadMetrics threadMetrics() {
        return new JvmThreadMetrics();
    }

    @Bean
    ProcessorMetrics processorMetrics() {
        return new ProcessorMetrics();
    }

    @Bean
    JvmGcMetrics gcMetrics() {
        return new JvmGcMetrics();
    }

    @Bean
    UptimeMetrics uptimeMetrics() {
        return new UptimeMetrics();
    }
}

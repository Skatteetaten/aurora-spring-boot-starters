package no.skatteetaten.aurora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.binder.JvmGcMetrics;
import io.micrometer.core.instrument.binder.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.ProcessorMetrics;
import io.micrometer.core.instrument.binder.UptimeMetrics;

@Configuration
public class AuroraMetricsConfig {

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

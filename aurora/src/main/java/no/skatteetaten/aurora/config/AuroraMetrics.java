package no.skatteetaten.aurora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.binder.JvmGcMetrics;
import io.micrometer.core.instrument.binder.ProcessorMetrics;
import io.micrometer.core.instrument.binder.ThreadMetrics;

@Configuration
public class AuroraMetrics {

    @Bean
    ThreadMetrics threadMetrics() {
        return new ThreadMetrics();
    }

    @Bean
    ProcessorMetrics processorMetrics() {
        return new ProcessorMetrics();
    }

    @Bean
    JvmGcMetrics gcMetrics() {
        return new JvmGcMetrics();

    }
}

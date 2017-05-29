package ske.aurora.config;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.prometheus.client.CollectorRegistry;
import ske.aurora.prometheus.MetricsConfig;
import ske.aurora.prometheus.collector.HttpMetricsCollector;

@Configuration
public class MetricsAutoConfig {

    private static final Logger logger = LoggerFactory.getLogger(MetricsAutoConfig.class);

    @Bean
    public CollectorRegistry prometheusRegistry(Set<HttpMetricsCollector> httpCollectors) {
        try {
            return MetricsConfig.init(CollectorRegistry.defaultRegistry, httpCollectors);
        } catch (IllegalArgumentException e) {
            logger.debug("Already initialized", e);
            return CollectorRegistry.defaultRegistry;
        }
    }

}

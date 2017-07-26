package no.skatteetaten.aurora.config;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.prometheus.client.CollectorRegistry;
import no.skatteetaten.aurora.prometheus.AuroraSpringBootMetricsCollector;
import no.skatteetaten.aurora.prometheus.MetricsConfig;
import no.skatteetaten.aurora.prometheus.collector.HttpMetricsCollector;

@Configuration
public class MetricsAutoConfig {

    private static final Logger logger = LoggerFactory.getLogger(MetricsAutoConfig.class);

    @Bean
    public CollectorRegistry prometheusRegistry(Set<HttpMetricsCollector> httpCollectors) {
        try {
            CollectorRegistry registry = new CollectorRegistry(true);
            MetricsConfig.init(registry, httpCollectors);
            return registry;
        } catch (IllegalArgumentException e) {
            logger.debug("Already initialized", e);
            return CollectorRegistry.defaultRegistry;
        }
    }

    @Bean
    public AuroraSpringBootMetricsCollector springBootMetricsCollector(
        Collection<PublicMetrics> publicMetrics,
        CollectorRegistry registry) {

        AuroraSpringBootMetricsCollector collector = new AuroraSpringBootMetricsCollector(publicMetrics);
        collector.register(registry);
        return collector;
    }

}

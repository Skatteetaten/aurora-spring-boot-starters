package ske.aurora.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ske.aurora.prometheus.collector.HttpMetricsCollector;

@Configuration
@EnableConfigurationProperties(HttpMetricsCollectorConfig.AuroraClientConfiguration.class)
public class HttpMetricsCollectorConfig {

    @Bean(name = "client")
    public HttpMetricsCollector clientHttpMetricsCollector(AuroraClientConfiguration configuration) {
        return new HttpMetricsCollector(true, configuration.asPathGroup(), configuration.isStrict());
    }

    @Bean(name = "server")
    public HttpMetricsCollector serverHttpMetricsCollector() {
        return new HttpMetricsCollector(false, Collections.emptyList(), false);
    }

    //TODO: Add posibility to exclude some regexes
    @ConfigurationProperties(prefix = "aurora.client")
    public static class AuroraClientConfiguration {

        private boolean strict = false;
        private Map<String, String> metrics = new HashMap<>();

        public Map<String, String> getMetrics() {
            return this.metrics;
        }

        public boolean isStrict() {
            return strict;
        }

        public void setStrict(boolean strict) {
            this.strict = strict;
        }

        List<HttpMetricsCollector.PathGroup> asPathGroup() {
            return metrics.entrySet().stream()
                .map(e -> new HttpMetricsCollector.PathGroup(e.getValue(), e.getKey()))
                .collect(Collectors.toList());

        }
    }

}

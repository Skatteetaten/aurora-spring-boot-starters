package ske.aurora.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ske.aurora.prometheus.HttpMetricsCollectorSpecification;
import ske.aurora.prometheus.collector.HttpMetricsCollector;

@Configuration
@EnableConfigurationProperties({HttpMetricsCollectorConfig.AuroraClientConfiguration.class,
    HttpMetricsCollectorConfig.AuroraServerConfiguration.class})
public class HttpMetricsCollectorConfig {

    @Bean(name = "client")
    public HttpMetricsCollector clientHttpMetricsCollector(AuroraClientConfiguration configuration) {
        return new HttpMetricsCollector(true, configuration);
    }

    @Bean(name = "server")
    public HttpMetricsCollector serverHttpMetricsCollector(AuroraServerConfiguration configuration) {
        return new HttpMetricsCollector(false, configuration);
    }

    public enum MetricsMode {
        ALL,
        INCLUDE_MAPPINGS,
        INCLUDE,
        EXCLUDE
    }

    @ConfigurationProperties(prefix = "aurora.server")
    static class AuroraServerConfiguration extends AuroraConfigurationTemplate {

    }

    @ConfigurationProperties(prefix = "aurora.client")
    static class AuroraClientConfiguration extends AuroraConfigurationTemplate {

    }

    public static class AuroraConfigurationTemplate implements HttpMetricsCollectorSpecification {

        private MetricsMode mode = MetricsMode.ALL;
        private LinkedHashMap<String, String> metricsPathLabelGroupings = new LinkedHashMap<>();
        private LinkedHashMap<String, String> includes = new LinkedHashMap<>();
        private LinkedHashMap<String, String> excludes = new LinkedHashMap<>();

        @Override
        public Map<String, String> getMetricsPathLabelGroupings() {
            return metricsPathLabelGroupings;
        }

        @Override
        public MetricsMode getMode() {
            return mode;
        }

        @Override
        public void setMode(MetricsMode mode) {
            this.mode = mode;
        }

        @Override
        public Map<String, String> getIncludes() {
            return includes;
        }

        @Override
        public Map<String, String> getExcludes() {
            return excludes;
        }
    }

}

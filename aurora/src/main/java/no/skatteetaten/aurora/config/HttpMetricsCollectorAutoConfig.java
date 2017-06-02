package no.skatteetaten.aurora.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.skatteetaten.aurora.prometheus.HttpMetricsCollectorConfig;
import no.skatteetaten.aurora.prometheus.collector.HttpMetricsCollector;

@Configuration
@EnableConfigurationProperties({ HttpMetricsCollectorAutoConfig.AuroraClientConfiguration.class,
    HttpMetricsCollectorAutoConfig.AuroraServerConfiguration.class})
public class HttpMetricsCollectorAutoConfig {

    @Bean(name = "client")
    public HttpMetricsCollector clientHttpMetricsCollector(AuroraClientConfiguration configuration) {
        return new HttpMetricsCollector(true, configuration);
    }

    @Bean(name = "server")
    public HttpMetricsCollector serverHttpMetricsCollector(AuroraServerConfiguration configuration) {
        return new HttpMetricsCollector(false, configuration);
    }


    @ConfigurationProperties(prefix = "aurora.server")
    static class AuroraServerConfiguration extends HttpMetricsCollectorConfig {

    }

    @ConfigurationProperties(prefix = "aurora.client")
    static class AuroraClientConfiguration extends HttpMetricsCollectorConfig {

    }

}

package ske.aurora.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ske.aurora.prometheus.collector.HttpMetricsCollector;

@Configuration
public class HttpMetricsCollectorConfig {

    //TODO: Read this from config
    @Bean(name = "client")
    public HttpMetricsCollector clientHttpMetricsCollector() {
        return new HttpMetricsCollector(true, Collections.emptyList(), false);
    }

    @Bean(name = "server")
    public HttpMetricsCollector serverHttpMetricsCollector() {
        return new HttpMetricsCollector(false, Collections.emptyList(), false);
    }
}

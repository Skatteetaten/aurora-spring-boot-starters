package ske.aurora.prometheus;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.ManagementContextConfiguration;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.hotspot.DefaultExports;

@ManagementContextConfiguration
public class PrometheusConfiguration {

    @PostConstruct
    public void configureMetrics() {

        DefaultExports.initialize();
        new JvmGcMetrics().register();

        // logback metrics
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        LogbackMetricsAppender logbackMetricsAppender = new LogbackMetricsAppender(CollectorRegistry.defaultRegistry);
        logbackMetricsAppender.setContext(lc);
        logbackMetricsAppender.start();
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(logbackMetricsAppender);
    }
}

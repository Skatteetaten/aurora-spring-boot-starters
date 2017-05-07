package ske.aurora.config;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import io.prometheus.client.hotspot.ThreadExports;
import ske.aurora.prometheus.ClientMetricsInterceptor;
import ske.aurora.prometheus.Execute;
import ske.aurora.prometheus.JvmGcMetrics;
import ske.aurora.prometheus.LogbackMetricsAppender;
import ske.aurora.prometheus.ServerMetricsFilter;

@Configuration
public class MetricsConfig {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MetricsConfig.class);

    @Bean
    public CollectorRegistry prometheusRegistry(ServerMetricsFilter serverMetricsFilter,
        ClientMetricsInterceptor clientMetricsInterceptor) {
        CollectorRegistry registry = CollectorRegistry.defaultRegistry;

        serverMetricsFilter.register(registry);
        clientMetricsInterceptor.register(registry);

        //do not register the default metrics since we want full control here? Is
        new StandardExports().register(registry);
        new MemoryPoolsExports().register(registry);
        new ThreadExports().register(registry);

        //We do not need these. No need to make metrics we will never scrape
        //new ClassLoadingExports().register(registry);
        //new VersionInfoExports().register(registry);

        //do we need both of these?
        //new GarbageCollectorExports().register(registry);
        new JvmGcMetrics().register(registry);
        Execute.getInstance().register(registry);

        // logback metrics
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        LogbackMetricsAppender logbackMetricsAppender = new LogbackMetricsAppender(registry);
        logbackMetricsAppender.setContext(lc);
        logbackMetricsAppender.start();
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(logbackMetricsAppender);

        logger.debug("Registered gc metrics, execute metrics and logback metrics");
        return registry;
    }
}

package no.skatteetaten.aurora.config;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.ThreadMetrics;
import io.micrometer.spring.SpringMeters;

@Configuration
@ConditionalOnProperty(prefix = "aurora", value = "db")
class AuroraMetricsConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Collection<DataSourcePoolMetadataProvider> metadataProviders;

    @Autowired
    private MeterRegistry registry;

    @PostConstruct
    private void instrumentDataSource() {

        SpringMeters.monitor(
            registry,
            dataSource,
            metadataProviders,
            "data.source");
    }

    @Bean
    ThreadMetrics threadMetrics() {
        return new ThreadMetrics();
    }
}
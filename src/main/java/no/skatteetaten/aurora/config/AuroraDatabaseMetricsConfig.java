package no.skatteetaten.aurora.config;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.spring.jdbc.DataSourcePoolMetrics;

@Configuration
@ConditionalOnBean(DataSource.class)
class AuroraDatabaseMetricsConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Collection<DataSourcePoolMetadataProvider> metadataProviders;

    @Autowired
    private MeterRegistry registry;

    @PostConstruct
    private void instrumentDataSource() {

        new DataSourcePoolMetrics(
            dataSource,
            metadataProviders,
            "data.source",
            Collections.emptyList()).bindTo(registry);
    }
}

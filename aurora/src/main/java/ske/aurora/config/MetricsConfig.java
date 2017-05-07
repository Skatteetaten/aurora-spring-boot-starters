package ske.aurora.config;

import java.util.Collections;

import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import io.prometheus.client.hotspot.ThreadExports;
import okhttp3.OkHttpClient;
import ske.aurora.prometheus.ClientMetricsInterceptor;
import ske.aurora.prometheus.Execute;
import ske.aurora.prometheus.JvmGcMetrics;
import ske.aurora.prometheus.LogbackMetricsAppender;
import ske.aurora.prometheus.ServerMetricsFilter;

@Configuration
public class MetricsConfig {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MetricsConfig.class);

    @Bean
    public ServerMetricsFilter serverMetricsFilter() {
        //TODO: Read config from yaml fil here.
        return new ServerMetricsFilter(Collections.emptyList(), false);
    }

    @Bean
    public FilterRegistrationBean serverMetrics(ServerMetricsFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.addUrlPatterns("/*");
        registrationBean.setFilter(filter);
        return registrationBean;
    }

    @Bean
    public OkHttpClient client(ClientMetricsInterceptor interceptor) {

        //TODO: read config from yaml file here.
        return new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build();

    }

    @Bean
    public RestTemplate restTemplate(OkHttpClient client) {

        OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory = new OkHttp3ClientHttpRequestFactory(client);
        return new RestTemplate(okHttp3ClientHttpRequestFactory);
    }

    @Bean
    public ClientMetricsInterceptor clientMetrics() {
        return new ClientMetricsInterceptor(Collections.emptyList(), false);
    }

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

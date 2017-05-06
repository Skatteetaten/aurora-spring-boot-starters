package ske.aurora.config;

import java.util.Collections;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import io.prometheus.client.CollectorRegistry;
import okhttp3.OkHttpClient;
import ske.aurora.prometheus.ClientMetricsInterceptor;
import ske.aurora.prometheus.ServerMetricsFilter;

@Configuration
public class MetricsConfig {


    @Bean
    public FilterRegistrationBean serverMetrics() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.addUrlPatterns("/*");
        registrationBean.setFilter(new ServerMetricsFilter(Collections.emptyList(), CollectorRegistry.defaultRegistry));
        return registrationBean;
    }

    @Bean
    public RestTemplate restTemplate(ClientMetricsInterceptor interceptor) {

        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build();

        OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory = new OkHttp3ClientHttpRequestFactory(client);
        return new RestTemplate(okHttp3ClientHttpRequestFactory);
    }

    @Bean
    public ClientMetricsInterceptor clientMetrics() {
        return new ClientMetricsInterceptor(Collections.emptyList(), CollectorRegistry.defaultRegistry);
    }
}

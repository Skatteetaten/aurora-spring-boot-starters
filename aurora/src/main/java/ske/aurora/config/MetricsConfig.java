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
import ske.aurora.prometheus.Execute;
import ske.aurora.prometheus.ServerMetricsFilter;

@Configuration
public class MetricsConfig {

    @Bean
    public FilterRegistrationBean serverMetrics() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.addUrlPatterns("/*");
        //TODO: Read config from yaml fil here.
        registrationBean.setFilter(new ServerMetricsFilter(Collections.emptyList(), CollectorRegistry.defaultRegistry));
        return registrationBean;
    }

    @Bean
    public Execute execute() {
        return new Execute();
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
        return new ClientMetricsInterceptor(Collections.emptyList(), CollectorRegistry.defaultRegistry);
    }
}

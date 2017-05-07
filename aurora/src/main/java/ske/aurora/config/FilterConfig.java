package ske.aurora.config;

import java.util.Collections;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import okhttp3.OkHttpClient;
import ske.aurora.prometheus.ClientMetricsInterceptor;
import ske.aurora.prometheus.ServerMetricsFilter;

@Configuration
public class FilterConfig {

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

}

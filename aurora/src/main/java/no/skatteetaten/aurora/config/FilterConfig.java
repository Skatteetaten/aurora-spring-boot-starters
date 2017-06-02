package no.skatteetaten.aurora.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import no.skatteetaten.aurora.prometheus.ServerMetricsCollector;
import no.skatteetaten.aurora.prometheus.TimingInterceptor;

@Configuration
public class FilterConfig {

    private Set<RestTemplate> restTemplates;
    private TimingInterceptor timeInterceptor;

    public FilterConfig(Set<RestTemplate> restTemplates, TimingInterceptor timeInterceptor) {

        this.restTemplates = restTemplates;
        this.timeInterceptor = timeInterceptor;
    }

    @PostConstruct
    public void init() {
        if (restTemplates == null) {
            throw new IllegalArgumentException("no resttemplates?");
        }

        restTemplates.forEach(this::registerInterceptors);

    }

    private void registerInterceptors(RestTemplate rt) {
        List<ClientHttpRequestInterceptor> interceptors = rt.getInterceptors();

        for (ClientHttpRequestInterceptor interceptor : interceptors) {
            if (interceptor instanceof TimingInterceptor) {
                return;
            }
        }

        interceptors = new ArrayList<>(interceptors);
        interceptors.add(timeInterceptor);
        rt.setInterceptors(interceptors);

    }

    @Bean
    public FilterRegistrationBean serverMetrics(ServerMetricsCollector filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.addUrlPatterns("/*");
        registrationBean.setFilter(filter);
        return registrationBean;
    }

}

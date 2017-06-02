package no.skatteetaten.aurora.prometheus;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import no.skatteetaten.aurora.prometheus.collector.HttpMetricsCollector;

@Component
public class ServerMetricsCollector  implements Filter {

    private HttpMetricsCollector collector;

    public ServerMetricsCollector(@Qualifier("server") HttpMetricsCollector collector) {
        this.collector = collector;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        long start = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            collector.record(request.getMethod(), request.getRequestURI(), response.getStatus(), start);
        }
    }

    @Override
    public void destroy() {
        //we do not need to destroy
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //nothing to init

    }

}

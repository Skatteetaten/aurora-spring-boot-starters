package ske.aurora.prometheus;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.prometheus.client.CollectorRegistry;

public class ServerMetricsFilter extends CommonMetricsFilter implements Filter {

    public ServerMetricsFilter(List<PathGroup> aggregations, CollectorRegistry registry) {
        super(false, aggregations, registry);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
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
            record(request.getMethod(), request.getRequestURI(), response.getStatus(), start);
        }
    }

    @Override
    public void destroy() {

    }

}

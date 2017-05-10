package ske.aurora.prometheus.collector;

import static ske.aurora.utils.PrometheusUrlNormalizer.normalize;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;

import io.prometheus.client.Collector;
import io.prometheus.client.Histogram;
import ske.aurora.prometheus.HttpMetricsCollectorSpecification;

public class HttpMetricsCollector extends Collector {

    private final Histogram requests;
    private boolean isClient;
    private HttpMetricsCollectorSpecification specification;

    public HttpMetricsCollector(boolean isClient, HttpMetricsCollectorSpecification specification) {
        this.isClient = isClient;
        this.specification = specification;

        requests = Histogram.build()
            .name(String.format("http_%s_requests", isClient ? "client" : "server"))
            .help(String.format("Http %s requests", isClient ? "client" : "server"))
            .labelNames("http_method", "http_status", "http_status_group", "path")
            .create();
    }

    public void record(String method, String requestUri, int statusCode, long start) {
        Optional<String> name = findMatchingPathGroup(specification.getMetricsPathLabelGroupings(), requestUri);

        switch (specification.getMode()) {
        case INCLUDE_MAPPINGS:
            if (!name.isPresent()) {
                return;
            }
            break;
        case INCLUDE:
            if (!findMatchingPathGroup(specification.getIncludes(), requestUri).isPresent()) {
                return;
            }
            break;
        case EXCLUDE:
            if (findMatchingPathGroup(specification.getExcludes(), requestUri).isPresent()) {
                return;
            }
            break;
        default:
            break;
        }

        String path = name.orElse(normalize(requestUri, isClient));

        long duration = System.nanoTime() - start;
        requests.labels(
            method,
            String.valueOf(statusCode),
            HttpStatus.Series.valueOf(statusCode).name(),
            path
        ).observe(duration / Collector.NANOSECONDS_PER_SECOND);
    }

    private Optional<String> findMatchingPathGroup(Map<String, String> mappings, String url) {

        return mappings.entrySet().stream()
            .filter(e -> url.matches(e.getValue()))
            .map(Map.Entry::getKey)
            .findFirst();

    }

    @Override
    public List<MetricFamilySamples> collect() {
        return requests.collect();
    }

}

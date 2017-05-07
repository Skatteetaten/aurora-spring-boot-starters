package ske.aurora.prometheus;

import static ske.aurora.utils.PrometheusUrlNormalizer.normalize;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;

import io.prometheus.client.Collector;
import io.prometheus.client.Histogram;
import io.prometheus.client.SimpleTimer;

public class CommonMetricsFilter extends Collector {

    private boolean isClient;
    private final List<PathGroup> aggregations;
    private final Histogram requests;
    private boolean strictMode;

    public CommonMetricsFilter(boolean isClient, List<PathGroup> aggregations,
        boolean strictMode) {
        this.isClient = isClient;
        this.aggregations = Collections.unmodifiableList(aggregations);
        this.strictMode = strictMode;

        if (strictMode && aggregations.isEmpty()) {
            throw new IllegalArgumentException("Strict mode with no PathGroups is not allowed.");
        }
        requests = Histogram.build()
            .name(String.format("http_%s_requests", isClient ? "client" : "server"))
            .help(String.format("Http %s requests", isClient ? "client" : "server"))
            .labelNames("http_method", "http_status", "http_status_group", "path")
            .create();
    }

    void record(String method, String requestUri, int statusCode, SimpleTimer timer) {

        Optional<PathGroup> pathGroup = findMatchingPathGroup(requestUri);

        if (strictMode && !pathGroup.isPresent()) {
            return;
        }

        String path = pathGroup
            .map(e -> e.name)
            .orElse(normalize(requestUri, isClient));

        requests.labels(
            method,
            String.valueOf(statusCode),
            HttpStatus.Series.valueOf(statusCode).name(),
            path
        ).observe(timer.elapsedSeconds());
    }

    private Optional<PathGroup> findMatchingPathGroup(String url) {

        return aggregations.stream()
            .filter(e -> url.matches(e.regex))
            .findFirst();

    }

    @Override
    public List<MetricFamilySamples> collect() {
        return requests.collect();
    }

    public static class PathGroup {
        private final String regex;
        private final String name;

        public PathGroup(String regex, String name) {
            this.regex = regex;
            this.name = name;
        }
    }
}

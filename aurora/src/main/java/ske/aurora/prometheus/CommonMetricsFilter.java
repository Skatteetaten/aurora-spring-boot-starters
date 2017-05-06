package ske.aurora.prometheus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;

public class CommonMetricsFilter {

    private final List<PathGroup> aggregations;
    private final Histogram requests;

    public CommonMetricsFilter(boolean isClient, List<PathGroup> aggregations, CollectorRegistry registry) {
        this.aggregations = Collections.unmodifiableList(aggregations);
        requests = Histogram.build()
            .name("http_" + (isClient ? "client" : "server") + "_requests")
            .help("Http requests")
            .labelNames("http_method", "http_status", "path")
            .register(registry);
    }

    void record(String method, String requestUri, int statusCode, long startTimeNano) {
        long duration = System.nanoTime() - startTimeNano;
        requests.labels(
            method,
            ServerLabel.fromStatusCode(statusCode).name(),
            path(requestUri)).observe(duration / Collector.NANOSECONDS_PER_SECOND);
    }

    private String path(String url) {
        Optional<String> name = aggregations.stream()
            .filter(e -> url.matches(e.regex))
            .findFirst()
            .map(e -> e.name);

        String key;

        if (name.isPresent()) {
            key = name.get();
        } else {
            key = url;
            key = key.replace("https://", "");
            key = key.replace("http://", "");
            if (key.startsWith("/")) {
                key = key.substring(1);
            }
            if (key.endsWith("/")) {
                key = key.substring(0, key.length() - 1);
            }
            key = key.replaceAll(":", "_");
            key = key.replaceAll("/", "_");
            key = key.replaceAll("-", "_");
        }
        return key;
    }

    public static class PathGroup {
        private final String regex;
        private final String name;

        public PathGroup(String regex, String name) {
            this.regex = regex;
            this.name = name;
        }
    }

    public enum ServerLabel {
        SUCCESS,
        NOT_FOUND,
        CLIENT_ERROR,
        SERVER_ERROR,
        OTHER,
        EXCEPTION;

        public static ServerLabel fromStatusCode(int statusCode) {
            if (statusCode == 0) {
                return EXCEPTION;
            }
            HttpStatus.Series series = HttpStatus.Series.valueOf(statusCode);
            HttpStatus httpStatus = HttpStatus.valueOf(statusCode);

            if (series == HttpStatus.Series.SUCCESSFUL) {
                return SUCCESS;
            } else if (httpStatus == HttpStatus.NOT_FOUND) {
                return NOT_FOUND;
            } else if (series == HttpStatus.Series.CLIENT_ERROR) {
                return CLIENT_ERROR;
            } else if (series == HttpStatus.Series.SERVER_ERROR) {
                return SERVER_ERROR;
            } else {
                return OTHER;
            }

        }

    }
}

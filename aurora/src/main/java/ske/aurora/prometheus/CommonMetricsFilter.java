package ske.aurora.prometheus;

import static ske.aurora.utils.PrometheusUrlNormalizer.normalize;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;

public class CommonMetricsFilter {

    private final List<PathGroup> aggregations;
    private final Histogram requests;


    //TODO: prefer delegation to inheritance.
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

        //TODO: handle strict mode. If we want it.
       return aggregations.stream()
            .filter(e -> url.matches(e.regex))
            .findFirst()
            .map(e -> e.name)
            .orElse(normalize(url));
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

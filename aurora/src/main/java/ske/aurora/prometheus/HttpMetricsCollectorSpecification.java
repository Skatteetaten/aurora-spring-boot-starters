package ske.aurora.prometheus;

import java.util.Map;

import ske.aurora.config.HttpMetricsCollectorConfig;

public interface HttpMetricsCollectorSpecification {

    Map<String, String> getMetricsPathLabelGroupings();

    HttpMetricsCollectorConfig.MetricsMode getMode();

    void setMode(HttpMetricsCollectorConfig.MetricsMode mode);

    Map<String, String> getIncludes();

    Map<String, String> getExcludes();
}

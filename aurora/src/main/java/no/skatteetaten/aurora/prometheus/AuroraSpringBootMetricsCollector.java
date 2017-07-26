package no.skatteetaten.aurora.prometheus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;

import io.prometheus.client.Collector;

@Component
public class AuroraSpringBootMetricsCollector extends Collector implements Collector.Describable {
    private final Collection<PublicMetrics> publicMetrics;

    @Autowired
    public AuroraSpringBootMetricsCollector(Collection<PublicMetrics> publicMetrics) {
        this.publicMetrics = publicMetrics;
    }

    @Override
    public List<MetricFamilySamples> collect() {
        ArrayList<MetricFamilySamples> samples = new ArrayList<>();
        for (PublicMetrics publicMetric : this.publicMetrics) {
            for (Metric<?> metric : publicMetric.metrics()) {
                String name = Collector.sanitizeMetricName(metric.getName());
                double value = metric.getValue().doubleValue();
                MetricFamilySamples metricFamilySamples = new MetricFamilySamples(
                    name, Type.GAUGE, name, Collections.singletonList(
                    new MetricFamilySamples.Sample(name, new ArrayList<>(), new ArrayList<>(), value)));
                samples.add(metricFamilySamples);
            }
        }
        return samples;
    }

    @Override
    public List<MetricFamilySamples> describe() {
        return new ArrayList<>();
    }
}

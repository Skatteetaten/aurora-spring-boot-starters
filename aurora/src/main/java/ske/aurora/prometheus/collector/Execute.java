package ske.aurora.prometheus.collector;

import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.Collector;
import io.prometheus.client.Histogram;
import io.prometheus.client.SimpleTimer;

public final class Execute extends Collector {

    private static final Logger logger = LoggerFactory.getLogger(Execute.class);

    private static Execute instance;

    private final Histogram executions;

    public Execute() {
        executions = Histogram.build()
            .name("execute")
            .help("Manual executions that we want statistics on")
            .labelNames("type", "group", "name")
            .create();
        logger.debug("executions histogram registered");

    }

    public static <T> T withMetrics(Class claz, String name,
        Supplier<T> s) {
        return withMetrics(claz.getName(), name, s);
    }

    public static <T> T withMetrics(String group, String name,
        Supplier<T> s) {

        SimpleTimer requestTimer = new SimpleTimer();
        String type = "success";
        try {
            return s.get();
        } catch (Exception e) {
            type = e.getClass().getSimpleName();
            throw e;
        } finally {
            instance.executions.labels(type, group, name)
                .observe(requestTimer.elapsedSeconds());
        }
    }

    public static Execute getInstance() {

        if (instance == null) {

            logger.debug("Create new execute metrics");
            instance = new Execute();
        }
        return instance;
    }

    @Override
    public List<MetricFamilySamples> collect() {
        return executions.collect();
    }

}

package ske.aurora.prometheus.collector;

import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.Collector;
import io.prometheus.client.Histogram;
import io.prometheus.client.SimpleTimer;

public final class Operation extends Collector {

    private static final Logger logger = LoggerFactory.getLogger(Operation.class);

    private static Operation instance;

    private final Histogram executions;

    public Operation() {
        executions = Histogram.build()
            .name("operations")
            .help("Manual operation that we want statistics on")
            .labelNames("type", "name")
            .create();
        logger.debug("executions histogram registered");

    }

    public static <T> T measureOperationString(String name, Supplier<T> s) {

        SimpleTimer requestTimer = new SimpleTimer();
        String type = "success";
        try {
            return s.get();
        } catch (Exception e) {
            type = e.getClass().getSimpleName();
            throw e;
        } finally {
            instance.executions.labels(type, name)
                .observe(requestTimer.elapsedSeconds());
        }
    }

    public static Operation getInstance() {

        if (instance == null) {

            logger.debug("Create new execute metrics");
            instance = new Operation();
        }
        return instance;
    }

    @Override
    public List<MetricFamilySamples> collect() {
        return executions.collect();
    }

}

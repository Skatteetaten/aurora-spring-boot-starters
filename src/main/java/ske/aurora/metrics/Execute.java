package ske.aurora.metrics;

import static com.codahale.metrics.MetricRegistry.name;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * A utility class that simplifies creation and maintenance of metrics related to the operational status specific
 * operations.
 */
public final class Execute {
    public static final String EXECUTION_TIMER_SUFFIX = "executionTimer";
    public static final String SUCCESS_COUNTER_SUFFIX = "successCount";
    public static final String ERROR_COUNTER_SUFFIX = "errorCount";

    private Execute() {
    }

    public static <T> T withHealth(AtomicInteger helsesjekkFeil, Supplier<T> s) {
        try {
            T result = s.get();
            helsesjekkFeil.set(0);
            return result;
        } catch (Exception e) {
            helsesjekkFeil.incrementAndGet();
            throw e;
        }
    }

    public static <T> T withMetrics(Class myClass, String metricSuffix, MetricRegistry metricRegistry,
        AtomicInteger healthCounter, Supplier<T> s) {
        return withMetrics(name(myClass.getName(), metricSuffix), metricRegistry, healthCounter, s);
    }

    /**
     * Updates metrics for execution time and either the success or error count of the specified operation.
     *
     * @param baseMetricName
     * @param metricRegistry
     * @param healthCounter
     * @param s
     * @param <T>
     * @return
     */
    public static <T> T withMetrics(String baseMetricName, MetricRegistry metricRegistry, AtomicInteger healthCounter,
        Supplier<T> s) {
        Timer executionTimer = getExecutionTimer(baseMetricName, metricRegistry);
        final Timer.Context timer = executionTimer.time();
        Counter successCounter = getSuccessCounter(baseMetricName, metricRegistry);
        Counter errorCounter = getErrorCounter(baseMetricName, metricRegistry);

        try {
            T result = s.get();
            successCounter.inc();
            healthCounter.set(0);
            return result;
        } catch (Exception e) {
            healthCounter.incrementAndGet();
            errorCounter.inc();
            throw e;
        } finally {
            timer.stop();
        }
    }

    private static Timer getExecutionTimer(String name, MetricRegistry metricRegistry) {
        return metricRegistry.timer(name(name, EXECUTION_TIMER_SUFFIX));
    }

    private static Counter getSuccessCounter(String name, MetricRegistry metricRegistry) {
        return metricRegistry.counter(name(name, SUCCESS_COUNTER_SUFFIX));
    }

    private static Counter getErrorCounter(String name, MetricRegistry metricRegistry) {
        return metricRegistry.counter(name(name, ERROR_COUNTER_SUFFIX));
    }
}

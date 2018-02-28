package no.skatteetaten.aurora;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;

@Component
public class AuroraMetrics {

    private MeterRegistry registry;

    public AuroraMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public <T> T withMetrics(String name, Supplier<T> s) {
        return withMetricsInternal("operations", Arrays.asList(Tag.of("name", name)), s);
    }

    public <T> T withMetrics(String name, List<Tag> inputTags, Supplier<T> s) {
        return withMetricsInternal("operations_" + name, inputTags, s);
    }

    private <T> T withMetricsInternal(String name, List<Tag> inputTags, Supplier<T> s) {
        long startTime = System.nanoTime();

        String result = "success";
        try {
            return s.get();
        } catch (Exception e) {
            result = e.getClass().getSimpleName();
            throw e;
        } finally {

            List<Tag> tags = new ArrayList<>();
            tags.add(Tag.of("result", result));
            tags.addAll(inputTags);

            Timer.builder(name)
                .tags(tags)
                .description("Manual operation that we want metrics on")
                .publishPercentileHistogram()
                .register(registry)
                .record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        }
    }

    public void status(String name, StatusValue value) {
        statusInternal("", value, Arrays.asList(Tag.of("name", name)));
    }

    public void status(String name, StatusValue value, List<Tag> inputTags) {
        statusInternal("_" + name, value, inputTags);
    }

    private void statusInternal(String name, StatusValue value, List<Tag> inputTags) {

        List<Tag> tags = new ArrayList<>();
        tags.addAll(inputTags);

        registry.gauge("last_status" + name, tags, value.getValue());
        tags.add(Tag.of("status", value.name()));
        registry.counter("statuses" + name, tags).increment();
    }

    public enum StatusValue {

        OK(0),
        WARNING(1),
        CRITICAL(2),
        UNKNOWN(3);

        private int value;

        StatusValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}

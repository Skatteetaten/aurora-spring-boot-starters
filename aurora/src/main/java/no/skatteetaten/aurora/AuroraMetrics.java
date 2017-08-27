package no.skatteetaten.aurora;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

@Component
public final class AuroraMetrics {

    private MeterRegistry registry;

    public AuroraMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public <T> T withMetrics(String name, Supplier<T> s) {
        return withMetrics(name, "operation", s);
    }

    public <T> T withMetrics(String name, String type, Supplier<T> s) {
        long startTime = System.nanoTime();

        String result = "success";
        try {
            return s.get();
        } catch (Exception e) {
            result = e.getClass().getSimpleName();
            throw e;
        } finally {
            List<Tag> tags = asList(Tag.of("result", result),
                Tag.of("type", type),
                Tag.of("name", name));

            registry.timerBuilder("operations")
                .tags(tags)
                .description("Manual operation that we want metrics on")
                .create().record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);

        }

    }

    public void status(String name, StatusValue value) {

        List<Tag> tags = Collections.singletonList(Tag.of("name", name));

        registry.gauge("last_status", tags, value.getValue());

        List<Tag> tags2 = asList(Tag.of("name", name), Tag.of("status", value.name()));
        registry.counter("statuses", tags2).increment();
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

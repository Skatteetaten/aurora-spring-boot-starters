package ske.aurora.prometheus;

import java.util.function.Supplier;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.prometheus.client.Histogram;
import io.prometheus.client.SimpleTimer;

@Service
public final class Execute {

    private static final Logger logger = LoggerFactory.getLogger(Execute.class);

    private static Execute instance;

    private final Histogram executions;

    public Execute() {
        executions = Histogram.build()
            .name("execute")
            .help("Manual executions that we want statics off")
            .labelNames("type", "group", "name")
            .register();
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
            T result = s.get();
            return result;
        } catch (Exception e) {
            type = "failure";
            throw e;
        } finally {
            instance.executions.labels(type, group, name)
                .observe(requestTimer.elapsedSeconds());
        }
    }
    @PostConstruct
    public void registerInstance() {
        instance = this;
    }
}

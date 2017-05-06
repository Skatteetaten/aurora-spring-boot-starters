package ske.aurora.prometheus;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;

public class LogbackMetricsAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private final Counter.Child traceLabel;
    private final Counter.Child debugLabel;
    private final Counter.Child infoLabel;
    private final Counter.Child warnLabel;
    private final Counter.Child errorLabel;

    /**
     * Create a new instrumented appender using the default registry.
     */
    public LogbackMetricsAppender(CollectorRegistry registry) {
        Counter counter = Counter.build().name("logback_appender_total")
            .help("Logback log statements at various log levels")
            .labelNames("level")
            .register(registry);

        traceLabel = counter.labels("trace");
        debugLabel = counter.labels("debug");
        infoLabel = counter.labels("info");
        warnLabel = counter.labels("warn");
        errorLabel = counter.labels("error");
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    protected void append(ILoggingEvent event) {
        switch (event.getLevel().toInt()) {
        case Level.TRACE_INT:
            traceLabel.inc();
            break;
        case Level.DEBUG_INT:
            debugLabel.inc();
            break;
        case Level.INFO_INT:
            infoLabel.inc();
            break;
        case Level.WARN_INT:
            warnLabel.inc();
            break;
        case Level.ERROR_INT:
            errorLabel.inc();
            break;
        default:
            break;
        }
    }
}

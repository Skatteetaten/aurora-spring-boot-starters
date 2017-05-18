package ske.aurora;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

public class GracefulShutdown implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {

    public static final int TIMEOUT_VALUE = 10;
    private static Logger logger = LoggerFactory.getLogger(GracefulShutdown.class);
    private Connector connector;

    @Override
    public void customize(Connector c) {
        this.connector = c;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        this.connector.pause();
        Executor executor = this.connector.getProtocolHandler().getExecutor();
        if (!(executor instanceof ThreadPoolExecutor)) {
            return;
        }

        ThreadPoolExecutor tpe = (ThreadPoolExecutor) executor;
        try {
            tpe.shutdown();
            if (!tpe.awaitTermination(TIMEOUT_VALUE, TimeUnit.SECONDS)) {
                logger.warn(
                    "Tomcat thread pool did not shut down gracefully within $shutdownTimeout $unit. Proceeding with "
                        + "forceful shutdown");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

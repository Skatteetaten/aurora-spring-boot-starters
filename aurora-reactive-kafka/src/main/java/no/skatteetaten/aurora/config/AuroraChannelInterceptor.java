package no.skatteetaten.aurora.config;

import static no.skatteetaten.aurora.filter.logging.AuroraHeaderFilter.KORRELASJONS_ID;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;

/**
 * A global channel interceptor to add prometheus metrics and forward our CORRELATION_ID header
 */
@Component
@GlobalChannelInterceptor
public class AuroraChannelInterceptor extends ChannelInterceptorAdapter {
    private static Logger logger = LoggerFactory.getLogger(AuroraChannelInterceptor.class);
    private final Histogram channels;

    public AuroraChannelInterceptor(CollectorRegistry registry) {

        channels = Histogram.build()
            .name("channels")
            .help("message sent over message channels")
            .labelNames("channel", "success")
            .register(registry);

    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel messageChannel) {

        Object correlationId = message.getHeaders().get(KORRELASJONS_ID);

        if (correlationId == null) {
            correlationId = MDC.get(KORRELASJONS_ID);
        }

        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put(KORRELASJONS_ID, correlationId.toString());

        return MessageBuilder.fromMessage(message).setHeader(KORRELASJONS_ID, correlationId).build();

    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel messageChannel, boolean b, Exception e) {

        MessageHeaders headers = message.getHeaders();
        long duration = System.currentTimeMillis() - headers.getTimestamp();
        channels.labels(messageChannel.toString(),
            b ? "true" : "false").observe(duration / Collector.MILLISECONDS_PER_SECOND);
    }

    @Override
    public boolean preReceive(MessageChannel messageChannel) {
        logger.debug("preReceive {},", messageChannel);

        return true;
    }

    @Override
    public void afterReceiveCompletion(Message<?> message, MessageChannel messageChannel, Exception e) {
        logger.debug("afterReceiveCompletion {}, {}, {}", message, messageChannel, e);
    }
}

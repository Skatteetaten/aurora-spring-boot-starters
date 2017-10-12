package no.skatteetaten.aurora;

import java.time.Duration;

import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class StringToDurationConverter implements Converter<String, Duration> {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(StringToDurationConverter.class);

    @Override
    public Duration convert(String source) {
        Duration duration = Duration.parse(source);
        logger.debug("Duration is {}", duration);
        return duration;
    }
}

package ske.aurora.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import ske.aurora.filter.logging.AuroraHeaderFilter;

/**
 * Class for doing basic application configuration and initialization. You can add to this class, but you should
 * probably not change very much (know why, at the very least).
 */
@Configuration
public class ApplicationConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationConfig.class);

    @Autowired
    private ConfigurableEnvironment env;

    /**
     * Register the {@link AuroraHeaderFilter} to apply to /api/*
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean auroraHeaderFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.addUrlPatterns("/*");
        registration.setFilter(new AuroraHeaderFilter());
        return registration;
    }

    /**
     * Creates a PropertySource for configuration in the Aurora Secret properties file. This properties file is mounted
     * in the container by OpenShift when the application is deployed. In most instances, AOC is used to manage the
     * configuration that ultimately ends up in this file.
     *
     * @return
     */
    @Bean
    public PropertiesPropertySource secretProperties() {
        return createAuroraPropertySource("auroraConfig[secret]", "AURORA_SECRET_PREFIX");
    }

    /**
     * Creates a PropertySource for configuration in the Aurora Env properties file. This properties file is mounted
     * in the container by OpenShift when the application is deployed. In most instances, AOC is used to manage the
     * configuration that ultimately ends up in this file.
     *
     * @return
     */
    @Bean
    public PropertiesPropertySource configProperties() {
        return createAuroraPropertySource("auroraConfig[env]", "AURORA_ENV_PREFIX");
    }

    /**
     * Creates a PropertySource for some of the environment variables that exposed via the OpenShift deployment
     * configuration. The values of these environment variables are controlled by AOC.
     *
     * @return
     */
    @Bean
    public PropertiesPropertySource auroraProperties() {

        Properties props = new Properties();
        Stream.of("AURORA_VERSION", "APP_VERSION").forEach(p -> {
            String value = System.getenv(p);
            if (value != null) {
                props.put(p, value);
            }
        });
        PropertiesPropertySource imageProps = new PropertiesPropertySource("auroraConfig[image]", props);

        env.getPropertySources().addLast(imageProps);
        return imageProps;
    }

    private PropertiesPropertySource createAuroraPropertySource(String name, String envPrefix) {

        String secretPrefix = System.getenv(envPrefix);
        if (secretPrefix == null) {
            return null;
        }

        String propertiesName = secretPrefix + ".properties";
        Properties props = new Properties();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(propertiesName))) {
            props.load(reader);
        } catch (IOException e) {
            LOG.debug("Could not read file location " + propertiesName);
            return null;
        }

        PropertiesPropertySource secretProps = new PropertiesPropertySource(name, props);
        env.getPropertySources().addLast(secretProps);

        return secretProps;
    }

}

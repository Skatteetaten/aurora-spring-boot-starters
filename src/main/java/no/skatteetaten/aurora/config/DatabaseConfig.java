package no.skatteetaten.aurora.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

/**
 * Configure database if the aurora.db property is set.
 * This should be moved to a repo of its own.
 */
@Configuration
@ConditionalOnProperty("db.properties")
@Profile("openshift")
public class DatabaseConfig implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private final String propertiesEnv;
    private ConfigurableEnvironment env;

    public DatabaseConfig(ConfigurableEnvironment env, @Value("${aurora.db:}") String propertiesName) {

        this.env = env;
        if (propertiesName == null || propertiesName.isEmpty()) {
            this.propertiesEnv = "DB_PROPERTIES";
        } else {
            this.propertiesEnv = String.format("%s_DB_PROPERTIES", propertiesName).toUpperCase();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Properties props = getProperties();
        if (props == null) {
            return;
        }

        logger.debug("Found database property with url:{}, username:{}, password:{}",
            props.getProperty("jdbc.url"), props.getProperty("jdbc.user"), props.getProperty("jdbc.password").length());
        System.setProperty("spring.datasource.url", props.getProperty("jdbc.url"));
        System.setProperty("spring.datasource.username", props.getProperty("jdbc.user"));
        System.setProperty("spring.datasource.password", props.getProperty("jdbc.password"));

    }

    @Bean
    public PropertiesPropertySource databaseProperties() throws IOException {

        Properties props = getProperties();
        if (props == null) {
            return null;
        }
        PropertiesPropertySource pps = new PropertiesPropertySource(
            "auroraDatabase[db]", props);
        env.getPropertySources().addLast(pps);

        return pps;
    }

    private Properties getProperties() throws IOException {
        String databasePath = System.getenv(this.propertiesEnv);

        if (databasePath == null) {
            logger.debug("The environment variable {} is not set.", this.propertiesEnv);
            return null;
        }

        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream(databasePath)) {
            props.load(input);
        }
        return props;
    }

}

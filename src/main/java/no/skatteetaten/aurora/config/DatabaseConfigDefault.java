package no.skatteetaten.aurora.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
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
@ConditionalOnExpression("'${aurora.db:true}' == 'true'")
@ConditionalOnProperty("db.properties")
public class DatabaseConfigDefault {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfigDefault.class);
    private ConfigurableEnvironment env;

    public DatabaseConfigDefault(ConfigurableEnvironment env) {
        this.env = env;
    }

    @Bean
    @Profile("openshift")
    public DataSource dataSource() throws IOException {

        Properties props = getProperties();
        if (props == null) {
            return null;
        }

        logger.debug("Found database property with url:{}, username:{}, password:{}",
            props.getProperty("jdbc.url"), props.getProperty("jdbc.user"), props.getProperty("jdbc.password").length());

        return DataSourceBuilder.create()
            .url(props.getProperty("jdbc.url"))
            .username(props.getProperty("jdbc.user"))
            .password(props.getProperty("jdbc.password"))
            .build();
    }

    @Bean
    @Profile("openshift")
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
        String databasePath = System.getenv("DB_PROPERTIES");

        if (databasePath == null) {
            logger.debug("The environment variable {} is not set.", "DB_PROPERTIES");
            return null;
        }

        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream(databasePath)) {
            props.load(input);
        }
        return props;
    }

}

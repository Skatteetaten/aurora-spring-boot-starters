package ske.aurora.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(DatabaseConfig.AuroraProperties.class)
@ConditionalOnProperty(prefix = "aurora", value = "db")
public class DatabaseConfig {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseConfig.class);

    @Autowired
    private ConfigurableEnvironment env;

    @Autowired
    private AuroraProperties auroraProperties;

    @Bean
    @Profile("openshift")
    public DataSource dataSource() throws IOException {

        Properties props = getProperties();
        if (props == null) {
            return null;
        }

        LOG.debug("Found database property with url" + props.getProperty("jdbc.url"));

        return DataSourceBuilder.create()
            .url(props.getProperty("jdbc.url"))
            .username(props.getProperty("jdbc.username"))
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
            "auroraDatabase[" + auroraProperties.db + "]", props);
        env.getPropertySources().addLast(pps);

        return pps;
    }

    private Properties getProperties() throws IOException {
        String envName = auroraProperties.db + "_DB_PROPERTIES";
        String databasePath = System.getenv(envName.toUpperCase());

        if (databasePath == null) {
            LOG.debug("Could not find an database env with the name {}", envName.toUpperCase());
            return null;
        }

        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream(databasePath)) {
            props.load(input);
        }
        return props;
    }

    @ConfigurationProperties("aurora")
    public static class AuroraProperties {
        private String db;

        public String getDb() {
            return db;
        }

        public void setDb(String db) {
            LOG.debug("setting db property on aurora to=" + db);
            this.db = db;
        }
    }

}

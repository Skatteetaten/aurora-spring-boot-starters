package no.skatteetaten.aurora;

import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

public class DatabasePropertiesListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DatabasePropertiesListener.class);

    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();

        String dbPropertiesEnv = "DB_PROPERTIES";
        String dbName = environment.getProperty("aurora.db");
        if (dbName != null && !dbName.isEmpty()) {
            dbPropertiesEnv = String.format("%s_DB_PROPERTIES", dbName).toUpperCase();
        } else {
            dbName = "db";
        }

        String path = environment.getProperty(dbPropertiesEnv);

        if (path == null) {
            logger.debug("The environment variable {} is not set.", dbPropertiesEnv);
            return;
        }
        environment.getPropertySources().addFirst(databaseProperties(dbName, path));
    }

    public PropertiesPropertySource databaseProperties(String name, String path) {

        Properties props = getProperties(path);
        logger.debug("Found database property with url:{}, username:{}, password:{}", props.getProperty("jdbc.url"),
            props.getProperty("jdbc.user"), props.getProperty("jdbc.password").length());
        Properties datasourceProps = new Properties();
        datasourceProps.setProperty("spring.datasource.url", props.getProperty("jdbc.url"));
        datasourceProps.setProperty("spring.datasource.username", props.getProperty("jdbc.user"));
        datasourceProps.setProperty("spring.datasource.password", props.getProperty("jdbc.password"));

        return new PropertiesPropertySource(
            "auroraDatabase[" + name + "]", datasourceProps);
    }

    private Properties getProperties(String databasePath) {

        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream(databasePath)) {
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Loading properties failed", e);
        }
        return props;
    }

}

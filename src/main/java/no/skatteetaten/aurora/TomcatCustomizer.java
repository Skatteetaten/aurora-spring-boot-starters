package no.skatteetaten.aurora;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "org.apache.catalina.startup.Tomcat")
public class TomcatCustomizer {

    @Bean
    public GracefulShutdown gs() {
        return new GracefulShutdown();
    }

    @Bean
    public EmbeddedServletContainerCustomizer tomcatCustomizer(GracefulShutdown gs) {
        return configurableEmbeddedServletContainer -> {
            if (configurableEmbeddedServletContainer instanceof TomcatEmbeddedServletContainerFactory) {
                ((TomcatEmbeddedServletContainerFactory) configurableEmbeddedServletContainer)
                    .addConnectorCustomizers(gs);
            }
        };
    }
}

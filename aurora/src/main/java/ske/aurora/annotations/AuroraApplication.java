package ske.aurora.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.PropertySource;

import io.prometheus.client.spring.boot.EnablePrometheusEndpoint;
import io.prometheus.client.spring.boot.EnableSpringBootMetricsCollector;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@PropertySource("classpath:aurora-openshift-spring-boot-starter.properties")
@EnablePrometheusEndpoint
@EnableSpringBootMetricsCollector
public @interface AuroraApplication {

}

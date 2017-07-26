package no.skatteetaten.aurora.prometheus;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;

@Component
public class AuroraPrometheusEndpoint extends AbstractEndpoint<ResponseEntity<String>> {

    private final CollectorRegistry collectorRegistry;

    @Autowired
    public AuroraPrometheusEndpoint(CollectorRegistry collectorRegistry) {
        super("prometheus");
        this.collectorRegistry = collectorRegistry;
    }

    @Override
    public ResponseEntity<String> invoke() {
        try {
            Writer writer = new StringWriter();
            TextFormat.write004(writer, collectorRegistry.metricFamilySamples());
            return ResponseEntity.ok()
                .header(CONTENT_TYPE, TextFormat.CONTENT_TYPE_004)
                .body(writer.toString());
        } catch (IOException e) {
            // This actually never happens since StringWriter::write() doesn't throw any IOException
            throw new RuntimeException("Writing metrics failed", e);
        }
    }
}

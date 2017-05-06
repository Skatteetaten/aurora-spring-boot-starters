package ske.aurora.prometheus;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import io.prometheus.client.CollectorRegistry;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ClientMetricsInterceptor extends CommonMetricsFilter implements Interceptor {

    public ClientMetricsInterceptor(List<PathGroup> aggregations, CollectorRegistry registry) {
        super(true, aggregations, registry);
    }

    /**
     * Used for testing
     */
    public ClientMetricsInterceptor() {
        this(Collections.emptyList(), new CollectorRegistry(true));
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        final long startTime = System.nanoTime();
        int statusCode = 0;
        try {
            Response response = chain.proceed(request);
            statusCode = response.code();
            return response;
        } finally {
            record(request.method(), request.url().toString(), statusCode, startTime);
        }
    }

}

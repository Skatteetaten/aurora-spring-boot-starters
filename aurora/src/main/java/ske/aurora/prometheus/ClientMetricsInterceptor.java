package ske.aurora.prometheus;

import java.io.IOException;
import java.util.List;

import io.prometheus.client.SimpleTimer;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ClientMetricsInterceptor extends CommonMetricsFilter implements Interceptor {

    public ClientMetricsInterceptor(List<PathGroup> aggregations, boolean strictMode) {
        super(true, aggregations, strictMode);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        SimpleTimer requestTimer = new SimpleTimer();
        int statusCode = 0;
        try {
            Response response = chain.proceed(request);
            statusCode = response.code();
            return response;
        } finally {
            record(request.method(), request.url().toString(), statusCode, requestTimer);
        }
    }

}

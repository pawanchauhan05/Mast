package com.app.mast.retrofit.interceptors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by pawansingh on 01/04/18.
 */

public class LoggingInterceptor implements Interceptor {

    private Map<String, String> headers = new HashMap<>();

    public LoggingInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        // Request customization: add request headers

        Request.Builder requestBuilder = original.newBuilder(); // <-- this is the important line

        for (Map.Entry<String, String> pairs : headers.entrySet()) {
            if (pairs.getValue() != null) {
                requestBuilder.header(pairs.getKey(), pairs.getValue());
            }
        }

        requestBuilder.method(original.method(), original.body());
        Request request = requestBuilder.build();

        return chain.proceed(request);
    }
}

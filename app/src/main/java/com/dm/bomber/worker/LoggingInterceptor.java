package com.dm.bomber.worker;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class LoggingInterceptor implements Interceptor {

    private static final String TAG = "Request";

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Log.v(TAG, "Sending request " + request.url());

        Response response = chain.proceed(request);
        Log.v(TAG, "Received response for " + response.request().url() + " with status code " + response.code());

        return response;
    }
}

package com.dm.bomber.services;

import java.util.Objects;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class ParamsService extends SimpleBaseService {

    public ParamsService(String url, String method, int... countryCodes) {
        super(url, method, countryCodes);
    }

    public ParamsService(String url, int... countryCodes) {
        super(url, null, countryCodes);
    }

    public ParamsService() {
    }

    public void run(OkHttpClient client, Callback callback) {
        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        buildParams(httpBuilder);

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(httpBuilder.build().toString());

        if (method != null)
            requestBuilder.method(method, RequestBody.create("", null));

        client.newCall(buildRequest(requestBuilder)).enqueue(callback);
    }

    public abstract void buildParams(HttpUrl.Builder builder);
}

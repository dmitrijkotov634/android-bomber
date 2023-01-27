package com.dm.bomber.services.core;

import java.util.Objects;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class ParamsService extends Service {

    protected String url;
    protected String method;

    protected Request.Builder request;
    protected HttpUrl.Builder builder;

    public ParamsService(String url, String method, int... countryCodes) {
        super(countryCodes);

        this.url = url;
        this.method = method;
    }

    public ParamsService(String url, int... countryCodes) {
        this(url, null, countryCodes);
    }

    public void run(OkHttpClient client, Callback callback, Phone phone) {
        request = new Request.Builder();
        builder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();

        buildParams(phone);

        request.url(builder.build().toString());

        if (method != null)
            request.method(method, RequestBody.create("", null));

        client.newCall(request.build()).enqueue(callback);
    }

    public abstract void buildParams(Phone phone);
}

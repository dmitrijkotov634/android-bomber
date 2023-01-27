package com.dm.bomber.services.core;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public abstract class MultipartService extends Service {

    protected String url;
    protected String method;

    protected Request.Builder request;
    protected MultipartBody.Builder builder;

    public MultipartService(String url, String method, int... countryCodes) {
        super(countryCodes);

        this.url = url;
        this.method = method;
    }

    public MultipartService(String url, int... countryCodes) {
        this(url, "POST", countryCodes);
    }

    public void run(OkHttpClient client, Callback callback, Phone phone) {
        request = new Request.Builder();
        builder = new MultipartBody.Builder();

        buildBody(phone);

        request.url(url);
        request.method(method, builder.build());

        client.newCall(request.build()).enqueue(callback);
    }

    public abstract void buildBody(Phone phone);
}

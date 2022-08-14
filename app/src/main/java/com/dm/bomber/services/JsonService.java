package com.dm.bomber.services;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class JsonService extends Service {

    protected String url;
    protected String method;

    protected Request.Builder request;

    public JsonService(String url, String method, int... countryCodes) {
        super(countryCodes);

        this.url = url;
        this.method = method;
    }

    public JsonService(String url, int... countryCodes) {
        this(url, "POST", countryCodes);
    }

    public void run(OkHttpClient client, Callback callback, Phone phone) {
        request = new Request.Builder();

        RequestBody body = RequestBody.create(
                buildJson(phone), MediaType.parse("application/json"));

        request.url(url);
        request.method(method, body);

        client.newCall(request.build()).enqueue(callback);
    }

    public abstract String buildJson(Phone phone);
}

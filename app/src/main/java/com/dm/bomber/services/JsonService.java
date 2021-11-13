package com.dm.bomber.services;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class JsonService extends SimpleBaseService {

    public void run(Callback callback) {
        RequestBody body = RequestBody.create(
                buildJson(), MediaType.parse("application/json"));

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        requestBuilder.method(method, body);

        client.newCall(buildRequest(requestBuilder)).enqueue(callback);
    }

    public abstract String buildJson();
}

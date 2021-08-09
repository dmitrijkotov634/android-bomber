package com.dm.bomber.services;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class ParamsService extends SimpleBaseService {

    public void run(Callback callback) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        buildParams(httpBuilder);

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(httpBuilder.build().toString());

        if (method != null)
            requestBuilder.method(method, RequestBody.create("", null));

        client.newCall(buildRequest(requestBuilder)).enqueue(callback);
    }

    public abstract void buildParams(HttpUrl.Builder builder);
}

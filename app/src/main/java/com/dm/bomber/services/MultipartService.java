package com.dm.bomber.services;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public abstract class MultipartService extends SimpleBaseService {

    public MultipartService(String url, String method, int... countryCodes) {
        super(url, method, countryCodes);
    }

    public MultipartService(String url, int... countryCodes) {
        super(url, "POST", countryCodes);
    }

    public MultipartService() {
    }

    public void run(OkHttpClient client, Callback callback) {
        Request.Builder requestBuilder = new Request.Builder();

        MultipartBody.Builder formBuilder = new MultipartBody.Builder();
        buildBody(formBuilder);

        requestBuilder.url(url);
        requestBuilder.method(method, formBuilder.build());

        client.newCall(buildRequest(requestBuilder)).enqueue(callback);
    }

    public abstract void buildBody(MultipartBody.Builder builder);
}

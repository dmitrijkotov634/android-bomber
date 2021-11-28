package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public abstract class FormService extends SimpleBaseService {

    public FormService(String url, String method, int... countryCodes) {
        super(url, method, countryCodes);
    }

    public FormService(String url, int... countryCodes) {
        super(url, "POST", countryCodes);
    }

    public FormService() {
    }

    public void run(OkHttpClient client, Callback callback) {
        Request.Builder requestBuilder = new Request.Builder();

        FormBody.Builder formBuilder = new FormBody.Builder();
        buildBody(formBuilder);

        requestBuilder.url(url);
        requestBuilder.method(method, formBuilder.build());

        client.newCall(buildRequest(requestBuilder)).enqueue(callback);
    }

    public abstract void buildBody(FormBody.Builder builder);
}

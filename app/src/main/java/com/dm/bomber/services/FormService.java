package com.dm.bomber.services;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;

public abstract class FormService extends SimpleBaseService {

    public void run(Callback callback) {
        Request.Builder requestBuilder = new Request.Builder();

        FormBody.Builder formBuilder = new FormBody.Builder();
        buildBody(formBuilder);

        requestBuilder.url(url);
        requestBuilder.method(method, formBuilder.build());

        client.newCall(buildRequest(requestBuilder)).enqueue(callback);
    }

    public abstract void buildBody(FormBody.Builder builder);
}

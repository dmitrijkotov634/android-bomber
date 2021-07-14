package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public abstract class FormService extends SimpleBaseService {

    public FormService(String url, String method) {
        super(url, method);
    }

    public FormService(String url, String method, String requireCode) {
        super(url, method, requireCode);
    }

    public Request run() {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);

        FormBody.Builder formBuilder = new FormBody.Builder();
        buildBody(formBuilder);

        requestBuilder.method(method, formBuilder.build());

        return buildRequest(requestBuilder);
    }

    public abstract void buildBody(FormBody.Builder builder);
}

package com.dm.bomber.services;

import okhttp3.HttpUrl;
import okhttp3.FormBody;
import okhttp3.Request;

public class MoeZdorovie extends ParamsService {

    public MoeZdorovie() {
        setUrl("https://mc.moezdorovie.ru/api/identity/Account/LoginApi");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        FormBody.Builder body = new FormBody.Builder();
        body.add("phone", "+" + getFormattedPhone());
        
        builder.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 11; None Build/RKQ1.201004.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/92.0.4515.159 Mobile Safari/537.36");

        builder.method(method, body.build());
        return super.buildRequest(builder);
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("phoneNumber", "+" + getFormattedPhone());
    }
}

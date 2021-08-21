package com.dm.bomber.services;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class Benzuber extends ParamsService {

    public Benzuber() {
        setUrl("https://app.benzuber.ru/app/1.8/auth/login");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 11; M2010J19SY Build/RKQ1.201004.002)");

        return super.buildRequest(builder);
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("phone", getFormattedPhone());
        builder.addQueryParameter("flag", "A");
        builder.addQueryParameter("lng", "ru");
        builder.addQueryParameter("token", "*");
    }
}

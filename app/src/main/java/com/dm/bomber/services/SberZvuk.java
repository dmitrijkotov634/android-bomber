package com.dm.bomber.services;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class SberZvuk extends ParamsService {

    public SberZvuk() {
        setUrl("https://zvuk.com/api/tiny/get-otp");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "OpenPlay|4.5.4|Android|11|Xiaomi M2010J19SY");

        return super.buildRequest(builder);
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("phone", "+" + getFormattedPhone());
        builder.addQueryParameter("type", "login");
        builder.addQueryParameter("length", "10");
    }
}

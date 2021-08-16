package com.dm.bomber.services;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class Smotrim extends ParamsService {

    public Smotrim() {
        setUrl("https://account.smotrim.ru/api/v1/auth");
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "Smotrim/7.6_70502 (Redmi, M2010J19SY, Android 11)");

        return super.buildRequest(builder);
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("phone", getFormattedPhone());
    }
}

package com.dm.bomber.services;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class Sokolov extends ParamsService {

    public Sokolov() {
        setUrl("https://api1.imshop.io/v1/clients/sokolov/users/login/sokolov");
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("client-version", "5.8.9");
        builder.addHeader("install-id", "null");

        return super.buildRequest(builder);
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("phone", format(getFormattedPhone(), "+*+(***)+***-****"));
    }
}

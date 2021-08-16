package com.dm.bomber.services;

import okhttp3.Request;

public class Samokat extends Fivepost {

    public Samokat() {
        setUrl("https://api.samokat.ru/showcase/confirmation/code");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "okhttp/4.9.1");
        builder.addHeader("x-application-platform", "android");
        builder.addHeader("x-application-version", "3.16.2");

        return super.buildRequest(builder);
    }
}

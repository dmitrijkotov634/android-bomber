package com.dm.bomber.services;

import java.util.UUID;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class Call2Friends extends ParamsService {

    public Call2Friends() {
        setUrl("https://call2friends.com/call-my-phone/web/request-free-call");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Cookie", "smscookie=" + UUID.randomUUID().toString());

        return super.buildRequest(builder);
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("phone", getFormattedPhone());
        builder.addQueryParameter("domain", "CALL2FRIENDS");
        builder.addQueryParameter("browser", "{\"mozilla\":true,\"version\":\"89.0\"}");
        builder.addQueryParameter("fgp", UUID.randomUUID().toString());
        builder.addQueryParameter("fgp2", UUID.randomUUID().toString());
    }
}

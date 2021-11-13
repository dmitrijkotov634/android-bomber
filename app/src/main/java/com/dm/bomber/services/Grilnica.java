package com.dm.bomber.services;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class Grilnica extends ParamsService {

    public Grilnica() {
        setUrl("https://api.grilnica.ru/store/api/client/phone-confirmation-token/send/");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("authorizationClient", "Basic c2l0ZTphRWRTQSNmZg==");
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:94.0) Gecko/20100101 Firefox/94.0");
        builder.addHeader("version", "1.8");

        return super.buildRequest(builder);
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addPathSegment("+" + getFormattedPhone());
    }
}

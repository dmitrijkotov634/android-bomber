package com.dm.bomber.services;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class MFC extends ParamsService {

    public MFC() {
        setUrl("https://api.mfc-d.com/v1/auth/phone");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "MFC/1.2.40 (com.mfcd.digital; build:68; Android 11 (30))");

        return super.buildRequest(builder);
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("phone", "+" + getFormattedPhone());
    }
}

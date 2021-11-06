package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public class Yarus extends FormService {

    public Yarus() {
        setUrl("https://api.yarus.ru/reg");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36");
        builder.addHeader("X-API-KEY", "PELQTQN2mWfml8XVYsJwaB9Qi4t8XE");
        builder.addHeader("X-APP", "3");
        builder.addHeader("X-DEVICE-ID", "ID-1636210971139");

        return super.buildRequest(builder);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("phone", format(phone, "+7(***) ***-****"));
    }
}

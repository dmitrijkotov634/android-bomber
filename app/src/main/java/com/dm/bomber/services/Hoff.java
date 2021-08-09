package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class Hoff extends ParamsService {

    public Hoff() {
        setUrl("https://hoff.ru/api/v2/auth");
        setMethod(POST);
        setPhoneCode("7");
    }
    
    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("type", "confirm_phone");
        builder.addQueryParameter("phone", getFormattedPhone());
        builder.addQueryParameter("device_id", "abcdefghjk");
    }
}

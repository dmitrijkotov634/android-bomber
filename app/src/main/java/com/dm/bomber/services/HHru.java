package com.dm.bomber.services;

import okhttp3.Request;
import okhttp3.HttpUrl;

public class HHru extends ParamsService {

    public HHru() {
        setUrl("https://api.hh.ru/one_time_password/phone/generate");
        setMethod(POST);
        setPhoneCode("7");
    }
    
    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "ru.hh.android/6.36.11083, Device: M2010J19SY, Android OS: 11 (UUID: b9b32fc8-2088-42cf-a87a-1c99aa980bfe)");
        builder.addHeader("Authorization", "Bearer K811HJNKQA8V1UN53I6PN1J1CMAD2L1M3LU6LPAU849BCT031KDSSM485FDPJ6UF");

        return super.buildRequest(builder);
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("login", getFormattedPhone());
        builder.addQueryParameter("notification_type", "call");
        builder.addQueryParameter("host", "hh.ru");
        builder.addQueryParameter("locale", "RU");
    }
}

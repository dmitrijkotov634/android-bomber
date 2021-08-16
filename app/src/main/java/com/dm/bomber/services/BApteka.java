package com.dm.bomber.services;

import okhttp3.Request;

public class BApteka extends Gorparkovka {

    public BApteka() {
        setUrl("https://b-apteka.ru/api/lk/send_code");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 11; M2010J19SY Build/RKQ1.201004.002)");
        builder.addHeader("b-apteka-session", "null");

        return super.buildRequest(builder);
    }
}

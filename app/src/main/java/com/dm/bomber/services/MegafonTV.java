package com.dm.bomber.services;

import okhttp3.Request;

public class MegafonTV extends YotaTV {

    public MegafonTV() {
        setUrl("https://bmp.megafon.tv/api/v10/auth/password_reset");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Cookie", "SessionID=9bu7oyJSGEoGRkOho-5kOR7DcG7JC_4t0zaeM2bJ1YM");
        return super.buildRequest(builder);
    }
}

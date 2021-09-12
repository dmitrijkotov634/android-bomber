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
        builder.addHeader("Cookie", "SessionID=cD52ZTEFTR5JPrL3T94T7K-lV5wfZEKz8VV1GQlFL6w");
        return super.buildRequest(builder);
    }
}

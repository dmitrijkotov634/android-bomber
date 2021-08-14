package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class MFC extends ParamsService {

    public MFC() {
        setUrl("https://api.mfc-d.com/v1/auth/phone");
        setPhoneCode("7");
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("phone", "+" + getFormattedPhone());
    }
}

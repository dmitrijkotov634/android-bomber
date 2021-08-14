package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class Kari extends ParamsService {

    public Kari() {
        setUrl("https://i.api.kari.com/ecommerce/client/registration/verify/phone/code");
        setPhoneCode("7");
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("phone", "+" + getFormattedPhone());
    }
}

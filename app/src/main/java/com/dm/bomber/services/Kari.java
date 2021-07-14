package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class Kari extends ParamsService {
    public Kari() {
        super("https://i.api.kari.com/ecommerce/client/registration/verify/phone/code");
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("phone", "+" + getFormattedPhone());
    }
}

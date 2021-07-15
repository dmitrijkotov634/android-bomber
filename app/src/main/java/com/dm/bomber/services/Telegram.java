package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class Telegram extends ParamsService {

    public Telegram() {
        setUrl("https://my.telegram.org/auth/send_password");
        setMethod(POST);
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("phone", "+" + getFormattedPhone());
    }
}

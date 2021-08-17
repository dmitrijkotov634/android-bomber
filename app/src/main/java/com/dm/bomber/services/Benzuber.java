package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class Benzuber extends ParamsService {

    public Benzuber() {
        setUrl("https://app.benzuber.ru/app/1.8/auth/login");
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("phone", getFormattedPhone());
        builder.addQueryParameter("flag", "A");
        builder.addQueryParameter("lng", "ru");
        builder.addQueryParameter("token", "*");
    }
}

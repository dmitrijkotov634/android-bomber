package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class Benzuber extends ParamsService {

    public Benzuber() {
        setUrl("https://app.benzuber.ru/app/1.7/auth/login");
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("phone", phone);
        builder.addQueryParameter("flag", "A");
        builder.addQueryParameter("lng", "ru");
        builder.addQueryParameter("token", "*");
    }
}

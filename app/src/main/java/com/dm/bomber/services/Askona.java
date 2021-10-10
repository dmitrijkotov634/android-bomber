package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class Askona extends ParamsService {

    public Askona() {
        setUrl("https://www.askona.ru/api/registration/sendcode");
        setPhoneCode("7");
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("csrf_token", "f3ba0b13b52ee360489b8806d1ef6a89");
        builder.addQueryParameter("contact[phone]", getFormattedPhone());
    }
}

package com.dm.bomber.services;

import okhttp3.FormBody;

public class Yandex extends FormService {

    public Yandex() {
        setUrl("https://mobileproxy.passport.yandex.net/1/bundle/phone/confirm/submit");
        setMethod(POST);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("number", "+" + getFormattedPhone());
        builder.add("display_language", "ru");
    }
}

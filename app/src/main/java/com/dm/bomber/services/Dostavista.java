package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public class Dostavista extends FormService {

    public Dostavista() {
        setUrl("https://robot.dostavista.ru/api/send-registration-sms");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("user-agent", "ru-courier-app-main-android/2.61.5.2377");

        return super.buildRequest(builder);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("v", "1.141");
        builder.add("phone", "+" + getFormattedPhone());
        builder.add("unique_device_id", "null");
    }
}

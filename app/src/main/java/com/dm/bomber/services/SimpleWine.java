package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public class SimpleWine extends FormService {

    public SimpleWine() {
        setUrl("https://simplewine.ru/api/v2/user/phone/");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("x-develop-protocol", "26");
        builder.addHeader("x-develop-version", "147");

        return super.buildRequest(builder);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("phone", "+" + getFormattedPhone());
    }
}

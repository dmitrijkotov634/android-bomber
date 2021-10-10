package com.dm.bomber.services;

import okhttp3.FormBody;

public class Mirkorma extends FormService {

    public Mirkorma() {
        setUrl("https://www.mirkorma.ru/");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("by", "phone");
        builder.add("action", "getCode");
        builder.add("login", format(phone, "+7 (***) ***-**-**"));
        builder.add("is_auth_request", "y");
        builder.add("ajaxId", "de5ddc6af00fafe60b5c9bf48d2e7d6e");
    }
}

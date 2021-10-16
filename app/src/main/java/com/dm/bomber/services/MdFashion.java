package com.dm.bomber.services;

import okhttp3.FormBody;

public class MdFashion extends FormService {

    public MdFashion() {
        setUrl("https://md-fashion.com.ua/bpm/validate-contact");
        setMethod(POST);
        setPhoneCode("380");
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("phone", "+" + getFormattedPhone());
    }
}

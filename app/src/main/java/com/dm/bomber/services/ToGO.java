package com.dm.bomber.services;

import okhttp3.FormBody;

public class ToGO extends FormService {

    public ToGO() {
        setUrl("https://togo.bumerang.tech/api/v6/sendSmsToken");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("phone", getFormattedPhone());
    }
}

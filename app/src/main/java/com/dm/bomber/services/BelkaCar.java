package com.dm.bomber.services;

import okhttp3.FormBody;

public class BelkaCar extends FormService {

    public BelkaCar() {
        setUrl("https://api.belkacar.ru/v2.12-covid19/auth/get-code");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("phone", getFormattedPhone());
        builder.add("device_id", "null");
    }
}

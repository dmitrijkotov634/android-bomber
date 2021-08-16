package com.dm.bomber.services;

import okhttp3.FormBody;

public class GreenBee extends FormService {

    public GreenBee() {
        setUrl("https://admin.gbee.app/api/v6/sendSmsToken");
        setMethod(POST);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("phone", getFormattedPhone());
    }
}

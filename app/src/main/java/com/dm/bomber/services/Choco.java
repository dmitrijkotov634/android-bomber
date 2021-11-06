package com.dm.bomber.services;

import okhttp3.FormBody;

public class Choco extends FormService {

    public Choco() {
        setUrl("https://api-proxy.choco.kz/user/v2/code");
        setMethod(POST);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("login", getFormattedPhone());
        builder.add("client_id", "-5");
        builder.add("dispatch_type", "call");
    }
}

package com.dm.bomber.services;

import okhttp3.FormBody;

public class BCS extends FormService {

    public BCS() {
        setUrl("https://auth-ext.usvc.bcs.ru/auth/realms/Broker/protocol/openid-connect/token");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("client_id", "broker_otp_guest2");
        builder.add("grant_type", "password");
        builder.add("username", getFormattedPhone());
    }
}

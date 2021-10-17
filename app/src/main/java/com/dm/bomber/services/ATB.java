package com.dm.bomber.services;

import okhttp3.FormBody;

public class ATB extends FormService {

    public ATB() {
        setUrl("https://www.atb.su/local/templates/main/ajax/main/send_sms_new.php");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("phone", phone);
        builder.add("page", "new");
    }
}

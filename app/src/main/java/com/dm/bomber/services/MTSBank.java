package com.dm.bomber.services;

import okhttp3.FormBody;

public class MTSBank extends FormService {

    public MTSBank() {
        setUrl("https://www.mtsbank.ru/ajax/sms.php");
        setMethod(POST);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("phone", getFormattedPhone());
    }
}

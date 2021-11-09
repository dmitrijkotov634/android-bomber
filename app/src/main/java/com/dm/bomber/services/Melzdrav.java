package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class Melzdrav extends ParamsService {

    public Melzdrav() {
        setUrl("https://melzdrav.ru/local/templates/mz_gtech/ajax/sms.php");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("ajaxtype", "SEND-SMS");
        builder.addQueryParameter("PHONE", format(phone, "7 (***) ***-**-**"));
    }
}

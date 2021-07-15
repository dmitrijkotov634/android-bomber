package com.dm.bomber.services;

import okhttp3.FormBody;

public class Alltime extends FormService {

    public Alltime() {
        setUrl("https://www.alltime.ru/sservice/2020/form_register_phone.php");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("action", "send");
        builder.add("back", "/");
        builder.add("phone", format(getFormattedPhone(), "+*+(***)+***-**-**"));
    }
}

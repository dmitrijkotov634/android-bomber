package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public class BibiSushi extends FormService {

    public BibiSushi() {
        setUrl("https://bibi-sushi.ru/?action=auth");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36");

        return super.buildRequest(builder);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        String password = getUserName();

        builder.add("CSRF", "");
        builder.add("ACTION", "REGISTER");
        builder.add("MODE", "PHONE");
        builder.add("PHONE", format(phone, "+7 (***) ***-**-**"));
        builder.add("PASSWORD", password);
        builder.add("PASSWORD2", password);
    }
}

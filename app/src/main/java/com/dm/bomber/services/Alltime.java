package com.dm.bomber.services;

import okhttp3.FormBody;

public class Alltime extends FormService {

    public Alltime() {
        super("https://www.alltime.ru/sservice/2020/form_register_phone.php", POST, "7");
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("action", "send");
        builder.add("back", "/");
        builder.add("phone", format(getFormattedPhone(), "+*+(***)+***-**-**"));
    }
}

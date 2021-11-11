package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class Grafkrasnov extends ParamsService {

    public Grafkrasnov() {
        setUrl("https://grafkrasnov.ru/wp-content/themes/grafkrasnov/iiko/sq.php");
        setPhoneCode("7");
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("phone", format(phone, "+7 (***) ***-**-**"));
        builder.addQueryParameter("action", "createcode");
        builder.addQueryParameter("code", "");
    }
}

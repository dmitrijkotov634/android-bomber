package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class Citilink extends ParamsService {

    public Citilink() {
        setUrl("https://www.citilink.ru/registration/confirm/phone/");
        setMethod(POST);
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addPathSegment("+" + getFormattedPhone() + "/");
    }
}

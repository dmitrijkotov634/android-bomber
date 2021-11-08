package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class SatUa extends ParamsService {

    public SatUa() {
        setUrl("https://urm.sat.ua/openws/hs/api/v2.0/auth/check/json");
        setPhoneCode("+380");
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("phone", "+" + getFormattedPhone());
        builder.addQueryParameter("app", "cabinet");
        builder.addQueryParameter("language", "ru");
    }
}

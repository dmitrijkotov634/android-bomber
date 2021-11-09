package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class VapeZone extends ParamsService {

    public VapeZone() {
        setUrl("https://vapezone.pro/index.php");
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("dispatch", "csc_sms.generate_code");
        builder.addQueryParameter("phone", "+" + getFormattedPhone());
        builder.addQueryParameter("prefix", "");
        builder.addQueryParameter("is_ajax", "1");
    }
}

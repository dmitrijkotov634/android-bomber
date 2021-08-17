package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public class inDriver extends FormService {

    public inDriver() {
        setUrl("https://rukzbrothers.ru/api/authorization?locale=ru_RU");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 10; Redmi Note 3 Build/QQ3A.200905.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/87.0.4280.101 Mobile Safari/537.36");

        return super.buildRequest(builder);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("phone", phone);
        builder.add("mode", "request");
        builder.add("phoneCode", "+" + phoneCode);
        builder.add("countryIso", "RU");
        builder.add("phone_permission", "unknown");
        builder.add("stream_id", "0");
        builder.add("v", "7");
        builder.add("imei", "");
        builder.add("regid", "");
        builder.add("appversion", "3.26.0");
        builder.add("osversion", "kal");
        builder.add("devicemodel", "kal");
    }
}

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Mozen extends JsonService {

    public Mozen() {
        setUrl("https://mobile.mozen.io/api/auth/1/login/phone");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("x-app-id", "e2010c2f-3983-4e5c-b297-ccfb82a678ee");
        builder.addHeader("x-device-id", "null");
        builder.addHeader("x-app-version", "v1.4.344-mozen-release");
        builder.addHeader("x-app-flavour", "mozen");
        builder.addHeader("x-platform", "android");
        builder.addHeader("x-user-location", "lat=0.000000&lon=0.000000");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", "+" + getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

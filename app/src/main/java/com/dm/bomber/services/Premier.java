package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Premier extends JsonService {

    public Premier() {
        setUrl("https://premier.one/app/v1.1.3/user/register/send-otp-password");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("x-device-type", "mobile");
        builder.addHeader("x-device-id", "bruh");
        builder.addHeader("x-auth-token", "");
        builder.addHeader("User-Agent", "premier-one-Android-2.19.0");

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

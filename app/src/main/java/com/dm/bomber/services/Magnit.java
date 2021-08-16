package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Magnit extends JsonService {

    public Magnit() {
        setUrl("https://middle-api.magnit.ru/v3/auth/otp");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Device-Platform", "Android");
        builder.addHeader("x-device-platform", "Android");
        builder.addHeader("x-app-version", "6.18.5");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

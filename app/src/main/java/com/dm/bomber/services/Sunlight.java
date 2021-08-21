package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Sunlight extends JsonService {

    public Sunlight() {
        setUrl("https://api.sunlight.net/v1/auth/send_code/");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "sunlight-android, 12801080");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("type", "phone");
            json.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

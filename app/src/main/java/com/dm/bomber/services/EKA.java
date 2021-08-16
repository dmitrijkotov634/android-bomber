package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class EKA extends JsonService {

    public EKA() {
        setUrl("https://app.eka.ru/Api/Auth/Login");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "ru.growapps.eka/2.9 (M2010J19SY; Android 11)");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("Login", "+" + getFormattedPhone());
            json.put("Type", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

package com.dm.bomber.services;

import org.json.JSONObject;

import okhttp3.Request;

public class Apteka extends JsonService {

    public Apteka() {
        setUrl("https://api.apteka.ru/Auth/Auth_Code");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "Android_Apteka/3.2.12 (Redmi Note 7, Ver 10, Density 2.625)");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", "+" + getFormattedPhone());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

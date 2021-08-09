package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class KazanExpress extends JsonService {

    public KazanExpress() {
        setUrl("https://api.kazanexpress.ru/api/restore");
        setMethod(POST);
        setPhoneCode("7");
    }
    
    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "KazanExpress/Android (com.kazanexpress.ke_app; 1.4.5)");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("login", getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.Request;

public class Samokat extends JsonService {

    public Samokat() {
        setUrl("https://api.samokat.ru/showcase/confirmation/code");
        setMethod(POST);
        setPhoneCode("7");
    }
    
    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "okhttp/4.9.1");
        builder.addHeader("x-application-platform", "android");
        builder.addHeader("x-application-version", "3.16.2");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phoneNumber", "+" + getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

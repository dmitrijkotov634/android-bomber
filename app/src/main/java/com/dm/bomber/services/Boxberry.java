package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Boxberry extends JsonService {

    public Boxberry() {
        setUrl("https://mobile.boxberry.ru/api/v1/sms/code/send");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("platform", "android");
        builder.addHeader("os-version", "11");
        builder.addHeader("app-version", "1.5.2");
        builder.addHeader("device", "null");
        builder.addHeader("x-access-token", "null");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", getFormattedPhone());
            json.put("reason", "registration");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

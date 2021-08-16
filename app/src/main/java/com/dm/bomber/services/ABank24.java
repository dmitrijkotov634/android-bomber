package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class ABank24 extends JsonService {

    public ABank24() {
        setUrl("https://a24m.a-bank.com.ua/syncapi");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "mob;aos;phone;M2010J19SY|Xiaomi;11;2.13.7;abcabcabcabcabcc;00000000-0000-0000-0000-000000000000;null;true");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();
        JSONObject data = new JSONObject();

        try {
            data.put("lang", "ru");
            data.put("login", "+" + getFormattedPhone());
            data.put("login_enter_type", "manual");

            json.put("cmd", "init");
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

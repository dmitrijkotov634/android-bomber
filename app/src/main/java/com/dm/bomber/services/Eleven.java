package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Eleven extends JsonService {

    public Eleven() {
        setUrl("https://goeleven.io/api/login/");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("user-agent", "okhttp/4.9.1");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("country_code", phoneCode);
            json.put("phone_number", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

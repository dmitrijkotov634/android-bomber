package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class METRO extends JsonService {

    public METRO() {
        setUrl("https://api.metro-cc.ru/auth/api/v1/public/send_otp");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("authorization", "9c0fe65e-51a9-4b7c-a54d-b2b28f3a922f");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", "+" + getFormattedPhone());
            json.put("smsHash", "hi2LBOdVq64");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

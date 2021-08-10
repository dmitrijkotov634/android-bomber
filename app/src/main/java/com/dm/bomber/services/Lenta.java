package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Lenta extends JsonService {

    public Lenta() {
        setUrl("https://lenta.com/api/v1/registration/requestValidationCode");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:90.0) Gecko/20100101 Firefox/90.0");
        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Citydrive extends JsonService {

    public Citydrive() {
        setUrl("https://cs-v2.youdrive.today/signup");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "carsharing/4.1.1 (Linux; Android 11; M2010J19SY Build/REL)");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("os", "android");
            json.put("phone", phone);
            json.put("phone_code", countryCode);
            json.put("token", "null");
            json.put("token_type", "fcm");
            json.put("vendor_id", "null");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

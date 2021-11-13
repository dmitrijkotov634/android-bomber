package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Aitu extends JsonService {

    public Aitu() {
        setUrl("https://passport.aitu.io/api/v1/sms/request-code");
        setMethod(POST);
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

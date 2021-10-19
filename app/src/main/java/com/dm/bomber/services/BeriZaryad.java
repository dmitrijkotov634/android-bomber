package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class BeriZaryad extends JsonService {

    public BeriZaryad() {
        setUrl("https://mobileapi.berizaryad.ru/auth");
        setMethod(POST);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("password", "1234567890");
            json.put("phone", getFormattedPhone());
            json.put("verification_method", "call_last4");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

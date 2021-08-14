package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Sravni extends JsonService {

    public Sravni() {
        setUrl("https://mobile.sravni.ru/v1/auth");
        setMethod(POST);
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

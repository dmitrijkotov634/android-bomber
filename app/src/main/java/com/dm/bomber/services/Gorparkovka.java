package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Gorparkovka extends JsonService {

    public Gorparkovka() {
        setUrl("https://belparking.ru/auth/api/1.0/pincodes");
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

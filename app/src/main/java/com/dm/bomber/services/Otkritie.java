package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Otkritie extends JsonService {

    public Otkritie() {
        setUrl("https://services.open.ru/anketa/api/public/otp");
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

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class VoprosRU extends JsonService {

    public VoprosRU() {
        setUrl("https://vopros.ru/api/users/identity_by_msisdn");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("msisdn", getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class TeaRU extends JsonService {

    public TeaRU() {
        setUrl("https://1cmaster.tea.ru/api/v1/auth/authorize");
        setMethod(POST);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", phone);
            json.put("countryCode", phoneCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

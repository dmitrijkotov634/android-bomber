package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class HiceBank extends JsonService {

    public HiceBank() {
        setUrl("https://api.hicebank.ru/api/v1.7/auth/code/");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone_number", getFormattedPhone());
            json.put("delivery_option", "SMS");
            json.put("install_id", "null");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Fivepost extends JsonService {

    public Fivepost() {
        setUrl("https://api-omni.x5.ru/api/v1/clients-portal/auth/send-sms-code");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phoneNumber", "+" + getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

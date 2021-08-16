package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Stolichki extends JsonService {

    public Stolichki() {
        setUrl("https://app.neo-pharm.ru/v5/auth/send-sms-code");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("action", "sign-up");
            json.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

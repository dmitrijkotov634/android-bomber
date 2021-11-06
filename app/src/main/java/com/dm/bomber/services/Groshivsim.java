package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Groshivsim extends JsonService {

    public Groshivsim() {
        setUrl("https://admin1.groshivsim.com/api/sms/phone-verification/create");
        setMethod(POST);
        setPhoneCode("380");
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

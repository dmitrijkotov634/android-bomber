package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Biua extends JsonService {

    public Biua() {
        setUrl("https://bi.ua/api/v1/accounts");
        setMethod(POST);
        setPhoneCode("380");
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("grand_type", "sms_code");
            json.put("stage", "1");
            json.put("login", "Дмитрий");
            json.put("phone", getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class MegafonBank extends JsonService {

    public MegafonBank() {
        setUrl("https://bank.megafon.ru/mobileapi/api/v31/user/register/");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone_number", getFormattedPhone());
            json.put("platform", "android");
            json.put("recovery", "false");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class SberMobile extends JsonService {

    public SberMobile() {
        setUrl("https://mobius.sberbank-tele.com/v2/api/gateway/send_password");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("number", phone);
            json.put("type", "authorization");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

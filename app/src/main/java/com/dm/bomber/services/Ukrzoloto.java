package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Ukrzoloto extends JsonService {

    public Ukrzoloto() {
        setUrl("https://ukrzoloto.ua/mobile/v1/auth/phone");
        setMethod(POST);
        setPhoneCode("380");
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("data", new JSONObject().put("telephoneNumber", getFormattedPhone()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

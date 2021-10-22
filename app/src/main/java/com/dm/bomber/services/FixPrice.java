package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class FixPrice extends JsonService {

    public FixPrice() {
        setUrl("https://a-api.fix-price.com/buyer/v1/registration/phone/request");
        setMethod(POST);
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

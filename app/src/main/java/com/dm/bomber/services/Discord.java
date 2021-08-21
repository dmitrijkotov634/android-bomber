package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Discord extends JsonService {

    public Discord() {
        setUrl("https://discord.com/api/v9/auth/register/phone");
        setMethod(POST);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", "+" + getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

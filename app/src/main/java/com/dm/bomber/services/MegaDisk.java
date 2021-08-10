package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class MegaDisk extends JsonService {

    public MegaDisk() {
        setUrl("https://disk.megafon.ru/api/3/md_otp_tokens/");
        setMethod(POST);
        setPhoneCode("7");
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

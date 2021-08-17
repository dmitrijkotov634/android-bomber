package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class GoldApple extends JsonService {

    public GoldApple() {
        setUrl("https://goldapple.ru/rest/V2.1/mobile/auth/send_sms_code?store_id=1&type=android");
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

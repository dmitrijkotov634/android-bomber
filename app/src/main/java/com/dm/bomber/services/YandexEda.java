package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class YandexEda extends JsonService {

    public YandexEda() {
        super("https://eda.yandex/api/v1/user/request_authentication_code", POST);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone_number", "+" + getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

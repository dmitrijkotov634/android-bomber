package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class PerekrestokDostavka extends JsonService {

    public PerekrestokDostavka() {
        setUrl("https://auth.perekrestok-dostavka.ru/api/auth/sms");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", format(phone, "+7 (***) ***-**-**"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

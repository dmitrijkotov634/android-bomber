package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Eldorado extends JsonService {

    public Eldorado() {
        setUrl("https://www.eldorado.ru/_ajax/spa/auth/v2/auth_with_login.php");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("user_login", format(getFormattedPhone(), "+* (***) *** ****"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

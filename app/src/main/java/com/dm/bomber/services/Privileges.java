package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Privileges extends JsonService {

    public Privileges() {
        setUrl("https://customerservice.manzanagroup.ru/CustomerOfficeService/Identity/RequestAdvancedPhoneEmailRegistration");
        setMethod(POST);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("parameter", "{\"MobilePhone\":\"" + "+" + getFormattedPhone() + "\",\"AppId\":\"15\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Fonbet extends JsonService {

    public Fonbet() {
        setUrl("https://clientsapi12.bkfon-resources.com/cps/superRegistration/createProcess");
        setMethod(POST);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("advertInfo", "");
            json.put("birthday", "2001-12-12");
            json.put("deviceId", "A107AAD70D4D03410D73B978D6FB2A75");
            json.put("ecupis", true);
            json.put("email", "");
            json.put("emailAdvertAccepted", true);
            json.put("fio", "");
            json.put("lang", "ru");
            json.put("password", getUserName() + "123_");
            json.put("phoneNumber", "+" + getFormattedPhone());
            json.put("platformInfo", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36");
            json.put("promoId", "");
            json.put("sysId", 1);
            json.put("webReferrer", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

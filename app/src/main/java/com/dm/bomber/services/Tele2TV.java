package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Tele2TV extends JsonService {

    public Tele2TV() {
        setUrl("http://tele2mwapp.cdnvideo.ru/api/v1/user/request-password.json");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("MSISDN", getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class PikBroker extends JsonService {

    public PikBroker() {
        setUrl("https://back.pik-broker.ru/api-react/userpanel-v1/signup");
        setMethod(POST);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("ga_id", "");
            json.put("name", "Дмитрий");
            json.put("phone", getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

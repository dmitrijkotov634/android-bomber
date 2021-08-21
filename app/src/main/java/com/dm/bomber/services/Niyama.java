package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Niyama extends JsonService {

    public Niyama() {
        setUrl("https://mob.niyama.ru/mobileapp/api/checkphone");
        setMethod(POST);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", format(getFormattedPhone(), "+* (***) ***-**-**"));
            json.put("code", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

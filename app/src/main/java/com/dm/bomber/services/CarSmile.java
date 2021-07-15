package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class CarSmile extends JsonService {

    public CarSmile() {
        super("https://api.carsmile.com/", POST);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("operationName", "enterPhone");
            json.put("variables", new JSONObject().put("phone", getFormattedPhone()));
            json.put("query", "mutation enterPhone($phone: String!) {\n  enterPhone(phone: $phone)\n}\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

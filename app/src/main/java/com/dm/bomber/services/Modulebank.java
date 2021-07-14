package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Modulebank extends JsonService {
    public Modulebank() {
        super("https://my.modulbank.ru/api/v2/registration/nameAndPhone", POST);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("FirstName", getRussianName());
            json.put("CellPhone", phone);
            json.put("Package", "optimal");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

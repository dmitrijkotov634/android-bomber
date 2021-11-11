package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class SixSeasons extends JsonService {

    public SixSeasons() {
        setUrl("https://api.6seasons.ru/graphql");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("operationName", null);
            json.put("query", "mutation ($phone: String!, $code: String, $withOutCode: Boolean) {\n" +
                    "  authorize(phone: $phone, code: $code, withOutCode: $withOutCode)\n" +
                    "}\n");
            json.put("variables", new JSONObject()
                    .put("phone", phone)
                    .put("withOutCode", false));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

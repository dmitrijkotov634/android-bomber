package com.dm.bomber.services;

import org.json.JSONObject;

public class TochkaBank extends JsonService {

    public TochkaBank() {
        setUrl("https://x.tochka.com/api/v1/auth/v3/public");
        setMethod(POST);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("id", "caece419-b6f1-0ea4-b953-338c2c7399c5");
            json.put("jsonrpc", "2.0");
            json.put("method", "second_factor_auth");
            json.put("params", new JSONObject()
                    .put("value", "+" + getFormattedPhone())
                    .put("auth_method", "smsotp"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

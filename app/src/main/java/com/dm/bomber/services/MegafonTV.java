package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class MegafonTV extends JsonService {

    public MegafonTV() {
        setUrl("https://bmp.megafon.tv/api/v10/auth/register/msisdn");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Cookie", "SessionID=9bu7oyJSGEoGRkOho-5kOR7DcG7JC_4t0zaeM2bJ1YM");
        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("msisdn", getFormattedPhone());
            json.put("password", "1234657");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

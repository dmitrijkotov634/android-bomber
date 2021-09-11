package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class YotaTV extends JsonService {

    public YotaTV() {
        setUrl("https://bmp.tv.yota.ru/api/v10/auth/register/msisdn");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Cookie", "SessionID=892g6kpDXMvRJlFt5wSGGED1P0slOse2jIx6WvrTfdA");
        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("msisdn", getFormattedPhone());
            json.put("password", "91234657");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class YotaTV extends JsonService {

    public YotaTV() {
        setUrl("https://bmp.tv.yota.ru/api/v10/auth/password_reset");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Cookie", "SessionID=JZvtAJt2mxHlb941cj_asAwDihjQT-8TT76mJRX_vXo");
        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("msisdn", "+" + getFormattedPhone());
            json.put("password", "1234657");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

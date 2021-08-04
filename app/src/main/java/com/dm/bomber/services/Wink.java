package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Wink extends JsonService {

    public Wink() {
        setUrl("https://cnt-vlmr-itv01.svc.iptv.rt.ru/api/v2/portal/send_sms_code");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("session_id", "82e3c374-f507-11eb-8c0b-9c1d36dcd072:1951416:2237006:2");
        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("action", "register");
            json.put("phone", getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

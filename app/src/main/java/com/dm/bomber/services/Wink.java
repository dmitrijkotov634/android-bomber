package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Wink extends JsonService {

    public Wink() {
        setUrl("https://cnt-vlmr-itv02.svc.iptv.rt.ru/api/v2/portal/send_sms_code");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("session_id", "6048b2b6-244a-11ec-8146-4857027601a0:1951416:2237006:2");
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

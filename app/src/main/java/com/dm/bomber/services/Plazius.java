package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Plazius extends JsonService {

    public Plazius() {
        setUrl("https://plazius.ru/api/mobile/v3/auth/sendSms");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "Android SberFood/6.6.6 (build:589; Android 11)");
        builder.addHeader("AppVersion", "6.6.6");
        builder.addHeader("AppPlatform", "Android");
        builder.addHeader("DeviceId", "bruhhhhhhhhhhhhh");
        builder.addHeader("AppKey", "Android-10487d30-003b-416e-8fd8-6468bc90246f");
        builder.addHeader("Features", "Afisha, SplitOrder2, ReferralCampaign");
        builder.addHeader("mrid", "null");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("userPhone", "+" + getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

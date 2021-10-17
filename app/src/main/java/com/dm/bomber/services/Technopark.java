package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Technopark extends JsonService {

    public Technopark() {
        setUrl("https://www.technopark.ru/graphql/");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("operationName", "AuthStepOne");
            json.put("query", "mutation AuthStepOne($phone: String!, $token: String!, $cityId: ID!) @access(token: $token) @city(id: $cityId) {\n" +
                    "  sendOTP(phone: $phone)\n" +
                    "}\n");
            json.put("variables", new JSONObject()
                    .put("cityId", "36966")
                    .put("phone", phone)
                    .put("token", "pntja84v6ga71ovrlshebss2t4"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Uchiru extends JsonService {

    public Uchiru() {
        setUrl("https://uchi.ru/teens/gateway");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("operationName", "StudentSignUp_UserSmsLoginRequest");
            json.put("variables", new JSONObject()
                    .put("consent", true)
                    .put("phone", format(phone, "+7 (***) ***-**-**")));
            json.put("query", "mutation StudentSignUp_UserSmsLoginRequest($phone: String!, $consent: Boolean) {\n" +
                    "  userSmsLoginRequest(input: {phone: $phone, type: student, consentUseData: $consent}) {\n" +
                    "    payload {\n" +
                    "      success\n" +
                    "      resendTimeout\n" +
                    "      __typename\n" +
                    "    }\n" +
                    "    __typename\n" +
                    "  }\n" +
                    "}\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

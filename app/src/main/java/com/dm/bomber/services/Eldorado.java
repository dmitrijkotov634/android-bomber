package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.Request;

public class Eldorado extends JsonService {

    public Eldorado() {
        setUrl("https://www.eldorado.ru/_ajax/spa/auth/v2/auth_with_login.php");
        setMethod(POST);
        setPhoneCode("7");
    }
    
    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Authorization", "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjp7IlNJRCI6ImRicXVsZDd0a2M3NDZ0OTNhYmdjdWdmdXJqIn0sImV4cCI6MTYyODQxMzUwNH0.Sc2PhcxAW2f8HLkeEJnEeKhAB4Q5O1TZvPJjbeH3ITbAZHfuRIZedNPv1m8g7oQ4l8VMKJMogjF0wf51kbrhXQ");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("user_login", format(getFormattedPhone(), "+* (***) *** ****"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

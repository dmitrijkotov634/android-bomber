package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class FriendsClub extends JsonService {

    public FriendsClub() {
        setUrl("https://lk.friendsclub.ru/CustomerOfficeService/Identity/RequestAdvancedPhoneEmailRegistration");
        setMethod(POST);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("signature", "CaS5aeKZO1B");
            json.put("mobilePhone", "+" + getFormattedPhone());
            json.put("Source", "11");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

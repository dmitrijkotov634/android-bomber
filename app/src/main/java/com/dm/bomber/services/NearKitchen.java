package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class NearKitchen extends JsonService {

    public NearKitchen() {
        setUrl("https://api.localkitchen.ru/registration");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cGUiOiJKV1QifQ.eyJxdWlldCI6dHJ1ZSwicGVybWlzc2lvbnMiOnRydWUsImluc3RhbGxlZCI6dHJ1ZSwiZ29vZ2xlX3BsYXkiOnRydWUsInNoaXRfYXBwcyI6ZmFsc2UsImRlbGl2ZXJ5IjpmYWxzZSwiZW11bGF0b3IiOmZhbHNlLCJyb290Ijp0cnVlLCJzaWduIjoiVW1XS1kxR0V0KzAweW42aDhoWERFOTdkdGFvPSIsImRhdGFfZGlyIjoiXC9kYXRhXC91c2VyXC8wXC9jb20uZmFzdHJ1bmtpdGNoZW4iLCJhcHBfZGlyIjoiXC9kYXRhXC91c2VyXC8wXC9jb20uZmFzdHJ1bmtpdGNoZW5cL2ZpbGVzIiwiY291bnRzIjoiMjYgMTYgNyIsInByb2Nlc3MiOiJjb20uZmFzdHJ1bmtpdGNoZW4iLCJqcyI6e30sImFwcCI6ImNvbS5mYXN0cnVua2l0Y2hlbiIsIm1uYyI6IjAyIiwiYXBwdmMiOjY4NiwiZHV1aWQiOiJhNDIyNDcyNGQ2ZGY3YWU1IiwibWNjIjoiMjUwIiwicGxhdGZvcm0iOiJhbmRyb2lkIiwib3N2bWFqIjozMCwiZGV2IjpmYWxzZSwiZGV2bSI6IlhpYW9taSIsImRldm4iOiJNMjAxMEoxOVNZIiwiYXBwdiI6IjYuOC43NCIsImlhdCI6MTYyOTMyNDQwOCwib3NuYW1lIjoiQW5kcm9pZCAxMSIsInZlbmRvcl9pZCI6ImI5YjMyZmM4LTIwODgtNDJjZi1hODdhLTFjOTlhYTk4MGJmZSJ9.HeOOBDZf2mzAqKncnSF8P1BcSvkB141HZXDkknXfpb8");
        builder.addHeader("user-agent", "okhttp/4.9.1");

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

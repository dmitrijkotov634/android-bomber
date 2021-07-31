package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Farfor extends JsonService {

    public Farfor() {
        setUrl("https://api.farfor.ru/v3/842b03f5-7db9-4850-9cb1-407f894abf5e/ufa/auth/request_code/");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Accept", "application/json, text/plain, */*");
        builder.addHeader("Accept-Encoding", "gzip, deflate, br");
        builder.addHeader("Accept-Language", "ru,en-US;q=0.7,en;q=0.3");
        builder.addHeader("Connection", "keep-alive");
        builder.addHeader("Content-Length", "44");
        builder.addHeader("Content-Type", "application/json;charset=utf-8");
        builder.addHeader("Cookie", "sessionid=2oolpyf0e2k296f9tjs9xsqu8nlowdlk; _ym_uid=16277474121051730478; _ym_d=1627747412; _ga=GA1.2.118116920.1627747412; _gid=GA1.2.1816423293.1627747412; _ym_isad=1; _fbp=fb.1.1627747412390.1736093513; _ym_visorc=w; cityId=1; csrftoken2=IhRcNZn7227SDnP9jMOyYY7417wCGBtSo2OO0O3kNtEvLNgF4v8D0ckRBuFlZscE; site_version=desktop; carrotquest_session=8fpsc4jc7x2ifcgpsm6w20fegkzxm47a; carrotquest_session_started=1; carrotquest_device_guid=3cd4fc77-f1a5-4df5-b53b-386e634f3da8; carrotquest_uid=970054057118927458; carrotquest_auth_token=user.970054057118927458.31150-37323e6335c8cd0f23c2c532a8.57f15754343a95904eca12cd3e4d8b3f88df60e6fd9e84c9; carrotquest_realtime_services_transport=wss; amp_f3662d=mGl6vB3OYTEQS_8JOMhL_V...1fbukuc60.1fbul3e8r.3.1.4");
        builder.addHeader("Host", "api.farfor.ru");
        builder.addHeader("Origin", "https://ufa.farfor.ru");
        builder.addHeader("Referer", "https://ufa.farfor.ru/");
        builder.addHeader("Sec-Fetch-Dest", "empty");
        builder.addHeader("Sec-Fetch-Mode", "cors");
        builder.addHeader("Sec-Fetch-Site", "same-site");
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:90.0) Gecko/20100101 Firefox/90.0");
        builder.addHeader("X-CSRFTOKEN", "IhRcNZn7227SDnP9jMOyYY7417wCGBtSo2OO0O3kNtEvLNgF4v8D0ckRBuFlZscE");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", getFormattedPhone());
            json.put("ui_element", "login");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

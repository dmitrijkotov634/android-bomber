package com.dm.bomber.services;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Baucenter extends Service {

    @Override
    public void run(Callback callback) {
        client.newCall(new Request.Builder()
                .url("https://ma.baucenter.ru/auth/sessionRegister")
                .header("x-api-key", "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCtj8uTFmdASuHU")
                .header("x-auth-token", "")
                .post(RequestBody.create(new byte[0]))
                .build()).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());
                    JSONObject req = new JSONObject();

                    req.put("phone", getFormattedPhone());

                    client.newCall(new Request.Builder()
                            .url("https://ma.baucenter.ru/auth/authByPhone")
                            .header("x-api-key", "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCtj8uTFmdASuHU")
                            .header("x-auth-token", json.getJSONObject("data").getString("token"))
                            .post(RequestBody.create(req.toString(), MediaType.parse("application/json")))
                            .build()).enqueue(callback);
                } catch (JSONException e) {
                    callback.onError(e);
                }
            }
        });
    }
}
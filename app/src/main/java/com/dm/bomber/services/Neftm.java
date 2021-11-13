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

public class Neftm extends Service {

    @Override
    public void run(Callback callback) {
        JSONObject json = new JSONObject();

        try {
            json.put("deviceId", "abcabcabccc12345");
            json.put("timezoneOffset", "300");
            json.put("pushEnabled", "false");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        client.newCall(new Request.Builder()
                .url("https://apimobile.neftm.ru/api/Auth/UpdateDevice")
                .header("User-Agent", "centrida.neftmagistral/7.1.0 (M2010J19SY; Android 11)")
                .post(RequestBody.create(json.toString(), MediaType.parse("application/json")))
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

                    req.put("phone", "+" + getFormattedPhone());

                    client.newCall(new Request.Builder()
                            .url("https://apimobile.neftm.ru/api/Auth/Register")
                            .header("User-Agent", "centrida.neftmagistral/7.1.0 (M2010J19SY; Android 11)")
                            .header("X-DeviceId", json.getJSONObject("data").getString("token"))
                            .post(RequestBody.create(req.toString(), MediaType.parse("application/json")))
                            .build()).enqueue(callback);
                } catch (JSONException e) {
                    callback.onError(e);
                }
            }
        });
    }
}
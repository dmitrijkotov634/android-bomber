package com.dm.bomber.services;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RabotaRu extends Service {

    @Override
    public void run(Callback callback) {
        JSONObject req = new JSONObject();
        JSONObject request = new JSONObject();

        try {
            request.put("device_id", "b4703808bff93aec");
            request.put("google_id", "");
            request.put("is_new_user", false);
            request.put("mobile_app_platform", "android");
            request.put("region_id", "3");
            request.put("user_agent", "Android 11");
            request.put("user_tags", new JSONArray());
            request.put("mobile_app_version", "4.33.7");
            request.put("yandex_id", "");

            req.put("request", request);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        client.newCall(new Request.Builder()
            .url("https://api.rabota.ru/v4/settings.json")
            .header("User-Agent", "Rabota/4.33.7 (ru.rabota.app2; build:2021043307; Android 11) okhttp/4.8.0")
            .post(RequestBody.create(req.toString(), MediaType.parse("application/json")))
            .build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONObject request = new JSONObject();
                    JSONObject req = new JSONObject();

                    request.put("is_during_response", false);
                    request.put("login", getFormattedPhone());

                    req.put("request", request);
                    req.put("user_tags", new JSONArray());
                    req.put("rabota_ru_id", json.getJSONObject("response").getString("rabota_ru_id"));
                    req.put("application_id", "10");

                    client.newCall(new Request.Builder()
                        .url("https://api.rabota.ru/v4/register.json")
                        .header("User-Agent", "Rabota/4.33.7 (ru.rabota.app2; build:2021043307; Android 11) okhttp/4.8.0")
                        .post(RequestBody.create(req.toString(), MediaType.parse("application/json")))
                        .build()).enqueue(callback);
                } catch (JSONException e) {
                    callback.onResponse(call, response);
                }
            }
        });
    }
}

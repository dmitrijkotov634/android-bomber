package com.dm.bomber.services;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DvaBerega extends Service {

    @Override
    public void run(Callback callback) {
        client.newCall(new Request.Builder()
                .url("https://2bmob.2-berega.ru/api/v1/registration/guest")
                .post(new FormBody.Builder().build())
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

                    req.put("phone", getFormattedPhone());
                    req.put("regionId", "1");
                    req.put("showcase", "2");

                    client.newCall(new Request.Builder()
                            .url("https://2bmob.2-berega.ru/api/v1/registration")
                            .header("Authorization", "Bearer " + json
                                    .getJSONObject("data")
                                    .getString("bearer"))
                            .post(RequestBody.create(req.toString(), MediaType.parse("application/json")))
                            .build()).enqueue(callback);
                } catch (JSONException e) {
                    callback.onResponse(call, response);
                }
            }
        });
    }
}
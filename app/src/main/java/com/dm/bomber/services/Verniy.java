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

public class Verniy extends Service {

    @Override
    public void run(Callback callback) {
        client.newCall(new Request.Builder()
                .url("https://loymax.ivoin.ru/publicapi/token?client_id=OAIvoinMP")
                .post(new FormBody.Builder().add("grant_type", "anonymous").build())
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

                    req.put("login", getFormattedPhone());
                    req.put("password", "");

                    client.newCall(new Request.Builder()
                            .url("https://loymax.ivoin.ru/publicapi/v1.2/Registration/BeginRegistration")
                            .header("Authorization", "Bearer " + json.getString("access_token"))
                            .post(RequestBody.create(req.toString(), MediaType.parse("application/json")))
                            .build()).enqueue(callback);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

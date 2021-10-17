package com.dm.bomber.services;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Velobike extends Service {

    private final Headers HEADERS = new Headers.Builder()
            .add("Cookie", "csrftoken_v4=14rGy12nTsyauwztFh0LbiPJt26K9UGbBc9OZjC5hRFf0oX9UTymbvLXjjQjKGV4; language_v4=ru; _ym_uid=163448028030498968; _ym_d=1634480280; _ym_visorc=w; _ym_isad=2")
            .add("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36")
            .add("X-CSRFToken", "14rGy12nTsyauwztFh0LbiPJt26K9UGbBc9OZjC5hRFf0oX9UTymbvLXjjQjKGV4")
            .build();

    public Velobike() {
        setPhoneCode("7");
    }

    @Override
    public void run(Callback callback) {
        JSONObject json = new JSONObject();

        try {
            json.put("agree_terms", true);
            json.put("email", getEmail());
            json.put("first_name", getRussianName());
            json.put("last_name", getRussianName());
            json.put("phone_number", getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        client.newCall(new Request.Builder()
                .url("https://velobike.ru/api/registration/")
                .headers(HEADERS)
                .post(RequestBody.create(
                        json.toString(), MediaType.parse("application/json")))
                .build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                client.newCall(new Request.Builder()
                        .url("https://velobike.ru/api/restore_pin/")
                        .headers(HEADERS)
                        .post(new FormBody.Builder()
                                .add("phone_number", format(phone, "7(***)***-**-**"))
                                .build())
                        .build()).enqueue(callback);
            }
        });
    }
}
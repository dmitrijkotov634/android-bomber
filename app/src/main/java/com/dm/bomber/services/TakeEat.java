package com.dm.bomber.services;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class TakeEat extends Service {

    public TakeEat() {
        setPhoneCode("7");
    }

    @Override
    public void run(Callback callback) {
        client.newCall(new Request.Builder()
                .url("https://petrodv.takeeat.ru/ajax/user_check.php")
                .post(new FormBody.Builder()
                        .add("phone", format(phone, "+7 *** ***-**-**"))
                        .build())
                .build()).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                client.newCall(new Request.Builder()
                        .url("https://petrodv.takeeat.ru/ajax/auth2.php")
                        .get()
                        .build()).enqueue(callback);
            }
        });
    }
}

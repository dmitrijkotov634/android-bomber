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

public class MegafonTV extends Service {

    public MegafonTV() {
        setPhoneCode("7");
    }

    @Override
    public void run(Callback callback) {
        client.newCall(new Request.Builder()
                .url("https://megafon.tv/")
                .get().build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                JSONObject json = new JSONObject();

                try {
                    json.put("msisdn", getFormattedPhone());
                    json.put("password", "91234657");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (String cookie : response.headers().get("Set-Cookie").split(";")) {
                    if (cookie.startsWith("SessionID"))
                        client.newCall(new Request.Builder()
                                .url("https://bmp.megafon.tv/api/v10/auth/register/msisdn")
                                .addHeader("Cookie", cookie)
                                .post(RequestBody.create(
                                        json.toString(), MediaType.parse("application/json")))
                                .build()).enqueue(callback);
                }
            }
        });
    }
}

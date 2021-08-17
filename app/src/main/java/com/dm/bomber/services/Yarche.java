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

public class Yarche extends Service {

    @Override
    public void run(Callback callback) {
        client.newCall(new Request.Builder()
                .url("https://api.magonline.ru/api/auth/token?shop_code=yarche&meta%5Bexact_delivery_address%5D=true")
                .header("User-Agent", "com.yarche.app/2012252100 (Redmi/M2010J19SY Android/11)")
                .get()
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

                    req.put("operationName", "RequestSignInVerificationCode");
                    req.put("variables", new JSONObject().put("input", new JSONObject().put("phone", "+" + getFormattedPhone())));
                    req.put("query", "mutation RequestSignInVerificationCode($input: RequestPhoneVerificationInput!) { requestSignInPhoneVerificationCode(input: $input) { __typename ...phoneVerification } } fragment phoneVerification on PhoneVerification { __typename count maxCount resendTime isVerified }");

                    client.newCall(new Request.Builder()
                            .url("https://moappsmapi.sportmaster.ru/api/v1/code")
                            .header("Token", response.headers().get("Token"))
                            .header("User-Agent", "com.yarche.app/2012252100 (Redmi/M2010J19SY Android/11)")
                            .post(RequestBody.create(req.toString(), MediaType.parse("application/json")))
                            .build()).enqueue(callback);
                } catch (JSONException e) {
                    callback.onResponse(call, response);
                }
            }
        });
    }
}
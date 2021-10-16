package com.dm.bomber.services;

import android.util.Log;

import java.util.Random;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class Service {
    public static final String TAG = "Service";
    public static final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request request = chain.request();
                Log.v(TAG, String.format("Sending request %s", request.url()));

                Response response = chain.proceed(request);
                Log.v(TAG, String.format("Received response for %s with status code %s",
                        response.request().url(), response.code()));

                return response;
            }).build();

    public String phoneCode;
    public String phone;

    public String requireCode;

    private static String randomString(char min, char max, int length) {
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++)
            result.append((char) (random.nextInt(max - min + 1) + min));

        return result.toString();
    }

    public static String getRussianName() {
        return Service.randomString('а', 'я', 5);
    }

    public static String getUserName() {
        return Service.randomString('a', 'z', 12);
    }

    public static String getEmail() {
        Random random = new Random();
        return getUserName() + "@" + new String[]{"gmail.com", "mail.ru", "yandex.ru"}[random.nextInt(3)];
    }

    public static String format(String phone, String mask) {
        StringBuilder formattedPhone = new StringBuilder();

        int index = 0;
        for (char symbol : mask.toCharArray()) {
            if (symbol == '*') {
                formattedPhone.append(phone.charAt(index));
                index++;
            } else {
                formattedPhone.append(symbol);
            }
        }

        return formattedPhone.toString();
    }

    public void setPhoneCode(String phoneCode) {
        this.requireCode = phoneCode;
    }

    public void prepare(String phoneCode, String phone) {
        this.phoneCode = phoneCode;
        this.phone = phone;
    }

    public String getFormattedPhone() {
        return phoneCode + phone;
    }

    public abstract void run(Callback callback);
}

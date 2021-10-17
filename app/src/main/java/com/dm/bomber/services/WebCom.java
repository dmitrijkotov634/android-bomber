package com.dm.bomber.services;

import java.util.Random;

import okhttp3.HttpUrl;

public class WebCom extends ParamsService {

    public WebCom() {
        setUrl("https://my3.webcom.mobi/sendsms.php");
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        Random random = new Random();

        builder.addQueryParameter("user", "flashcall");
        builder.addQueryParameter("pwd", "xW2C5SUy");
        builder.addQueryParameter("sadr", "Flashcall");
        builder.addQueryParameter("dadr", phone);
        builder.addQueryParameter("text", String.valueOf(random.nextInt(9000) + 1000));
    }
}

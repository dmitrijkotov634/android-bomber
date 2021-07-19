package com.dm.bomber.services;

import org.json.JSONObject;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Tele2 extends ParamsService {

    public Tele2() {
        setUrl("https://msk.tele2.ru/api/validation/number/");
        setMethod(POST);
    }

    @Override
    public Request extendRequest(Request.Builder builder) {
        JSONObject json = new JSONObject();

        RequestBody body = RequestBody.create(
                "{\"sender\": \"Tele2\"}", MediaType.parse("application/json"));

        builder.method(method, body);
        return super.extendRequest(builder);
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addPathSegment(getFormattedPhone());
    }
}

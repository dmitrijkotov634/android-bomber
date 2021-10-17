package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Profi extends JsonService {

    public Profi() {
        setUrl("https://api.profi.ru/warp/v2/graphql/");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "okhttp/4.6.0");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("query", "$GQLID{fd4add255650ea66deccb1cfc83f30e2}");
            json.put("variables", new JSONObject()
                    .put("scenario", "AUTH_SCENARIO_FORK")
                    .put("phone", "+" + getFormattedPhone())
                    .toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

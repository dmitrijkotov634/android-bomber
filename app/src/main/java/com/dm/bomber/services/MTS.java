package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class MTS extends ParamsService {

    public MTS() {
        setUrl("https://prod.tvh.mts.ru/tvh-public-api-gateway/public/rest/general/send-code");
        setMethod(POST);
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("msisdn", getFormattedPhone());
    }
}

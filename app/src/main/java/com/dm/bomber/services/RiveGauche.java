package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class RiveGauche extends ParamsService {

    public RiveGauche() {
        setUrl("https://api.c5ia1s20aa-aromaluxe1-p1-public.model-t.cc.commerce.ondemand.com/rg/v1/newRG/customers/current/contacts/send-code");
        setMethod(POST);
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("configGroupCode", "default");
        builder.addQueryParameter("contact", getFormattedPhone());
        builder.addQueryParameter("contactMustBeUnique", "true");
        builder.addQueryParameter("contactUserMustExist", "false");
    }
}

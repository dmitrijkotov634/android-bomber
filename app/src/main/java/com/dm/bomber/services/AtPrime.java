package com.dm.bomber.services;

import okhttp3.FormBody;

public class AtPrime extends FormService {

    public AtPrime() {
        super("https://api-prime.anytime.global/api/v2/auth/sendVerificationCode", POST);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("phone", getFormattedPhone());
    }
}

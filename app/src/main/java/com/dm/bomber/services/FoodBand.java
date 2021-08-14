package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class FoodBand extends ParamsService {

    public FoodBand() {
        setUrl("https://is.foodband.ru/api/rest/public/v2/customers/sendVerificationCode");
        setPhoneCode("7");
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("phone", phone);
        builder.addQueryParameter("token", "173f3678-b66c-4074-a280-f7df26009d26");
    }
}

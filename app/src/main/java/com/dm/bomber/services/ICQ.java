package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class ICQ extends ParamsService {

    public ICQ() {
        setUrl("https://www.icq.com/smsreg/requestPhoneValidation.php");
    }

    @Override
    public void buildParams(HttpUrl.Builder builder) {
        builder.addQueryParameter("msisdn", getFormattedPhone());
        builder.addQueryParameter("locale", "en");
        builder.addQueryParameter("countryCode", "ru");
        builder.addQueryParameter("version", "1");
        builder.addQueryParameter("k", "ic1rtwz1s1Hj1O0r");
        builder.addQueryParameter("r", "46763");
    }
}

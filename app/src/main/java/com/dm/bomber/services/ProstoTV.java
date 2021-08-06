package com.dm.bomber.services;

import okhttp3.FormBody;

public class ProstoTV extends FormService {

    public ProstoTV() {
        setUrl("https://prosto.tv/wp-admin/admin-ajax.php");
        setMethod(POST);
        setPhoneCode("380");
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("action", "check-phone");
        builder.add("phone", "+" + getFormattedPhone());
        builder.add("username", getRussianName());
        builder.add("_nonce", "5b97688abd");
    }
}

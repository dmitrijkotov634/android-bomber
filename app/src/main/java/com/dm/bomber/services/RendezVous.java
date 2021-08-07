package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public class RendezVous extends FormService {

    public RendezVous() {
        setUrl("https://www.rendez-vous.ru/ajax/SendPhoneConfirmationNewMess/");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        builder.addHeader("Accept-Encoding", "gzip, deflate, br");
        builder.addHeader("Accept-Language", "ru,en-US;q=0.7,en;q=0.3");
        builder.addHeader("Connection", "keep-alive");
        builder.addHeader("Content-Length", "45");
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        builder.addHeader("Cookie", "partnerId=%22ggl-seo%22; clickStream=%5B%22ggl-seo%22%5D; PHPSESSID3=738cc7ff77e68871f8353e00131be4c9; customercheckout=1; bin1=%7B%7D; scarab.visitor=%22550820D83362BF6A%22; _ga=GA1.2.1502576194.1628341762; _gid=GA1.2.445652503.1628341762; _ym_uid=1628341763326560575; _ym_d=1628341763; _gcl_au=1.1.178885816.1628341763; _ym_isad=1; _fbp=fb.1.1628341763514.812073545; _ym_visorc=w; cto_bundle=iZ_yvl9JV1Zsc3R3aXdFclk4c1hqUklpNGdscVk4Y2JtV1l5UjRPNUhCeUZ2M3RESDY3ZTJ2bXFORTRvNlNneDhZUzlWU2htUWZ6TkpYeTE1JTJGJTJCYzVSNXNhdnV3ZDVHUTlLb0VyNGJXZXU0Y2duSTdQODUzV3pVSHAlMkZuYmU1d01iTHF1S3p6VzREQkhkdklZam85aEs0a1BFOUElM0QlM0Q; _gat_UA-27415597-1=1; flocktory-uuid=414a99b9-c715-4bde-915a-eb24683ff0dc-3; SIDCAPTCHA=eb7e0c0c8922ea76c12c8a3d0c84600f");
        builder.addHeader("Host", "www.rendez-vous.ru");
        builder.addHeader("Origin", "https://www.rendez-vous.ru");
        builder.addHeader("Referer", "https://www.rendez-vous.ru/");
        builder.addHeader("Sec-Fetch-Dest", "empty");
        builder.addHeader("Sec-Fetch-Mode", "cors");
        builder.addHeader("Sec-Fetch-Site", "same-origin");
        builder.addHeader("TE", "trailers");
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:90.0) Gecko/20100101 Firefox/90.0");
        builder.addHeader("X-INSTANA-L", "1,correlationType=web;correlationId=28d9005cad9492b5");
        builder.addHeader("X-INSTANA-S", "28d9005cad9492b5");
        builder.addHeader("X-INSTANA-T", "28d9005cad9492b5");
        builder.addHeader("X-Requested-With", "XMLHttpRequest");

        return super.buildRequest(builder);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("phone", format(getFormattedPhone(), "+*(***)***-**-**"));
        builder.add("alien", "0");
        builder.add("captcha", "9241");
    }
}

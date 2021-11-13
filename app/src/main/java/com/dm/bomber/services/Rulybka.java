package com.dm.bomber.services;

import okhttp3.Request;
import okhttp3.RequestBody;

public class Rulybka extends Service {

    public Rulybka() {
        setPhoneCode("7");
    }

    @Override
    public void run(Callback callback) {
        client.newCall(new Request.Builder()
                .url("https://www.r-ulybka.ru/login/form_ajax.php")
                .addHeader("Cookie", "PHPSESSID=95868ae5856f9cc72abaf31c175f8afb; last_city=116.120.64.268; BITRIX_SM_SALE_UID=425371265; BITRIX_CONVERSION_CONTEXT_s2={\"ID\":29,\"EXPIRE\":1634504340,\"UNIQUE\":[\"conversion_visit_day\"]}; amp_5b19e2=azUKGkuvVtc6Cvr-bulw4m...1fi78gds9.1fi78gds9.0.0.0; _gcl_au=1.1.1522422477.1634478864; BX_USER_ID=120ad71f7ab56868105c66af5370fcde; _userGUID=0:kuvaijqq:uVqYs4s8DmMcLQB5iqqNbH90K5xfuAZy; dSesn=df97d050-3d20-6ec0-1eae-a3bba66f44b8; _dvs=0:kuvaijqq:1ZBmJ3F2gqbl9v7w_rfkgtIyHFJMOVxo; _ym_uid=1634478865758138009; _ym_d=1634478865; BITRIX_SM_advcake_trackid=ca7b1f76-55f1-c864-bf1b-4c2a4f3687f1; advcake_session_id=dadcad03-0370-7081-23ef-224cae79c426; rrpvid=895831779514476; _ga=GA1.2.1458148895.1634478866; _gid=GA1.2.1154709380.1634478866; rcuid=61599a1653897c0001d741da; cto_bundle=clNQ9F9Zc3NUaWkwZElyRExDcm9vMnV6OXk3ZHZ1UFVhSmxRYlpuaUYwSWM2VU5sJTJGenhWcGFsaEhhVXYlMkYlMkJ1WmZrbnRib2NJS3V4cFNsJTJCdDdEMHNuNVp6ZFM0RFVyVlc3JTJCR3BRbGxtYUR3THNtMmEyJTJCbURwVkVic29mbW5EZFNQTWdkcXRQU1dSWmE2UDJ2TlQ2JTJCbmkySnQ2dyUzRCUzRA; _fbp=fb.1.1634478867716.604549193; _ym_isad=2; _gat_UA-87603375-1=1; tmr_lvid=06c91556372e770c96e3dc561c151e60; tmr_lvidTS=1634478868787; uxs_uid=c1d0dd30-2f51-11ec-bf3a-658f5ee312bb; tmr_detect=0|1634478873298; tmr_reqNum=4")
                .addHeader("x-bitrix-csrf-token", "dd98bb54be4e23a13e0228b717afa7de")
                .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundaryub6lUB4ZV7ea7hKD")
                .post(RequestBody.create("------WebKitFormBoundaryub6lUB4ZV7ea7hKD\n" +
                        "Content-Disposition: form-data; name=\"json\"\n" +
                        "\n" +
                        "{\"action\":\"auth\",\"phone\":\"" + format(phone, "+7(***)***-**-**") + "\"}\n" +
                        "------WebKitFormBoundaryub6lUB4ZV7ea7hKD--", null))
                .build()).enqueue(callback);
    }
}

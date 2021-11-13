package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public class Vpodarok extends FormService {

    public Vpodarok() {
        setUrl("https://vpodarok.ru/user/sendSms");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6InFub3RVcDRiSmppRzE4TDBENExRMnc9PSIsInZhbHVlIjoiV3ZHdTNyQ0JDcm5BQjlDM1JSNHViVnJyMzVETFdlbm55b0JoVmQ2K2hYeXU0OUJRcTRQd0hsSldrRlZOa2JPdyIsIm1hYyI6Ijc2ZGViOTA3MjE2ZGM1MTI1NTUxOGVlNmFhNzEyMzgzZjFiNTg3YzFhMDk4OGVhZTE0MDZiY2Q2NWEzZmMwMzgifQ%3D%3D; vpodarok_session=eyJpdiI6ImJJcG50YUlTR3lGc1hrSUtQMFFUb2c9PSIsInZhbHVlIjoiZVRkcjhwbno0WmJQbGo0M05TT0ZFWURlUWRjTE5RaWhZaFdJSWl3QVd1cDJFOEpFWjlUekNZRkF3cDNWNUlpTyIsIm1hYyI6ImVjYjI0NWJhZjAyNTRmYzNmY2FkMjViYWMzZGE5ZDhmYzI2Y2I4MGJiMDkwMWE2MjZlOTg4MTliNGFjYTEwNDQifQ%3D%3D; _ym_uid=1636824334350782514; _ym_d=1636824334; _ga=GA1.2.337684060.1636824334; _gid=GA1.2.1627277202.1636824334; _gat_gtag_UA_144163978_1=1; _ym_isad=1; _ym_visorc=w");
        builder.addHeader("X-CSRF-TOKEN", "y8caVWi1c0UXor8mrdViEzS08cWorH20p6zDXedK");

        return super.buildRequest(builder);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("phone", format(phone, "+7+(***)+***-**-**"));
    }
}

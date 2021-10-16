package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public class AlloUa extends FormService {

    public AlloUa() {
        setUrl("https://allo.ua/ua/customer/account/createPostVue/?isAjax=1&currentLocale=uk_UA");
        setMethod(POST);
        setPhoneCode("380");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36");
        builder.addHeader("Cookie", "store=default_ua; __uzma=f6be5bd4-bbe2-41aa-810b-5cdb22b0648f; __uzmb=1634383493; __uzme=3263; frontend=68ff3d1df724472480e26cd67c04b8f2; is_bot=0; detect_mobile_type=0; _gcl_au=1.1.114366438.1634383494; __utmc=45757819; __utmz=45757819.1634383494.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __ssds=2; sc=4147F0E2-C7FF-AA92-CD49-E08241BDBEC0; __uzmaj2=00eeddc1-826f-4db6-b2b4-38a485a66fce; __uzmbj2=1634383494; _ga=GA1.2.1595744579.1634383494; _gid=GA1.2.1257376910.1634383495; _fbp=fb.1.1634383495244.968970905; __exponea_etc__=9268efea-e10e-4301-b985-d40382917e34; _hjid=d7988f57-9af8-4318-a498-b39abf63b2a6; _userGUID=0:kutpqi7u:6O2odjj01K88hzrVCAf_oXk5ytiwiuZC; frontend_hash=oWqS3Q68ff3d1df724472480e26cd67c04b8f2aKq3rv; store=default_ua; protocol=https; city_id=4; site_visited=1634469967.1; lapuid=9d64e877-de8a-4707-9c4a-874ad5f791b5; __utma=45757819.1595744579.1634383494.1634383494.1634398307.2; __utmt=1; __exponea_time2__=-0.4974370002746582; dSesn=cdcabd6d-703b-5cf2-f58c-a105b7cbd545; _dvs=0:kutyjyjz:RerJYl3G1JjK4Y1Pnoz~YKuEZvlhJ4sc; __insp_wid=1964961402; __insp_nv=true; __insp_targlpu=aHR0cHM6Ly9hbGxvLnVhLw==; __insp_targlpt=0IbQvdGC0LXRgNC90LXRgiDQvNCw0LPQsNC30LjQvSDQvNC_0LHRltC70YzQvdC40YUg0YLQtdC70LXRhNC_0L3RltCyLCDQvdC_0YPRgtCx0YPQutGW0LIsINGC0LXQu9C10LLRltC30L7RgNGW0LIsINGE0L7RgtC_0LDQv9Cw0YDQsNGC0ZbQsiwg0LLRltC00LXQvtC60LDQvNC10YAsINC/0L7QsdGD0YLQvtCy0L7RlyDRgtC10YXQvdGW0LrQuC4g0JTQvtGB0YLQsNCy0LrQsCDQsiDQmtC40ZfQsiwg0JTQvdGW0L/RgNC_0L/QtdGC0YDQvtCy0YHRjNC6LCDQpdCw0YDQutGW0LIsINCU0L7QvdC10YbRjNC6LCDQntC00LXRgdCwLCDQv9C_INCj0LrRgNCw0ZfQvdGWIOKAkyDRltC90YLQtdGA0L3QtdGCLdC80LDQs9Cw0LfQuNC9IEFMTE8udWEh; __insp_norec_sess=true; __ssuzjsr2=a9be0cd8e; __uzmcj2=365912555320; __uzmdj2=1634398392; __insp_slim=1634398397747; session_id=3c8a3812-f321-4ace-b7db-2b3510a8af76; session_pageview=1634398402.1; __utmb=45757819.6.9.1634398453829; __uzmd=1634398453; __uzmc=7954613673178; private_content_version=e493bdad27418dae2fe387c57a8a9872; t_s_c_f_l=0:2:e1e1a4b3aedc91c0:r6YcGsUVmhfE8KdPSFNqMQ==");
        builder.addHeader("referer", "https://allo.ua");

        return super.buildRequest(builder);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("firstname", getRussianName());
        builder.add("telephone", "0" + phone);
        builder.add("email", getEmail());
        builder.add("password", getUserName());
        builder.add("form_key", "VtmOoPg0ccEcEnIg");
    }
}

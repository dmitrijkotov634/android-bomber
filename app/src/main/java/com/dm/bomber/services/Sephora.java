package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public class Sephora extends FormService {

    public Sephora() {
        setUrl("https://sephora.ru/");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        builder.addHeader("Accept-Encoding", "gzip, deflate, br");
        builder.addHeader("Accept-Language", "ru,en-US;q=0.7,en;q=0.3");
        builder.addHeader("Connection", "keep-alive");
        builder.addHeader("Content-Length", "134");
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        builder.addHeader("Cookie", "_abck=989037603F72F0EBB07C9E1EFCAEC3D8~0~YAAQXmojF9z4bJl6AQAAIhcw/QaUZdOgtqBscoObDIAXhra9HLWL+2E7G+WeSkJ4c7dNwuLo2U5hJNFWkZNamMZZA14i/1rP4HrnrfyUl4BF7eOzntIuaHXYD+/Y0no6GOeQnKVdOjTeCAf/qrvmuPs6oQL+Jji93rzDjrzR+MM6oqClqA0FK8yuNAzUK3/mSKFqAxB7Rp+slv0ZwgmxNFzFyVHhWMHvsdXBg7cVWcvdpLVnq+Dazxhz93bDusxshpEejGLbVhksDyJ0BbROHtBy+Rn+z6Gf89aUAU7ZC/nqMJmtZn+/L8nDw+ioT5gSukq9Y6i4HZ3e9mVy85pe0bIM26hDGdNftJxw2WlTMWYmr6NrpCILoo3EPIKuUH06IE7O9wHlV8w9ZR+i28WL+KPrbTObVk6G~-1~||-1||~-1; bm_sz=068E27067264AC90C986B4937F2581E9~YAAQXmojF9T4bJl6AQAA7PYv/QwOf7BPH3zZTHGBmbXCAfQsmPC1O8Bm5VPjOKuRAE3DIi6XKAODz3E+UgAlHmEyQOYjCeTsOVwTkLM6NiYUt4wg/TO94wIfOEDZzfCmaIXG6ipRi2G5BFT/lDM7XSsqp7Gk4ENE5lTPDPl/VJYYIt7J02xVl8+1ls53m9loMjf+6U4a1RvFzg1olTPK3+bDK9FdJly41Q3LFwnHaDP8q3bWM7f5lwmH1B/LCPCqe/GNTINF6r78JC1CAHnOOGsCVXCMN6EW48wqShJKkQlcoeI=~3682369~4601667; ak_bmsc=DC33B04288AC37766B18D9FF415E13DF~000000000000000000000000000000~YAAQXmojF9X4bJl6AQAApfgv/QxtbDXSk/tJ9bu8KCkMklZ+XVJuwGQrO3tKYgwclHxPpdEODdvE9iWGgIHcFJED1ZPKBJa0X0MC28DjQd3PJUsPRFRLbtUQijs3TTOlECX4jLSmQ3t8WoTdAk9VfbLt27uIYai3O6kA3vnGakSzhzMAbb1aHLCsuO8U98UI8fR6JS0xLBmAF/YMWEqomlQ4D5VNRQtGrvAnpaLbBvE6MTLdIKQ6QstMFmokOrPLxZLZVbKCgt+CF3fti74VAN4gBV6a/CiroIncSYppVZt1oKZchnV9MKadh3t6mHERy3M8RR7e+g/6Xlxx7+W8KyYxBABOVqW4TUif8VWu6dg7y6dZhXyvcMR+3Gs6B7w48rxXcdQVHEkxgQ==; t2s-analytics=bde3abb9-e022-4587-a0cf-ec049e739143; t2s-p=bde3abb9-e022-4587-a0cf-ec049e739143; _gcl_au=1.1.382432208.1627745425; t2s-rank=rank1; dSesn=cb46af79-fca0-f75d-6d4f-23d56365bc66; _userGUID=0:krrxll8x:QP_n7b5TIrJW53phfENhiVQs1egnL_Bh; _dvs=0:krrxll8x:F3wmIrpYgWdcLZrlhhCXoHfStfGQLQ2g; tmr_reqNum=7; tmr_lvid=603b87fa6c1bb6976e1204b85e65661e; tmr_lvidTS=1627745428222; _ga=GA1.2.254093212.1627745428; _gid=GA1.2.1072652726.1627745428; _ym_uid=1627745429266924586; _ym_d=1627745429; _gat_UA-87625978-40=1; _fbp=fb.1.1627745429912.1395352383; flocktory-uuid=b440792a-87e9-44e9-b5e1-4ac3e968d7bb-3; tmr_detect=1%7C1627745430013; _ym_isad=1; _ym_visorc=w; cto_bundle=qjh8Ll9jaGx1cldKN2JpNVJMM01JaXZzWHE0Y1JWMjZrYk4wMzNscTFGSHJVTnNsVFZlNkFsVjZQakZhYUtjUXVvVGg3T0RWSm5WalNnbWNWalp5eTI4eFk5aFlodGdlTENxbEdueEZuOVliSzFVNzFDaG1SSU9oY2FJUUhaMFQ4Q1ZSa3RXOVBZYXAydTdtNDJKUCUyRkFhdkVuQSUzRCUzRA");
        builder.addHeader("Host", "sephora.ru");
        builder.addHeader("Origin", "https://sephora.ru");
        builder.addHeader("Referer", "https://sephora.ru/");
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
        builder.add("s_login", phone);
        builder.add("m_mail", getEmail());
        builder.add("s_pass", getUserName());
        builder.add("b_accept_privacy_policy", "1");
        builder.add("submit_register", "1");
        builder.add("s_action", "code_confirm_send");
    }
}

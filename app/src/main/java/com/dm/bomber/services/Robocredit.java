package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Robocredit extends JsonService {

    public Robocredit() {
        setUrl("https://robocredit.ru/api/v1/registration/code/send");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Accept", "application/json");
        builder.addHeader("Accept-Encoding", "gzip, deflate, br");
        builder.addHeader("Accept-Language", "ru,en-US;q=0.7,en;q=0.3");
        builder.addHeader("Authorization", "react_application_client");
        builder.addHeader("Connection", "keep-alive");
        builder.addHeader("Content-Length", "175");
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IkZDMFloNzZzRkhBRjhzSTVyaHFWQVE9PSIsInZhbHVlIjoieFNVY0drVGR2blB0UlZBVWY4QjNZSm5DZHdSdDM0WUxpUkZDeW8ydGlxSDdJcnZzYWxITzhCMmJqSlp3Wk5TMmtldEpwdG9NREkwcWdDcUFoRXZvUEEyT1NBL2VSUkRRdTQ1UlA4bkdhVE0xVlBSZW5qWGlPZU5hVUxlRHBjOU8iLCJtYWMiOiI2NDQ2M2VhZTRlMjJhYjg0OGY1ODA5MjVjMGRkNzJkMDQzMGQwZWNmNGIzMDBiZjJiYjdhOTljYzUyNTQ5ZTk3In0%3D; robocredit_session=eyJpdiI6InVyOExObXVjNkdZSFdRdW4zRU5LbWc9PSIsInZhbHVlIjoid0N5NFBYa1EzRW02NDdETlJmSG53eHdDZ1hRaVZLUDRUSUh6aVZmQ2JhSW8xK1NxTjhrd2dMbmtYSWV2OS8remlRNEhnVFlROWZhK2NGekJqcEdrSWJoY3NwNmFVcEpEbmJlWWc3NmRELzFHbW8vVk45K2VEUHRSdkFtem1tYTMiLCJtYWMiOiIxZTcwNTMxOGFkMmI1ZmZlOWNlOTc1Y2I3M2I3ZGUxMDVjZmM2MTVjODkwMzg3YmRlMDg3ZjY1ODgyNTEyNDRhIn0%3D; SERVERID=node01; __cfruid=88814902276cd09899db6bf2f44fdfe49ec80785-1628344822; tmr_reqNum=5; tmr_lvid=a9af81bd62954373fb30b1dbe5852790; tmr_lvidTS=1628344829462; tmr_detect=1%7C1628344829552; _ga=GA1.2.572378467.1628344830; _gid=GA1.2.1844127440.1628344830; _ym_uid=1628344830347478702; _ym_d=1628344830; ec_cache=1cba26dce8b888347624658d3bdfcff49f84d75e0bae3a1850283ec2d9e1eca4; ec_etag=1cba26dce8b888347624658d3bdfcff49f84d75e0bae3a1850283ec2d9e1eca4; __utma=231239964.572378467.1628344830.1628344831.1628344831.1; __utmb=231239964.0.10.1628344863231; __utmc=231239964; __utmz=231239964.1628344831.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _ym_isad=1; supportOnlineTalkID=FzrLpzswSp3numy4EJfLVCoCkQJaZTF4; _fbp=fb.1.1628344831572.99578837; _ym_visorc=w; ec_png=1cba26dce8b888347624658d3bdfcff49f84d75e0bae3a1850283ec2d9e1eca4; ec_id=1cba26dce8b888347624658d3bdfcff49f84d75e0bae3a1850283ec2d9e1eca4");
        builder.addHeader("Host", "robocredit.ru");
        builder.addHeader("Origin", "https://robocredit.ru");
        builder.addHeader("Referer", "https://robocredit.ru/registration/personal");
        builder.addHeader("Sec-Fetch-Dest", "empty");
        builder.addHeader("Sec-Fetch-Mode", "cors");
        builder.addHeader("Sec-Fetch-Site", "same-origin");
        builder.addHeader("TE", "trailers");
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:90.0) Gecko/20100101 Firefox/90.0");
        builder.addHeader("X-CSRF-TOKEN", "YufMbFkxmcj9sbFGo8MSy11ZLlSPXcQtLRFqjfoZ");
        builder.addHeader("X-Requested-With", "XMLHttpRequest");
        builder.addHeader("X-XSRF-TOKEN", "eyJpdiI6IkZDMFloNzZzRkhBRjhzSTVyaHFWQVE9PSIsInZhbHVlIjoieFNVY0drVGR2blB0UlZBVWY4QjNZSm5DZHdSdDM0WUxpUkZDeW8ydGlxSDdJcnZzYWxITzhCMmJqSlp3Wk5TMmtldEpwdG9NREkwcWdDcUFoRXZvUEEyT1NBL2VSUkRRdTQ1UlA4bkdhVE0xVlBSZW5qWGlPZU5hVUxlRHBjOU8iLCJtYWMiOiI2NDQ2M2VhZTRlMjJhYjg0OGY1ODA5MjVjMGRkNzJkMDQzMGQwZWNmNGIzMDBiZjJiYjdhOTljYzUyNTQ5ZTk3In0=");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", format(getFormattedPhone(), "+* (***) ***-****"));
            json.put("email", getEmail());
            json.put("firstName", getRussianName());
            json.put("middleName", getRussianName());
            json.put("lastName", getRussianName());
            json.put("via", "sms");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

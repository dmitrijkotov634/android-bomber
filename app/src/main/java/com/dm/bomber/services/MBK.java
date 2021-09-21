package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public class MBK extends FormService {

    public MBK() {
        setUrl("https://www.mbk.ru/recall-send");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("phone", format(phone, "+7 *** *** ** **"));
        builder.add("name", "Дмитрий");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("x-csrf-token", "wHwdsu3nbvaKUrQlIv584X3TQIMoDdfw49w9O8ha");
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36");
        builder.addHeader("Cookie", "_ym_uid=1632239171836515994; _ym_d=1632239171; tmr_lvid=5e79609d1c2e5c684d07fbb86e45e683; tmr_lvidTS=1632239172549; k50uuid=eab04476-53a0-45e4-82fa-0953bf7a76eb; k50sid=44b5c786-23f4-4590-aa35-444f2fd9aa5f; _ga=GA1.2.954495202.1632239173; _gid=GA1.2.869577097.1632239173; _ym_isad=2; _ym_visorc=w; _fbp=fb.1.1632239173810.349487315; XSRF-TOKEN=eyJpdiI6Ijg4dU9hbnQzb3VnS2Z6Q1NzNmZva0E9PSIsInZhbHVlIjoiRllrbms2cWZDaVJ1SENFajkvWGlZazhPdTdHbHdMU0MvcUJodDhVUExrSFM3ZjQ1ZFdpbjNBMlIwT09DaTBzb2RVYlNtKzZBUEJkb3B6ZWJsQk1iMnEwMVdSck1tSjV3TzNzT01valRYRjZYakxBWFJ3Y2ZIdkk3eUdnRnl0dzYiLCJtYWMiOiIzZTVmZGI0ODA0MzcyYmZlN2Y2NjhhNmFhMjNjZDlmMGUxZGRkZjQyYjAwZGNlMGExM2MyMWM1ZTU4NDZlNTY4In0%3D; mbk_session=eyJpdiI6InZNZGZoT1Byd1Nta1MyTUQ4ZjRvaVE9PSIsInZhbHVlIjoiZHYvR09yVHJ1MjdQUjM1UVVNTHovNTU1cmFhNjJoWUpmUU5oZlNyNWJNbmtSbXNiVktMSHAwRlkvcUtkR3A2N2lsSTZoWDltRGszVEdPVjZJODY0My93R2w0aDlPOHE0MURxbWhXbXZpNmJFNmpQbmZ5WUVjZGVNZkxBbHd5ZlAiLCJtYWMiOiJhZDhjOWY4ZmNkZDA1OTJkMWE2ZTdiMDk4M2QyMzEwNjczZmE3MDcwZDIwMjZlYTU5OWRmNjNjYjFjMjA5Mjc1In0%3D; k50lastvisit=5e66b839b5fe87ac40628689c00d5d639267ca62.595c3cce2409a55c13076f1bac5edee529fc2e58.5e66b839b5fe87ac40628689c00d5d639267ca62.da39a3ee5e6b4b0d3255bfef95601890afd80709.1632239268849; 794791647935278_k50cookie=38807.67085.16322392565208; tmr_detect=0%7C1632239273973; tmr_reqNum=16");

        return super.buildRequest(builder);
    }
}
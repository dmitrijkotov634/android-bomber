package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Eva extends JsonService {

    public Eva() {
        setUrl("https://eva.ua/ua/graphql");
        setMethod(POST);
        setPhoneCode("380");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Cookie", "is_upgrade=true; _gcl_aw=GCL.1635187487.CjwKCAjwq9mLBhB2EiwAuYdMtfZ1wSi-P0-Ew9vc4ydLMv2bHHV5ZEjr-K-vpQV8x_oW0dAGZPgRYBoCMoEQAvD_BwE; _ms=4f46abcd-49cf-40a1-8d0f-79be9a7ecee5; CpaMarketing=gclid; _hjid=c1116263-415f-4996-b4c9-9a02897f70b7; mage-cache-storage=%7B%7D; mage-cache-storage-section-invalidation=%7B%7D; mage-messages=; recently_viewed_product=%7B%7D; recently_viewed_product_previous=%7B%7D; recently_compared_product=%7B%7D; recently_compared_product_previous=%7B%7D; product_data_storage=%7B%7D; PHPSESSID=49kqf2trkop3uofp1f5tp094n7; form_key=993t2w4BoaCQsAsI; store=default_ukr; private_content_version=d38c3435c07e429584cddd23768ca3ee; __cf_bm=2st3uHTHNIGzIqSnXgZPjWeZ3b0IDomBEnHjvzhqdA0-1635278075-0-AfS6cwRr0gCcKe09FklMLr7PnGBb+5TzM1x1Xa+DHqI4Ya8M07B6+4E2XzYd28Kkapi40bMv70YHux6HmCAYTEs=; _ga=GA1.2.2067067131.1635187496; _gid=GA1.2.239782911.1635278081; _ttgclid=CjwKCAjwzt6LBhBeEiwAbPGOgVO6WXIFOsV3rAyZOda3Ls8wBPO9demYb1KeJHUp6P5IQK4Kh6x_ZhoCacQQAvD_BwE; _ttgclid=CjwKCAjwzt6LBhBeEiwAbPGOgVO6WXIFOsV3rAyZOda3Ls8wBPO9demYb1KeJHUp6P5IQK4Kh6x_ZhoCacQQAvD_BwE; _hjAbsoluteSessionInProgress=0; mage-cache-sessid=true; section_data_ids=%7B%22cart%22%3A1635278086%7D; _gac_UA-52541154-6=1.1635278220.CjwKCAjwzt6LBhBeEiwAbPGOgVO6WXIFOsV3rAyZOda3Ls8wBPO9demYb1KeJHUp6P5IQK4Kh6x_ZhoCacQQAvD_BwE; _dc_gtm_UA-52541154-6=1; _gac_UA-52541154-2=1.1635278220.CjwKCAjwzt6LBhBeEiwAbPGOgVO6WXIFOsV3rAyZOda3Ls8wBPO9demYb1KeJHUp6P5IQK4Kh6x_ZhoCacQQAvD_BwE; _dc_gtm_UA-52541154-2=1; _ga_49LQ0CFR5L=GS1.1.1635278077.2.0.1635278219.60");
        builder.addHeader("Referer", "https://eva.ua/ua/?gclid=CjwKCAjwzt6LBhBeEiwAbPGOgVO6WXIFOsV3rAyZOda3Ls8wBPO9demYb1KeJHUp6P5IQK4Kh6x_ZhoCacQQAvD_BwE");
        builder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("operationName", "sendCode");
            json.put("variables", new JSONObject().put("phone", getFormattedPhone()));
            json.put("query", "mutation sendCode($phone: String!) {\n  sendCode(phone: $phone) {\n    id\n    resultCode\n  }\n}\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

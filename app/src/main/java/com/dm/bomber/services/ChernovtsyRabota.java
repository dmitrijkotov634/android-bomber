package com.dm.bomber.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class ChernovtsyRabota extends JsonService {

    public ChernovtsyRabota() {
        setUrl("https://chernovtsy.rabota.ru/api-web/v6/code/send.json");
        setMethod(POST);
        setPhoneCode("380");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Cookie", "frontend:region=chernovtsy%3A1880; frontend:rabota-id:v1=616b0c9f211248003394866342942900; frontend:location:v4=null; tmr_lvid=9baf2ab036dce945b662b83be6d963a2; tmr_lvidTS=1634405541448; _ga=GA1.2.942388139.1634405542; _ym_d=1634405542; _ym_uid=1634405542182425930; user_tags=%5B%7B%22id%22%3A0%2C%22add_date%22%3A%222021-10-16%22%2C%22name%22%3A%22main_page_carees_story_control2%22%7D%2C%7B%22id%22%3A0%2C%22add_date%22%3A%222021-10-16%22%2C%22name%22%3A%22careers_main_page_widget_target%22%7D%2C%7B%22id%22%3A0%2C%22add_date%22%3A%222021-10-16%22%2C%22name%22%3A%22hr_banners_show%22%7D%2C%7B%22id%22%3A0%2C%22add_date%22%3A%222021-10-16%22%2C%22name%22%3A%22hr_login_form_spa%22%7D%2C%7B%22id%22%3A0%2C%22add_date%22%3A%222021-10-16%22%2C%22name%22%3A%22vacancy_split_view_careers_widget_target%22%7D%2C%7B%22id%22%3A0%2C%22add_date%22%3A%222021-10-16%22%2C%22name%22%3A%22courses_widget_control2%22%7D%2C%7B%22id%22%3A0%2C%22add_date%22%3A%222021-10-16%22%2C%22name%22%3A%22usp_company_review_form_target%22%7D%2C%7B%22id%22%3A0%2C%22add_date%22%3A%222021-10-16%22%2C%22name%22%3A%22profession_widget_control2%22%7D%2C%7B%22id%22%3A0%2C%22add_date%22%3A%222021-10-16%22%2C%22name%22%3A%22web_snippetclick2_control1%22%7D%2C%7B%22id%22%3A0%2C%22add_date%22%3A%222021-10-16%22%2C%22name%22%3A%22hr_new_scheduled_action_list_active%22%7D%2C%7B%22id%22%3A0%2C%22add_date%22%3A%222021-10-25%22%2C%22name%22%3A%22main_page_careers_story2_control2%22%7D%5D; mobile-app-popup-next-show-timer=1; mobile-app-popup-close-count=11; tmr_reqNum=16; qrator_ssid=1635339916.326.pKcDG62ujL7G3tOC-f1d0aj2bb2ut8r38h6n8mu2fc5umnsta; story_group_count=4; story_group_time=Wed%20Oct%2027%202021%2016%3A05%3A34%20GMT%2B0300%20(GMT%2B03%3A00); _gid=GA1.2.424888797.1635339936; _gat_gtag_UA_3926701_1=1; SRVID=front2-msk2.rabota|YXlOr|YXlOo; sid=6nEXiaDr2f7eGfJwdbRucfa7mo8lzigp");
        builder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("request", new JSONObject().put("login", "+" + getFormattedPhone()));
            json.put("request_id", "32803401");
            json.put("application_id", "13");
            json.put("rabota_ru_id", "616b0c9f211248003394866342942900");
            json.put("user_tags", new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

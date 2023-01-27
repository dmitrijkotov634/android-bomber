package com.dm.bomber.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkQuery;
import androidx.work.WorkRequest;

import com.dm.bomber.BuildVars;
import com.dm.bomber.R;
import com.dm.bomber.services.DefaultRepository;
import com.dm.bomber.services.MainServices;
import com.dm.bomber.services.core.ServicesRepository;
import com.dm.bomber.services.remote.RemoteRepository;
import com.dm.bomber.worker.AttackWorker;
import com.dm.bomber.worker.DownloadWorker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MainViewModel extends ViewModel {

    private final Repository repository;
    private final WorkManager workManager;

    private final MainServices services = new MainServices();

    private UUID currentWordId;

    private final MutableLiveData<Boolean> proxyEnabled;
    private final MutableLiveData<Boolean> snowfallEnabled;
    private final MutableLiveData<Boolean> hintShown;

    private final MutableLiveData<Progress> progress = new MutableLiveData<>(new Progress(R.drawable.logo, R.string.attack));

    private final MutableLiveData<Boolean> workStatus = new MutableLiveData<>(false);
    private final MutableLiveData<DataSnapshot> updates = new MutableLiveData<>();

    private final MutableLiveData<Integer> servicesCount = new MutableLiveData<>(0);
    private final MutableLiveData<RepositoriesLoadingProgress> repositoriesProgress
            = new MutableLiveData<>(new RepositoriesLoadingProgress(0, 0));

    private String lastCountryCode = BuildVars.COUNTRY_CODES[0];

    private final LiveData<List<WorkInfo>> scheduledAttacks;

    public static final String KEY_PROGRESS = "progress";
    public static final String KEY_MAX_PROGRESS = "max_progress";

    public final static String ATTACK = "attack";
    public final static String UPDATE = "update";

    public static class RepositoriesLoadingProgress {
        private final int currentProgress;
        private final int maxProgress;

        public RepositoriesLoadingProgress(int currentProgress, int maxProgress) {
            this.currentProgress = currentProgress;
            this.maxProgress = maxProgress;
        }

        public int getCurrentProgress() {
            return currentProgress;
        }

        public int getMaxProgress() {
            return maxProgress;
        }
    }

    public static class Progress {
        private final int currentProgress;
        private final int maxProgress;

        private final int iconResource;
        private final int titleResource;

        public Progress(int currentProgress, int maxProgress, int iconResource, int titleResource) {
            this.currentProgress = currentProgress;
            this.maxProgress = maxProgress;

            this.iconResource = iconResource;
            this.titleResource = titleResource;
        }

        public Progress(int iconResource, int titleResource) {
            this(0, 0, iconResource, titleResource);
        }

        public int getCurrentProgress() {
            return currentProgress;
        }

        public int getMaxProgress() {
            return maxProgress;
        }

        public int getIconResource() {
            return iconResource;
        }

        public int getTitleResource() {
            return titleResource;
        }
    }

    public MainViewModel(Repository preferences, WorkManager workManager) {
        this.repository = preferences;
        this.workManager = workManager;

        proxyEnabled = new MutableLiveData<>(repository.isProxyEnabled());
        snowfallEnabled = new MutableLiveData<>(repository.isSnowfallEnabled());
        hintShown = new MutableLiveData<>(repository.isShownHint());

        workManager.getWorkInfosLiveData(
                WorkQuery.Builder.fromStates(Arrays.asList(WorkInfo.State.RUNNING,
                                WorkInfo.State.CANCELLED,
                                WorkInfo.State.SUCCEEDED,
                                WorkInfo.State.FAILED
                        ))
                        .build()).observeForever(workInfos -> {
            for (WorkInfo workInfo : workInfos)
                if (workInfo.getId().equals(currentWordId)) {
                    if (workInfo.getState().isFinished())
                        workStatus.setValue(false);

                    Data data = workInfo.getProgress();

                    for (String tag : workInfo.getTags()) {
                        switch (tag) {
                            case ATTACK:
                                progress.setValue(new Progress(
                                        data.getInt(KEY_PROGRESS, 0),
                                        data.getInt(KEY_MAX_PROGRESS, 0),
                                        R.drawable.logo,
                                        R.string.attack));
                                break;
                            case UPDATE:
                                progress.setValue(new Progress(
                                        data.getInt(KEY_PROGRESS, 0),
                                        data.getInt(KEY_MAX_PROGRESS, 0),
                                        R.drawable.ic_baseline_download_24,
                                        R.string.update));
                                break;
                        }
                    }
                }
        });

        scheduledAttacks = workManager.getWorkInfosLiveData(
                WorkQuery.Builder.fromStates(Arrays.asList(
                                WorkInfo.State.RUNNING,
                                WorkInfo.State.ENQUEUED
                        ))
                        .addTags(Collections.singletonList(ATTACK))
                        .build());

        checkUpdates();

        services.registerOnCollectAllListener((progress, maxProgress) -> {
            repositoriesProgress.postValue(new RepositoriesLoadingProgress(progress, maxProgress));
            selectCountryCode(lastCountryCode);
        });

        collectAll();
    }

    public LiveData<List<WorkInfo>> getScheduledAttacks() {
        return scheduledAttacks;
    }

    public LiveData<DataSnapshot> getUpdates() {
        return updates;
    }

    public LiveData<Integer> getServicesCount() {
        return servicesCount;
    }

    public LiveData<RepositoriesLoadingProgress> getRepositoriesProgress() {
        return repositoriesProgress;
    }

    public void collectAll() {
        new Thread(() -> {
            ArrayList<ServicesRepository> repositories = new ArrayList<>();

            if (!repository.isDefaultDisabled()) repositories.add(new DefaultRepository());
            if (repository.isRemoteServicesEnabled())
                for (String url : repository.getRemoteServicesUrls())
                    repositories.add(new RemoteRepository(new OkHttpClient(), url));

            services.setRepositories(repositories);
            services.collectAll();
        }).start();
    }

    public void selectCountryCode(String countryCode) {
        lastCountryCode = countryCode;
        servicesCount.postValue(services.getServices(countryCode).size());
    }

    public void setProxyEnabled(boolean enabled) {
        repository.setProxyEnabled(enabled);
        proxyEnabled.setValue(enabled);
    }

    public void setSnowfallEnabled(boolean enabled) {
        repository.setSnowfallEnabled(enabled);
        snowfallEnabled.setValue(enabled);
    }

    public void showHint() {
        repository.showHint();
        hintShown.setValue(true);
    }

    public LiveData<Boolean> isProxyEnabled() {
        return proxyEnabled;
    }

    public LiveData<Boolean> isSnowfallEnabled() {
        return snowfallEnabled;
    }

    public LiveData<Boolean> isShownHint() {
        return hintShown;
    }

    public LiveData<Progress> getProgress() {
        return progress;
    }

    public LiveData<Boolean> getWorkStatus() {
        return workStatus;
    }

    public void checkUpdates() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(BuildVars.DATABASE_URL);
        database.getReference("updates2").get().addOnSuccessListener(updates::setValue);
    }

    public void downloadUpdate(String url) {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                .addTag(UPDATE)
                .setInputData(new Data.Builder()
                        .putString(DownloadWorker.URL, url)
                        .build())
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build();

        progress.setValue(new Progress(R.drawable.ic_baseline_download_24, R.string.update));
        pushCurrentWork(workRequest);

        workManager.enqueue(workRequest);
    }

    public void cancelUpdates() {
        updates.setValue(null);
    }

    public void scheduleAttack(String countryCode, String phoneNumber, int repeats, long date, long current) {
        Data inputData = new Data.Builder()
                .putString(AttackWorker.KEY_COUNTRY_CODE, countryCode)
                .putString(AttackWorker.KEY_PHONE, phoneNumber)
                .putInt(AttackWorker.KEY_REPEATS, Math.min(repeats, BuildVars.MAX_REPEATS_COUNT))
                .putBoolean(AttackWorker.KEY_PROXY_ENABLED, repository.isProxyEnabled())
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(AttackWorker.class)
                .addTag(ATTACK)
                .addTag("+" + countryCode + phoneNumber + ";" + date)
                .setInitialDelay(date - current, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build();

        if (current == 0) {
            progress.setValue(new Progress(R.drawable.logo, R.string.attack));
            pushCurrentWork(workRequest);
        }

        workManager.enqueue(workRequest);
    }

    private void pushCurrentWork(WorkRequest request) {
        currentWordId = request.getId();
        workStatus.setValue(true);
    }

    public void startAttack(String countryCode, String phoneNumber, int numberOfCyclesNum) {
        scheduleAttack(countryCode, phoneNumber, numberOfCyclesNum, 0, 0);
    }

    public void cancelCurrentWork() {
        workManager.cancelWorkById(currentWordId);
    }
}

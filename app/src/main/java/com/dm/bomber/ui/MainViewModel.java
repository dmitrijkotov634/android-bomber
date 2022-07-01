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

import com.dm.bomber.workers.AttackWorker;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainViewModel extends ViewModel {

    private final Repository repository;
    private final WorkManager workManager;

    private UUID currentAttackId;

    private final MutableLiveData<Boolean> proxyEnabled;

    private final MutableLiveData<Integer> currentProgress = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> maxProgress = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> attackStatus = new MutableLiveData<>(false);

    public static final String[] countryCodes = {"7", "380", ""};

    public MainViewModel(Repository preferences, WorkManager workManager) {
        this.repository = preferences;
        this.workManager = workManager;

        proxyEnabled = new MutableLiveData<>(repository.isProxyEnabled());

        workManager.getWorkInfosLiveData(
                WorkQuery.Builder.fromStates(Arrays.asList(WorkInfo.State.RUNNING,
                        WorkInfo.State.CANCELLED,
                        WorkInfo.State.SUCCEEDED,
                        WorkInfo.State.FAILED
                )).build()).observeForever(workInfos -> {
            for (WorkInfo workInfo : workInfos)
                if (workInfo.getId().equals(currentAttackId)) {
                    if (workInfo.getState().isFinished())
                        attackStatus.setValue(false);

                    Data data = workInfo.getProgress();

                    currentProgress.setValue(data.getInt(AttackWorker.KEY_PROGRESS, 0));
                    maxProgress.setValue(data.getInt(AttackWorker.KEY_MAX_PROGRESS, 0));
                }
        });
    }

    public void setProxyEnabled(boolean enabled) {
        repository.setProxyEnabled(enabled);
        proxyEnabled.setValue(enabled);
    }

    public LiveData<Boolean> isProxyEnabled() {
        return proxyEnabled;
    }

    public LiveData<Integer> getCurrentProgress() {
        return currentProgress;
    }

    public LiveData<Integer> getMaxProgress() {
        return maxProgress;
    }

    public LiveData<Boolean> getAttackStatus() {
        return attackStatus;
    }

    public void scheduleAttack(int countryCode, String phoneNumber, int repeats, long delay) {
        Data inputData = new Data.Builder()
                .putString(AttackWorker.KEY_COUNTRY_CODE, countryCodes[countryCode])
                .putString(AttackWorker.KEY_PHONE, phoneNumber)
                .putInt(AttackWorker.KEY_REPEATS, Math.min(repeats, 10))
                .putBoolean(AttackWorker.KEY_PROXY_ENABLED, repository.isProxyEnabled())
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(AttackWorker.class)
                .addTag("+" + countryCodes[countryCode] + phoneNumber)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build();

        if (delay == 0) {
            currentAttackId = workRequest.getId();
            attackStatus.setValue(true);
        }

        workManager.enqueue(workRequest);
    }

    public void startAttack(int countryCode, String phoneNumber, int numberOfCyclesNum) {
        scheduleAttack(countryCode, phoneNumber, numberOfCyclesNum, 0);
    }

    public void stopAttack() {
        workManager.cancelWorkById(currentAttackId);
    }
}

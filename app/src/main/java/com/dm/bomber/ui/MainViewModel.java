package com.dm.bomber.ui;

import android.util.Pair;

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

import com.dm.bomber.BuildVars;
import com.dm.bomber.worker.AttackWorker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainViewModel extends ViewModel {

    private final Repository repository;
    private final WorkManager workManager;

    private UUID currentAttackId;

    private final MutableLiveData<Boolean> proxyEnabled;
    private final MutableLiveData<Boolean> snowfallEnabled;

    private final MutableLiveData<Pair<Integer, Integer>> progress = new MutableLiveData<>(new Pair<>(0, 0));
    private final MutableLiveData<Boolean> attackStatus = new MutableLiveData<>(false);
    private final MutableLiveData<DataSnapshot> updates = new MutableLiveData<>();

    private final LiveData<List<WorkInfo>> scheduledAttacks;

    public MainViewModel(Repository preferences, WorkManager workManager) {
        this.repository = preferences;
        this.workManager = workManager;

        proxyEnabled = new MutableLiveData<>(repository.isProxyEnabled());
        snowfallEnabled = new MutableLiveData<>(repository.isSnowfallEnabled());

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

                    progress.setValue(new Pair<>(
                            data.getInt(AttackWorker.KEY_PROGRESS, 0),
                            data.getInt(AttackWorker.KEY_MAX_PROGRESS, 0)));
                }
        });

        scheduledAttacks = workManager.getWorkInfosLiveData(
                WorkQuery.Builder.fromStates(Arrays.asList(
                        WorkInfo.State.RUNNING,
                        WorkInfo.State.ENQUEUED
                )).build());

        checkUpdates();
    }

    public LiveData<List<WorkInfo>> getScheduledAttacks() {
        return scheduledAttacks;
    }

    public LiveData<DataSnapshot> getUpdates() {
        return updates;
    }

    public void setProxyEnabled(boolean enabled) {
        repository.setProxyEnabled(enabled);
        proxyEnabled.setValue(enabled);
    }

    public void setSnowfallEnabled(boolean enabled) {
        repository.setSnowfallEnabled(enabled);
        snowfallEnabled.setValue(enabled);
    }

    public LiveData<Boolean> isProxyEnabled() {
        return proxyEnabled;
    }

    public LiveData<Boolean> isSnowfallEnabled() {
        return snowfallEnabled;

    }

    public LiveData<Pair<Integer, Integer>> getProgress() {
        return progress;
    }

    public LiveData<Boolean> getAttackStatus() {
        return attackStatus;
    }

    public void checkUpdates() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(BuildVars.DATABASE_URL);
        database.getReference("updates").get().addOnSuccessListener(updates::setValue);
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
                .addTag("+" + countryCode + phoneNumber + ";" + date)
                .setInitialDelay(date - current, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build();

        if (current == 0) {
            currentAttackId = workRequest.getId();
            attackStatus.setValue(true);
        }

        workManager.enqueue(workRequest);
    }

    public void startAttack(String countryCode, String phoneNumber, int numberOfCyclesNum) {
        scheduleAttack(countryCode, phoneNumber, numberOfCyclesNum, 0, 0);
    }

    public void stopAttack() {
        workManager.cancelWorkById(currentAttackId);
    }
}

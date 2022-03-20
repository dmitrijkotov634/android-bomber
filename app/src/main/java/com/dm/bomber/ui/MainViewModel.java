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

import com.dm.bomber.workers.AttackWorker;

import java.util.UUID;

public class MainViewModel extends ViewModel {
    private final MainRepository repository;
    private final WorkManager workManager;

    private UUID currentAttackId;

    private final MutableLiveData<Boolean> proxyEnabled;
    private final MutableLiveData<Boolean> promotionShown;

    private final MutableLiveData<Integer> currentProgress = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> maxProgress = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> attackStatus = new MutableLiveData<>(false);

    private static final String ATTACK = "attack";
    public static final String[] countryCodes = {"7", "380", ""};

    public MainViewModel(MainRepository preferences, WorkManager workManager) {
        this.repository = preferences;
        this.workManager = workManager;

        promotionShown = new MutableLiveData<>(repository.getPromotionShown());
        proxyEnabled = new MutableLiveData<>(repository.isProxyEnabled());

        workManager.getWorkInfosByTagLiveData(ATTACK).observeForever(workInfos -> {
            if (workInfos.isEmpty()) {
                return;
            }

            for (WorkInfo workInfo : workInfos)
                if (workInfo.getId().equals(currentAttackId)) {
                    if (workInfo.getState().isFinished()) {
                        attackStatus.setValue(false);
                    }

                    Data data = workInfo.getProgress();

                    currentProgress.setValue(data.getInt(AttackWorker.KEY_PROGRESS, 0));
                    maxProgress.setValue(data.getInt(AttackWorker.KEY_MAX_PROGRESS, 0));
                }
        });
    }

    public void showPromotion() {
        promotionShown.setValue(true);
    }

    public void closePromotion() {
        repository.setPromotionShown(true);
        showPromotion();
    }

    public void setProxyEnabled(boolean enabled) {
        repository.setProxyEnabled(enabled);
        proxyEnabled.setValue(enabled);
    }

    public LiveData<Boolean> isPromotionShown() {
        return promotionShown;
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

    public void startAttack(int countryCode, String phoneNumber, int numberOfCyclesNum) {
        Data inputData = new Data.Builder()
                .putString(AttackWorker.KEY_COUNTRY_CODE, countryCodes[countryCode])
                .putString(AttackWorker.KEY_PHONE, phoneNumber)
                .putInt(AttackWorker.KEY_NUMBER_OF_CYCLES, numberOfCyclesNum)
                .putBoolean(AttackWorker.KEY_PROXY_ENABLED, repository.isProxyEnabled())
                .build();

        OneTimeWorkRequest attack = new OneTimeWorkRequest.Builder(AttackWorker.class)
                .addTag(ATTACK)
                .setInputData(inputData)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build();

        currentAttackId = attack.getId();
        attackStatus.setValue(true);

        workManager.enqueue(attack);
    }

    public void stopAttack() {
        workManager.cancelWorkById(currentAttackId);
    }
}

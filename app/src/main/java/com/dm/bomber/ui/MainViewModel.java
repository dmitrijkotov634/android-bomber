package com.dm.bomber.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dm.bomber.MainRepository;
import com.dm.bomber.bomber.Attack;
import com.dm.bomber.bomber.Callback;

import java.util.ArrayList;

public class MainViewModel extends ViewModel implements Callback {
    private final MainRepository repository;

    private Attack attack;

    private int countryCode;
    private String phoneNumber;

    private MutableLiveData<Boolean> snowfallEnabled;
    private MutableLiveData<Boolean> proxyEnabled;
    private MutableLiveData<Boolean> promotionShown;

    private MutableLiveData<Integer> currentProgress;
    private MutableLiveData<Integer> maxProgress;
    private MutableLiveData<Boolean> attackStatus;

    public static final String[] phoneCodes = {"7", "380", ""};

    public MainViewModel(MainRepository preferences) {
        this.repository = preferences;
    }

    @Override
    public void onAttackEnd() {
        attackStatus.postValue(false);

        repository.setLastPhoneCode(countryCode);
        repository.setLastPhone(phoneNumber);
    }

    @Override
    public void onAttackStart(int serviceCount, int numberOfCycles) {
        attackStatus.postValue(true);

        maxProgress.postValue(serviceCount * numberOfCycles);
        currentProgress.postValue(0);
    }

    @Override
    public void onProgressChange(int progress) {
        currentProgress.postValue(progress);
    }

    public void enableSnowfall() {
        snowfallEnabled.setValue(true);
    }

    public void showPromotion() {
        promotionShown.setValue(true);
    }

    public void closePromotion() {
        repository.setPromotionShown(true);
    }

    public void setProxyEnabled(boolean enabled) {
        repository.setProxyEnabled(enabled);
        proxyEnabled.setValue(enabled);
    }

    public void startAttack(int countryCode, String phoneNumber, int numberOfCyclesNum) {
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;

        attack = new Attack(this, phoneCodes[countryCode], phoneNumber, numberOfCyclesNum,
                repository.isProxyEnabled() ? repository.getProxy() : new ArrayList<>());

        attack.start();
    }

    public Boolean stopAttack() {
        if (attackStatus.getValue() != null && attackStatus.getValue())
            attack.interrupt();

        return attackStatus.getValue();
    }

    public LiveData<Boolean> isSnowfallEnabled() {
        if (snowfallEnabled == null)
            snowfallEnabled = new MutableLiveData<>(false);

        return snowfallEnabled;
    }

    public LiveData<Boolean> isPromotionShown() {
        if (promotionShown == null)
            promotionShown = new MutableLiveData<>(repository.getPromotionShown());

        return promotionShown;
    }

    public LiveData<Boolean> isProxyEnabled() {
        if (proxyEnabled == null)
            proxyEnabled = new MutableLiveData<>(repository.isProxyEnabled());

        return proxyEnabled;
    }

    public LiveData<Integer> getCurrentProgress() {
        if (currentProgress == null)
            currentProgress = new MutableLiveData<>(0);

        return currentProgress;
    }

    public LiveData<Integer> getMaxProgress() {
        if (maxProgress == null)
            maxProgress = new MutableLiveData<>(0);

        return maxProgress;
    }

    public LiveData<Boolean> getAttackStatus() {
        if (attackStatus == null)
            attackStatus = new MutableLiveData<>(false);

        return attackStatus;
    }
}

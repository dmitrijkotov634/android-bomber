package com.dm.bomber.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dm.bomber.MainRepository;

public class MainViewModel extends ViewModel {
    private final MainRepository repository;

    private MutableLiveData<Boolean> snowfallEnabled;
    private MutableLiveData<Boolean> blurEnabled;
    private MutableLiveData<Boolean> promotionShown;

    public MainViewModel(MainRepository preferences) {
        this.repository = preferences;
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

    public void setBlurEnabled(boolean enabled) {
        blurEnabled.setValue(enabled);
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

    public LiveData<Boolean> isBlurEnabled() {
        if (blurEnabled == null)
            blurEnabled = new MutableLiveData<>(false);

        return blurEnabled;
    }
}

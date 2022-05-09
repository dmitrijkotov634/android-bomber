package com.dm.bomber.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.WorkManager;

public class MainModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MainRepository repository;
    private final WorkManager workManager;

    public MainModelFactory(MainRepository preferences, WorkManager workManager) {
        super();
        this.repository = preferences;
        this.workManager = workManager;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == MainViewModel.class)
            return (T) new MainViewModel(repository, workManager);

        throw new IllegalArgumentException();
    }
}

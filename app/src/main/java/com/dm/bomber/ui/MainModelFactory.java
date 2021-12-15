package com.dm.bomber.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.dm.bomber.MainRepository;

public class MainModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MainRepository repository;

    public MainModelFactory(MainRepository preferences) {
        super();
        this.repository = preferences;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == MainViewModel.class) {
            return (T) new MainViewModel(repository);
        }
        throw new IllegalArgumentException();
    }
}

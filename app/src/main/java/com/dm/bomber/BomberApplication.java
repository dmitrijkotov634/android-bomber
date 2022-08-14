package com.dm.bomber;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.work.Configuration;

import com.dm.bomber.ui.MainRepository;
import com.google.android.material.color.DynamicColors;

public class BomberApplication extends Application implements Configuration.Provider {
    @Override
    public void onCreate() {
        AppCompatDelegate.setDefaultNightMode(new MainRepository(getApplicationContext()).getTheme());
        DynamicColors.applyToActivitiesIfAvailable(this);

        super.onCreate();
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(Log.ERROR)
                .build();

    }
}

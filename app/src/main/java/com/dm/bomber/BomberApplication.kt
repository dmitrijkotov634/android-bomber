package com.dm.bomber

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.Configuration
import com.dm.bomber.ui.MainRepository
import com.google.android.material.color.DynamicColors

class BomberApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        AppCompatDelegate.setDefaultNightMode(MainRepository(applicationContext).theme)
        DynamicColors.applyToActivitiesIfAvailable(this)
        super.onCreate()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .build()
    }
}

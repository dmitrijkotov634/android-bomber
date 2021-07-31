package com.dm.bomber;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class AppPreferences {
    private SharedPreferences prefs;

    private static final String DARKMODE = "darkmode";

    public AppPreferences(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setTheme(int mode) {
        prefs.edit().putInt(DARKMODE, mode).apply();
    }

    public int getTheme() {
        return prefs.getInt(DARKMODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }
}

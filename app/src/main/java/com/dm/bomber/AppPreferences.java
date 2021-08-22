package com.dm.bomber;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class AppPreferences {
    private final SharedPreferences prefs;

    private static final String DARKMODE = "darkmode";
    private static final String IGNORE_CODE = "ignore_code";

    public AppPreferences(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setTheme(int mode) {
        prefs.edit().putInt(DARKMODE, mode).apply();
    }

    public int getTheme() {
        return prefs.getInt(DARKMODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void setIgnoreCode(boolean status) {
        prefs.edit().putBoolean(IGNORE_CODE, status).apply();
    }

    public boolean getIgnoreCode() {
        return prefs.getBoolean(IGNORE_CODE, false);
    }
}

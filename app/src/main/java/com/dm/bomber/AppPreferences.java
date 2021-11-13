package com.dm.bomber;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class AppPreferences {
    private final SharedPreferences prefs;

    private static final String DARKMODE = "darkmode";
    private static final String LAST_PHONE = "lastphone";
    private static final String LAST_PHONECODE = "lastphonecode";
    private static final String PROMOTION_SHOWN = "promotionshown";

    public AppPreferences(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setTheme(int mode) {
        prefs.edit().putInt(DARKMODE, mode).apply();
    }

    public int getTheme() {
        return prefs.getInt(DARKMODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void setLastPhone(String phoneNumber) {
        prefs.edit().putString(LAST_PHONE, phoneNumber).apply();
    }

    public String getLastPhone() {
        return prefs.getString(LAST_PHONE, "");
    }

    public void setLastPhoneCode(int phoneCode) {
        prefs.edit().putInt(LAST_PHONECODE, phoneCode).apply();
    }

    public int getLastPhoneCode() {
        return prefs.getInt(LAST_PHONECODE, 0);
    }

    public void setPromotionShown(boolean status) {
        prefs.edit().putBoolean(PROMOTION_SHOWN, status).apply();
    }

    public boolean getPromotionShown() {
        return prefs.getBoolean(PROMOTION_SHOWN, false);
    }
}

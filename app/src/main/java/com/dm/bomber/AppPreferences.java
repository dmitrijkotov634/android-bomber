package com.dm.bomber;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

public class AppPreferences {
    private final SharedPreferences prefs;

    private static final String DARKMODE = "darkmode";
    private static final String LAST_PHONE = "last_phone";
    private static final String LAST_PHONECODE = "last_phonecode";
    private static final String PROMOTION_SHOWN = "promotion_shown";
    private static final String PROXY = "proxy";
    private static final String PROXY_ENABLED = "proxy_enabled";

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

    public void setRawProxy(String proxyStrings) {
        prefs.edit().putString(PROXY, proxyStrings).apply();
    }

    public String getRawProxy() {
        return prefs.getString(PROXY, "");
    }

    public List<Proxy> getProxy() {
        return parseProxy(getRawProxy());
    }

    public List<Proxy> parseProxy(String proxyStrings) {
        if (proxyStrings.isEmpty())
            return new ArrayList<>();

        List<Proxy> proxies = new ArrayList<>();
        for (String proxy : proxyStrings.split("\n")) {
            String[] proxyData = proxy.split(":");
            proxies.add(new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyData[0], Integer.parseInt(proxyData[1]))));
        }
        return proxies;
    }

    public void setProxyEnabled(boolean enabled) {
        prefs.edit().putBoolean(PROXY_ENABLED, enabled).apply();
    }

    public boolean isProxyEnabled() {
        return prefs.getBoolean(PROXY_ENABLED, false);
    }
}

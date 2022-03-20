package com.dm.bomber.ui;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.dm.bomber.workers.AuthProxy;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Credentials;

public class MainRepository {
    private final SharedPreferences preferences;

    private static final String DARKMODE = "darkmode";
    private static final String LAST_PHONE = "last_phone";
    private static final String LAST_COUNTRYCODE = "last_countrycode";
    private static final String PROMOTION_SHOWN = "promotion_shown3";
    private static final String PROXY = "proxy";
    private static final String PROXY_ENABLED = "proxy_enabled";

    public MainRepository(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setTheme(int mode) {
        preferences.edit().putInt(DARKMODE, mode).apply();
    }

    public int getTheme() {
        return preferences.getInt(DARKMODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void setLastPhone(String phoneNumber) {
        preferences.edit().putString(LAST_PHONE, phoneNumber).apply();
    }

    public String getLastPhone() {
        return preferences.getString(LAST_PHONE, "");
    }

    public void setLastCountryCode(int phoneCode) {
        preferences.edit().putInt(LAST_COUNTRYCODE, phoneCode).apply();
    }

    public int getLastCountryCode() {
        return preferences.getInt(LAST_COUNTRYCODE, 0);
    }

    public void setPromotionShown(boolean status) {
        preferences.edit().putBoolean(PROMOTION_SHOWN, status).apply();
    }

    public boolean getPromotionShown() {
        return preferences.getBoolean(PROMOTION_SHOWN, false);
    }

    public void setRawProxy(String proxyStrings) {
        preferences.edit().putString(PROXY, proxyStrings).apply();
    }

    public String getRawProxy() {
        return preferences.getString(PROXY, "");
    }

    public List<AuthProxy> getProxy() {
        return parseProxy(getRawProxy());
    }

    public List<AuthProxy> parseProxy(String proxyStrings) {
        if (proxyStrings.isEmpty())
            return new ArrayList<>();

        List<AuthProxy> proxies = new ArrayList<>();
        for (String proxy : proxyStrings.split("\n")) {
            String credential = null;

            if (proxy.contains(" ")) {
                String[] data = proxy.split(" ", 2);
                String[] loginData = data[1].split(":", 2);

                credential = Credentials.basic(loginData[0], loginData[1]);

                proxy = data[0];
            }

            String[] proxyData = proxy.split(":", 2);
            proxies.add(new AuthProxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyData[0], Integer.parseInt(proxyData[1])), credential));
        }

        return proxies;
    }

    public void setProxyEnabled(boolean enabled) {
        preferences.edit().putBoolean(PROXY_ENABLED, enabled).apply();
    }

    public boolean isProxyEnabled() {
        return preferences.getBoolean(PROXY_ENABLED, false);
    }
}

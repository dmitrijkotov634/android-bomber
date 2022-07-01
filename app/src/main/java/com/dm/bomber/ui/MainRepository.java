package com.dm.bomber.ui;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.dm.bomber.workers.AuthableProxy;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Credentials;

public class MainRepository implements Repository {
    private final SharedPreferences preferences;

    private static final String THEME = "theme";
    private static final String LAST_PHONE = "last_phone";
    private static final String LAST_COUNTRY_CODE = "last_country_code";
    private static final String PROXY = "proxy";
    private static final String PROXY_ENABLED = "proxy_enabled";

    public MainRepository(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void setTheme(int mode) {
        preferences.edit().putInt(THEME, mode).apply();
    }

    @Override
    public int getTheme() {
        return preferences.getInt(THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    @Override
    public void setLastPhone(String phoneNumber) {
        preferences.edit().putString(LAST_PHONE, phoneNumber).apply();
    }

    @Override
    public String getLastPhone() {
        return preferences.getString(LAST_PHONE, "");
    }

    @Override
    public void setLastCountryCode(int phoneCode) {
        preferences.edit().putInt(LAST_COUNTRY_CODE, phoneCode).apply();
    }

    @Override
    public int getLastCountryCode() {
        return preferences.getInt(LAST_COUNTRY_CODE, 0);
    }

    @Override
    public void setRawProxy(String proxyStrings) {
        preferences.edit().putString(PROXY, proxyStrings).apply();
    }

    @Override
    public String getRawProxy() {
        return preferences.getString(PROXY, "");
    }

    @Override
    public List<AuthableProxy> getProxy() {
        return parseProxy(getRawProxy());
    }

    @Override
    public List<AuthableProxy> parseProxy(String proxyStrings) {
        if (proxyStrings.isEmpty())
            return new ArrayList<>();

        List<AuthableProxy> proxies = new ArrayList<>();
        for (String proxy : proxyStrings.split("\n")) {
            String credential = null;

            if (proxy.contains(" ")) {
                String[] data = proxy.split(" ", 2);
                String[] loginData = data[1].split(":", 2);

                credential = Credentials.basic(loginData[0], loginData[1]);

                proxy = data[0];
            }

            String[] proxyData = proxy.split(":", 2);
            proxies.add(new AuthableProxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyData[0], Integer.parseInt(proxyData[1])), credential));
        }

        return proxies;
    }

    @Override
    public void setProxyEnabled(boolean enabled) {
        preferences.edit().putBoolean(PROXY_ENABLED, enabled).apply();
    }

    @Override
    public boolean isProxyEnabled() {
        return preferences.getBoolean(PROXY_ENABLED, false);
    }
}

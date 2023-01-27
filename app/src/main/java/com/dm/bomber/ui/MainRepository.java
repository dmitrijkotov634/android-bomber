package com.dm.bomber.ui;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.dm.bomber.worker.AuthableProxy;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Credentials;

public class MainRepository implements Repository {
    private final SharedPreferences preferences;

    private static final String THEME = "theme";
    private static final String LAST_PHONE = "last_phone";
    private static final String LAST_COUNTRY_CODE = "last_country_code";
    private static final String PROXY = "proxy";
    private static final String PROXY_ENABLED = "proxy_enabled";
    private static final String SNOWFALL_ENABLED = "snowfall_enabled";
    private static final String HINT_SHOWN = "hint_shown";

    private static final String DEFAULT_SERVICES_DISABLED = "default_services_disabled";
    private static final String REMOTE_SERVICES = "remote_services";
    private static final String REMOTE_SERVICES_URL = "remote_services_url";

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

    @Override
    public void setSnowfallEnabled(boolean enabled) {
        preferences.edit().putBoolean(SNOWFALL_ENABLED, enabled).apply();
    }

    @Override
    public boolean isSnowfallEnabled() {
        return preferences.getBoolean(SNOWFALL_ENABLED, false);
    }

    @Override
    public void showHint() {
        preferences.edit().putBoolean(HINT_SHOWN, true).apply();
    }

    @Override
    public boolean isShownHint() {
        return preferences.getBoolean(HINT_SHOWN, false);
    }

    @Override
    public void setDefaultDisabled(boolean disabled) {
        preferences.edit().putBoolean(DEFAULT_SERVICES_DISABLED, disabled).apply();
    }

    @Override
    public boolean isDefaultDisabled() {
        return preferences.getBoolean(DEFAULT_SERVICES_DISABLED, false);
    }

    @Override
    public void setRemoteServicesEnabled(boolean enabled) {
        preferences.edit().putBoolean(REMOTE_SERVICES, enabled).apply();
    }

    @Override
    public void setRemoteServicesUrls(Set<String> urls) {
        preferences.edit().putStringSet(REMOTE_SERVICES_URL, urls).apply();
    }

    @Override
    public Set<String> getRemoteServicesUrls() {
        return preferences.getStringSet(REMOTE_SERVICES_URL, new HashSet<>());
    }

    @Override
    public boolean isRemoteServicesEnabled() {
        return preferences.getBoolean(REMOTE_SERVICES, false);
    }
}

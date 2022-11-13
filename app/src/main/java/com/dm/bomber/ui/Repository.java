package com.dm.bomber.ui;

import com.dm.bomber.worker.AuthableProxy;

import java.util.List;

public interface Repository {
    void setTheme(int mode);

    int getTheme();

    void setLastPhone(String phoneNumber);

    String getLastPhone();

    void setLastCountryCode(int phoneCode);

    int getLastCountryCode();

    void setRawProxy(String proxyStrings);

    String getRawProxy();

    List<AuthableProxy> getProxy();

    List<AuthableProxy> parseProxy(String proxyStrings);

    void setProxyEnabled(boolean enabled);

    boolean isProxyEnabled();

    void setSnowfallEnabled(boolean enabled);

    boolean isSnowfallEnabled();
}

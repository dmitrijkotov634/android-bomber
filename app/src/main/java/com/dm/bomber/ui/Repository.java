package com.dm.bomber.ui;

import com.dm.bomber.worker.AuthableProxy;

import java.util.List;
import java.util.Set;

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

    void showHint();

    boolean isShownHint();

    void setDefaultDisabled(boolean disabled);

    boolean isDefaultDisabled();

    void setRemoteServicesEnabled(boolean enabled);

    void setRemoteServicesUrls(Set<String> urls);

    Set<String> getRemoteServicesUrls();

    boolean isRemoteServicesEnabled();
}

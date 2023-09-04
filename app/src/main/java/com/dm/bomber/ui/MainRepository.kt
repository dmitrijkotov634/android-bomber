package com.dm.bomber.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.dm.bomber.BuildVars.AttackSpeed
import com.dm.bomber.services.core.Service
import com.dm.bomber.services.repository.callCentersServices
import com.dm.bomber.services.repository.collect
import com.dm.bomber.services.repository.services
import com.dm.bomber.worker.AuthProxy
import okhttp3.Credentials.basic
import okhttp3.OkHttpClient
import java.net.InetSocketAddress
import java.net.Proxy

class MainRepository(context: Context?) {
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context!!)

    var theme: Int
        get() = preferences.getInt(THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        set(mode) {
            preferences.edit().putInt(THEME, mode).apply()
        }

    var lastPhone: String?
        get() = preferences.getString(LAST_PHONE, "")
        set(phoneNumber) {
            preferences.edit().putString(LAST_PHONE, phoneNumber).apply()
        }

    var lastCountryCode: Int
        get() = preferences.getInt(LAST_COUNTRY_CODE, 0)
        set(phoneCode) {
            preferences.edit().putInt(LAST_COUNTRY_CODE, phoneCode).apply()
        }

    var rawProxy: String?
        get() = preferences.getString(PROXY, "")
        set(proxyStrings) {
            preferences.edit().putString(PROXY, proxyStrings).apply()
        }

    val proxy: List<AuthProxy>
        get() = parseProxy(rawProxy)

    fun parseProxy(proxyStrings: String?): List<AuthProxy> {
        if (proxyStrings!!.isEmpty()) return ArrayList()

        require(!proxyStrings.startsWith("\n"))

        return mutableListOf<AuthProxy>().apply {
            proxyStrings.split("\n")
                .forEach { proxy ->
                    var proxyString = proxy
                    var credential: String? = null

                    if (proxy.contains(" ")) {
                        val data = proxyString.split(" ", limit = 2)
                        val loginData = data[1].split(":", limit = 2)
                        credential = basic(loginData[0], loginData[1])
                        proxyString = data[0]
                    }

                    proxyString
                        .split(":", limit = 2)
                        .apply {
                            if (size > 1)
                                add(
                                    AuthProxy(
                                        Proxy.Type.HTTP,
                                        InetSocketAddress.createUnresolved(get(0), get(1).toInt()),
                                        credential
                                    )
                                )
                        }
                }
        }
    }

    var isProxyEnabled: Boolean
        get() = preferences.getBoolean(PROXY_ENABLED, false)
        set(enabled) {
            preferences.edit().putBoolean(PROXY_ENABLED, enabled).apply()
        }

    var isSnowfallEnabled: Boolean
        get() = preferences.getBoolean(SNOWFALL_ENABLED, false)
        set(enabled) {
            preferences.edit().putBoolean(SNOWFALL_ENABLED, enabled).apply()
        }

    var remoteServicesUrls: Set<String>?
        get() = preferences.getStringSet(REMOTE_SERVICES_URL, HashSet())
        set(urls) {
            preferences.edit().putStringSet(REMOTE_SERVICES_URL, urls).apply()
        }

    var isRemoteServicesEnabled: Boolean
        get() = preferences.getBoolean(REMOTE_SERVICES, false)
        set(enabled) {
            preferences.edit().putBoolean(REMOTE_SERVICES, enabled).apply()
        }

    var isCallCentersEnabled: Boolean
        get() = preferences.getBoolean(CALL_CENTERS, true)
        set(enabled) {
            preferences.edit().putBoolean(CALL_CENTERS, enabled).apply()
        }

    fun getAllRepositories(client: OkHttpClient): List<() -> List<Service>> = buildList {
        add { services }

        if (isCallCentersEnabled)
            add { callCentersServices }

        if (isRemoteServicesEnabled)
            for (url in remoteServicesUrls!!)
                add { collect(client, url) }
    }

    var attackSpeed: AttackSpeed
        get() = AttackSpeed.entries[preferences.getInt(ATTACK_SPEED, AttackSpeed.SLOW.ordinal)]
        set(attackSpeed) {
            preferences.edit().putInt(ATTACK_SPEED, attackSpeed.ordinal).apply()
        }

    companion object {
        private const val THEME = "theme"
        private const val LAST_PHONE = "last_phone"
        private const val LAST_COUNTRY_CODE = "last_country_code"
        private const val PROXY = "proxy"
        private const val PROXY_ENABLED = "proxy_enabled"
        private const val SNOWFALL_ENABLED = "snowfall_enabled"
        private const val ATTACK_SPEED = "attack_speed"
        private const val REMOTE_SERVICES = "remote_services"
        private const val CALL_CENTERS = "call_centers"
        private const val REMOTE_SERVICES_URL = "remote_services_url"
    }
}

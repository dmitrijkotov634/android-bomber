package com.dm.bomber.ui

import com.dm.bomber.BuildConfig
import kotlinx.serialization.Serializable

@Serializable
data class Updates(
    val important: Boolean = false,
    val allowDirect: Boolean = false,
    val description: Map<String, String> = mapOf(),
    val directUrl: String = "",
    val onlyDirect: Boolean = false,
    val telegramUrl: String = "",
    val versionCode: Int = BuildConfig.VERSION_CODE
)

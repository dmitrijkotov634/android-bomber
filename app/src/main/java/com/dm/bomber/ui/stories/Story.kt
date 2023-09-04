package com.dm.bomber.ui.stories

import com.dm.bomber.BuildConfig
import kotlinx.serialization.Serializable

@Serializable
data class Story(
    val preview: String,
    val pages: List<Page>,
    val minVersionCode: Int = 0,
    val maxVersionCode: Int = BuildConfig.VERSION_CODE
)
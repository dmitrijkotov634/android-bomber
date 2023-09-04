package com.dm.bomber.ui

import com.dm.bomber.ui.stories.Story
import kotlinx.serialization.Serializable

@Serializable
data class CloudStatic(
    val updates: Updates,
    val stories: List<Story>
)
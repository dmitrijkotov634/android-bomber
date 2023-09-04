package com.dm.bomber.ui.stories

import kotlinx.serialization.Serializable

@Serializable
data class Page(
    val image: String,
    val caption: String = "",
    val captionTop: String = "",
    val captionCenter: String = ""
)

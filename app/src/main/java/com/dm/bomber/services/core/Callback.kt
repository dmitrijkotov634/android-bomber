package com.dm.bomber.services.core

import okhttp3.Call
import okhttp3.Callback
import java.io.IOException

fun interface Callback : Callback {
    fun onError(call: Call, e: Exception) {}

    override fun onFailure(call: Call, e: IOException) {
        onError(call, e)
    }
}

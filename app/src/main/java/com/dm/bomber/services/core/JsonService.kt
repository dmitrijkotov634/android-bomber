package com.dm.bomber.services.core

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

abstract class JsonService(
    protected val url: String,
    protected val method: String,
    vararg countryCodes: Int
) : Service(*countryCodes) {

    protected lateinit var request: Request.Builder

    constructor(url: String, vararg countryCodes: Int) : this(url, "POST", *countryCodes)

    override fun run(client: OkHttpClient, callback: Callback, phone: Phone) {
        request = Request.Builder()

        val body: RequestBody = buildJson(phone)!!.toRequestBody("application/json".toMediaType())

        request.url(url)
        request.method(method, body)

        client.newCall(request.build()).enqueue(callback)
    }

    abstract fun buildJson(phone: Phone): String?
}

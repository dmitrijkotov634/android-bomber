package com.dm.bomber.services.core

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

abstract class ParamsService(
    protected val url: String,
    protected val method: String?,
    vararg countryCodes: Int
) : Service(*countryCodes) {

    protected var request: Request.Builder = Request.Builder()

    protected lateinit var builder: HttpUrl.Builder

    constructor(url: String, vararg countryCodes: Int) : this(url, null, *countryCodes)

    override fun run(client: OkHttpClient, callback: Callback, phone: Phone) {
        builder = Objects.requireNonNull<HttpUrl>(url.toHttpUrlOrNull()).newBuilder()

        buildParams(phone)

        request.url(builder.build().toString())

        if (method != null)
            request.method(method, "".toRequestBody(null))

        client.newCall(request.build()).enqueue(callback)
    }

    abstract fun buildParams(phone: Phone)
}

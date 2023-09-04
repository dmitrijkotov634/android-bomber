package com.dm.bomber.services.core

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request

abstract class MultipartService(
    protected val url: String,
    protected val method: String,
    vararg countryCodes: Int
) : Service(*countryCodes) {

    protected var request: Request.Builder = Request.Builder()

    protected lateinit var builder: MultipartBody.Builder

    constructor(url: String, vararg countryCodes: Int) : this(url, "POST", *countryCodes)

    override fun run(client: OkHttpClient, callback: Callback, phone: Phone) {
        builder = MultipartBody.Builder()

        buildBody(phone)

        request.url(url)
        request.method(method, builder.build())

        client.newCall(request.build()).enqueue(callback)
    }

    abstract fun buildBody(phone: Phone)
}

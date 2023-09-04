package com.dm.bomber.services.core

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

abstract class FormService(
    protected val url: String,
    protected val method: String,
    vararg countryCodes: Int
) : Service(*countryCodes) {

    protected var request: Request.Builder = Request.Builder()

    protected lateinit var builder: FormBody.Builder

    constructor(url: String, vararg countryCodes: Int) : this(url, "POST", *countryCodes)

    override fun run(client: OkHttpClient, callback: Callback, phone: Phone) {
        builder = FormBody.Builder()

        buildBody(phone)

        request.url(url)
        request.method(method, builder.build())

        client.newCall(request.build()).enqueue(callback)
    }

    abstract fun buildBody(phone: Phone)
}

package com.dm.bomber.services.dsl

import com.dm.bomber.services.core.Callback
import com.dm.bomber.services.core.Phone
import com.dm.bomber.services.core.Service
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class RequestBuilder(
    val phone: Phone,
    var client: OkHttpClient,
    var callback: Callback,
    private val firstCallback: Callback
) {
    var method: String = GET

    var url: String? = null

    var body: RequestBody? = null
    var headers: Headers = Headers.headersOf()

    fun headers(builder: Headers.Builder.() -> Unit) {
        val headersBuilder = Headers.Builder()
        builder(headersBuilder)
        headers = headersBuilder.build()
    }

    fun url(value: String, body: HttpUrl.Builder.() -> Unit) {
        val builder = value.toHttpUrl().newBuilder()
        body(builder)
        url = builder.toString()
    }

    fun json(value: String) {
        body = value.toRequestBody(APPLICATION_JSON)
    }

    fun formBody(builder: FormBody.Builder.() -> Unit) {
        val formBodyBuilder = FormBody.Builder()
        builder(formBodyBuilder)
        body = formBodyBuilder.build()
    }

    fun multipart(builder: MultipartBody.Builder.() -> Unit) {
        val multipartBodyBuilder = MultipartBody.Builder()
        builder(multipartBodyBuilder)
        body = multipartBodyBuilder.build()
    }

    fun emptyBody() {
        body = "".toRequestBody(null)
    }

    fun multipart(boundary: String, builder: MultipartBody.Builder.() -> Unit) {
        val multipartBodyBuilder = MultipartBody.Builder(boundary)
        multipartBodyBuilder.setType(MultipartBody.FORM)
        builder(multipartBodyBuilder)
        body = multipartBodyBuilder.build()
    }

    fun getJson(body: RequestBuilder.(json: JsonElement) -> Unit) {
        onSuccess { _, response ->
            response.body?.string()?.let {
                body(this, Json.decodeFromString<JsonElement>(it))
            }
        }
    }

    fun onSuccess(body: RequestBuilder.(call: Call, response: Response) -> Unit) {
        callback = Callback { call, response ->
            val requestBuilder = RequestBuilder(
                phone,
                client,
                firstCallback,
                firstCallback
            )

            try {
                body(requestBuilder, call, response)

                client
                    .newCall(requestBuilder.build())
                    .enqueue(requestBuilder.callback)
            } catch (e: Exception) {
                firstCallback.onError(call, e)
            }
        }
    }

    fun build(): Request =
        Request.Builder()
            .url(url!!)
            .method(method, body)
            .headers(headers)
            .build()

    companion object {
        val APPLICATION_JSON = "application/json".toMediaType()

        const val GET = "GET"
        const val POST = "POST"
    }
}

fun service(
    vararg phoneNumbers: Int,
    body: RequestBuilder.() -> Unit
): Service {
    return object : Service(*phoneNumbers) {
        override fun run(client: OkHttpClient, callback: Callback, phone: Phone) {
            val requestBuilder = RequestBuilder(
                phone,
                client,
                callback,
                callback
            )

            body(requestBuilder)

            client
                .newCall(requestBuilder.build())
                .enqueue(requestBuilder.callback)
        }
    }
}
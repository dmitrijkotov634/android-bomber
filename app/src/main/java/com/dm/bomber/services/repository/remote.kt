package com.dm.bomber.services.repository

import com.dm.bomber.services.DefaultFormatter.format
import com.dm.bomber.services.core.Callback
import com.dm.bomber.services.core.Phone
import com.dm.bomber.services.core.Service
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


@Serializable
data class RemoteService(
    @SerialName("phone_codes")
    val phoneCodes: List<Int>,
    val requests: List<RemoteRequest>
)


@Serializable
data class RemoteRequest(
    val method: String,
    val url: String,
    val json: JsonObject? = null,
    val data: HashMap<String, String>? = null,
    val headers: HashMap<String, String>? = null
)


private fun processRemoteService(
    phoneCodes: IntArray,
    request: RemoteRequest
): Service {
    return object : Service(*phoneCodes) {
        override fun run(
            client: OkHttpClient,
            callback: Callback,
            phone: Phone
        ) {
            val body: RequestBody? = when {
                request.json != null -> {
                    format(
                        request.json.toString(),
                        phone
                    ).toRequestBody("application/json".toMediaType())
                }

                request.data != null -> {
                    FormBody.Builder().apply {
                        request.data.forEach { entry ->
                            add(entry.key, format(entry.value, phone))
                        }
                    }.build()
                }

                else -> null
            }

            val headersBuilder = Headers.Builder()

            if (request.headers != null)
                request.headers.map { entry ->
                    headersBuilder.addUnsafeNonAscii(entry.key, format(entry.value, phone))
                }

            client.newCall(
                Request.Builder()
                    .url(format(request.url, phone))
                    .headers(headersBuilder.build())
                    .method(request.method, body)
                    .build()
            )
                .enqueue(callback)
        }
    }
}

private val json = Json {
    ignoreUnknownKeys = true
}

fun collect(
    client: OkHttpClient,
    url: String
): MutableList<Service> = try {
    val response = client.newCall(
        Request.Builder()
            .url(url)
            .get()
            .build()
    ).execute()

    val remoteServices = json.decodeFromString<List<RemoteService>>(response.body!!.string())

    mutableListOf<Service>().apply {
        remoteServices.forEach { remoteService ->
            remoteService.requests.forEach { request ->
                add(
                    processRemoteService(
                        remoteService.phoneCodes.toIntArray(),
                        request
                    )
                )
            }
        }
    }
} catch (e: Exception) {
    e.printStackTrace()
    mutableListOf()
}
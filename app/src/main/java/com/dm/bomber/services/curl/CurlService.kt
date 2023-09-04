package com.dm.bomber.services.curl

import com.dm.bomber.services.DefaultFormatter.format
import com.dm.bomber.services.core.Callback
import com.dm.bomber.services.core.Phone
import com.dm.bomber.services.core.Service
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class CurlService(
    command: String,
    vararg countryCodes: Int
) : Service(*countryCodes) {
    private val args: List<String> = ArgumentTokenizer.tokenize(command)

    private var method = "POST"
    private var mediaType: MediaType? = null

    override fun run(client: OkHttpClient, callback: Callback, phone: Phone) {
        val builder = Request.Builder()

        var i = 0
        while (i < args.size) {
            when (val arg = args[i]) {
                "curl", "\n", "--compressed" -> {}
                "-H" -> {
                    var header = args[++i].split(": ", limit = 2)

                    if (header.size == 1)
                        header = listOf(header[0].substring(0, header[0].length - 2), "")

                    if (header[0].equals("content-type", ignoreCase = true))
                        mediaType = header[1].toMediaType()

                    builder.addHeader(header[0], format(header[1], phone))
                }

                "-X" -> method = args[++i]
                "--data-raw" -> builder.method(
                    method, format(args[++i], phone).toRequestBody(mediaType)
                )

                else -> builder.url(format(arg, phone))
            }
            i++
        }

        client
            .newCall(builder.build())
            .enqueue(callback)
    }
}

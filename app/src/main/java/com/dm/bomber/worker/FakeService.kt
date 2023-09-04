package com.dm.bomber.worker

import com.dm.bomber.services.core.Callback
import com.dm.bomber.services.core.Phone
import com.dm.bomber.services.core.Service
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*

class FakeService : Service() {
    private val fakeServices = arrayOf(
        "https://www.yahoo.com",
        "https://www.amazon.com",
        "https://apis.flowwow.com",
        "https://anketa.rencredit.ru",
        "https://kino.tricolor.tv",
        "https://khesflowers.ru"
    )

    override fun run(client: OkHttpClient, callback: Callback, phone: Phone) {
        client.newCall(
            Request.Builder()
                .url(fakeServices[Random().nextInt(fakeServices.size)])
                .get()
                .build()
        ).enqueue(callback)
    }
}

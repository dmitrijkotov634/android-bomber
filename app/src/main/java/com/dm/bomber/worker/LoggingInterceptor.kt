package com.dm.bomber.worker

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        Log.v(TAG, "Sending request " + request.url)

        val response: Response = chain.proceed(request)
        Log.v(TAG, "Received response for " + response.request.url + " with status code " + response.code)

        return response
    }

    companion object {
        private const val TAG = "Request"
    }
}

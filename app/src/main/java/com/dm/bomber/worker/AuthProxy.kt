package com.dm.bomber.worker

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.net.InetSocketAddress
import java.net.Proxy

class AuthProxy(
    type: Type?,
    sa: InetSocketAddress?,
    private val credential: String?
) : Proxy(type, sa), Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return if (credential != null) response.request.newBuilder()
            .header("Proxy-Authorization", credential)
            .build() else null
    }
}

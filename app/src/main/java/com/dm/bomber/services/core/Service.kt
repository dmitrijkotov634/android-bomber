package com.dm.bomber.services.core

import okhttp3.OkHttpClient
import kotlin.random.Random

abstract class Service(vararg val countryCodes: Int) {

    abstract fun run(client: OkHttpClient, callback: Callback, phone: Phone)

    companion object {
        private fun randomString(min: Char, max: Char, length: Int): String =
            (1..length)
                .map { Random.nextInt((max.code - min.code + 1) + min.code).toChar() }
                .joinToString("")

        val russianName: String
            get() = randomString('а', 'я', 5)

        val userName: String
            get() = randomString('a', 'z', 12)

        val email: String
            get() = userName + "@" + arrayOf("gmail.com", "mail.ru", "yandex.ru")[Random.nextInt(3)]
    }
}

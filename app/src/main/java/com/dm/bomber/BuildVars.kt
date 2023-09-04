package com.dm.bomber

object BuildVars {
    const val TELEGRAM_URL = "https://t.me/androidsmsbomber"
    const val SOURCECODE_URL = "https://github.com/dmitrijkotov634/android-bomber/"
    const val DONATE_URL = "https://smsbomber.page.link/donate"

    const val DATA_SOURCE = "https://gitlab.com/retrocat/bomber-static/-/raw/main/static75.json?ref_type=heads"

    val COUNTRY_CODES = arrayOf("7", "380", "375", "77", "")

    val COUNTRY_FLAGS = intArrayOf(
        R.drawable.ic_ru,
        R.drawable.ic_uk,
        R.drawable.ic_by,
        R.drawable.ic_kz,
        R.drawable.ic_all
    )

    const val PHONE_ANY_LENGTH = 0

    val MAX_PHONE_LENGTH = intArrayOf(10, 9, 9, 9, PHONE_ANY_LENGTH)

    const val SCHEDULED_ATTACKS_LIMIT = 12

    const val MAX_REPEATS_COUNT = 10
    const val REPEATS_MAX_LENGTH = MAX_REPEATS_COUNT.toString().length

    enum class AttackSpeed(val chunkSize: Int) {
        SLOW(1),
        DEFAULT(8),
        FAST(17)
    }
}

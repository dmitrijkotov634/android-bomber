package com.dm.bomber.services.core

class Phone(
    val countryCode: String,
    val phone: String
) {
    fun format(mask: String): String {
        return format(phone, mask)
    }

    override fun toString(): String {
        return countryCode + phone
    }

    companion object {
        fun format(phone: String, mask: String): String {
            val formattedPhone = StringBuilder()
            var index = 0

            for (symbol in mask.toCharArray())
                if (index < phone.length)
                    formattedPhone.append(if (symbol == '*') phone[index++] else symbol)

            return formattedPhone.toString()
        }
    }
}

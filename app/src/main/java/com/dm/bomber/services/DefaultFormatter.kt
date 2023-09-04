package com.dm.bomber.services

import com.dm.bomber.services.core.Phone
import com.dm.bomber.services.core.Phone.Companion.format
import java.util.regex.Pattern

object DefaultFormatter {

    private val phonePattern = Pattern.compile("\\{formatted_phone\\:(.*)\\}")
    private val phonePatternAlt = Pattern.compile("\\{phone\\:([^}]*)\\}")

    fun format(text: String, phone: Phone): String {
        var newString = text

        phonePattern.matcher(text).run {
            if (find()) newString = newString.replace(
                group(), format(phone.toString(), group(1)!!)
            )
        }

        phonePatternAlt.matcher(text).run {
            if (find()) newString = newString.replace(
                group(), format(phone.phone, group(1)!!)
            )
        }

        return newString
            .replace("\\{full_phone\\}".toRegex(), phone.toString())
            .replace("\\{phone\\}".toRegex(), phone.phone)
    }
}


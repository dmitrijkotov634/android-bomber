package com.dm.bomber.services;

import com.dm.bomber.services.core.Phone;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultFormatting {

    private final static Pattern phonePattern = Pattern.compile("\\{formatted_phone\\:(.*)\\}");
    private final static Pattern phonePatternAlt = Pattern.compile("\\{phone\\:([^}]*)\\}");

    public static String format(String text, Phone phone) {
        String newString = text;

        Matcher matcher = phonePattern.matcher(text);
        if (matcher.find())
            newString = newString.replace(matcher.group(),
                    Phone.format(phone.toString(), Objects.requireNonNull(matcher.group(1))));

        Matcher matcherAlt = phonePatternAlt.matcher(text);
        if (matcherAlt.find())
            newString = newString.replace(matcherAlt.group(),
                    Phone.format(phone.getPhone(), Objects.requireNonNull(matcherAlt.group(1))));

        return newString
                .replaceAll("\\{full_phone\\}", phone.toString())
                .replaceAll("\\{phone\\}", phone.getPhone());
    }
}

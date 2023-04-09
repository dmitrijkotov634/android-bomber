package com.dm.bomber.services.core;

import androidx.annotation.NonNull;

public class Phone {

    private final String countryCode;
    private final String phone;

    public Phone(String countryCode, String phone) {
        this.countryCode = countryCode;
        this.phone = phone;
    }

    public static String format(String phone, String mask) {
        StringBuilder formattedPhone = new StringBuilder();

        int index = 0;
        for (char symbol : mask.toCharArray())
            if (index < phone.length())
                formattedPhone.append(symbol == '*' ? phone.charAt(index++) : symbol);

        return formattedPhone.toString();
    }

    public String format(String mask) {
        return format(phone, mask);
    }

    public String getPhone() {
        return phone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    @NonNull
    public String toString() {
        return countryCode + phone;
    }
}

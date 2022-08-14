package com.dm.bomber.services;

import androidx.annotation.NonNull;

public class Phone {

    private final String countryCode;
    private final String phone;

    public Phone(String countryCode, String phone) {
        this.countryCode = countryCode;
        this.phone = phone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getPhone() {
        return phone;
    }

    @NonNull
    public String toString() {
        return countryCode + phone;
    }
}

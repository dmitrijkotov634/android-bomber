package com.dm.bomber.services;

public class FarforSMS extends FarforCall {

    public FarforSMS() {
        setUrl("https://api.farfor.ru/v2/auth/signup/order-code/by-sms/");
        setMethod(POST);
        setPhoneCode("7");
    }
}

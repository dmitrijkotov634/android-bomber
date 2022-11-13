package com.dm.bomber;

public class BuildVars {
    public static final String DATABASE_URL = "https://androidsmsbomber-default-rtdb.europe-west1.firebasedatabase.app";

    public static final String TELEGRAM_URL = "https://t.me/androidsmsbomber";
    public static final String SOURCECODE_URL = "https://github.com/dmitrijkotov634/android-bomber/";
    public static final String DONATE_URL = "https://smsbomber.page.link/donate";

    public static final String[] COUNTRY_CODES = {"7", "380", "375", "77", ""};

    public static final int[] COUNTRY_FLAGS = {
            R.drawable.ic_ru,
            R.drawable.ic_uk,
            R.drawable.ic_by,
            R.drawable.ic_kz,
            R.drawable.ic_all};

    public static final int[] MAX_PHONE_LENGTH = {10, 9, 9, 9, 0};

    public static final int SCHEDULED_ATTACKS_LIMIT = 4;
    public static final int MAX_REPEATS_COUNT = 10;
    public static final int REPEATS_MAX_LENGTH = String.valueOf(MAX_REPEATS_COUNT).length();
}

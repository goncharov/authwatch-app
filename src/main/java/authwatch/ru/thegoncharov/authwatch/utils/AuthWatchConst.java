package ru.thegoncharov.authwatch.utils;

public final class AuthWatchConst {
    public static final String KEY = "ru.thegoncharov.authwatch.AuthWatch";
    public static final String NAME = "AuthWatch";

    public static final String REFRESH_INTERVAL_MS = "authwatch_update_interval_ms";

    public static final String INDICATOR_COLOR = "authwatch_indicator_color_argb";
    public static final String INDICATOR_COLOR_CUSTOM = "authwatch_indicator_color_custom";
    public static final String SHOW_DIVIDERS = "authwatch_show_dividers";
    public static final String ROBOTO_BLACK = "authwatch_roboto_black";
    public static final String VIBRATE = "authwatch_vibrate";
    public static final String CHUNKED_CODE = "authwatch_split";
    public static final String PAGE_INDICATOR = "page_indicator";
//    public static final String ANALYTICS = "google_analytics";

    public static final int IF_NECESSARY = 0;
    public static final int ALWAYS = 1;
    public static final int NEVER = -1;

    public static final String FIRST_START = "authwatch_first_start";
    //public static final long POPUP_INTERVAL = 1000 * 60 * 1; // 10 minutes
    public static final long POPUP_INTERVAL = 1000 * 60 * 60 * 3; // 3 hours
}

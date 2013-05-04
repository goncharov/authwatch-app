package ru.thegoncharov.authwatch.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import ru.thegoncharov.authwatch.R;

import static ru.thegoncharov.authwatch.R.bool.default_roboto_black;
import static ru.thegoncharov.authwatch.R.bool.default_show_dividers;
import static ru.thegoncharov.authwatch.R.bool.default_vibrate;
import static ru.thegoncharov.authwatch.R.string.*;

/**
 * Created with IntelliJ IDEA.
 * User: nata
 * Date: 22.10.12
 * Time: 12:11
 */
public class PrefsHolder implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final SharedPreferences prefs;
    private final Context context;
    private Listener listener;

    private long updateIntervalMs = 1000;

    private String indicatorARGB = "";
    private int indicatorCustom = 0;

    private int split = 0;
    private int pager = 0;
    private boolean showDividers = true;
    private boolean vibrate = true;
    private boolean useRobotoBlack = true;
    private long firstStart;

    public PrefsHolder(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);
        this.context = context;

        onSharedPreferenceChanged(prefs, AuthWatchConst.REFRESH_INTERVAL_MS);
        onSharedPreferenceChanged(prefs, AuthWatchConst.INDICATOR_COLOR);
        onSharedPreferenceChanged(prefs, AuthWatchConst.INDICATOR_COLOR_CUSTOM);
        onSharedPreferenceChanged(prefs, AuthWatchConst.SHOW_DIVIDERS);
        onSharedPreferenceChanged(prefs, AuthWatchConst.VIBRATE);
        onSharedPreferenceChanged(prefs, AuthWatchConst.CHUNKED_CODE);
        onSharedPreferenceChanged(prefs, AuthWatchConst.PAGE_INDICATOR);
//        onSharedPreferenceChanged(prefs, AuthWatchConst.ANALYTICS);
        onSharedPreferenceChanged(prefs, AuthWatchConst.ROBOTO_BLACK);
        firstStart = initFirstStart(prefs);
    }

    public PrefsHolder(Context context, Listener listener) {
        this(context);
        this.listener = listener;
    }

    public SharedPreferences getPreferences() {
        return prefs;
    }

    public void unregister() {
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
        if (key.equals(AuthWatchConst.REFRESH_INTERVAL_MS)) {
            updateIntervalMs = Long.parseLong(prefs.getString(key, "1000"));
        } else if (key.equals(AuthWatchConst.INDICATOR_COLOR)) {
            indicatorARGB = prefs.getString(key,
                    context.getResources().getString(default_indicator_color));
        } else if (key.equals(AuthWatchConst.SHOW_DIVIDERS)) {
            showDividers = prefs.getBoolean(key,
                    context.getResources().getBoolean(default_show_dividers));
        } else if (key.equals(AuthWatchConst.VIBRATE)) {
            vibrate = prefs.getBoolean(key,
                    context.getResources().getBoolean(default_vibrate));
        } else if (key.equals(AuthWatchConst.CHUNKED_CODE)) {
            split = Integer.parseInt(prefs.getString(key,
                    context.getResources().getString(default_chanked_code)));
        } else if (key.equals(AuthWatchConst.ROBOTO_BLACK)) {
            useRobotoBlack = prefs.getBoolean(key,
                    context.getResources().getBoolean(default_roboto_black));
        } else if (key.equals(AuthWatchConst.PAGE_INDICATOR)) {
            pager = initPager2(prefs);
        } else if (key.equals(AuthWatchConst.INDICATOR_COLOR_CUSTOM)) {
            indicatorCustom = prefs.getInt(key, context.getResources().getInteger(R.integer.authwatch_cutom_color_default));
        } else {
            return;
        }
        if (listener != null)
            listener.onPreferenceChanged(key);
    }

    private int initPager2(SharedPreferences prefs) {
        String s = prefs.getString(AuthWatchConst.PAGE_INDICATOR,
                context.getResources().getString(default_page_indicator));
        if (s.equals("pager_only_if_necessary"))
            return AuthWatchConst.IF_NECESSARY;
        else if (s.equals("pager_always"))
            return AuthWatchConst.ALWAYS;
        else if (s.equals("pager_never"))
            return AuthWatchConst.NEVER;
        else
            return AuthWatchConst.IF_NECESSARY;
    }

    public long getUpdateIntervalMs() {
        return updateIntervalMs;
    }

    public int getIndicatorColor() {
        if (indicatorARGB.length() == 9 && indicatorARGB.startsWith("#")) {
            return Color.parseColor(indicatorARGB);
        } else if (indicatorARGB.equals("custom")) {
            return indicatorCustom;
        }
        return Color.parseColor("#ff6b8afc");
    }

    public boolean isShowDividers() {
        return showDividers;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public int getPagerMode() {
        return pager;
    }

    public int getChunkMode() {
        return split;
    }

    public boolean isUseRobotoBlack() {
        return useRobotoBlack;
    }

    private long initFirstStart(SharedPreferences prefs) {
        long firstStart = prefs.getLong(AuthWatchConst.FIRST_START, 0);
        if (firstStart == 0L) setFirstStartTime();
        return firstStart;
    }

    public boolean isFirstStart() {
        return firstStart == 0;
    }

    public void setFirstStartTime() {
        prefs.edit().putLong(AuthWatchConst.FIRST_START, System.currentTimeMillis()).commit();
    }

    public void disposePopup() {
        prefs.edit().putLong(AuthWatchConst.FIRST_START, -1L).commit();
    }

    public boolean isShowPopup() {
        firstStart = initFirstStart(prefs);
        boolean result = (firstStart > 0) && ((System.currentTimeMillis() - firstStart) > AuthWatchConst.POPUP_INTERVAL);
        if (result) disposePopup();
        return result;
    }

    public static interface Listener {
        void onPreferenceChanged(String key);
    }
}

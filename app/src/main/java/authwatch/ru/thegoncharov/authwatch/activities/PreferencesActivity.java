package ru.thegoncharov.authwatch.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import ru.thegoncharov.authwatch.R;
import ru.thegoncharov.authwatch.billing.IabHelper;
import ru.thegoncharov.authwatch.billing.IabResult;
import ru.thegoncharov.authwatch.billing.Inventory;
import ru.thegoncharov.authwatch.billing.Purchase;
import ru.thegoncharov.authwatch.utils.AuthWatchConst;
import ru.thegoncharov.authwatch.utils.PrefsHolder;

import ru.thegoncharov.authwatch.BuildConfig;

import static ru.thegoncharov.authwatch.billing.IabHelper.*;

public class PreferencesActivity extends PreferenceActivity implements PrefsHolder.Listener {
    private PrefsHolder prefs;

    private Preference updateInterval;
    private Preference indicatorColor;
    private Preference indicatorColorCustom;
    private Preference pager;
    private Preference split;

    private Preference donate;
    private boolean alreadyDonated = false;
    private IabHelper helper;
    private static final String BILLING_KEY = BuildConfig.BILLING_KEY;
    private static final String DONATE = "donate";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.authwatch_preferences);
        updateInterval = findPreference(AuthWatchConst.REFRESH_INTERVAL_MS);
        indicatorColor = findPreference(AuthWatchConst.INDICATOR_COLOR);
        indicatorColorCustom = findPreference(AuthWatchConst.INDICATOR_COLOR_CUSTOM);
        pager = findPreference(AuthWatchConst.PAGE_INDICATOR);
        split = findPreference(AuthWatchConst.CHUNKED_CODE);
        donate = findPreference("authwatch_donate");
        findPreference("authwatch_version").setSummary(getVersionSummary());

        prefs = new PrefsHolder(this, this);

        helper = new IabHelper(this, BILLING_KEY);
        helper.startSetup(new OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) return;
                helper.queryInventoryAsync(inventoryListener);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!helper.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prefs.unregister();
        helper.dispose();
        helper = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        summarySplit();
        summaryRefreshInterval();
        summaryPager();
        summaryIndicatorColor();

        refreshIndicatorColorCustom();

        /*if (prefs.isShowPopup()) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.popup_message)
                    .setTitle(R.string.popup_title)
                    .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            try {
                                Intent marketIntent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=" + getPackageName()));
                                marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                                        | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                startActivity(marketIntent);
                            } catch (Exception e) {
                                Toast.makeText(PreferencesActivity.this,
                                        "Unable to open market app", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.popup_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create()
                    .show();
        } */
    }

    @Override
    public void onPreferenceChanged(String key) {
        if (key.equals(AuthWatchConst.PAGE_INDICATOR)) {
            summaryPager();
        } else if (key.equals(AuthWatchConst.INDICATOR_COLOR)) {
            summaryIndicatorColor();
        } else if (key.equals(AuthWatchConst.CHUNKED_CODE)) {
            summarySplit();
        } else if (key.equals(AuthWatchConst.REFRESH_INTERVAL_MS)) {
            summaryRefreshInterval();
        }
    }

    private void updateUi() {
        if (helper == null) return;
        if (alreadyDonated) {
            donate.setEnabled(true);
            donate.setSelectable(false);
            donate.setTitle(R.string.donate_donated);
        } else {
            donate.setEnabled(true);
            donate.setSelectable(true);
            donate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    onDonateClicked();
                    return false;
                }
            });
        }
    }

    QueryInventoryFinishedListener inventoryListener = new QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
            if (!result.isSuccess()) return;
            alreadyDonated = inv.hasPurchase(DONATE);
            PreferencesActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUi();
                }
            });
        }
    };

    OnIabPurchaseFinishedListener purchaseFinishListener = new OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase info) {
            if (!result.isSuccess()) return;
            if (info.getSku().equals(DONATE)) {
                alreadyDonated = true;
                PreferencesActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUi();
                    }
                });
            }
        }
    };

    public void onDonateClicked() {
        helper.launchPurchaseFlow(this, DONATE, 10001, purchaseFinishListener);
    }

    private void summarySplit() {
        int s = prefs.getChunkMode();
        String[] titles = getResources().getStringArray(R.array.chunked_code_titles);
        if (s == 0) {
            split.setSummary(titles[0]);
        } else if (s > 0 && s <= 4) {
            split.setSummary(getString(R.string.chunked_summary, titles[s]));
        }
    }

    private void summaryRefreshInterval() {
        String summary = summaryListPreference(AuthWatchConst.REFRESH_INTERVAL_MS,
                getString(R.string.default_refresh_interval),
                R.array.refresh_interval_titles,
                R.array.refresh_interval_values_ms,
                "");
        updateInterval.setSummary(summary);
    }

    private void summaryPager() {
        String summary = summaryListPreference(AuthWatchConst.PAGE_INDICATOR,
                getString(R.string.default_page_indicator),
                R.array.page_indicator_titles,
                R.array.page_indicator_values,
                "");
        pager.setSummary(summary);
    }

    private void summaryIndicatorColor() {
        String summary = summaryListPreference(AuthWatchConst.INDICATOR_COLOR,
                getString(R.string.default_indicator_color),
                R.array.indicator_color_titles,
                R.array.indicator_color_values,
                "");
        indicatorColor.setSummary(summary);

        refreshIndicatorColorCustom();
    }

    private String summaryListPreference(String key, String defaultValue, int titlesArray,
                                         int valuesArray, String errorSummary) {
        String value = getPreferenceValue(key, defaultValue);
        String[] titles = getResources().getStringArray(titlesArray);
        String[] values = getResources().getStringArray(valuesArray);

        try {
            return findTitleForValue(value, titles, values);
        } catch (Exception e) {
            return errorSummary;
        }
    }

    private String getPreferenceValue(String key, String defaultValue) {
        return prefs.getPreferences().getString(key, defaultValue);
    }

    private String findTitleForValue(String currentValue, String[] titles, String[] values) {
        if (titles.length != values.length)
            throw new IllegalArgumentException();

        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            if (value.equals(currentValue)) {
                return titles[i];
            }
        }
        throw new IllegalArgumentException();
    }

    private String getVersionSummary() {
        try {
            String googleAuthVersion = getString(R.string.google_auth_version);

            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            if (packageInfo == null)
                return googleAuthVersion;

            String v = packageInfo.versionName;
            if (v == null || !v.startsWith(googleAuthVersion))
                return googleAuthVersion;

            String authWatchVersion = v.replace(googleAuthVersion, "0");
            return getString(R.string.version_summary, authWatchVersion, googleAuthVersion);
        } catch (Exception e) {
        }
        return "";
    }

    private boolean refreshIndicatorColorCustom() {
        String color = getPreferenceValue(AuthWatchConst.INDICATOR_COLOR,
                getString(R.string.default_indicator_color));
        return refreshIndicatorColorCustom(color);
    }

    private boolean refreshIndicatorColorCustom(String color) {
        if (color.equals("custom")) {
            indicatorColorCustom.setEnabled(true);
            return true;
        } else {
            indicatorColorCustom.setEnabled(false);
            return false;
        }
    }
}
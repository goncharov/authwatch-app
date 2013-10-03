package ru.thegoncharov.authwatch.utils;

import android.content.Context;
import android.os.PowerManager;
import com.google.android.apps.authenticator.AccountDb;
import com.google.android.apps.authenticator.OtpSource;
import com.google.android.apps.authenticator.OtpSourceException;
import com.google.android.apps.authenticator.TotpCounter;

import java.util.ArrayList;
import java.util.List;

import static android.os.PowerManager.PARTIAL_WAKE_LOCK;
import static ru.thegoncharov.authwatch.utils.AuthWatchConst.KEY;

public class Utils {

    public static PowerManager.WakeLock createWakeLock(Context context) {
        PowerManager powerMan = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerMan == null)
            return null;
        return powerMan.newWakeLock(PARTIAL_WAKE_LOCK, KEY);
    }

    public static String formatOtp(PrefsHolder prefs, String otp) {
        return formatOtp(prefs.getChunkMode(), otp);
    }

    public static String formatOtp(int format, String otp) {
        if (format == 1) {
            return Utils.splitString(otp, ' ', 2);
        } else if (format == 2) {
            return Utils.splitString(otp, '-', 2);
        } else if (format == 3) {
            return Utils.splitString(otp, ' ', 3);
        } else if (format == 4) {
            return Utils.splitString(otp, '-', 3);
        }
        return otp;
    }

    public static String splitString(String source, char delimeter, int chunkLength) {
        StringBuilder result = new StringBuilder(source.length() + source.length() / chunkLength);
        for (int i = 0; i < source.length(); i += chunkLength) {
            if (result.length() > 0) result.append(delimeter);
            result.append(source.substring(i, Math.min(source.length(), i + chunkLength)));
        }
        return result.toString();
    }

    public static void computeHotpSecret(OtpSource source, OtpAccount account) {
        try {
            if (account.type == AccountDb.OtpType.HOTP) {
                account.ota = source.getNextCode(account.account);
            }
        } catch (OtpSourceException e) {
        }
    }

    public static List<OtpAccount> getAllAccounts(AccountDb db, OtpSource source) {
        ArrayList<String> names = new ArrayList<String>();
        db.getNames(names);
        ArrayList<OtpAccount> result = new ArrayList<OtpAccount>();
        for (String name : names) {
            if (db.getType(name) == AccountDb.OtpType.TOTP) {
                OtpAccount acc = new OtpAccount();
                acc.account = name;
                try {
                    acc.ota = source.getNextCode(name);
                } catch (OtpSourceException e) {}
                //acc.ota = db.getSecret(name);
                acc.type = AccountDb.OtpType.TOTP;
                result.add(acc);
            } else {
                OtpAccount acc = new OtpAccount();
                acc.account = name;
                acc.ota = OtpAccount.HOTP_SECRET;
                acc.type = AccountDb.OtpType.HOTP;
                result.add(acc);
            }
        }
        return result;
    }

    public static List<OtpAccount> updateSecrets(OtpSource source, List<OtpAccount> list) {
        if (source == null || list == null) return null;
        for (OtpAccount a : list) {
            if (a != null) {
                if (a.type == AccountDb.OtpType.TOTP) {
                    try {
                        a.ota = source.getNextCode(a.account);
                    } catch (OtpSourceException e) {}
//                    a.ota = db.getSecret(a.account);
                }
            }
        }
        return list;
    }

    public static boolean isRequiredFullRefresh(AccountDb db, List<OtpAccount> currentList) {
        if (db == null || currentList == null) return false;
        ArrayList<String> newList = new ArrayList<String>();
        db.getNames(newList);
        if (newList.size() != currentList.size())
            return true;
        for (OtpAccount c : currentList) {
            if (!newList.contains(c.account))
                return true;
        }
        return false;
    }

    @Deprecated
    public static List<String> getAccountNames(OtpSource otp) {
        List<String> names = new ArrayList<String>();
        otp.enumerateAccounts(names);
        return names;
    }

    public static int calculateTotalPages(int accountsCount, int displayedItemsCount) {
        return (int) Math.ceil(accountsCount / (double) displayedItemsCount);
    }

    public static double calculatePhase(OtpSource otp) {
        TotpCounter counter = otp.getTotpCounter();
        long currentTimeMs = otp.getTotpClock().currentTimeMillis();
        long currentValue = counter.getValueAtTime(currentTimeMs / 1000);
        long nextTimeMs = counter.getValueStartTime(currentValue + 1) * 1000;

        long timeLeftMs = nextTimeMs - currentTimeMs;
        long stepTimeMs = counter.getTimeStep() * 1000;
        return (double) timeLeftMs / (double) stepTimeMs;
    }
}

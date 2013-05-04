package ru.thegoncharov.authwatch.utils;

import com.google.android.apps.authenticator.OtpSource;
import com.google.android.apps.authenticator.TotpCounter;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String formatOtp(PrefsHolder prefs, String otp) {
        int split = prefs.getChunkMode();
        if (split == 1) {
            return Utils.splitString(otp, ' ', 2);
        } else if (split == 2) {
            return Utils.splitString(otp, '-', 2);
        } else if (split == 3) {
            return Utils.splitString(otp, ' ', 3);
        } else if (split == 4) {
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

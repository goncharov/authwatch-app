package ru.thegoncharov.authwatch.utils;

import com.google.android.apps.authenticator.AccountDb;

public class OtpAccount {
    public static final String HOTP_SECRET = "______";

    public String account;
    public String ota;
    public AccountDb.OtpType type;

    public OtpAccount() {
    }

    public OtpAccount(String account, String ota) {
        this.account = account;
        this.ota = ota;
    }

    public OtpAccount(String account, String code, AccountDb.OtpType type) {
        this.account = account;
        this.ota = code;
        this.type = type;
    }

    public boolean isValid() {
        return account != null && ota != null && type != null;
    }

}
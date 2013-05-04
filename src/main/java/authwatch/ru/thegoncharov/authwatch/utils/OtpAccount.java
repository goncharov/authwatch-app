package ru.thegoncharov.authwatch.utils;

public class OtpAccount {
    public String account;
    public String ota;

    public OtpAccount() {
    }

    public OtpAccount(String account, String ota) {
        this.account = account;
        this.ota = ota;
    }
}
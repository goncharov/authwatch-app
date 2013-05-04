package ru.thegoncharov.authwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AuthWatchReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        intent.setClass(context, AuthWatchService.class);
        context.startService(intent);
    }
}

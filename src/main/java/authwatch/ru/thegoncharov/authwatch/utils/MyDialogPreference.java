package ru.thegoncharov.authwatch.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;

public class MyDialogPreference extends DialogPreference {
    public MyDialogPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setNegativeButton(null,null);
    }
}

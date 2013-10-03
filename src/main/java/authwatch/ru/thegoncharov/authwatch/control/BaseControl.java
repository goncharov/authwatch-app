package ru.thegoncharov.authwatch.control;

import android.content.Context;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;

public abstract class BaseControl extends ControlExtension {
    protected final Context context;

    public BaseControl(Context context, String hostAppPackageName) {
        super(context, hostAppPackageName);
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public abstract int getWidth();

    public abstract int getHeight();

}

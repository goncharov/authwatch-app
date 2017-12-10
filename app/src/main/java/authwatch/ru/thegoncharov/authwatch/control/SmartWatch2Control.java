package ru.thegoncharov.authwatch.control;

import android.content.Context;
import android.os.Handler;

import ru.thegoncharov.authwatch.R;

public class SmartWatch2Control extends SmartWatchControl {
    private final int mWidth;
    private final int mHeight;

    public SmartWatch2Control(Context context, String host, Handler handler) {
        super(context, host, handler);

        mHeight = context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_height);
        mWidth = context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_width);
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public int getHeight() {
        return mHeight;
    }

    @Override
    public float getPagerMargins() {
        return 60f;
    }
}

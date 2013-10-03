package ru.thegoncharov.authwatch.control;

import android.content.Context;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import ru.thegoncharov.authwatch.R;

public class SmartWatch2Control extends BaseControl {
    public static final int WIDTH = 220;
    public static final int HEIGHT = 176;

    public SmartWatch2Control(Context context, String hostAppPackageName) {
        super(context, hostAppPackageName);
    }

    @Override
    public void onResume() {
        super.onResume();
        showLayout(R.layout.smartwatch2, null);
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }


}

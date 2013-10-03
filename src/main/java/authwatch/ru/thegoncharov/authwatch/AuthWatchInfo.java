package ru.thegoncharov.authwatch;

import android.content.ContentValues;
import android.content.Context;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;
import ru.thegoncharov.authwatch.activities.PreferencesActivity;
import ru.thegoncharov.authwatch.control.HeadsetControl;
import ru.thegoncharov.authwatch.control.SmartWatch2Control;
import ru.thegoncharov.authwatch.control.SmartWatchControl;
import ru.thegoncharov.authwatch.utils.AuthWatchConst;

import static com.sonyericsson.extras.liveware.aef.registration.Registration.ExtensionColumns.*;

class AuthWatchInfo extends RegistrationInformation {
    private final Context context;

    public AuthWatchInfo(Context context) {
        this.context = context;
    }

    @Override
    public boolean isDisplaySizeSupported(int width, int height) {
        return isSmartWatchDisplay(width, height) ||
               isSmartWatch2Display(width, height) ||
               isSmartHeadsetDisplay(width, height);
    }

    @Override
    public ContentValues getExtensionRegistrationConfiguration() {
        ContentValues v = new ContentValues();
        v.put(NAME, context.getString(R.string.authwatch));
        v.put(EXTENSION_KEY, AuthWatchConst.KEY);
        v.put(EXTENSION_ICON_URI, ExtensionUtils.getUriString(context, R.drawable.ic_watch));
        v.put(EXTENSION_ICON_URI_BLACK_WHITE, ExtensionUtils.getUriString(context, R.drawable.ic_black_white));
        v.put(HOST_APP_ICON_URI, ExtensionUtils.getUriString(context, R.drawable.ic_launcher_authenticator));
        v.put(CONFIGURATION_TEXT, context.getString(R.string.configure_addon));
        v.put(CONFIGURATION_ACTIVITY, PreferencesActivity.class.getName());
        v.put(NOTIFICATION_API_VERSION, getRequiredNotificationApiVersion());
        v.put(PACKAGE_NAME, context.getPackageName());
        return v;
    }

    @Override
    public int getRequiredNotificationApiVersion() {
        return API_NOT_REQUIRED;
    }

    @Override
    public int getRequiredWidgetApiVersion() {
        return API_NOT_REQUIRED;
    }

    @Override
    public int getRequiredControlApiVersion() {
        return 1;
    }

    @Override
    public int getTargetControlApiVersion() {
        return 2;
    }

    @Override
    public int getRequiredSensorApiVersion() {
        return API_NOT_REQUIRED;
    }

    private boolean isSmartWatchDisplay(int width, int height) {
        return (width == SmartWatchControl.WIDTH) &&
               (height == SmartWatchControl.HEIGHT);
    }

    private boolean isSmartHeadsetDisplay(int width, int height) {
        return (width == HeadsetControl.WIDTH) &&
               (height == HeadsetControl.HEIGHT);
    }

    private boolean isSmartWatch2Display(int width, int height) {
        return (width == SmartWatch2Control.WIDTH) &&
               (height == SmartWatch2Control.HEIGHT);
    }
}

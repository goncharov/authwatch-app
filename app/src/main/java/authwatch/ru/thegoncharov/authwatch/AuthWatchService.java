package ru.thegoncharov.authwatch;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.registration.*;
import ru.thegoncharov.authwatch.activities.PreferencesActivity;
import ru.thegoncharov.authwatch.control.HeadsetControl;
import ru.thegoncharov.authwatch.control.SmartWatch2Control;
import ru.thegoncharov.authwatch.control.SmartWatchControl;
import ru.thegoncharov.authwatch.utils.AuthWatchConst;

import static com.sonyericsson.extras.liveware.aef.registration.Registration.ExtensionColumns.*;
import static com.sonyericsson.extras.liveware.aef.registration.Registration.ExtensionColumns.PACKAGE_NAME;

public class AuthWatchService extends ExtensionService {
    public AuthWatchService() {
        super(AuthWatchConst.KEY);
    }

    @Override
    protected RegistrationInformation getRegistrationInformation() {
        return new RegistrationInfo(this);
    }

    @Override
    protected boolean keepRunningWhenConnected() {
        return false;
    }

    @Override
    public ControlExtension createControlExtension(String host) {
        HostApplicationInfo hostApp = RegistrationAdapter.getHostApplication(this, host);
        if (hostApp == null) return null;
        return controlForDisplay(hostApp, host);
    }

    public ControlExtension controlForDisplay(HostApplicationInfo hostApp, String hostPackage) {
        for (DeviceInfo device : hostApp.getDevices()) {
            for (DisplayInfo info : device.getDisplays()) {
                if (info == null) continue;

                boolean isSW2 = DeviceInfoHelper
                        .isSmartWatch2ApiAndScreenDetected(this, hostPackage);
                if (isSW2)
                    return new SmartWatch2Control(this, hostPackage, new Handler());
                else
                if (isSmartWatch(info))
                    return new SmartWatchControl(this, hostPackage, new Handler());
                else
                if (isHeadset(info))
                    return new HeadsetControl(this, hostPackage, new Handler());
            }
        }
        return null;
    }

    private boolean isHeadset(DisplayInfo info) {
        return info.sizeEquals(
                getResources().getDimensionPixelSize(R.dimen.headset_pro_control_width),
                getResources().getDimensionPixelSize(R.dimen.headset_pro_control_height));
    }

    private boolean isSmartWatch(DisplayInfo info) {
        return info.sizeEquals(
                getResources().getDimensionPixelSize(R.dimen.smart_watch_control_width),
                getResources().getDimensionPixelSize(R.dimen.smart_watch_control_height));
    }

    private boolean isSmartWatch2(DisplayInfo info) {
        return info.sizeEquals(
                getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_width),
                getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_width));
    }

    protected class RegistrationInfo extends RegistrationInformation {
        private final Context context;

        public RegistrationInfo(Context context) {
            this.context = context;
        }

        @Override
        public boolean isDisplaySizeSupported(int width, int height) {
            return isDisplayWidthAllowed(width) && isDisplayHeightAllowed(height);
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
        public int getRequiredSensorApiVersion() {
            return API_NOT_REQUIRED;
        }

        private boolean isDisplayWidthAllowed(int width) {
            return (width == getResources().getDimensionPixelSize(R.dimen.smart_watch_control_width)) ||
                   (width == getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_width)) ||
                   (width == getResources().getDimensionPixelSize(R.dimen.headset_pro_control_width));
        }

        private boolean isDisplayHeightAllowed(int height) {
            return (height == getResources().getDimensionPixelSize(R.dimen.smart_watch_control_height)) ||
                   (height == getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_height)) ||
                   (height == getResources().getDimensionPixelSize(R.dimen.headset_pro_control_height));
        }
    }
}

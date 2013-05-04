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
                if (info.sizeEquals(SmartWatchControl.WIDTH, SmartWatchControl.HEIGHT))
                    return new SmartWatchControl(this, hostPackage, new Handler());
                else if (info.sizeEquals(HeadsetControl.WIDTH, HeadsetControl.HEIGHT))
                    return new HeadsetControl(this, hostPackage, new Handler());
            }
        }
        return null;
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
            return (width == SmartWatchControl.WIDTH) ||
                   (width == HeadsetControl.WIDTH);
        }

        private boolean isDisplayHeightAllowed(int height) {
            return (height == SmartWatchControl.HEIGHT) ||
                   (height == HeadsetControl.HEIGHT);
        }
    }
}

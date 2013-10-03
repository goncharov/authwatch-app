package ru.thegoncharov.authwatch;

import android.os.Handler;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.registration.*;
import ru.thegoncharov.authwatch.control.HeadsetControl;
import ru.thegoncharov.authwatch.control.SmartWatch2Control;
import ru.thegoncharov.authwatch.control.SmartWatchControl;
import ru.thegoncharov.authwatch.utils.AuthWatchConst;

public class AuthWatchService extends ExtensionService {
    public AuthWatchService() {
        super(AuthWatchConst.KEY);
    }

    @Override
    protected RegistrationInformation getRegistrationInformation() {
        return new AuthWatchInfo(this);
    }

    @Override
    protected boolean keepRunningWhenConnected() {
        return false;
    }

    @Override
    public ControlExtension createControlExtension(String host) {
        boolean isSW2 = DeviceInfoHelper
                .isSmartWatch2ApiAndScreenDetected(this, host);
        if (isSW2) {
            return new SmartWatch2Control(this, host);
        } else {
            HostApplicationInfo hostApp = RegistrationAdapter
                    .getHostApplication(this, host);
            if (hostApp == null) return null;
            return controlForDisplay(hostApp, host);
        }
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

}

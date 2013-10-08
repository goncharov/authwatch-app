package ru.thegoncharov.authwatch.control;

import android.content.Context;
import android.os.Bundle;
import com.google.android.apps.authenticator.AccountDb;
import com.google.android.apps.authenticator.OtpSource;
import com.google.android.apps.authenticator.testability.DependencyInjector;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlListItem;
import ru.thegoncharov.authwatch.R;
import ru.thegoncharov.authwatch.utils.OtpAccount;
import ru.thegoncharov.authwatch.utils.PrefsHolder;
import ru.thegoncharov.authwatch.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SmartWatch2Control extends BaseControl {
    public static final int WIDTH = 220;
    public static final int HEIGHT = 176;

    protected final OtpSource otp;
    protected final AccountDb db;
    protected PrefsHolder prefs;

    List<OtpAccount> accs;

    public SmartWatch2Control(Context context, String hostAppPackageName) {
        super(context, hostAppPackageName);
        prefs = new PrefsHolder(context);
        otp = DependencyInjector.getOtpProvider();
        db = DependencyInjector.getAccountDb();
    }

    @Override
    public void onResume() {
        super.onResume();

        accs = Utils.getAllAccounts(db, otp);

        showLayout(R.layout.smartwatch2, null);
        sendListCount(R.id.smartwatch2_list, accs.size());

        sendListPosition(R.id.smartwatch2_list, 0);
        for (int i = 0; i < accs.size(); i++) {
            onRequestListItem(R.id.smartwatch2_list, i);

        }
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void onRequestListItem(int ref, int pos) {
        if (ref != -1 && pos != -1 && ref == R.id.smartwatch2_list) {
            sendItem(createControlListItem(ref, pos));
        }
    }

    private void sendItem(ControlListItem item) {
        if (item != null) {
            sendListItem(item);
        }
    }

    private ControlListItem createControlListItem(int ref, int pos) {
        ControlListItem item = new ControlListItem();

        item.layoutReference = ref;
        item.listItemPosition = pos;
        item.dataXmlLayout = R.layout.smartwatch2_item;
        item.listItemId = pos;

        Bundle nameBundle = new Bundle();
        nameBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.smartwatch2_name);
        nameBundle.putString(Control.Intents.EXTRA_TEXT, accs.get(pos).account);

        Bundle pinBundle = new Bundle();
        pinBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.smartwatch2_pin);
        pinBundle.putString(Control.Intents.EXTRA_TEXT, accs.get(pos).ota);

        item.layoutData = new Bundle[] {nameBundle, pinBundle};

        return item;
    }


}

package ru.thegoncharov.authwatch.control;

import android.content.Context;
import android.graphics.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.apps.authenticator.AccountDb;
import com.google.android.apps.authenticator.CountdownIndicator;
import com.google.android.apps.authenticator.OtpSource;
import com.google.android.apps.authenticator.testability.DependencyInjector;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlListItem;
import ru.thegoncharov.authwatch.R;
import ru.thegoncharov.authwatch.utils.OtpAccount;
import ru.thegoncharov.authwatch.utils.PrefsHolder;
import ru.thegoncharov.authwatch.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class SmartWatch2Control extends BaseControl {
    public static final int WIDTH = 220;
    public static final int HEIGHT = 176;

    protected final OtpSource otp;
    protected final AccountDb db;
    protected PrefsHolder prefs;

    private List<OtpAccount> accs;

    private final Paint stroke = new Paint(ANTI_ALIAS_FLAG);
    private final Paint body = new Paint(ANTI_ALIAS_FLAG);

    Bitmap indicator;

    public SmartWatch2Control(Context context, String hostAppPackageName) {
        super(context, hostAppPackageName);
        prefs = new PrefsHolder(context);
        otp = DependencyInjector.getOtpProvider();
        db = DependencyInjector.getAccountDb();

        stroke.setStrokeWidth(1.5f);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setColor(prefs.getIndicatorColor());
        body.setColor(stroke.getColor());
    }

    @Override
    public void onStart() {
        super.onStart();
        accs = Utils.getAllAccounts(db, otp);
        indicator = indicatorForPhase(Utils.calculatePhase(otp), context, body, stroke);
    }

    @Override
    public void onResume() {
        super.onResume();

        showLayout(R.layout.smartwatch2, null);
        sendListCount(R.id.smartwatch2_list, accs.size());

        sendListPosition(R.id.smartwatch2_list, 0);

        /*for (int i = 0; i < accs.size(); i++) {
            onRequestListItem(R.id.smartwatch2_list, i);

        } */
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

        Bundle timeBundle = new Bundle();
        timeBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.smartwatch2_time);
        timeBundle.putByteArray(Control.Intents.EXTRA_DATA, bitmapToByteArray(indicator));

        item.layoutData = new Bundle[] {nameBundle, pinBundle, timeBundle};

        return item;
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null)
            return new byte[0];

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private Bitmap indicatorForPhase(double phase, int width, int height,
                                     Paint body, Paint stroke) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);

        RectF rect = new RectF(1, 1, width - 1f, height - 1f);

        float remainingSweep = (float) (phase * 360);
        float remainingStart = 270 - remainingSweep;

        if (remainingStart < 360) {
            canvas.drawArc(rect, remainingStart, remainingSweep, true, body);
        } else {
            canvas.drawOval(rect, body);
        }
        canvas.drawOval(rect, stroke);
        return bitmap;
    }

    private Bitmap indicatorForPhase(double phase, Context context,
                                     Paint body, Paint stroke) {
        int width = context.getResources().getDimensionPixelSize(R.dimen.authwatch_sw2_indicator_width);
        int height = context.getResources().getDimensionPixelSize(R.dimen.authwatch_sw2_indicator_height);
        return indicatorForPhase(phase, width, height, body, stroke);
    }


    private class Updater implements Runnable {

        @Override
        public void run() {

        }
    }

}

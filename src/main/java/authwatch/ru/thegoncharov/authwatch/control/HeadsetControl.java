package ru.thegoncharov.authwatch.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.text.TextPaint;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.SmartWirelessHeadsetProUtil;
import ru.thegoncharov.authwatch.R;

public class HeadsetControl extends DeviceControl {
    public static final int WIDTH = 128;
    public static final int HEIGHT = 36;

    public HeadsetControl(Context context, String host, Handler handler) {
        super(context, host, handler);
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
    public int getDisplayedItemsCount() {
        return 1;
    }

    @Override
    public int getItemHeight(boolean pagerIsVisible) {
        return HEIGHT;
    }

    @Override
    public Pager createPager() {
        return null;
    }

    @Override
    public AuthWatchItem[] createItemsArray() {
        AuthWatchItem[] items = new AuthWatchItem[getDisplayedItemsCount()];

        for (int i = 0; i < items.length; i++) {
            items[i] = new AuthWatchItem(context);

            items[i].setIndicatorColor(Color.WHITE);
            items[i].getIndicatorStrokePaint().setStrokeWidth(0.5f);

            items[i].getAccountPaint().setTextSize(13f);
            items[i].getAccountPaint().setAntiAlias(false);
            items[i].getAccountPaint().setFakeBoldText(true);

            items[i].getOtaPaint().setTextSize(18f);
            items[i].getOtaPaint().setAntiAlias(false);
            items[i].getOtaPaint().setFakeBoldText(false);

            items[i].setLeft(0);
            items[i].setRight(WIDTH);
            items[i].setTop(0);
            items[i].setBottom(HEIGHT / getDisplayedItemsCount());
        }

        return items;
    }

    @Override
    protected void showNoAccounts() {
        Bitmap bitmap = createBitmap();
        Canvas canvas = new Canvas(bitmap);
        TextPaint paint = SmartWirelessHeadsetProUtil.createTextPaint(context);
        paint.setTextAlign(Paint.Align.CENTER);
        ExtensionUtils.drawText(canvas, context.getString(R.string.empty),
                getWidth() / 2, getHeight() / 2, paint, getWidth());
        showBitmap(bitmap);
    }

    @Override
    public Paint getDividerPaint() {
        return null;
    }

    @Override
    public void onKey(int action, int keyCode, long timeStamp) {
        if (action == Control.Intents.KEY_ACTION_PRESS
                && getAccountsCount() > 0 && !isPaging) {
            if (keyCode == Control.KeyCodes.KEYCODE_NEXT
                    || keyCode == Control.KeyCodes.KEYCODE_PREVIOUS) {
                isPaging = true;
                if (turnThePage(keyCode)) {
                    stopScreenUpdate();
                    startScreenUpdate();
                }
                isPaging = false;
            }
        }
    }
}

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
import ru.thegoncharov.authwatch.views.Passcode;
import ru.thegoncharov.authwatch.views.Pager;

public class HeadsetControl extends AbstractDeviceControl {
    private final int mWidth;
    private final int mHeight;

    public HeadsetControl(Context context, String host, Handler handler) {
        super(context, host, handler);

        mHeight = context.getResources().getDimensionPixelSize(R.dimen.headset_pro_control_height);
        mWidth = context.getResources().getDimensionPixelSize(R.dimen.headset_pro_control_width);
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
    public int getDisplayedItemsCount() {
        return 1;
    }

    @Override
    public int getItemHeight(boolean pagerIsVisible) {
        return mHeight;
    }

    @Override
    public Pager createPager() {
        return null;
    }

    @Override
    public Passcode[] createItemsArray() {
        Passcode[] items = new Passcode[getDisplayedItemsCount()];

        for (int i = 0; i < items.length; i++) {
            items[i] = new Passcode(context);

            items[i].setIndicatorColor(Color.WHITE);
            items[i].getIndicatorStrokePaint().setStrokeWidth(0.5f);

            items[i].getAccountPaint().setTextSize(13f);
            items[i].getAccountPaint().setAntiAlias(false);
            items[i].getAccountPaint().setFakeBoldText(true);

            items[i].getOtaPaint().setTextSize(18f);
            items[i].getOtaPaint().setAntiAlias(false);
            items[i].getOtaPaint().setFakeBoldText(false);

            items[i].layout(0, 0, mWidth, mHeight / getDisplayedItemsCount());
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

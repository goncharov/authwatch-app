package ru.thegoncharov.authwatch.control;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.widget.LinearLayout;

import com.google.android.apps.authenticator.AuthenticatorActivity;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;
import ru.thegoncharov.authwatch.R;
import ru.thegoncharov.authwatch.views.Passcode;
import ru.thegoncharov.authwatch.views.Pager;

public class SmartWatchControl extends AbstractDeviceControl {
    private final int mWidth;
    private final int mHeight;

    private final int mPagerHeight;

    private Paint dividerPaint;

    public SmartWatchControl(Context context, String host, Handler handler) {
        super(context, host, handler);

        mHeight = context.getResources().getDimensionPixelSize(R.dimen.smart_watch_control_height);
        mWidth = context.getResources().getDimensionPixelSize(R.dimen.smart_watch_control_width);
        mPagerHeight = context.getResources().getDimensionPixelSize(R.dimen.authwatch_page_indicator_height);
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
        return 3;
    }

    @Override
    public int getItemHeight(boolean pagerIsVisible) {
        int h = pagerIsVisible ? getHeight() - mPagerHeight : getHeight();
        return h / getDisplayedItemsCount();
    }

    @Override
    public Pager createPager() {
        return new Pager(context, new Rect(0, 0, getWidth(), mPagerHeight), getPagerMargins());
    }

    public float getPagerMargins() {
        return 30f;
    }

    @Override
    public Passcode[] createItemsArray() {
        Passcode[] items = new Passcode[getDisplayedItemsCount()];
        for (int i = 0; i < items.length; i++) {
            items[i] = new Passcode(context);
            items[i].layout(0, 0, getWidth(), getHeight() / getDisplayedItemsCount());
        }
        return items;
    }

    @Override
    protected void showNoAccounts() {
        Bitmap bitmap = createBitmap();
        Canvas canvas = new Canvas(bitmap);
        LinearLayout view = inflateLayout(context, R.layout.authwatch_empty);
        view.draw(canvas);
        showBitmap(bitmap);
    }

    @Override
    public Paint getDividerPaint() {
        if (dividerPaint == null) {
            dividerPaint = new Paint();
            dividerPaint.setColor(Color.parseColor("#ffdad8d4"));
        }
        return dividerPaint;
    }

    @Override
    public void onSwipe(int direction) {
        if (!isPaging && getAccountsCount() > 0) {
            isPaging = true;
            if (turnThePage(direction)) {
                stopScreenUpdate();
                startScreenUpdate();
            }
            isPaging = false;
        }
    }

    @Override
    public void onTouch(ControlTouchEvent event) {
        if (getAccountsCount() <= 0) {
            Intent intent = new Intent(context, AuthenticatorActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            /*
            File file = new File(Environment.getExternalStorageDirectory(), "authwatch.png");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                outputStream.writeTo(fos);
                fos.close();
                Log.i("GS", "screenshot saved in external memory: " + Environment.getExternalStorageDirectory());
            } catch (IOException e) {
                e.printStackTrace();
            }  */
        }
    }
}

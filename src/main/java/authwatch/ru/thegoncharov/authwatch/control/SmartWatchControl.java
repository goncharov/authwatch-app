package ru.thegoncharov.authwatch.control;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.widget.LinearLayout;
import com.google.android.apps.authenticator.AuthenticatorActivity;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;
import ru.thegoncharov.authwatch.R;

public class SmartWatchControl extends DeviceControl {
    public static final int WIDTH = 128;
    public static final int HEIGHT = 128;

    private Paint dividerPaint;

    public SmartWatchControl(Context context, String host, Handler handler) {
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
        return 3;
    }

    @Override
    public int getItemHeight(boolean pagerIsVisible) {
        int h = pagerIsVisible ? HEIGHT - Pager.HEIGHT : HEIGHT;
        return h / getDisplayedItemsCount();
    }

    @Override
    public Pager createPager() {
        return new Pager(context);
    }

    @Override
    public Item[] createItemsArray() {
        Item[] items = new Item[getDisplayedItemsCount()];
        for (int i = 0; i < items.length; i++) {
            items[i] = new Item(context);
            items[i].layout(0, 0, WIDTH, HEIGHT / getDisplayedItemsCount());
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

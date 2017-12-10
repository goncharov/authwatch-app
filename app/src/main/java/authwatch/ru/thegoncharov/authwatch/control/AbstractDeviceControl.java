package ru.thegoncharov.authwatch.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.google.android.apps.authenticator.*;
import com.google.android.apps.authenticator.testability.DependencyInjector;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import ru.thegoncharov.authwatch.R;
import ru.thegoncharov.authwatch.utils.AuthWatchConst;
import ru.thegoncharov.authwatch.utils.OtpAccount;
import ru.thegoncharov.authwatch.utils.PrefsHolder;
import ru.thegoncharov.authwatch.utils.Utils;
import ru.thegoncharov.authwatch.views.Passcode;
import ru.thegoncharov.authwatch.views.Pager;

import java.util.List;

import static android.os.PowerManager.PARTIAL_WAKE_LOCK;
import static com.sonyericsson.extras.liveware.aef.control.Control.Intents.SWIPE_DIRECTION_LEFT;
import static com.sonyericsson.extras.liveware.aef.control.Control.Intents.SWIPE_DIRECTION_RIGHT;
import static ru.thegoncharov.authwatch.utils.AuthWatchConst.KEY;

public abstract class AbstractDeviceControl extends ControlExtension {
    protected final Context context;
    protected final OtpSource otp;
    protected PrefsHolder prefs;

    protected final Handler handler;

    private ScreenUpdater updater;
    private ScreenHelper screen;
    private long updateInterval;
    private Bitmap bitmap;

    private int accountsCount;
    protected boolean isPaging = false;

    protected int page = 0;
    protected int totalPages = 0;

    private PowerManager.WakeLock wakeLock;

    public AbstractDeviceControl(Context context, String host, Handler handler) {
        super(context, host);
        this.context = context;
        this.handler = handler;
        prefs = new PrefsHolder(context);
        otp = DependencyInjector.getOtpProvider();
    }

    @Override
    public void onStart() {
        super.onStart();
        showLoading();
        updateInterval = prefs.getUpdateIntervalMs();
        setScreenState(Control.Intents.SCREEN_STATE_ON);
        if (screen == null)
            screen = new ScreenHelper(this, context, prefs);
    }

    @Override
    public void onResume() {
        wakeLock = createWakeLock(context);
        if (wakeLock != null) wakeLock.acquire();
        startScreenUpdate();
    }

    @Override
    public void onPause() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        stopScreenUpdate();
    }

    @Override
    public void onStop() {
        prefs.unregister();
    }

    protected void startScreenUpdate() {
        if (updater == null && handler != null) {
            updater = new ScreenUpdater();
            handler.post(updater);
        }
    }

    protected void stopScreenUpdate() {
        if (updater != null) {
            handler.removeCallbacks(updater);
            updater = null;
        }
    }

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getDisplayedItemsCount();

    public abstract int getItemHeight(boolean pagerIsVisible);

    public abstract Pager createPager();

    public abstract Passcode[] createItemsArray();

    protected abstract void showNoAccounts();

    public abstract Paint getDividerPaint();

    public int getAccountsCount() {
        return accountsCount;
    }

    public boolean isDrawDivider() {
        return getDividerPaint() != null;
    }

    protected boolean turnThePage(int direction) {
        int prevPage = page;
        if (direction == SWIPE_DIRECTION_LEFT) {
            page++;
        } else if (direction == SWIPE_DIRECTION_RIGHT) {
            page--;
        } else {
            return false;
        }
        if (page < 0)
            page = totalPages - 1;
        else if (page >= totalPages)
            page = 0;
        return true;
    }

    public Bitmap createBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(),
                getHeight(), Bitmap.Config.RGB_565);
        bitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return bitmap;
    }

    protected void showLoading() {
        Bitmap bitmap = createBitmap();
        Canvas canvas = new Canvas(bitmap);
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);
        String loadingText = context.getString(R.string.authwatch_loading_title);
        ExtensionUtils.drawText(canvas, loadingText, getHeight() / 2, getHeight() / 2, paint, getWidth());
        showBitmap(bitmap);
    }

    protected LinearLayout inflateLayout(Context context, int layout) {
        LinearLayout root = new LinearLayout(context);
        root.setLayoutParams(new ViewGroup.LayoutParams(getWidth(), getHeight()));
        LinearLayout view = (LinearLayout) LinearLayout.inflate(context, layout, root);
        view.measure(getWidth(), getHeight());
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        return view;
    }

    protected OtpAccount[] getAccounts(List<String> accounts, int page) {
        int count = accounts.size();

        totalPages = Utils.calculateTotalPages(count, getDisplayedItemsCount());

        OtpAccount[] items = null;

        if (page * getDisplayedItemsCount() < count) {
            int countForCreate = count - (page * getDisplayedItemsCount());
            int itemsCount = Math.min(getDisplayedItemsCount(), countForCreate);
            items = new OtpAccount[itemsCount];
            for (int i = 0; i < itemsCount; i++) {
                try {
                    String acc = accounts.get(page * getDisplayedItemsCount() + i);
                    String code = otp.getNextCode(acc);
                    items[i] = new OtpAccount(acc, code);
                } catch (OtpSourceException e) {
                    Log.e("GS", "OtpSourceException for account " + accounts.get(page * getDisplayedItemsCount() + i));
                }
            }
            return items;
        } else {
            return new OtpAccount[0];
        }
    }

    @Override
    protected void showBitmap(Bitmap bitmap) {
        if (bitmap != null)
            super.showBitmap(bitmap);
    }

    private PowerManager.WakeLock createWakeLock(Context context) {
        PowerManager powerMan = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerMan == null)
            return null;
        return powerMan.newWakeLock(PARTIAL_WAKE_LOCK, KEY);
    }

    private boolean checkPagerVisible(int count) {
        if (prefs.getPagerMode() != AuthWatchConst.NEVER) {
            if (count <= 3 && prefs.getPagerMode() == AuthWatchConst.IF_NECESSARY) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private class ScreenUpdater implements Runnable {
        private double previousPhase = 2d;

        @Override
        public void run() {
            List<String> accounts = Utils.getAccountNames(otp);

            accountsCount = accounts.size();

            if (accountsCount > 0) {
                double phase = Utils.calculatePhase(otp);

                OtpAccount[] items = getAccounts(accounts, page);

                if (previousPhase != 2d && phase > previousPhase && hasVibrator() && prefs.isVibrate()) {
                    startVibrator(100, 0, 1);
                }
                screen.updateItems(items, phase);

                if (checkPagerVisible(accounts.size())) {
                    screen.setPagerValues(page, totalPages);
                    screen.setPagerVisible(true);
                } else {
                    screen.setPagerVisible(false);
                }

                bitmap = screen.toBitmap();
                showBitmap(bitmap);

                previousPhase = phase;
            } else {
                showNoAccounts();
            }

            if (handler != null) {
                handler.removeCallbacks(this);
                handler.postDelayed(this, updateInterval);
            }
        }
    }
}

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

import java.util.List;

import static com.sonyericsson.extras.liveware.aef.control.Control.Intents.SWIPE_DIRECTION_LEFT;
import static com.sonyericsson.extras.liveware.aef.control.Control.Intents.SWIPE_DIRECTION_RIGHT;

public abstract class DeviceControl extends BaseControl {
    protected final OtpSource otp;
    protected final AccountDb db;
    protected PrefsHolder prefs;

    protected final Handler handler;

    private ScreenUpdater2 updater;

    private Screen screen;
    private long updateInterval;
    private Bitmap bitmap;

    private int accountsCount;
    protected boolean isPaging = false;

    private List<OtpAccount> accs;

    protected int page = 0;
    protected int totalPages = 0;

    private PowerManager.WakeLock wakeLock;

    public DeviceControl(Context context, String host, Handler handler) {
        super(context, host);
        this.handler = handler;
        prefs = new PrefsHolder(context);
        otp = DependencyInjector.getOtpProvider();
        db = DependencyInjector.getAccountDb();
    }

    @Override
    public void onStart() {
        super.onStart();
        showLoading();
        updateInterval = prefs.getUpdateIntervalMs();
        setScreenState(Control.Intents.SCREEN_STATE_ON);
        if (screen == null)
            screen = new Screen(this, context);

    }

    @Override
    public void onResume() {
        wakeLock = Utils.createWakeLock(context);
        if (wakeLock != null) wakeLock.acquire();
        accs = Utils.getAllAccounts(db, otp);
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
            updater = new ScreenUpdater2();
            handler.post(updater);
        }
    }

    protected void stopScreenUpdate() {
        if (updater != null) {
            handler.removeCallbacks(updater);
            updater = null;
        }
    }

    public abstract int getDisplayedItemsCount();

    public abstract int getItemHeight(boolean pagerIsVisible);

    public abstract Pager createPager();

    public abstract Item[] createItemsArray();

    protected abstract void showNoAccounts();

    public abstract Paint getDividerPaint();

    public int getAccountsCount() {
        return accs != null ? accs.size() : 0;
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

    @Override
    protected void showBitmap(Bitmap bitmap) {
        if (bitmap != null)
            super.showBitmap(bitmap);
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

    private class ScreenUpdater2 implements Runnable {
        private double previousPhase = 2d;

        @Override
        public void run() {

            if (accs.size() > 0) {
                double phase = Utils.calculatePhase(otp);

                if (previousPhase != 2d && phase > previousPhase && hasVibrator() && prefs.isVibrate()) {
                    startVibrator(100, 0, 1);
                }

                if ((previousPhase != 2d && phase > previousPhase) || Utils.isRequiredFullRefresh(db, accs)) {
                    accs = Utils.updateSecrets(otp, accs);
                }

                int startFrom = page * getDisplayedItemsCount();
                if (startFrom > accs.size()) startFrom = 0;

                totalPages = (int) Math.ceil(accs.size() / getDisplayedItemsCount());

                screen.updateItems(accs, startFrom, phase, prefs);

                if (checkPagerVisible(accs.size())) {
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
     /*
    @Deprecated
    private class ScreenUpdater implements Runnable {
        private double previousPhase = 2d;

        @Override
        public void run() {
            List<String> accounts = Utils.getAccountNames(otp);

            accountsCount = accounts.size();

            if (accountsCount > 0) {
                double phase = Utils.calculatePhase(otp);

                OtpAccount[] items = getAllAccounts(accounts, page);

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
    }   */
}

package ru.thegoncharov.authwatch.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.view.View;
import ru.thegoncharov.authwatch.utils.OtpAccount;
import ru.thegoncharov.authwatch.utils.PrefsHolder;
import ru.thegoncharov.authwatch.utils.Utils;
import ru.thegoncharov.authwatch.views.Passcode;
import ru.thegoncharov.authwatch.views.Pager;

public class ScreenHelper {
    private final AbstractDeviceControl control;

    private final PrefsHolder prefs;
    private final Pager pager;
    private final Passcode[] items;
    private final Typeface roboto;
    private int count;

    public ScreenHelper(AbstractDeviceControl control, Context context, PrefsHolder prefs) {
        this.control = control;
        this.prefs = prefs;
        items = control.createItemsArray();
        roboto = Typeface.createFromAsset(context.getAssets(), "src/main/assets/RobotoBlack.ttf");
        pager = control.createPager();
    }

    public void updateItems(OtpAccount[] newItems, double phase) {
        if (newItems == null) return;
        if (newItems.length <= 0 || newItems.length > control.getDisplayedItemsCount()) return;
        count = newItems.length;
        for (int i = 0; i < newItems.length; i++) {
            items[i].setValues(
                    newItems[i].account,
                    Utils.formatOtp(prefs, newItems[i].ota),
                    phase
            );
            items[i].setIndicatorColor(prefs.getIndicatorColor());
            items[i].setOtaTypeface(roboto);
        }
    }

    public void setPagerVisible(boolean show) {
        if (pager != null)
            pager.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setPagerValues(int current, int total) {
        if (pager != null)
            pager.setValues(current, total);
    }

    public boolean isPagerVisible() {
        return pager != null && pager.getVisibility() == View.VISIBLE;
    }

    public Bitmap toBitmap() {
        if (items == null || count == 0) return null;

        Bitmap bitmap = control.createBitmap();
        Canvas canvas = new Canvas(bitmap);

        if (pager != null && isPagerVisible()) {
            canvas.translate(pager.getScreenX(), pager.getScreenY());
            pager.draw(canvas);
            canvas.translate(0, pager.getPagerHeight());
        }

        for (int i = 0; i < count; i++) {
            items[i].layout(0, 0,
                    control.getWidth(),
                    control.getItemHeight(isPagerVisible()));
            items[i].draw(canvas);
            if (control.isDrawDivider() && prefs.isShowDividers() && i != 0) {
                canvas.drawLine(5, 0, control.getWidth() - 5, 0, control.getDividerPaint());
            }
            canvas.translate(0, items[i].getHeight());
        }

        return bitmap;
    }
}

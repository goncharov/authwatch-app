package ru.thegoncharov.authwatch.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.view.View;
import ru.thegoncharov.authwatch.utils.OtpAccount;
import ru.thegoncharov.authwatch.utils.PrefsHolder;

import java.util.ArrayList;
import java.util.List;

public class Screen {
    private final DeviceControl control;

    private final Pager pager;
    private final List<Item> items;
    private final Typeface roboto;

    private int count;
    private boolean drawDividers;
    private int otpFormat;

    public Screen(DeviceControl control, Context context) {
        this.control = control;
        items = new ArrayList<Item>(3);
        roboto = Typeface.createFromAsset(context.getAssets(), "RobotoBlack.ttf");
        pager = control.createPager();
    }

    public void updateItems(List<OtpAccount> viewItems, int startFrom, double phase, PrefsHolder prefs) {
        if (viewItems == null || viewItems.size() <= 0) return;

        items.clear();

        drawDividers = prefs.isShowDividers();
        otpFormat = prefs.getChunkMode();

        count = viewItems.size();
        if (count > control.getDisplayedItemsCount())
            count = control.getDisplayedItemsCount();

        for (int i = startFrom; i < startFrom + control.getDisplayedItemsCount(); i++) {
            OtpAccount acc = viewItems.get(i);
            if (acc == null) continue;
            Item item = new Item(control.getContext());
            item.setCodeFormat(otpFormat);
            item.setValues(acc, phase);
            item.setIndicatorColor(prefs.getIndicatorColor());
            item.setOtaTypeface(prefs.isUseRobotoBlack() ? roboto : null);
            items.add(item);
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
        if (pager != null)
            return pager.getVisibility() == View.VISIBLE;
        return false;
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

        for (int i = 0; i < items.size(); i++) {
            items.get(i).layout(0, 0,
                    control.getWidth(),
                    control.getItemHeight(isPagerVisible()));
            items.get(i).draw(canvas);
            if (control.isDrawDivider() && drawDividers && i != 0) {
                canvas.drawLine(5, 0, control.getWidth() - 5, 0, control.getDividerPaint());
            }
            canvas.translate(0, items.get(i).getHeight());
        }

        return bitmap;
    }
}

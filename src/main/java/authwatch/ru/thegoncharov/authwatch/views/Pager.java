package ru.thegoncharov.authwatch.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class Pager extends View {
    private TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private int currentPage = -1;
    private int totalPages = -1;

    private final int mWidth;
    private final int mHeight;
    private final float mMargins;

    private static final int X = 0;
    private static final int Y = 0;

    public Pager(Context context, Rect pagerRect, float sideMargins) {
        this(context, null, pagerRect, sideMargins);
    }

    public Pager(Context context, AttributeSet attrs, Rect pagerRect, float sideMargins) {
        super(context, attrs);

        layout(pagerRect.left, pagerRect.top, pagerRect.width(), pagerRect.height());
        mWidth = pagerRect.width();
        mHeight = pagerRect.height();
        mMargins = sideMargins;

        paint.setColor(Color.parseColor("#ffdad8d4"));
        paint.setTextSize(10f);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(true);
        paint.setStrokeWidth(3f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (currentPage >= 0 && totalPages >= 0) {
            String current = Integer.toString(currentPage + 1);
            String total = Integer.toString(totalPages);

            canvas.drawText(current, mMargins / 2, getHeight(), paint);
            canvas.drawText(total, getWidth() - mMargins / 2, getHeight(), paint);

            float lineWidth = getWidth() - mMargins * 2;
            float lineStart = mMargins;
            float lineEnd = ((currentPage + 1) * lineWidth / totalPages) + lineStart;

            canvas.drawLine(lineStart, getHeight() / 2, lineEnd, getHeight() / 2, paint);
        }
    }

    public void setValues(int current, int total) {
        currentPage = current;
        totalPages = total;
    }

    public int getScreenX() {
        return X;
    }

    public int getScreenY() {
        return Y;
    }

    public int getPagerWidth() {
        return mWidth;
    }

    public int getPagerHeight() {
        return mHeight;
    }
}

package ru.thegoncharov.authwatch.control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class Pager extends View {
    private TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private int currentPage = -1;
    private int totalPages = -1;

    public static final int WIDTH = SmartWatchControl.WIDTH;
    public static final int HEIGHT = 8;
    private static final int X = 0;
    private static final int Y = 0;
    private static final float MARGIN = 30f;

    public Pager(Context context) {
        this(context, null);
    }

    public Pager(Context context, AttributeSet attrs) {
        super(context, attrs);
        layout(0, 0, WIDTH, HEIGHT);
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

            canvas.drawText(current, MARGIN / 2, getHeight(), paint);
            canvas.drawText(total, getWidth() - MARGIN / 2, getHeight(), paint);

            float lineWidth = getWidth() - MARGIN * 2;
            float lineStart = MARGIN;
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
        return WIDTH;
    }

    public int getPagerHeight() {
        return HEIGHT;
    }
}

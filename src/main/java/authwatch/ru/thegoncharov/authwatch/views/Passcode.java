package ru.thegoncharov.authwatch.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static ru.thegoncharov.authwatch.R.styleable.*;

public class Passcode extends View {
    private String acc;
    private String ota;
    private double phase;

    private TextPaint accPaint = new TextPaint();
    private TextPaint otaPaint = new TextPaint();

    private float indicatorSize;
    private Paint indicatorStrokePaint = new Paint(ANTI_ALIAS_FLAG);
    private Paint indicatorBodyPaint = new Paint(ANTI_ALIAS_FLAG);

    public Passcode(Context context) {
        this(context, null);
    }

    public Passcode(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, Passcode);

        accPaint.setTextSize(typedArray.getDimensionPixelSize(Passcode_accountSize, 12));
        accPaint.setFakeBoldText(typedArray.getBoolean(Passcode_accountBold, true));
        accPaint.setColor(typedArray.getColor(Passcode_accountColor, Color.parseColor("#ffdad8d4")));
        if (typedArray.getBoolean(Passcode_accountAntiAlias, true))
            accPaint.setFlags(ANTI_ALIAS_FLAG);

        otaPaint.setTextSize(typedArray.getDimensionPixelSize(Passcode_otpSize, 22));
        otaPaint.setFakeBoldText(typedArray.getBoolean(Passcode_otpBold, true));
        otaPaint.setColor(typedArray.getColor(Passcode_otpColor, Color.parseColor("#ffffffff")));
        if (typedArray.getBoolean(Passcode_otaAntiAlias, true)) {
            otaPaint.setFlags(ANTI_ALIAS_FLAG);
        }

        indicatorSize = typedArray.getDimension(Passcode_indicatorSize, 24f);
        indicatorStrokePaint.setStrokeWidth(typedArray.getDimension(Passcode_indicatorStroke, 1.5f));
        indicatorStrokePaint.setStyle(Paint.Style.STROKE);
        indicatorStrokePaint.setColor(typedArray.getColor(Passcode_indicatorColor, Color.parseColor("#ff6b8afc")));
        indicatorBodyPaint.setColor(indicatorStrokePaint.getColor());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (acc == null || ota == null) return;
        float remainingSectorSweepAngle = (float) (phase * 360);
        float remainingSectorStartAngle = 270 - remainingSectorSweepAngle;

        RectF drawingRect = new RectF(getWidth() - indicatorSize - 2,
                getHeight() / 2 - indicatorSize / 2,
                getWidth() - 2,
                getHeight() / 2 + indicatorSize / 2);
        if (remainingSectorStartAngle < 360) {
            canvas.drawArc(drawingRect, remainingSectorStartAngle,
                    remainingSectorSweepAngle, true, indicatorBodyPaint);
        } else {
            canvas.drawOval(drawingRect, indicatorBodyPaint);
        }
        canvas.drawOval(drawingRect, indicatorStrokePaint);

        float y = ((getHeight() / 2) / 2) + 5;
        ExtensionUtils.drawText(canvas, acc, 3f, y, accPaint, getWidth() - (int) indicatorSize - 2);
        ExtensionUtils.drawText(canvas, ota, 3f, (getHeight() / 2) + y, otaPaint, getWidth() - (int) indicatorSize - 2);
    }

    public void setValues(String acc, String ota, double phase) {
        if (phase >= 0 || phase <= 1) {
            this.phase = phase;
        }
        this.acc = acc;
        this.ota = ota;
//        invalidate();
    }

    public void setPhase(double phase) {
        if (phase >= 0 || phase <= 1) {
            this.phase = phase;
            invalidate();
        }
    }

    public void setOtaTypeface(Typeface typeface) {
        otaPaint.setTypeface(typeface);
    }

    public void setIndicatorColor(int color) {
        indicatorStrokePaint.setColor(color);
        indicatorBodyPaint.setColor(indicatorStrokePaint.getColor());
    }

    public void setHeight(int height) {
//        setBottom(height);
        layout(0, 0, 128, height);
//        ViewGroup.LayoutParams params = getLayoutParams();
//        params.height = height;
//        setLayoutParams(params);
    }

    public TextPaint getAccountPaint() {
        return accPaint;
    }

    public TextPaint getOtaPaint() {
        return otaPaint;
    }

    public Paint getIndicatorStrokePaint() {
        return indicatorStrokePaint;
    }

    public Paint getIndicatorBodyPaint() {
        return indicatorBodyPaint;
    }
}



package ru.thegoncharov.authwatch.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import com.google.android.apps.authenticator.AccountDb;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import ru.thegoncharov.authwatch.R;
import ru.thegoncharov.authwatch.utils.OtpAccount;
import ru.thegoncharov.authwatch.utils.Utils;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class Item extends View {
    private OtpAccount account;
    private double phase;
    private int codeFormat;

    private TextPaint accPaint = new TextPaint();
    private TextPaint otaPaint = new TextPaint();

    private float indicatorSize;
    private Paint indicatorStrokePaint = new Paint(ANTI_ALIAS_FLAG);
    private Paint indicatorBodyPaint = new Paint(ANTI_ALIAS_FLAG);
    private Paint indicatorByCounterBodyPaint;

    public Item(Context context) {
        this(context, null);
    }

    public Item(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AuthWatchItem);

        accPaint.setTextSize(a.getDimensionPixelSize(R.styleable.AuthWatchItem_accountSize, 12));
        accPaint.setFakeBoldText(a.getBoolean(R.styleable.AuthWatchItem_accountBold, true));
        accPaint.setColor(a.getColor(R.styleable.AuthWatchItem_accountColor, Color.parseColor("#ffdad8d4")));
        if (a.getBoolean(R.styleable.AuthWatchItem_accountAntiAlias, true))
            accPaint.setFlags(ANTI_ALIAS_FLAG);

        otaPaint.setTextSize(a.getDimensionPixelSize(R.styleable.AuthWatchItem_otpSize, 22));
        otaPaint.setFakeBoldText(a.getBoolean(R.styleable.AuthWatchItem_otpBold, true));
        otaPaint.setColor(a.getColor(R.styleable.AuthWatchItem_otpColor, Color.parseColor("#ffffffff")));
        if (a.getBoolean(R.styleable.AuthWatchItem_otaAntiAlias, true)) {
            otaPaint.setFlags(ANTI_ALIAS_FLAG);
        }

        indicatorSize = a.getDimension(R.styleable.AuthWatchItem_indicatorSize, 24f);
        indicatorStrokePaint.setStrokeWidth(a.getDimension(R.styleable.AuthWatchItem_indicatorStroke, 1.5f));
        indicatorStrokePaint.setStyle(Paint.Style.STROKE);
        indicatorStrokePaint.setColor(a.getColor(R.styleable.AuthWatchItem_indicatorColor, Color.parseColor("#ff6b8afc")));
        indicatorBodyPaint.setColor(indicatorStrokePaint.getColor());

        indicatorByCounterBodyPaint = new Paint(indicatorBodyPaint);
        indicatorByCounterBodyPaint.setAlpha(100);

        a.recycle();

        codeFormat = 0;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (account == null || !account.isValid()) return;

        RectF drawingRect = getIndicatorRect();

        if (account.type == AccountDb.OtpType.TOTP) {
            float remainingSectorSweepAngle = (float) (phase * 360);
            float remainingSectorStartAngle = 270 - remainingSectorSweepAngle;

            if (remainingSectorStartAngle < 360) {
                canvas.drawArc(drawingRect, remainingSectorStartAngle,
                        remainingSectorSweepAngle, true, indicatorBodyPaint);
            } else {
                canvas.drawOval(drawingRect, indicatorBodyPaint);
            }
            canvas.drawOval(drawingRect, indicatorStrokePaint);
        } else {
            canvas.drawOval(drawingRect, indicatorStrokePaint);
            canvas.drawOval(drawingRect, indicatorByCounterBodyPaint);
        }

        float titleY = ((getHeight() / 2) / 2) + 5;
        float codeY = (getHeight() / 2) + titleY;
        int textWidth = getWidth() - (int) indicatorSize - 2;
        String formattedCode = Utils.formatOtp(codeFormat, account.ota);

        ExtensionUtils.drawText(canvas, account.account, 3f, titleY, accPaint, textWidth);
        ExtensionUtils.drawText(canvas, formattedCode, 3f, codeY, otaPaint, textWidth);
    }

    private RectF getIndicatorRect() {
        return new RectF(getWidth() - indicatorSize - 2,
                getHeight() / 2 - indicatorSize / 2,
                getWidth() - 2,
                getHeight() / 2 + indicatorSize / 2);
    }

    public void setCodeFormat(int codeFormat) {
        this.codeFormat = codeFormat;
    }

    public void setValues(OtpAccount account, double phase) {
        if (phase >= 0 || phase <= 1) {
            this.phase = phase;
        }
        this.account = account;
    }

    @Deprecated
    public void setPhase(double phase) {
        if (phase >= 0 || phase <= 1) {
            this.phase = phase;
        }
    }

    public void setOtaTypeface(Typeface typeface) {
        otaPaint.setTypeface(typeface);
    }

    public void setIndicatorColor(int color) {
        indicatorStrokePaint.setColor(color);
        indicatorBodyPaint.setColor(indicatorStrokePaint.getColor());
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



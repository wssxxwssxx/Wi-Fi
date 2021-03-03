package com.example.wifiapplication.radar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class RadarView extends View {
    private Point mCenter;  // Центр радара
    private int mRadius;    // Максимальный радиус окружности радара

    private int mTextSize;    // Размер шрифта
    private int mMarkerSize;  // Размер маркера

    RectF mOuterBox, mInnerBox;  // Границы внешнего и внутреннего колец

    private Paint mCirclePaint;
    private Paint mAxisPaint;
    private Paint mTextPaint;

    // Радар
    private RadialGradient mRadarRadialGradient;
    private int[] mRadarGradientColors;
    private float [] mRadarGradientPositions;
    private Paint mRadarGradientPaint;

    // Стекло
    private RadialGradient mGlassRadialGradient;
    private int[] mGlassGradientColors;
    private float[] mGlassGradientPositions;
    private Paint mGlassPaint;

    private ArrayList<AccessPoint> mAccessPoints = null;

    public RadarView(Context context) {
        super(context);
        init();
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadarView(Context context, AttributeSet ats, int defaultStyle) {
        super(context, ats, defaultStyle);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);
        int dimension = Math.min(measuredWidth, measuredHeight);
        setMeasuredDimension(dimension, dimension);
    }

    private int measure(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.UNSPECIFIED) {
            result = 100;
        } else {
            result = specSize;
        }
        return result;
    }

    public void setData (ArrayList<AccessPoint> accessPoints) {
        this.mAccessPoints = accessPoints;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int px = w / 2;
        int py = h / 2;
        mCenter = new Point(px, py);

        mRadius = Math.min(px, py) - 20; // Отступ от краев канвы
        int ringWidth = mRadius / 20;    // Ширина кольца

        mTextSize = mRadius / 16;
        mMarkerSize = ringWidth / 2;

        mOuterBox = new RectF(mCenter.x - mRadius,
                mCenter.y - mRadius,
                mCenter.x + mRadius,
                mCenter.y + mRadius);

        mInnerBox = new RectF(mCenter.x - mRadius + ringWidth,
                mCenter.y - mRadius + ringWidth,
                mCenter.x + mRadius - ringWidth,
                mCenter.y + mRadius - ringWidth);

        mRadarRadialGradient = new RadialGradient(mCenter.x, mCenter.y, mRadius,
                mRadarGradientColors, mRadarGradientPositions, Shader.TileMode.CLAMP);

        mGlassRadialGradient = new RadialGradient(mCenter.x, mCenter.y, mRadius - ringWidth,
                mGlassGradientColors, mGlassGradientPositions, Shader.TileMode.CLAMP);
    }

    protected void init() {
        setFocusable(true);

        // Окружности
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.BLACK);
        mCirclePaint.setStyle(Paint.Style.STROKE);

        // Сетка
        mAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAxisPaint.setColor(Color.argb(0x60, 0xFF, 0xFF, 0xFF));
        mAxisPaint.setStyle(Paint.Style.STROKE);
        mAxisPaint.setStrokeWidth(1);

        // Текст и маркеры
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setSubpixelText(true);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Радар
        mRadarGradientColors = new int[4];
        mRadarGradientColors[0] = Color.rgb(0xB8, 0xE0, 0xFF);
        mRadarGradientColors[1] = Color.rgb(0xA1, 0xCF, 0xFF);
        mRadarGradientColors[2] = Color.rgb(0x62, 0xAA, 0xFF);
        mRadarGradientColors[3] = Color.BLACK;

        mRadarGradientPositions = new float[4];
        mRadarGradientPositions[0] = 0.0f;
        mRadarGradientPositions[1] = 0.2f;
        mRadarGradientPositions[2] = 0.9f;
        mRadarGradientPositions[3] = 1.0f;

        mRadarGradientPaint = new Paint();

        // Стекло
        int glassColor = 0xF5;
        mGlassGradientColors = new int[5];
        mGlassGradientColors[0] = Color.argb(0, glassColor, glassColor, glassColor);
        mGlassGradientColors[1] = Color.argb(0, glassColor, glassColor, glassColor);
        mGlassGradientColors[2] = Color.argb(50, glassColor, glassColor, glassColor);
        mGlassGradientColors[3] = Color.argb(100, glassColor, glassColor, glassColor);
        mGlassGradientColors[4] = Color.argb(65, glassColor, glassColor, glassColor);

        mGlassGradientPositions = new float[5];
        mGlassGradientPositions[0] = 0.00f;
        mGlassGradientPositions[1] = 0.80f;
        mGlassGradientPositions[2] = 0.90f;
        mGlassGradientPositions[3] = 0.94f;
        mGlassGradientPositions[4] = 1.00f;

        mGlassPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Радиальный градиентный шейдер радара
        mRadarGradientPaint.setShader(mRadarRadialGradient);
        canvas.drawOval(mOuterBox, mRadarGradientPaint);

        // Сетка
        canvas.drawCircle(mCenter.x, mCenter.y, mRadius *0.10f, mAxisPaint);
        canvas.drawCircle(mCenter.x, mCenter.y, mRadius *0.40f, mAxisPaint);
        canvas.drawCircle(mCenter.x, mCenter.y, mRadius *0.70f, mAxisPaint);

        canvas.save();
        canvas.rotate(15, mCenter.x, mCenter.y);
        for (int i = 0; i < 12; i++) {
            canvas.drawLine(mCenter.x, mCenter.y, mCenter.x + mRadius * 0.75f, mCenter.y, mAxisPaint);
            canvas.rotate(30, mCenter.x, mCenter.y);
        }
        canvas.restore();

        // Данные
        drawData(canvas);

        // Шейдер стекла
        mGlassPaint.setShader(mGlassRadialGradient);
        canvas.drawOval(mInnerBox, mGlassPaint);

        // Внешняя окружность кольца
        mCirclePaint.setStrokeWidth(1);
        canvas.drawOval(mOuterBox, mCirclePaint);

        // Внутренняя окружность кольца
        mCirclePaint.setStrokeWidth(2);
        canvas.drawOval(mInnerBox, mCirclePaint);
    }

    private void drawData(Canvas canvas){
        if (mAccessPoints == null) return;

        mTextPaint.setTextSize(mTextSize);

        float zoom = mRadius *0.75f / RadarActivity.maxSignalLevel;

        for (AccessPoint AP : mAccessPoints) {

            int channel = AP.getChannel();
            if (channel < 1 || channel > 12) continue;

            int level = AP.getLevel();
            int security = AP.getSecurity();
            boolean wps = AP.getWPS();
            String ssid = AP.getSSID();

            float alpha = 30 * channel;

            float dx = (float) ((RadarActivity.maxSignalLevel - level) * Math.cos(alpha * (float)Math.PI / 180));
            float dy = (float) ((RadarActivity.maxSignalLevel - level) * Math.sin(alpha * (float)Math.PI / 180));

            float x = mCenter.x + dx * zoom;
            float y = mCenter.y - dy * zoom;

            int transparentValue = level * 200 / RadarActivity.maxSignalLevel + 55;

            // Открытая сеть или WEP
            if (security == 0 || security == 1) {
                mTextPaint.setColor(Color.argb(transparentValue, 0x00, 0x60, 0x00));
                canvas.drawText(ssid, x + mMarkerSize, y - mMarkerSize, mTextPaint);
            }
            else
                // WPA или WPA2, но с поддержкой WPS
                if (security == 2 && wps) {
                    mTextPaint.setColor(Color.argb(transparentValue, 0x00, 0x00, 0x80));
                    canvas.drawText(ssid, x + mMarkerSize, y - mMarkerSize, mTextPaint);
                }
                else
                    // WPA или WPA2 без с поддержки WPS
                    mTextPaint.setColor(Color.argb(transparentValue, 0xFF, 0xFF, 0xFF));

            canvas.drawCircle(x, y, mMarkerSize, mTextPaint);
        }
    }
}

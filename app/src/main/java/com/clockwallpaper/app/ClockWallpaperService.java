package com.clockwallpaper.app;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClockWallpaperService extends WallpaperService {

    private static final String PREFS_NAME = "ClockPrefs";
    private static final String KEY_STYLE = "clock_style";

    @Override
    public Engine onCreateEngine() {
        return new ClockEngine();
    }

    private class ClockEngine extends Engine {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private boolean visible = false;
        private int width = 0;
        private int height = 0;

        private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint cardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint accentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint timePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint secPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint datePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
        private final SimpleDateFormat secAmPmFormat = new SimpleDateFormat(":ss a", Locale.getDefault());
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault());

        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
                if (visible) {
                    handler.postDelayed(this, 1000);
                }
            }
        };

        ClockEngine() {
            cardPaint.setStyle(Paint.Style.FILL);
            borderPaint.setStyle(Paint.Style.STROKE);
            accentPaint.setStyle(Paint.Style.STROKE);

            timePaint.setTextAlign(Paint.Align.CENTER);
            secPaint.setTextAlign(Paint.Align.CENTER);
            datePaint.setTextAlign(Paint.Align.CENTER);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            this.width = width;
            this.height = height;

            timePaint.setTextSize(width * 0.19f);
            secPaint.setTextSize(width * 0.08f);
            datePaint.setTextSize(width * 0.046f);

            draw();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.removeCallbacks(drawRunner);
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            visible = false;
            handler.removeCallbacks(drawRunner);
        }

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null && width > 0 && height > 0) {
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    int style = prefs.getInt(KEY_STYLE, 0);

                    Date now = new Date();
                    String timeStr = timeFormat.format(now);
                    String secStr = secAmPmFormat.format(now).toUpperCase(Locale.getDefault());
                    String dateStr = dateFormat.format(now);

                    float centerX = width / 2f;
                    float centerY = height * 0.42f;
                    float cardWidth = width * 0.84f;
                    float cardHeight = height * 0.24f;

                    RectF cardRect = new RectF(
                            centerX - cardWidth / 2f,
                            centerY - cardHeight / 2f,
                            centerX + cardWidth / 2f,
                            centerY + cardHeight / 2f
                    );

                    // Reset default typography
                    timePaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
                    secPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
                    datePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
                    timePaint.clearShadowLayer();
                    borderPaint.setStrokeWidth(4f);

                    switch (style) {
                        case 1: // 2. Neon Cyber (Cyan & Pink Glow)
                            setBgGradient("#050514", "#120624", "#080212");
                            canvas.drawRect(0, 0, width, height, bgPaint);

                            cardPaint.setColor(Color.argb(80, 0, 229, 255));
                            borderPaint.setColor(Color.parseColor("#FF007F"));
                            borderPaint.setStrokeWidth(6f);
                            canvas.drawRoundRect(cardRect, 20f, 20f, cardPaint);
                            canvas.drawRoundRect(cardRect, 20f, 20f, borderPaint);

                            timePaint.setColor(Color.parseColor("#00E5FF"));
                            timePaint.setShadowLayer(18f, 0f, 0f, Color.parseColor("#00E5FF"));
                            secPaint.setColor(Color.parseColor("#FF4081"));
                            datePaint.setColor(Color.parseColor("#E0F7FA"));
                            drawStandardText(canvas, timeStr, secStr, dateStr, centerX, centerY, cardHeight);
                            break;

                        case 2: // 3. Gold Luxury (Black & Gold)
                            setBgGradient("#0A0A0A", "#1C1810", "#050505");
                            canvas.drawRect(0, 0, width, height, bgPaint);

                            cardPaint.setColor(Color.argb(140, 20, 18, 12));
                            borderPaint.setColor(Color.parseColor("#FFD700"));
                            borderPaint.setStrokeWidth(5f);
                            canvas.drawRoundRect(cardRect, 35f, 35f, cardPaint);
                            canvas.drawRoundRect(cardRect, 35f, 35f, borderPaint);

                            // Inner thin gold border
                            RectF innerGold = new RectF(cardRect.left + 12, cardRect.top + 12, cardRect.right - 12, cardRect.bottom - 12);
                            accentPaint.setColor(Color.argb(130, 255, 215, 0));
                            accentPaint.setStrokeWidth(2f);
                            canvas.drawRoundRect(innerGold, 25f, 25f, accentPaint);

                            timePaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
                            timePaint.setColor(Color.parseColor("#FFD700"));
                            timePaint.setShadowLayer(10f, 0f, 0f, Color.parseColor("#AA8800"));
                            secPaint.setColor(Color.parseColor("#FFF8E1"));
                            datePaint.setColor(Color.parseColor("#D4AF37"));
                            drawStandardText(canvas, timeStr, secStr, dateStr, centerX, centerY, cardHeight);
                            break;

                        case 3: // 4. Ocean Water (Blue Water Glass)
                            setBgGradient("#021B2B", "#053B5E", "#011627");
                            canvas.drawRect(0, 0, width, height, bgPaint);

                            cardPaint.setColor(Color.argb(70, 64, 196, 255));
                            borderPaint.setColor(Color.argb(180, 128, 216, 255));
                            borderPaint.setStrokeWidth(4f);
                            canvas.drawRoundRect(cardRect, 70f, 70f, cardPaint);
                            canvas.drawRoundRect(cardRect, 70f, 70f, borderPaint);

                            timePaint.setColor(Color.WHITE);
                            timePaint.setShadowLayer(14f, 0f, 0f, Color.parseColor("#40C4FF"));
                            secPaint.setColor(Color.parseColor("#80D8FF"));
                            datePaint.setColor(Color.parseColor("#E1F5FE"));
                            drawStandardText(canvas, timeStr, secStr, dateStr, centerX, centerY, cardHeight);
                            break;

                        case 4: // 5. Red Gamer (Red Neon Sharp Border)
                            setBgGradient("#120202", "#260508", "#0D0101");
                            canvas.drawRect(0, 0, width, height, bgPaint);

                            cardPaint.setColor(Color.argb(95, 40, 5, 10));
                            borderPaint.setColor(Color.parseColor("#FF1744"));
                            borderPaint.setStrokeWidth(7f);
                            canvas.drawRoundRect(cardRect, 12f, 12f, cardPaint);
                            canvas.drawRoundRect(cardRect, 12f, 12f, borderPaint);

                            timePaint.setColor(Color.parseColor("#FF1744"));
                            timePaint.setShadowLayer(16f, 0f, 0f, Color.parseColor("#FF5252"));
                            secPaint.setColor(Color.WHITE);
                            datePaint.setColor(Color.parseColor("#FF8A80"));
                            drawStandardText(canvas, timeStr, secStr, dateStr, centerX, centerY, cardHeight);
                            break;

                        case 5: // 6. Minimal Clean (No Box, Ultra Clean)
                            setBgGradient("#111317", "#1B1F26", "#0D0F12");
                            canvas.drawRect(0, 0, width, height, bgPaint);

                            timePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
                            timePaint.setColor(Color.parseColor("#FFFFFF"));
                            secPaint.setColor(Color.parseColor("#90A4AE"));
                            datePaint.setColor(Color.parseColor("#78909C"));

                            // Thin divider line
                            accentPaint.setColor(Color.argb(120, 255, 255, 255));
                            accentPaint.setStrokeWidth(3f);
                            canvas.drawLine(centerX - width * 0.25f, centerY + cardHeight * 0.28f,
                                    centerX + width * 0.25f, centerY + cardHeight * 0.28f, accentPaint);

                            drawStandardText(canvas, timeStr, secStr, dateStr, centerX, centerY, cardHeight);
                            break;

                        case 6: // 7. Purple Galaxy (Circle Ring Style)
                            setBgGradient("#0D0221", "#240046", "#10002B");
                            canvas.drawRect(0, 0, width, height, bgPaint);

                            float radius = width * 0.38f;
                            cardPaint.setColor(Color.argb(75, 156, 39, 176));
                            borderPaint.setColor(Color.parseColor("#E040FB"));
                            borderPaint.setStrokeWidth(8f);
                            canvas.drawCircle(centerX, centerY, radius, cardPaint);
                            canvas.drawCircle(centerX, centerY, radius, borderPaint);

                            accentPaint.setColor(Color.argb(120, 124, 77, 255));
                            accentPaint.setStrokeWidth(3f);
                            canvas.drawCircle(centerX, centerY, radius + 18f, accentPaint);

                            timePaint.setColor(Color.WHITE);
                            timePaint.setShadowLayer(16f, 0f, 0f, Color.parseColor("#E040FB"));
                            secPaint.setColor(Color.parseColor("#EA80FC"));
                            datePaint.setColor(Color.parseColor("#E1BEE7"));
                            drawStandardText(canvas, timeStr, secStr, dateStr, centerX, centerY, cardHeight);
                            break;

                        case 7: // 8. Sunset Retro (Orange & Warm Glow)
                            setBgGradient("#1A0B05", "#3E1404", "#120602");
                            canvas.drawRect(0, 0, width, height, bgPaint);

                            cardPaint.setColor(Color.argb(70, 255, 111, 0));
                            borderPaint.setColor(Color.parseColor("#FF9100"));
                            borderPaint.setStrokeWidth(5f);
                            canvas.drawRoundRect(cardRect, 50f, 50f, cardPaint);
                            canvas.drawRoundRect(cardRect, 50f, 50f, borderPaint);

                            timePaint.setColor(Color.parseColor("#FFD180"));
                            timePaint.setShadowLayer(14f, 0f, 0f, Color.parseColor("#FF6D00"));
                            secPaint.setColor(Color.parseColor("#FFAB40"));
                            datePaint.setColor(Color.parseColor("#FFE0B2"));
                            drawStandardText(canvas, timeStr, secStr, dateStr, centerX, centerY, cardHeight);
                            break;

                        case 0:
                        default: // 1. Green Glass (Original Style)
                            setBgGradient("#071114", "#0F2429", "#071412");
                            canvas.drawRect(0, 0, width, height, bgPaint);

                            cardPaint.setColor(Color.argb(65, 0, 230, 118));
                            borderPaint.setColor(Color.argb(140, 0, 230, 118));
                            borderPaint.setStrokeWidth(3f);
                            canvas.drawRoundRect(cardRect, 45f, 45f, cardPaint);
                            canvas.drawRoundRect(cardRect, 45f, 45f, borderPaint);

                            timePaint.setColor(Color.WHITE);
                            timePaint.setShadowLayer(12f, 0f, 0f, Color.parseColor("#00E676"));
                            secPaint.setColor(Color.parseColor("#00E676"));
                            datePaint.setColor(Color.parseColor("#CFD8DC"));
                            drawStandardText(canvas, timeStr, secStr, dateStr, centerX, centerY, cardHeight);
                            break;
                    }
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }

        private void setBgGradient(String c1, String c2, String c3) {
            bgPaint.setShader(new LinearGradient(
                    0, 0, width, height,
                    new int[]{Color.parseColor(c1), Color.parseColor(c2), Color.parseColor(c3)},
                    null,
                    Shader.TileMode.CLAMP
            ));
        }

        private void drawStandardText(Canvas canvas, String timeStr, String secStr, String dateStr,
                                      float centerX, float centerY, float cardHeight) {
            canvas.drawText(timeStr, centerX, centerY - (cardHeight * 0.04f), timePaint);
            canvas.drawText(secStr, centerX, centerY + (cardHeight * 0.22f), secPaint);
            canvas.drawText(dateStr, centerX, centerY + (cardHeight * 0.40f), datePaint);
        }
    }
}

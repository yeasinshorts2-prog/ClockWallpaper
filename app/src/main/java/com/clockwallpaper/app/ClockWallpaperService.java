package com.clockwallpaper.app;

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
            cardPaint.setColor(Color.argb(65, 0, 230, 118));
            cardPaint.setStyle(Paint.Style.FILL);

            borderPaint.setColor(Color.argb(140, 0, 230, 118));
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(3f);

            timePaint.setColor(Color.WHITE);
            timePaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
            timePaint.setTextAlign(Paint.Align.CENTER);
            timePaint.setShadowLayer(12f, 0f, 0f, Color.parseColor("#00E676"));

            secPaint.setColor(Color.parseColor("#00E676"));
            secPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
            secPaint.setTextAlign(Paint.Align.CENTER);

            datePaint.setColor(Color.parseColor("#CFD8DC"));
            datePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
            datePaint.setTextAlign(Paint.Align.CENTER);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            this.width = width;
            this.height = height;

            bgPaint.setShader(new LinearGradient(
                    0, 0, width, height,
                    new int[]{
                            Color.parseColor("#071114"),
                            Color.parseColor("#0F2429"),
                            Color.parseColor("#071412")
                    },
                    null,
                    Shader.TileMode.CLAMP
            ));

            timePaint.setTextSize(width * 0.19f);
            secPaint.setTextSize(width * 0.08f);
            datePaint.setTextSize(width * 0.048f);

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
                    canvas.drawRect(0, 0, width, height, bgPaint);

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
                    canvas.drawRoundRect(cardRect, 45f, 45f, cardPaint);
                    canvas.drawRoundRect(cardRect, 45f, 45f, borderPaint);

                    canvas.drawText(timeStr, centerX, centerY - (cardHeight * 0.04f), timePaint);
                    canvas.drawText(secStr, centerX, centerY + (cardHeight * 0.22f), secPaint);
                    canvas.drawText(dateStr, centerX, centerY + (cardHeight * 0.40f), datePaint);
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}

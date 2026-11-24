package com.clockwallpaper.app;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.parseColor("#0B1416"));
        layout.setPadding(60, 60, 60, 60);

        TextView title = new TextView(this);
        title.setText("Digital Clock Wallpaper");
        title.setTextColor(Color.parseColor("#00E676"));
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = new TextView(this);
        subtitle.setText("সব স্ক্রিনে অ্যাপের নিচে ডিজিটাল ঘড়ি দেখতে নিচের বাটনে ক্লিক করে ওয়ালপেপার সেট করুন।");
        subtitle.setTextColor(Color.parseColor("#B0BEC5"));
        subtitle.setTextSize(15);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 24, 0, 60);

        Button setBtn = new Button(this);
        setBtn.setText("Set Live Wallpaper");
        setBtn.setTextColor(Color.WHITE);
        setBtn.setTextSize(18);
        setBtn.setAllCaps(false);
        setBtn.setPadding(60, 32, 60, 32);

        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(Color.parseColor("#1B5E20"));
        btnBg.setCornerRadius(40);
        btnBg.setStroke(3, Color.parseColor("#00E676"));
        setBtn.setBackground(btnBg);

        setBtn.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent.putExtra(
                        WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        new ComponentName(MainActivity.this, ClockWallpaperService.class)
                );
                startActivity(intent);
            } catch (Exception e) {
                Intent fallback = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                startActivity(fallback);
            }
        });

        layout.addView(title);
        layout.addView(subtitle);
        layout.addView(setBtn);

        setContentView(layout);
    }
}

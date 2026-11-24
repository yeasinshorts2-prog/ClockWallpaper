package com.clockwallpaper.app;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String PREFS_NAME = "ClockPrefs";
    private static final String KEY_STYLE = "clock_style";

    private final String[] styleNames = {
            "1. Green Glass (আগের স্টাইল)",
            "2. Neon Cyber (সায়ান ও পিঙ্ক গ্লো)",
            "3. Gold Luxury (ব্ল্যাক ও গোল্ডেন)",
            "4. Ocean Water (নীল ওয়াটার গ্লাস)",
            "5. Red Gamer (লাল নিওন বর্ডার)",
            "6. Minimal Clean (সিম্পল ও ক্লিন)",
            "7. Purple Galaxy (পার্পল রিং স্টাইল)",
            "8. Sunset Retro (অরেঞ্জ ও গোল্ডেন গ্লো)"
    };

    private final int[] accentColors = {
            Color.parseColor("#00E676"),
            Color.parseColor("#00E5FF"),
            Color.parseColor("#FFD700"),
            Color.parseColor("#40C4FF"),
            Color.parseColor("#FF1744"),
            Color.parseColor("#ECEFF1"),
            Color.parseColor("#E040FB"),
            Color.parseColor("#FF9100")
    };

    private Button[] styleButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentStyle = prefs.getInt(KEY_STYLE, 0);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.parseColor("#0B1416"));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setPadding(48, 60, 48, 80);

        TextView title = new TextView(this);
        title.setText("Digital Clock Wallpaper");
        title.setTextColor(Color.parseColor("#00E676"));
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = new TextView(this);
        subtitle.setText("পছন্দের ঘড়ির স্টাইল সিলেক্ট করুন এবং নিচের বাটন থেকে ওয়ালপেপার সেট করুন:");
        subtitle.setTextColor(Color.parseColor("#B0BEC5"));
        subtitle.setTextSize(14);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 16, 0, 36);

        layout.addView(title);
        layout.addView(subtitle);

        styleButtons = new Button[styleNames.length];

        for (int i = 0; i < styleNames.length; i++) {
            final int index = i;
            Button btn = new Button(this);
            btn.setText(styleNames[i]);
            btn.setTextColor(Color.WHITE);
            btn.setTextSize(15);
            btn.setAllCaps(false);
            btn.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            btn.setPadding(40, 28, 40, 28);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 20);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> {
                prefs.edit().putInt(KEY_STYLE, index).apply();
                updateButtonStyles(index);
                Toast.makeText(MainActivity.this, styleNames[index] + " সিলেক্ট হয়েছে!", Toast.LENGTH_SHORT).show();
            });

            styleButtons[i] = btn;
            layout.addView(btn);
        }

        updateButtonStyles(currentStyle);

        Button setBtn = new Button(this);
        setBtn.setText("Set Live Wallpaper");
        setBtn.setTextColor(Color.WHITE);
        setBtn.setTextSize(18);
        setBtn.setAllCaps(false);
        setBtn.setPadding(60, 34, 60, 34);

        LinearLayout.LayoutParams setParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        setParams.setMargins(0, 30, 0, 20);
        setBtn.setLayoutParams(setParams);

        GradientDrawable setBg = new GradientDrawable();
        setBg.setColor(Color.parseColor("#1B5E20"));
        setBg.setCornerRadius(40);
        setBg.setStroke(3, Color.parseColor("#00E676"));
        setBtn.setBackground(setBg);

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

        layout.addView(setBtn);
        scrollView.addView(layout);
        setContentView(scrollView);
    }

    private void updateButtonStyles(int selectedIndex) {
        for (int i = 0; i < styleButtons.length; i++) {
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(28);
            if (i == selectedIndex) {
                bg.setColor(Color.parseColor("#1E3338"));
                bg.setStroke(4, accentColors[i]);
                styleButtons[i].setText("✓ " + styleNames[i]);
            } else {
                bg.setColor(Color.parseColor("#121F23"));
                bg.setStroke(2, Color.parseColor("#263238"));
                styleButtons[i].setText(styleNames[i]);
            }
            styleButtons[i].setBackground(bg);
        }
    }
}

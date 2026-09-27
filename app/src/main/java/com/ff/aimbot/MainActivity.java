package com.ff.aimbot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 80, 40, 40);
        root.setBackgroundColor(0xFF0D0D1A);

        TextView title = new TextView(this);
        title.setText("FF AIMBOT PANEL");
        title.setTextColor(0xFF00FF88);
        title.setTextSize(26);
        title.setPadding(0, 0, 0, 20);
        root.addView(title);

        TextView status = new TextView(this);
        status.setText("Status: Ready");
        status.setTextColor(0xFF88FF88);
        status.setTextSize(14);
        root.addView(status);

        Button startBtn = new Button(this);
        startBtn.setText("▶  START AIMBOT + LAUNCH FF");
        startBtn.setBackgroundColor(0xFF00CC55);
        startBtn.setTextColor(0xFFFFFFFF);
        startBtn.setTextSize(16);
        startBtn.setOnClickListener(v -> {
            status.setText("Status: Injecting...");
            startService(new Intent(this, OverlayService.class));
            launchFreeFire();
        });

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(0, 30, 0, 0);
        startBtn.setLayoutParams(btnParams);
        root.addView(startBtn);

        setContentView(root);
    }

    private void launchFreeFire() {
        String[] pkgs = {
            "com.dts.freefireth",
            "com.dts.freefiremax",
            "com.garena.game.ffthai"
        };
        for (String pkg : pkgs) {
            Intent intent = getPackageManager().getLaunchIntentForPackage(pkg);
            if (intent != null) {
                startActivity(intent);
                return;
            }
        }
        Toast.makeText(this, "Free Fire not found! Install it first.", Toast.LENGTH_LONG).show();
    }
}

package com.ff.aimbot;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class PermissionActivity extends AppCompatActivity {
    private static final int OVERLAY_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 80, 40, 40);
        root.setBackgroundColor(0xFF1A1A2E);

        TextView title = new TextView(this);
        title.setText("FF HEADSHOT AIMBOT");
        title.setTextColor(0xFF00FF88);
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 40);
        root.addView(title);

        TextView step1 = new TextView(this);
        step1.setText("STEP 1: Overlay Permission");
        step1.setTextColor(0xFFFFFFFF);
        step1.setTextSize(16);
        root.addView(step1);

        Button overlayBtn = new Button(this);
        overlayBtn.setText("Grant Overlay Permission");
        overlayBtn.setBackgroundColor(0xFF00FF88);
        overlayBtn.setTextColor(0xFF000000);
        overlayBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_REQUEST);
        });
        root.addView(overlayBtn);

        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2));
        divider.setBackgroundColor(0xFF333355);
        LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2);
        dp.setMargins(0, 30, 0, 30);
        divider.setLayoutParams(dp);
        root.addView(divider);

        Button nextBtn = new Button(this);
        nextBtn.setText("NEXT → LAUNCH AIMBOT");
        nextBtn.setBackgroundColor(0xFFFF0055);
        nextBtn.setTextColor(0xFFFFFFFF);
        nextBtn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Grant overlay permission first!", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
        });
        root.addView(nextBtn);

        setContentView(root);
    }
}

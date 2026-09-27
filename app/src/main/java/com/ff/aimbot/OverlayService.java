package com.ff.aimbot;

import android.app.Service;
import android.content.Intent;
import android.graphics.*;
import android.os.IBinder;
import android.view.*;

public class OverlayService extends Service {
    private WindowManager wm;
    private AimbotMenuView menuView;
    private WindowManager.LayoutParams params;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        params = new WindowManager.LayoutParams(
                260, 120,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 30;
        params.y = 200;

        menuView = new AimbotMenuView(this);
        wm.addView(menuView, params);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (menuView != null) wm.removeView(menuView);
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    class AimbotMenuView extends View {
        Paint bg, textPaint, btnPaint, closePaint;
        boolean aimbotOn = true;
        float dX, dY;

        AimbotMenuView(Service ctx) {
            super(ctx);
            bg = new Paint();
            bg.setColor(0xCC000000);
            bg.setStyle(Paint.Style.FILL);

            textPaint = new Paint();
            textPaint.setColor(0xFF00FF88);
            textPaint.setTextSize(22);
            textPaint.setAntiAlias(true);

            btnPaint = new Paint();
            btnPaint.setStyle(Paint.Style.FILL);
            btnPaint.setAntiAlias(true);

            closePaint = new Paint();
            closePaint.setColor(0xFFFF3333);
            closePaint.setTextSize(20);
            closePaint.setAntiAlias(true);
        }

        @Override
        protected void onDraw(Canvas c) {
            // Background box
            c.drawRoundRect(new RectF(0, 0, 260, 120), 18, 18, bg);

            // Title
            textPaint.setColor(0xFF00FFAA);
            textPaint.setTextSize(18);
            c.drawText("FF AIMBOT v1.0", 12, 26, textPaint);

            // X close button
            closePaint.setColor(0xFFFF3333);
            c.drawCircle(240, 18, 14, closePaint);
            closePaint.setColor(0xFFFFFFFF);
            c.drawText("✕", 233, 24, closePaint);

            // Headshot toggle button
            btnPaint.setColor(aimbotOn ? 0xFF00CC44 : 0xFFCC2222);
            c.drawRoundRect(new RectF(10, 42, 250, 85), 12, 12, btnPaint);
            textPaint.setColor(0xFFFFFFFF);
            textPaint.setTextSize(20);
            c.drawText(aimbotOn ? "HEADSHOT ONLY: ON" : "HEADSHOT ONLY: OFF", 16, 70, textPaint);

            // Status
            textPaint.setTextSize(13);
            textPaint.setColor(0xFF888888);
            c.drawText("Drag to move", 80, 110, textPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dX = e.getRawX() - params.x;
                    dY = e.getRawY() - params.y;

                    // Close button tapped
                    if (e.getX() >= 226 && e.getX() <= 254 && e.getY() >= 4 && e.getY() <= 32) {
                        stopSelf();
                        return true;
                    }
                    // Toggle tapped
                    if (e.getY() >= 42 && e.getY() <= 85) {
                        aimbotOn = !aimbotOn;
                        invalidate();
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    params.x = (int)(e.getRawX() - dX);
                    params.y = (int)(e.getRawY() - dY);
                    wm.updateViewLayout(this, params);
                    break;
            }
            return true;
        }
    }
}

package com.ffpanel.headshot

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.core.app.NotificationCompat

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var floatingView: View? = null
    private var menuExpanded = false
    var headshotEnabled = false

    companion object {
        var instance: OverlayService? = null
        const val CHANNEL_ID = "ff_overlay"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        startForeground(1, buildNotification())
        showFloatingButton()
    }

    private fun showFloatingButton() {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.END
        params.x = 10
        params.y = 200

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#CC000000"))
            setPadding(8, 8, 8, 8)
        }

        val toggleBtn = TextView(this).apply {
            text = "FF"
            setTextColor(Color.RED)
            textSize = 16f
            setPadding(16, 8, 16, 8)
            setBackgroundColor(Color.parseColor("#99FF0000"))
        }

        val menuBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
            setBackgroundColor(Color.parseColor("#EE111111"))
            setPadding(12, 12, 12, 12)
        }

        val headshotRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 8, 0, 8)
        }

        val headshotLabel = TextView(this).apply {
            text = "Headshot Only"
            setTextColor(Color.WHITE)
            textSize = 14f
        }

        val headshotSwitch = Switch(this).apply {
            isChecked = false
            setOnCheckedChangeListener { _, checked ->
                headshotEnabled = checked
                AccessibilityHeadshot.headshotActive = checked
            }
        }

        headshotRow.addView(headshotLabel)
        headshotRow.addView(headshotSwitch)

        val statusText = TextView(this).apply {
            text = "AIM: OFF"
            setTextColor(Color.GREEN)
            textSize = 12f
        }

        val closeBtn = TextView(this).apply {
            text = "✕ Close Menu"
            setTextColor(Color.RED)
            textSize = 13f
            setPadding(0, 12, 0, 0)
            setOnClickListener {
                menuBox.visibility = View.GONE
                menuExpanded = false
            }
        }

        val stopBtn = TextView(this).apply {
            text = "■ Stop Panel"
            setTextColor(Color.GRAY)
            textSize = 12f
            setPadding(0, 8, 0, 0)
            setOnClickListener { stopSelf() }
        }

        menuBox.addView(headshotRow)
        menuBox.addView(statusText)
        menuBox.addView(closeBtn)
        menuBox.addView(stopBtn)

        toggleBtn.setOnClickListener {
            menuExpanded = !menuExpanded
            menuBox.visibility = if (menuExpanded) View.VISIBLE else View.GONE
            statusText.text = if (headshotEnabled) "AIM: ON 🎯" else "AIM: OFF"
        }

        root.addView(toggleBtn)
        root.addView(menuBox)

        var lastX = 0; var lastY = 0
        var initX = 0; var initY = 0

        root.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.rawX.toInt()
                    lastY = event.rawY.toInt()
                    initX = params.x
                    initY = params.y
                    false
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initX - (event.rawX.toInt() - lastX)
                    params.y = initY + (event.rawY.toInt() - lastY)
                    windowManager.updateViewLayout(root, params)
                    true
                }
                else -> false
            }
        }

        floatingView = root
        windowManager.addView(root, params)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "FF Panel", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("FF Headshot Panel Active")
            .setContentText("Overlay running")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .build()
    }

    override fun onDestroy() {
        instance = null
        floatingView?.let { windowManager.removeView(it) }
        super.onDestroy()
    }
}

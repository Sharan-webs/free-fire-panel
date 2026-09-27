package com.ffpanel.headshot

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent

class AccessibilityHeadshot : AccessibilityService() {

    companion object {
        var instance: AccessibilityHeadshot? = null
        var headshotActive = false
        var headZoneYRatio = 0.28f
        var tapRadius = 40f

        fun isEnabled(context: Context): Boolean {
            val prefString = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            return prefString.contains(context.packageName)
        }
    }

    override fun onServiceConnected() {
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!headshotActive) return
        val pkg = event.packageName?.toString() ?: return
        if (!pkg.contains("freefire") && !pkg.contains("dts")) return
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            injectHeadshotTap()
        }
    }

    fun injectHeadshotTap() {
        if (!headshotActive) return
        val display = resources.displayMetrics
        val screenW = display.widthPixels.toFloat()
        val screenH = display.heightPixels.toFloat()
        val targetX = screenW / 2f
        val targetY = screenH * headZoneYRatio

        val path = Path().apply { moveTo(targetX, targetY) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 50))
            .build()

        dispatchGesture(gesture, null, null)
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }
}

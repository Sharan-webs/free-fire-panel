package com.ffpanel.headshot

import android.app.Activity
import android.content.pm.PackageManager
import rikka.shizuku.Shizuku

object ShizukuHelper {

    private const val REQUEST_CODE = 101

    fun isAvailable(): Boolean {
        return try {
            Shizuku.pingBinder() && checkPermission()
        } catch (e: Exception) { false }
    }

    fun checkPermission(): Boolean {
        return try {
            if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
                false
            } else {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            }
        } catch (e: Exception) { false }
    }

    fun requestPermission(activity: Activity) {
        try {
            if (Shizuku.isPreV11()) return
            if (Shizuku.shouldShowRequestPermissionRationale()) return
            Shizuku.requestPermission(REQUEST_CODE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun exec(cmd: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", cmd))
            process.inputStream.bufferedReader().readText()
        } catch (e: Exception) {
            e.message ?: "error"
        }
    }
}

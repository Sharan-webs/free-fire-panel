package com.ffpanel.headshot

import android.content.Context
import android.content.Intent

object FFLauncher {

    private val FF_PACKAGES = listOf(
        "com.dts.freefireth",
        "com.dts.freefiremax"
    )

    fun launch(context: Context): Boolean {
        for (pkg in FF_PACKAGES) {
            try {
                val intent = context.packageManager.getLaunchIntentForPackage(pkg)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    return true
                }
            } catch (e: Exception) {
                continue
            }
        }
        return false
    }

    fun isInstalled(context: Context): Boolean {
        return FF_PACKAGES.any { pkg ->
            try {
                context.packageManager.getPackageInfo(pkg, 0)
                true
            } catch (e: Exception) { false }
        }
    }
}

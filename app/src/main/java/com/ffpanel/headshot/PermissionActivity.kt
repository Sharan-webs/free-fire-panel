package com.ffpanel.headshot

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ffpanel.headshot.databinding.ActivityPermissionBinding

class PermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionBinding

    private val permissions = listOf(
        Permission("Overlay Permission", "Draw over other apps (mod menu)") {
            !Settings.canDrawOverlays(this)
        },
        Permission("Install Permission (PIP)", "Install apps from unknown sources") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                !packageManager.canRequestPackageInstalls()
            else false
        },
        Permission("Accessibility Service", "Auto headshot gesture injection") {
            !AccessibilityHeadshot.isEnabled(this)
        },
        Permission("Usage Stats", "Detect when FF is running") {
            !hasUsageStatsPermission()
        },
        Permission("Shizuku", "System-level access for deep injection") {
            !ShizukuHelper.isAvailable()
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setupUI()
    }

    private fun setupUI() {
        val sb = StringBuilder()
        var allGranted = true

        permissions.forEach { perm ->
            val needed = perm.isNeeded()
            val icon = if (needed) "✗" else "✓"
            val status = if (needed) "REQUIRED" else "GRANTED"
            sb.appendLine("$icon  ${perm.name}")
            sb.appendLine("     ${perm.description}")
            sb.appendLine("     [$status]\n")
            if (needed) allGranted = false
        }

        binding.tvPermissionStatus.text = sb.toString()

        binding.btnGrantAll.setOnClickListener {
            requestNextPermission()
        }

        binding.btnStart.isEnabled = allGranted
        binding.btnStart.setOnClickListener {
            startApp()
        }

        if (allGranted) {
            binding.tvTitle.text = "All Ready!"
            binding.btnGrantAll.text = "Re-check"
        }
    }

    private fun requestNextPermission() {
        val needed = permissions.firstOrNull { it.isNeeded() } ?: run {
            setupUI()
            return
        }

        when (needed.name) {
            "Overlay Permission" -> {
                startActivity(Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                ))
            }
            "Install Permission (PIP)" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startActivity(Intent(
                        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:$packageName")
                    ))
                }
            }
            "Accessibility Service" -> {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
            "Usage Stats" -> {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
            "Shizuku" -> {
                ShizukuHelper.requestPermission(this)
            }
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        return try {
            val appOps = getSystemService(APP_OPS_SERVICE) as android.app.AppOpsManager
            val mode = appOps.checkOpNoThrow(
                android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), packageName
            )
            mode == android.app.AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) { false }
    }

    private fun startApp() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        setupUI()
    }

    data class Permission(
        val name: String,
        val description: String,
        val isNeeded: PermissionActivity.() -> Boolean
    )
}

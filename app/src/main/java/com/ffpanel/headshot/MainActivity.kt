package com.ffpanel.headshot

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ffpanel.headshot.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            launchFFWithPanel()
        }

        binding.tvStatus.text = "Panel Ready\nShizuku: ${if (ShizukuHelper.isAvailable()) "✓" else "✗"}"
    }

    private fun launchFFWithPanel() {
        val serviceIntent = Intent(this, OverlayService::class.java)
        startForegroundService(serviceIntent)

        binding.btnStart.postDelayed({
            val launched = FFLauncher.launch(this)
            if (!launched) {
                Toast.makeText(this, "Free Fire not installed", Toast.LENGTH_SHORT).show()
            }
        }, 800)
    }
}

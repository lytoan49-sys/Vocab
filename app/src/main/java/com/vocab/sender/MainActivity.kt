package com.vocab.sender

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private val overlayPermissionRequestCode = 1001

    private lateinit var urlInput: EditText
    private lateinit var toggleBubbleButton: Button
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        urlInput = findViewById(R.id.url_input)
        toggleBubbleButton = findViewById(R.id.toggle_bubble_button)
        statusText = findViewById(R.id.status_text)
        val saveButton = findViewById<Button>(R.id.save_button)

        urlInput.setText(Prefs.getUrl(this))

        saveButton.setOnClickListener {
            Prefs.setUrl(this, urlInput.text.toString().trim())
            Toast.makeText(this, "Đã lưu URL", Toast.LENGTH_SHORT).show()
        }

        toggleBubbleButton.setOnClickListener {
            if (FloatingBubbleService.isRunning) {
                stopService(Intent(this, FloatingBubbleService::class.java))
                updateStatus()
            } else {
                requestOverlayPermissionOrStart()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun requestOverlayPermissionOrStart() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, overlayPermissionRequestCode)
        } else {
            startFloatingBubble()
        }
    }

    private fun startFloatingBubble() {
        startService(Intent(this, FloatingBubbleService::class.java))
        updateStatus()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == overlayPermissionRequestCode) {
            if (Settings.canDrawOverlays(this)) {
                startFloatingBubble()
            } else {
                Toast.makeText(
                    this,
                    "Cần cấp quyền hiển thị trên ứng dụng khác để dùng bong bóng nổi",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateStatus() {
        if (FloatingBubbleService.isRunning) {
            toggleBubbleButton.text = "Tắt bong bóng nổi"
            statusText.text = "Trạng thái: đang bật"
        } else {
            toggleBubbleButton.text = "Bật bong bóng nổi"
            statusText.text = "Trạng thái: đang tắt"
        }
    }
}

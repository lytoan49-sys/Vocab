package com.vocab.sender

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object TextSender {

    /**
     * Sends [text] to the URL saved in Prefs via HTTP POST on a background thread.
     * Shows a Toast with the result on the main thread.
     */
    fun send(context: Context, text: String) {
        val appContext = context.applicationContext
        val url = Prefs.getUrl(appContext)

        if (text.isBlank()) {
            toast(appContext, "Không có nội dung để gửi (Clipboard trống)")
            return
        }

        if (url.isBlank()) {
            toast(appContext, "Chưa cấu hình URL. Mở app để nhập URL Web App.")
            return
        }

        Thread {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val body = "text=" + URLEncoder.encode(text, "UTF-8")
                OutputStreamWriter(connection.outputStream).use { it.write(body) }

                val responseCode = connection.responseCode
                if (responseCode in 200..299) {
                    toast(appContext, "Đã gửi: ${truncate(text)}")
                } else {
                    toast(appContext, "Gửi thất bại (mã lỗi $responseCode)")
                }
                connection.disconnect()
            } catch (e: Exception) {
                toast(appContext, "Lỗi khi gửi: ${e.message}")
            }
        }.start()
    }

    private fun truncate(text: String): String {
        return if (text.length > 40) text.substring(0, 40) + "…" else text
    }

    private fun toast(context: Context, message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}

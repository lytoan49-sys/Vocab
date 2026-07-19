package com.vocab.sender

import android.content.Context

object Prefs {
    private const val PREFS_NAME = "vocab_sender_prefs"
    private const val KEY_URL = "target_url"

    fun getUrl(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_URL, "") ?: ""
    }

    fun setUrl(context: Context, url: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_URL, url).apply()
    }
}

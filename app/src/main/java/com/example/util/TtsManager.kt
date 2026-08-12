package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.Locale

class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ru", "RU"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Try fallback locale "ru"
                val fallback = tts?.setLanguage(Locale("ru"))
                isInitialized = fallback != TextToSpeech.LANG_MISSING_DATA && fallback != TextToSpeech.LANG_NOT_SUPPORTED
            } else {
                isInitialized = true
            }
        } else {
            isInitialized = false
        }
    }

    fun speak(text: String) {
        if (text.isBlank()) return
        if (isInitialized && tts != null) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ru_vocab_${System.currentTimeMillis()}")
        } else {
            Toast.makeText(context, "Pronouncing: $text", Toast.LENGTH_SHORT).show()
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

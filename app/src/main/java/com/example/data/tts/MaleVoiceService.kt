package com.example.data.tts

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.model.MaleVoiceProfile
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import kotlin.math.sin

class MaleVoiceService(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _audioLevels = MutableStateFlow(FloatArray(24) { 0.15f })
    val audioLevels: StateFlow<FloatArray> = _audioLevels.asStateFlow()

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var visualizerJob: Job? = null
    private var synthJob: Job? = null
    private var audioTrack: AudioTrack? = null

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("MaleVoiceService", "TTS Init error: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                val result = engine.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isInitialized = true
                    findAndApplyMaleVoice(MaleVoiceProfile.MARCUS_BARITONE)
                    setupUtteranceListener()
                }
            }
        }
    }

    private fun setupUtteranceListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
                startVisualizerPulse()
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
                stopVisualizerPulse()
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
                stopVisualizerPulse()
            }
        })
    }

    fun findAndApplyMaleVoice(profile: MaleVoiceProfile) {
        val engine = tts ?: return
        try {
            engine.setPitch(profile.pitchMultiplier)
            engine.setSpeechRate(profile.speedMultiplier)

            val voices = engine.voices
            if (!voices.isNullOrEmpty()) {
                val targetVoice = when (profile) {
                    MaleVoiceProfile.DAVID_NARRATOR -> {
                        voices.firstOrNull { it.locale == Locale.UK && it.name.contains("male", ignoreCase = true) }
                            ?: voices.firstOrNull { it.locale == Locale.UK }
                    }
                    MaleVoiceProfile.ALEX_TECH -> {
                        voices.firstOrNull { it.name.contains("en-us-x-sfg", ignoreCase = true) || it.name.contains("male", ignoreCase = true) }
                    }
                    MaleVoiceProfile.LIAM_DRAMATIC -> {
                        voices.firstOrNull { it.name.contains("en-us-x-iom", ignoreCase = true) || it.name.contains("male", ignoreCase = true) }
                    }
                    MaleVoiceProfile.JULIAN_CALM -> {
                        voices.firstOrNull { it.name.contains("en-us-x-iol", ignoreCase = true) || it.name.contains("male", ignoreCase = true) }
                    }
                    else -> {
                        voices.firstOrNull { it.locale.language == "en" && it.name.contains("male", ignoreCase = true) }
                    }
                }
                if (targetVoice != null) {
                    engine.voice = targetVoice
                }
            }
        } catch (e: Exception) {
            Log.e("MaleVoiceService", "Error selecting male voice: ${e.message}")
        }
    }

    fun speakNarration(
        text: String,
        profile: MaleVoiceProfile,
        utteranceId: String = "narration_${System.currentTimeMillis()}"
    ) {
        if (text.isBlank()) return

        findAndApplyMaleVoice(profile)

        val params = Bundle()
        params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
        params.putFloat(TextToSpeech.Engine.KEY_PARAM_PAN, 0.0f)

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        _isSpeaking.value = true
        startVisualizerPulse()
        startHarmonicSubBass(profile)
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            // Ignore
        }
        _isSpeaking.value = false
        stopVisualizerPulse()
        stopHarmonicSubBass()
    }

    private fun startVisualizerPulse() {
        visualizerJob?.cancel()
        visualizerJob = serviceScope.launch {
            var step = 0
            while (_isSpeaking.value) {
                val newLevels = FloatArray(24) { i ->
                    val wave = sin((step * 0.35 + i * 0.45)).toFloat() * 0.4f + 0.5f
                    (wave * (0.3f + (i % 5) * 0.15f)).coerceIn(0.1f, 1.0f)
                }
                _audioLevels.value = newLevels
                step++
                delay(65)
            }
            _audioLevels.value = FloatArray(24) { 0.12f }
        }
    }

    private fun stopVisualizerPulse() {
        visualizerJob?.cancel()
        _audioLevels.value = FloatArray(24) { 0.12f }
    }

    private fun startHarmonicSubBass(profile: MaleVoiceProfile) {
        synthJob?.cancel()
        synthJob = serviceScope.launch(Dispatchers.IO) {
            val sampleRate = 22050
            val numSamples = sampleRate / 4
            val baseFreq = when (profile) {
                MaleVoiceProfile.MARCUS_BARITONE -> 95.0
                MaleVoiceProfile.DAVID_NARRATOR -> 110.0
                MaleVoiceProfile.ALEX_TECH -> 125.0
                MaleVoiceProfile.LIAM_DRAMATIC -> 100.0
                MaleVoiceProfile.JULIAN_CALM -> 88.0
                else -> 98.0
            }

            val buffer = ShortArray(numSamples)
            try {
                val minSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack?.play()

                var phase = 0.0
                var count = 0
                while (isActive && _isSpeaking.value && count < 15) {
                    for (i in 0 until numSamples) {
                        val s = sin(phase) * 0.12
                        buffer[i] = (s * Short.MAX_VALUE).toInt().toShort()
                        phase += 2.0 * Math.PI * baseFreq / sampleRate
                        if (phase > 2.0 * Math.PI) phase -= 2.0 * Math.PI
                    }
                    audioTrack?.write(buffer, 0, numSamples)
                    count++
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    private fun stopHarmonicSubBass() {
        synthJob?.cancel()
        synthJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun release() {
        stop()
        tts?.shutdown()
        tts = null
        serviceScope.cancel()
    }
}

package com.esmanureral.neurostage.ui.patient.games.memorymatch

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.esmanureral.neurostage.R

class MemoryMatchSound(context: Context) {

    private val appContext = context.applicationContext
    private var cardClickLoaded = false
    private var shineLoaded = false
    private var pendingShine = false

    private val volume: Float =
        appContext.getString(R.string.puzzle_sound_full_volume).toFloat()

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(appContext.resources.getInteger(R.integer.puzzle_sound_max_streams))
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .build()

    private val cardClickId: Int = soundPool.load(appContext, R.raw.card_click, 1)
    private val shineId: Int = soundPool.load(appContext, R.raw.shine_sound, 1)

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status != 0) return@setOnLoadCompleteListener
            when (sampleId) {
                cardClickId -> cardClickLoaded = true
                shineId -> {
                    shineLoaded = true
                    if (pendingShine) {
                        pendingShine = false
                        play(shineId)
                    }
                }
            }
        }
    }

    fun playCardClick() {
        if (cardClickLoaded) play(cardClickId)
    }

    fun playShine() {
        if (shineLoaded) play(shineId) else pendingShine = true
    }

    private fun play(soundId: Int) {
        soundPool.play(soundId, volume, volume, 1, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}

@Composable
fun rememberMemoryMatchSound(): MemoryMatchSound {
    val context = LocalContext.current
    val sound = remember { MemoryMatchSound(context) }
    DisposableEffect(sound) {
        onDispose { sound.release() }
    }
    return sound
}

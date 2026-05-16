package com.esmanureral.neurostage.ui.patient.games.puzzle

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.esmanureral.neurostage.R

class PuzzleClickSound(context: Context) {

    private val appContext = context.applicationContext
    private var clickLoaded = false
    private var shineLoaded = false
    private var pendingSnap = false
    private var pendingShine = false

    private val pickupVolume: Float =
        appContext.getString(R.string.puzzle_sound_pickup_volume).toFloat()
    private val fullVolume: Float =
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

    private val clickId: Int = soundPool.load(appContext, R.raw.click_sound, 1)
    private val shineId: Int = soundPool.load(appContext, R.raw.shine_sound, 1)

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status != 0) return@setOnLoadCompleteListener
            when (sampleId) {
                clickId -> {
                    clickLoaded = true
                    if (pendingSnap) {
                        pendingSnap = false
                        play(clickId, fullVolume)
                    }
                }

                shineId -> {
                    shineLoaded = true
                    if (pendingShine) {
                        pendingShine = false
                        play(shineId, fullVolume)
                    }
                }
            }
        }
    }

    fun playPickup() {
        if (clickLoaded) play(clickId, pickupVolume)
    }

    fun playSnap() {
        if (clickLoaded) play(clickId, fullVolume) else pendingSnap = true
    }

    fun playShine() {
        if (shineLoaded) play(shineId, fullVolume) else pendingShine = true
    }

    private fun play(soundId: Int, volume: Float) {
        soundPool.play(soundId, volume, volume, 1, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}

@Composable
fun rememberPuzzleClickSound(): PuzzleClickSound {
    val context = LocalContext.current
    val sound = remember { PuzzleClickSound(context) }
    DisposableEffect(sound) {
        onDispose { sound.release() }
    }
    return sound
}

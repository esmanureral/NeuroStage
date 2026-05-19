package com.esmanureral.neurostage.ui.patient.games.colormatch

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.esmanureral.neurostage.R

class ColorMatchSound(context: Context) {
    private val appContext = context.applicationContext
    private var snapLoaded = false
    private var shineLoaded = false
    private var pendingSnap = false
    private var pendingShine = false

    private val volume: Float =
        appContext.getString(R.string.puzzle_sound_full_volume).toFloat()

    private val pool: SoundPool = SoundPool.Builder()
        .setMaxStreams(3)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .build()

    private val snapId: Int = pool.load(appContext, R.raw.click_sound, 1)
    private val shineId: Int = pool.load(appContext, R.raw.shine_sound, 1)

    init {
        pool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status != 0) return@setOnLoadCompleteListener
            when (sampleId) {
                snapId -> {
                    snapLoaded = true
                    if (pendingSnap) {
                        pendingSnap = false
                        play(snapId)
                    }
                }

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

    fun playSnap() {
        if (snapLoaded) play(snapId) else pendingSnap = true
    }

    fun playShine() {
        if (shineLoaded) play(shineId) else pendingShine = true
    }

    private fun play(soundId: Int) {
        pool.play(soundId, volume, volume, 1, 0, 1f)
    }

    fun release() = pool.release()
}

@Composable
fun rememberColorMatchSound(): ColorMatchSound {
    val context = LocalContext.current
    val sound = remember { ColorMatchSound(context) }
    DisposableEffect(sound) {
        onDispose { sound.release() }
    }
    return sound
}

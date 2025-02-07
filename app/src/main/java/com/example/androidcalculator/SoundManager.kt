package com.example.androidcalculator

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class SoundManager(context: Context) {
    private val soundPool: SoundPool
    private val buttonSoundId: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        buttonSoundId = soundPool.load(context, R.raw.buttonsound, 1)
    }

    fun playButtonSound() {
        soundPool.play(buttonSoundId, 0.2f, 0.2f, 0, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}

package com.trx.consumer.frozenmountain

import fm.liveswitch.AecPipe
import fm.liveswitch.AudioConfig
import fm.liveswitch.AudioSink
import fm.liveswitch.android.AudioRecordSource
import fm.liveswitch.android.AudioTrackSink
import fm.liveswitch.audioprocessing.AecProcessor
import fm.liveswitch.AecContext as FMAecContext

class AecContext : FMAecContext() {

    override fun createProcessor(): AecPipe {
        val config = AudioConfig(48000, 2)
        return AecProcessor(
            config,
            AudioTrackSink.getBufferDelay(config) + AudioRecordSource.getBufferDelay(config)
        )
    }

    override fun createOutputMixerSink(audioConfig: AudioConfig): AudioSink =
        AudioTrackSink(audioConfig)
}

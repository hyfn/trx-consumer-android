package com.trx.consumer.frozenmountain

import android.content.Context
import com.trx.consumer.extensions.lowerCased
import fm.liveswitch.AecContext
import fm.liveswitch.AudioConfig
import fm.liveswitch.AudioEncoder
import fm.liveswitch.AudioFormat
import fm.liveswitch.AudioSink
import fm.liveswitch.AudioSource
import fm.liveswitch.RtcLocalMedia
import fm.liveswitch.VideoEncoder
import fm.liveswitch.VideoFormat
import fm.liveswitch.VideoPipe
import fm.liveswitch.VideoSink
import fm.liveswitch.android.AudioRecordSource
import fm.liveswitch.yuv.ImageConverter
import fm.liveswitch.matroska.AudioSink as AudioSinkMatroska
import fm.liveswitch.matroska.VideoSink as VideoSinkMatroska
import fm.liveswitch.openh264.Encoder as EncoderH264
import fm.liveswitch.opus.Encoder as EncoderOpus
import fm.liveswitch.vp8.Encoder as EncoderVP8
import fm.liveswitch.vp9.Encoder as EncoderVP9

abstract class LocalMedia<TView>(
    private val context: Context,
    private val enableSoftwareH264: Boolean = false,
    disableAudio: Boolean,
    disableVideo: Boolean,
    aecContext: AecContext
) : RtcLocalMedia<TView>(disableAudio, disableVideo, aecContext) {

    init {
        super.initialize()
    }

    override fun createAudioRecorder(audioFormat: AudioFormat): AudioSink =
        AudioSinkMatroska("$id-local-audio-${audioFormat.name.lowerCased()}.mkv")

    override fun createAudioSource(audioConfig: AudioConfig?): AudioSource =
        AudioRecordSource(context, audioConfig)

    override fun createH264Encoder(): VideoEncoder? =
        if (enableSoftwareH264) EncoderH264() else null

    override fun createImageConverter(videoFormat: VideoFormat): VideoPipe =
        ImageConverter(videoFormat)

    override fun createOpusEncoder(audioConfig: AudioConfig): AudioEncoder =
        EncoderOpus(audioConfig)

    override fun createVideoRecorder(videoFormat: VideoFormat): VideoSink =
        VideoSinkMatroska("$id-local-video-${videoFormat.name.lowerCased()}.mkv")

    override fun createVp8Encoder(): VideoEncoder =
        EncoderVP8()

    override fun createVp9Encoder(): VideoEncoder =
        EncoderVP9()
}

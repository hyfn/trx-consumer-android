package com.trx.consumer.frozenmountain

import android.content.Context
import android.widget.FrameLayout
import com.trx.consumer.extensions.lowerCased
import fm.liveswitch.AecContext
import fm.liveswitch.AudioConfig
import fm.liveswitch.AudioDecoder
import fm.liveswitch.AudioFormat
import fm.liveswitch.AudioSink
import fm.liveswitch.RtcRemoteMedia
import fm.liveswitch.VideoDecoder
import fm.liveswitch.VideoFormat
import fm.liveswitch.VideoPipe
import fm.liveswitch.VideoSink
import fm.liveswitch.ViewSink
import fm.liveswitch.android.AudioTrackSink
import fm.liveswitch.android.OpenGLSink
import fm.liveswitch.yuv.ImageConverter
import fm.liveswitch.matroska.AudioSink as AudioSinkMatroska
import fm.liveswitch.matroska.VideoSink as VideoSinkMatroska
import fm.liveswitch.openh264.Decoder as DecoderH264
import fm.liveswitch.opus.Decoder as DecoderOpus
import fm.liveswitch.vp8.Decoder as DecoderVP8
import fm.liveswitch.vp9.Decoder as DecoderVP9

class RemoteMedia(
    private val context: Context,
    private val enableSoftwareH264: Boolean = false,
    disableAudio: Boolean,
    disableVideo: Boolean,
    aecContext: AecContext
) : RtcRemoteMedia<FrameLayout>(disableAudio, disableVideo, aecContext) {

    init {
        super.initialize()
    }

    override fun createAudioRecorder(audioFormat: AudioFormat): AudioSink =
        AudioSinkMatroska("$id-remote-audio-${audioFormat.name.lowerCased()}.mkv")

    override fun createAudioSink(audioConfig: AudioConfig): AudioSink =
        AudioTrackSink(audioConfig)

    override fun createH264Decoder(): VideoDecoder? =
        if (enableSoftwareH264) DecoderH264() else null

    override fun createImageConverter(videoFormat: VideoFormat): VideoPipe =
        ImageConverter(videoFormat)

    override fun createOpusDecoder(audioConfig: AudioConfig): AudioDecoder =
        DecoderOpus(audioConfig)

    override fun createVideoRecorder(videoFormat: VideoFormat): VideoSink =
        VideoSinkMatroska("$id-remote-video-${videoFormat.name.lowerCased()}.mkv")

    override fun createViewSink(): ViewSink<FrameLayout> =
        OpenGLSink(context)

    override fun createVp8Decoder(): VideoDecoder =
        DecoderVP8()

    override fun createVp9Decoder(): VideoDecoder =
        DecoderVP9()
}

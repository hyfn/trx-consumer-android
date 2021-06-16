package com.trx.consumer.frozenmountain

import android.content.Context
import android.view.View
import fm.liveswitch.AecContext
import fm.liveswitch.LayoutScale
import fm.liveswitch.VideoConfig
import fm.liveswitch.VideoSource
import fm.liveswitch.ViewSink
import fm.liveswitch.android.Camera2Source
import fm.liveswitch.android.CameraPreview

class LocalCameraMedia(
    private val context: Context,
    enableSoftwareH264: Boolean = false,
    disableAudio: Boolean,
    disableVideo: Boolean,
    aecContext: AecContext,
    enableSimulcast: Boolean
) : LocalMedia<View>(context, enableSoftwareH264, disableAudio, disableVideo, aecContext) {

    init {
        this.videoSimulcastDisabled = !enableSimulcast
        super.initialize()
    }

    private val viewSink: CameraPreview by lazy {
        CameraPreview(context, LayoutScale.Contain)
    }

    private val videoConfig: VideoConfig by lazy {
        VideoConfig(640, 480, 30.0)
    }

    override fun createViewSink(): ViewSink<View>? = null

    override fun createVideoSource(): VideoSource =
        Camera2Source(viewSink, videoConfig)

    override fun getView(): View = viewSink.view
}

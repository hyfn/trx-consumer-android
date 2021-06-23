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
    val context: Context,
    val enableSoftwareH264: Boolean = false,
    disableAudio: Boolean,
    disableVideo: Boolean,
    aecContext: AecContext,
    enableSimulcast: Boolean
) : LocalMedia<View>(context, enableSoftwareH264, disableAudio, disableVideo, aecContext) {

    private val viewSink: CameraPreview = CameraPreview(context, LayoutScale.Contain)

    private val videoConfig: VideoConfig = VideoConfig(640, 480, 30.0)

    init {
        this.initialize()
    }

    override fun createViewSink(): ViewSink<View>? = null

    override fun createVideoSource(): VideoSource =
        Camera2Source(viewSink, videoConfig)

    override fun getView(): View = viewSink.view as View
}

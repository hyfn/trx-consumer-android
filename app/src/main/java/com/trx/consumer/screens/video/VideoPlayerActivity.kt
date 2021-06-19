package com.trx.consumer.screens.video

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.event.Event
import com.brightcove.player.event.EventEmitter
import com.brightcove.player.event.EventType
import com.brightcove.player.mediacontroller.BrightcoveMediaController
import com.brightcove.player.mediacontroller.ShowHideController
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcovePlayer
import com.trx.consumer.BuildConfig
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton
import com.trx.consumer.common.CommonImageButton
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.margin
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.VideoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoPlayerActivity : BrightcovePlayer() {

    private lateinit var video: VideoModel
    private var videoSizeKnown = false
    private var videoWidth = 0
    private var videoHeight = 0

    private var orientationJob: Job? = null

    private val currentOrientation
        get() = resources.configuration.orientation

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_video)
        brightcoveVideoView = findViewById(R.id.viewPlayerContainer)
        setMediaController()
        super.onCreate(savedInstanceState)

        bind()
    }

    private fun bind() {
        video = NavigationManager.shared.params(intent) as VideoModel

        findViewById<CommonLabel>(R.id.lblTitle).text = video.name
        findViewById<CommonLabel>(R.id.lblTrainer).text = video.trainer.fullName

        findViewById<CommonButton>(R.id.btnRotate).action {
            rotate(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        }
        findViewById<CommonButton>(R.id.btnEndWorkout).action { finish() }
        findViewById<CommonButton>(R.id.btnScreenCast).action {
            Toast.makeText(
                this,
                R.string.player_screen_cast_coming_soon_message,
                Toast.LENGTH_LONG
            ).show()
        }

        loadPlayer()
        handleOrientation(currentOrientation)
    }

    private fun loadPlayer() {
        val eventEmitter = brightcoveVideoView.eventEmitter
        val catalog = Catalog
            .Builder(eventEmitter, BuildConfig.kBrightcoveAccountId)
            .setPolicy(BuildConfig.kBrightcovePolicyKey)
            .build()
        catalog.findVideoByID(
            video.id,
            object : VideoListener() {
                override fun onVideo(video: Video) {
                    brightcoveVideoView.add(video)
                    brightcoveVideoView.start()
                    if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                        rotate(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                    }
                }
            }
        )
        handleEvents(eventEmitter)
    }

    private fun handleOverlay() {
        val isPortrait = currentOrientation == Configuration.ORIENTATION_PORTRAIT
        findViewById<CommonButton>(R.id.btnEndWorkout).isEnabled = isPortrait
        brightcoveVideoView.apply {
            findViewById<View>(R.id.viewContent).apply {
                if (isPortrait) {
                    margin(left = 15f, right = 15f)
                } else {
                    margin(left = 30f, right = 30f)
                }
            }
            findViewById<View>(R.id.current_time).isVisible = !isPortrait
            findViewById<View>(R.id.end_time).isVisible = !isPortrait
            findViewById<View>(R.id.lblTitle).isVisible = !isPortrait
            findViewById<View>(R.id.lblTrainer).isVisible = !isPortrait
            this.findViewById<View>(R.id.btnEndWorkout).isVisible = !isPortrait
            findViewById<View>(R.id.btnClose).isVisible = !isPortrait
        }
    }

    private fun handleEvents(eventEmitter: EventEmitter) {
        eventEmitter.on(EventType.VIDEO_SIZE_KNOWN) { event ->
            videoWidth = event.getIntegerProperty(Event.VIDEO_WIDTH)
            videoHeight = event.getIntegerProperty(Event.VIDEO_HEIGHT)
            videoSizeKnown = true
            resizeVideo()
        }

        eventEmitter.on(ShowHideController.DID_SHOW_MEDIA_CONTROLS) {
            handleOverlay()
        }

        eventEmitter.on(ShowHideController.DID_HIDE_MEDIA_CONTROLS) {
            handleOverlay()
        }

        eventEmitter.on(EventType.CONFIGURATION_CHANGED) {
            handleOrientation(currentOrientation)
        }
    }

    private fun setMediaController() {
        brightcoveVideoView.apply {
            setMediaController(BrightcoveMediaController(this, R.layout.player_controls_layout))
        }
    }

    // Have to bind every time configuration changes
    private fun bindMediaController() {
        brightcoveVideoView.apply {
            with(video) {
                findViewById<CommonLabel>(R.id.lblTitle).text = name
                findViewById<CommonLabel>(R.id.lblTrainer).text = trainer.fullName
                findViewById<CommonButton>(R.id.btnEndWorkout).action { finish() }
                findViewById<CommonImageButton>(R.id.btnClose).action {
                    rotate(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                }
            }
        }
    }

    private fun rotate(orientation: Int) {
        requestedOrientation = orientation
    }

    private fun handleOrientation(orientation: Int) {
        cancelOrientationJob()

        when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                normalScreen()
                orientationJob = CoroutineScope(Dispatchers.IO).launch {
                    delay(timeMillis = 3000) // Give user time to rotate device
                    withContext(Dispatchers.Main) {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
                    }
                }
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                bindMediaController()
                fullScreen()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
        }
        resizeVideo()
    }

    private fun resizeVideo() {
        if (videoSizeKnown) {
            val aspectRatio = videoHeight / videoWidth
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            val videoWidth = metrics.widthPixels
            val videoHeight = (videoWidth * aspectRatio)
            brightcoveVideoView.renderView?.setVideoSize(videoWidth, videoHeight)
        }
    }

    private fun cancelOrientationJob() {
        orientationJob?.apply { if (isActive) cancel() }
    }

    override fun onDestroy() {
        cancelOrientationJob()
        videoSizeKnown = false

        super.onDestroy()
    }
}

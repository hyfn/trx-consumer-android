package com.trx.consumer.screens.player

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
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
import com.trx.consumer.common.CommonView
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.margin
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.params.PlayerParamsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerActivity : BrightcovePlayer() {

    private lateinit var video: VideoModel

    private var videoSizeKnown = false
    private var videoWidth = 0
    private var videoHeight = 0

    private lateinit var analyticsManager: AnalyticsManager
    private var hasCompleted25 = false

    private var viewBinding: ViewBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_player)
        brightcoveVideoView = findViewById(R.id.viewPlayerContainer)
        setMediaController()
        super.onCreate(savedInstanceState)

        bind()
    }

    private fun bind() {
        val playerParams = NavigationManager.shared.params(intent) as PlayerParamsModel
        playerParams.let {
            video = it.video
            analyticsManager = it.analyticsManager
        }

        analyticsManager.trackPageView(
            this.javaClass.simpleName.replace("Activity", "")
        )

        viewBinding = ViewBinding(
            lblTitle = findViewById(R.id.lblTitle),
            lblTrainer = findViewById(R.id.lblTrainer),
            viewInfo = findViewById(R.id.viewInfo),
            btnEndWorkout = findViewById(R.id.btnEndWorkout),
            btnRotate = findViewById(R.id.btnRotate),
            btnScreenCast = findViewById(R.id.btnScreenCast),
            btnClose = findViewById(R.id.btnClose)
        ).apply {
            btnRotate.action { rotate(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) }
            btnClose.action { rotate(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) }
            btnEndWorkout.action { finish() }
            btnScreenCast.action {
                Toast.makeText(
                    this@PlayerActivity,
                    R.string.player_screen_cast_coming_soon_message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        loadView(video)
        loadPlayer(video.id)

        val orientation = resources.configuration.orientation
        handleRotation(orientation)
    }

    private fun loadView(video: VideoModel) {
        viewBinding?.apply {
            lblTitle.text = video.name
            lblTrainer.text = video.trainer.fullName
        }
    }

    private fun loadPlayer(videoId: String) {
        val eventEmitter = brightcoveVideoView.eventEmitter
        val catalog = Catalog
            .Builder(eventEmitter, BuildConfig.kBrightcoveAccountId)
            .setPolicy(BuildConfig.kBrightcovePolicyKey)
            .build()
        catalog.findVideoByID(
            videoId,
            object : VideoListener() {
                override fun onVideo(video: Video) {
                    brightcoveVideoView.add(video)
                    brightcoveVideoView.start()
                }
            }
        )
        handleEvents(eventEmitter)
    }

    private fun handleOverlay(showingController: Boolean) {
        val isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        viewBinding?.apply {
            lblTitle.apply {
                textSize = if (isPortrait) 32f else 18f
                isVisible = isPortrait || showingController
            }
            lblTrainer.isVisible = isPortrait || showingController
            viewInfo.margin(top = if (isPortrait) 20f else 15f)
            btnEndWorkout.isEnabled = isPortrait
            btnClose.isVisible = !isPortrait && showingController
            brightcoveVideoView.findViewById<CommonLabel>(R.id.current_time).isVisible = !isPortrait
            brightcoveVideoView.findViewById<CommonLabel>(R.id.end_time).isVisible = !isPortrait
            val viewSeekBar = brightcoveVideoView.findViewById<CommonView>(R.id.viewSeekBar)
            if (isPortrait) {
                viewSeekBar.margin(left = 15f, right = 15f, bottom = 0f)
            } else {
                viewSeekBar.margin(left = 100f, right = 100f, bottom = 30f)
            }
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
            handleOverlay(true)
        }

        eventEmitter.on(ShowHideController.DID_HIDE_MEDIA_CONTROLS) {
            handleOverlay(false)
        }

        eventEmitter.on(EventType.PROGRESS) { event ->
            val percent =
                event.getIntegerProperty(Event.PLAYHEAD_POSITION) /
                    event.getIntegerProperty(Event.VIDEO_DURATION).toDouble()

            if ((.25 < percent) && !hasCompleted25) {
                analyticsManager.trackVideoComplete25(video)
                hasCompleted25 = true
            }
        }

        eventEmitter.on(EventType.COMPLETED) {
            analyticsManager.trackVideoComplete100(video)
        }
    }

    private fun setMediaController() {
        brightcoveVideoView.apply {
            setMediaController(BrightcoveMediaController(this, R.layout.player_controls_layout))
        }
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        super.onConfigurationChanged(configuration)

        handleRotation(configuration.orientation)
    }

    private fun rotate(orientation: Int) {
        requestedOrientation = orientation
        CoroutineScope(Dispatchers.IO).launch {
            delay(timeMillis = 5000) // Give user time to rotate device
            withContext(Dispatchers.Main) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
            }
        }
    }

    private fun handleRotation(orientation: Int) {
        when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                normalScreen()
                brightcoveVideoView.margin(top = 60f)
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                fullScreen()
                brightcoveVideoView.margin(top = 0f)
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

    override fun onDestroy() {
        super.onDestroy()

        viewBinding = null
        videoSizeKnown = false
    }

    private class ViewBinding(
        val lblTitle: CommonLabel,
        val lblTrainer: CommonLabel,
        val viewInfo: CommonView,
        val btnEndWorkout: CommonButton,
        val btnRotate: CommonImageButton,
        val btnScreenCast: CommonImageButton,
        val btnClose: CommonImageButton,
    )
}

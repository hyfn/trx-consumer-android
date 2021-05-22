package com.trx.consumer.screens.player

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
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
import com.trx.consumer.common.CommonImageView
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.margin
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.VideoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerActivity : BrightcovePlayer() {

    private var videoSizeKnown = false
    private var videoWidth = 0
    private var videoHeight = 0

    private var viewBinding: ViewBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_player)
        brightcoveVideoView = findViewById(R.id.viewPlayerContainer)
        setMediaController()
        super.onCreate(savedInstanceState)

        bind()
    }

    private fun bind() {
        val video = NavigationManager.shared.params(intent) as VideoModel

        viewBinding = ViewBinding(
            lblTitle = findViewById(R.id.lblTitle),
            lblTrainer = findViewById(R.id.lblTrainer),
            lblParticipants = findViewById(R.id.lblParticipants),
            imgParticipants = findViewById(R.id.imgParticipants),
            btnEndWorkout = findViewById(R.id.btnEndWorkout),
            btnRotate = findViewById(R.id.btnRotate),
            btnScreenCast = findViewById(R.id.btnScreenCast),
        ).apply {
            btnRotate.action { rotate() }
            btnEndWorkout.action { finish() }
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
            // TODO: Implement participants when ready
            lblParticipants.text = getString(R.string.player_participants_label, 0)
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
            imgParticipants.isVisible = isPortrait
            lblParticipants.isVisible = isPortrait
            lblTitle.textSize = if (isPortrait) 32f else 18f
            lblTitle.isVisible = isPortrait || showingController
            lblTrainer.isVisible = isPortrait || showingController
            btnEndWorkout.isEnabled = isPortrait
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
    }

    private fun setMediaController() {
        brightcoveVideoView.apply {
            setMediaController(
                BrightcoveMediaController(this, R.layout.player_controls_layout)
            )
        }
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        super.onConfigurationChanged(configuration)

        handleRotation(configuration.orientation)
    }

    private fun rotate() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        CoroutineScope(Dispatchers.IO).launch {
            delay(timeMillis = 3000) // Give user time to rotate device
            withContext(Dispatchers.Main) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
            }
        }
    }

    private fun handleRotation(orientation: Int) {
        when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                normalScreen()
                brightcoveVideoView.margin(top = 15f)
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
        val imgParticipants: CommonImageView,
        val lblParticipants: CommonLabel,
        val btnEndWorkout: CommonButton,
        val btnRotate: CommonImageButton,
        val btnScreenCast: CommonImageButton
    )
}

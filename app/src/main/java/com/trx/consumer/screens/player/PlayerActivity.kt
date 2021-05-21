package com.trx.consumer.screens.player

import android.os.Bundle
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcovePlayer
import com.trx.consumer.BuildConfig
import com.trx.consumer.R
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.VideoModel

class PlayerActivity : BrightcovePlayer() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val video = NavigationManager.shared.params(intent) as VideoModel
        loadView(video)
        loadPlayer(video.id)
    }

    private fun loadView(video: VideoModel) {
        findViewById<CommonLabel>(R.id.lblTitle).text = video.name
        findViewById<CommonLabel>(R.id.lblTrainer).text = video.trainer.fullName
    }

    private fun loadPlayer(videoId: String) {
        brightcoveVideoView = findViewById(R.id.viewPlayerContainer)
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
    }
}

package com.trx.consumer.screens.player

import android.os.Bundle
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcovePlayer
import com.trx.consumer.BuildConfig
import com.trx.consumer.R
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.VideoModel

class PlayerActivity : BrightcovePlayer() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val video = NavigationManager.shared.params(intent) as VideoModel

        brightcoveVideoView = findViewById(R.id.viewPlayerContainer)
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
                }
            }
        )
    }
}

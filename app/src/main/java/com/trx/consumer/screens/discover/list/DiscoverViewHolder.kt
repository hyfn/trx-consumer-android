package com.trx.consumer.screens.discover.list

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonImageView
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonView
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.extensions.load
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel

class DiscoverViewHolder(view: View) : CommonViewHolder(view) {

    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val lblSubtitle: CommonLabel = view.findViewById(R.id.lblSubtitle)
    private val lblHeader: CommonLabel = view.findViewById(R.id.lblHeader)
    private val imgPoster: CommonImageView = view.findViewById(R.id.imgPoster)
    private val viewMain: CommonView = view.findViewById(R.id.viewMain)

    fun setup(item: VideoModel, listener: DiscoverListener) {
        lblHeader.text = item.videoDuration
        lblTitle.text = item.name
        lblSubtitle.text = item.trainer.displayName
        imgPoster.load(item.poster)
        viewMain.action { listener.doTapDiscoverWorkout(item) }
    }
    fun setup(item: VideosModel, listener: DiscoverListener) {
        lblHeader.text = item.numberOfVideosDisplay
        lblTitle.text = item.title
        lblSubtitle.text = item.trainer.displayName
        imgPoster.load(item.poster)
        viewMain.action { listener.doTapDiscoverCollections(item) }
    }
}

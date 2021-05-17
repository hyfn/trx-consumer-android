package com.trx.consumer.screens.video.list

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonImageView
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonView
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.extensions.load
import com.trx.consumer.models.common.WorkoutModel

class VideoViewHolder(view: View) : CommonViewHolder(view) {

    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val lblSubtitle: CommonLabel = view.findViewById(R.id.lblSubtitle)
    private val lblHeader: CommonLabel = view.findViewById(R.id.lblHeader)
    private val imgPoster: CommonImageView = view.findViewById(R.id.imgPoster)
    private val viewMain: CommonView = view.findViewById(R.id.viewMain)

    fun setup(item: WorkoutModel, listener: VideoListener) {
        lblHeader.text = item.duration
        lblTitle.text = item.video.name
        lblSubtitle.text = item.trainer.displayName
        imgPoster.load(item.video.poster)
        viewMain.action { listener.doTapVideo(item) }
    }
}

package com.trx.consumer.screens.videoworkout

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton
import com.trx.consumer.common.CommonImageView
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.extensions.load
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel

class VideoWorkoutViewHolder(view: View) : CommonViewHolder(view) {

    private val lblWorkout: CommonLabel = view.findViewById(R.id.lblWorkout)
    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val lblSubtitle: CommonLabel = view.findViewById(R.id.lblSubtitle)
    private val imgBg: CommonImageView = view.findViewById(R.id.imgBg)
    private val btnSelect: CommonButton = view.findViewById(R.id.btnSelect)

    fun setup(item: VideoModel, listener: VideoWorkoutListener) {
        lblWorkout.text = item.videoDuration
        lblTitle.text = item.name
        lblSubtitle.text = item.trainer.displayName
        imgBg.load(item.poster)
        btnSelect.action { listener.doTapVideo(item) }
    }

    fun setup(item: VideosModel, listener: VideoWorkoutListener) {
        lblWorkout.text = item.numberOfVideosDisplay
        lblTitle.text = item.title
        lblSubtitle.text = item.trainer.displayName
        imgBg.load(item.poster)
        btnSelect.action { listener.doTapVideos(item) }
    }
}

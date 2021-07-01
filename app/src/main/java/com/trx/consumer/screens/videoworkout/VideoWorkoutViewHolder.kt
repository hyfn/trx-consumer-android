package com.trx.consumer.screens.videoworkout

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton
import com.trx.consumer.common.CommonImageView
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.extensions.applySkeleton
import com.trx.consumer.extensions.px
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel

class VideoWorkoutViewHolder(view: View) : CommonViewHolder(view) {

    private val lblWorkout: CommonLabel = view.findViewById(R.id.lblWorkout)
    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val lblSubtitle: CommonLabel = view.findViewById(R.id.lblSubtitle)
    private val imgBg: CommonImageView = view.findViewById(R.id.imgBg)
    private val btnSelect: CommonButton = view.findViewById(R.id.btnSelect)

    fun setup(item: VideoModel, listener: VideoWorkoutListener) {
        loadSkeletons()
        with(item) {
            lblWorkout.applySkeleton(isSkeleton, videoDuration)
            lblTitle.applySkeleton(isSkeleton, name)
            lblSubtitle.applySkeleton(isSkeleton, trainer.displayName)
            imgBg.applySkeleton(isSkeleton, color = R.color.black, urlString = poster)
            if (!isSkeleton) btnSelect.action { listener.doTapVideo(item) }
        }
    }

    fun setup(item: VideosModel, listener: VideoWorkoutListener) {
        loadSkeletons()
        with(item) {
            lblWorkout.applySkeleton(isSkeleton, numberOfVideosDisplay)
            lblTitle.applySkeleton(isSkeleton, title)
            lblSubtitle.applySkeleton(isSkeleton, trainer.displayName)
            imgBg.applySkeleton(isSkeleton, color = R.color.black, urlString = poster)
            if (!isSkeleton) btnSelect.action { listener.doTapVideos(item) }
        }
    }

    private fun loadSkeletons() {
        lblWorkout.makeSkeleton(width = 50.px, height = 10.px, color = R.color.yellow)
        lblTitle.makeSkeleton(width = 100.px, height = 15.px)
        lblSubtitle.makeSkeleton(width = 140.px, height = 10.px)
    }
}

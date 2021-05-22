package com.trx.consumer.screens.videoworkout

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton
import com.trx.consumer.common.CommonImageView
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.extensions.load
import com.trx.consumer.models.common.VideoModel

class VideoWorkoutViewHolder(view: View) : CommonViewHolder(view) {

    private val lblWorkout: CommonLabel = view.findViewById(R.id.lblWorkout)
    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val lblSubtitle: CommonLabel = view.findViewById(R.id.lblSubtitle)
    private val imgBg: CommonImageView = view.findViewById(R.id.imgBg)
    private val btnSelect: CommonButton = view.findViewById(R.id.btnSelect)

    fun setup(model: VideoModel, listener: VideoWorkoutListener) {
        lblTitle.text = model.name
        lblSubtitle.apply {
            text = context.getString(
                R.string.video_workout_subtitle_prefix_label,
                model.trainer?.fullName
            )
        }
        lblWorkout.text = model.videoDuration
        imgBg.load(model.poster)
        btnSelect.action { listener.doTapSelect(model) }
    }
}

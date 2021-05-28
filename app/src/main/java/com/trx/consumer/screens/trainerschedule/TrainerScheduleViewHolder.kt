package com.trx.consumer.screens.trainerschedule

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.models.common.TrainerScheduleModel

class TrainerScheduleViewHolder(view: View) : CommonViewHolder(view) {
    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val btnPrimary: CommonButton = view.findViewById(R.id.btnPrimary)

    fun setup(model: TrainerScheduleModel, listener: TrainerScheduleListener) {
        lblTitle.text = "10:00 AM"
        btnPrimary.text = "book"
        btnPrimary.action { listener.doTapTrainerSchedule(model) }
    }
}

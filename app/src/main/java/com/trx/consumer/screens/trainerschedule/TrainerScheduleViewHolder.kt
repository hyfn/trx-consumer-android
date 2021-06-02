package com.trx.consumer.screens.trainerschedule

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.models.common.TrainerScheduleModel

class TrainerScheduleViewHolder(view: View) : CommonViewHolder(view) {
    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val btnBook: CommonButton = view.findViewById(R.id.btnBook)
    private val btnSelect: CommonButton = view.findViewById(R.id.btnSelect)

    fun setup(model: TrainerScheduleModel, listener: TrainerScheduleListener) {
        lblTitle.text = "10:00 AM"
        btnBook.text = "book"
        btnSelect.action { listener.doTapClass(model) }
    }
}

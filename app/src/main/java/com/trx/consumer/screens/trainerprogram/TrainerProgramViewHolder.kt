package com.trx.consumer.screens.trainerprogram

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton
import com.trx.consumer.common.CommonImageView
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.extensions.load
import com.trx.consumer.models.common.TrainerProgramModel

class TrainerProgramViewHolder(view: View) : CommonViewHolder(view) {

    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val lblSubTitle: CommonLabel = view.findViewById(R.id.lblSubTitle)
    private val btnPrimary: CommonButton = view.findViewById(R.id.btnPrimary)
    private val imgLogo: CommonImageView = view.findViewById(R.id.imgLogo)

    fun setup(model: TrainerProgramModel, listener: TrainerProgramViewListener) {
        lblTitle.text = model.name
        lblSubTitle.text = model.subtitle
        imgLogo.load(model.image)
        btnPrimary.action { listener.doTapProgram(model) }
    }
}
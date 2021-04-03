package com.trx.consumer.screens.trainerprofile

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton
import com.trx.consumer.common.CommonImageView
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.extensions.load
import com.trx.consumer.models.common.TrainerModel

class TrainerProfileViewHolder(view: View) : CommonViewHolder(view) {

    private val imgPhoto: CommonImageView = view.findViewById(R.id.imgPhoto)
    private val lblName: CommonLabel = view.findViewById(R.id.lblName)
    private val btnSelect: CommonButton = view.findViewById(R.id.btnSelect)

    fun setup(model: TrainerModel, listener: TrainerProfileListener) {
        imgPhoto.load(model.profilePhoto)
        lblName.text(model.firstNameAndLastInitial)
        btnSelect.action { listener.doTapTrainerProfile(model) }
    }
}

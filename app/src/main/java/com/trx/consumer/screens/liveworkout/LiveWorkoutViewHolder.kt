package com.trx.consumer.screens.liveworkout

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton
import com.trx.consumer.common.CommonImageView
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.extensions.load
import com.trx.consumer.models.common.LiveWorkoutModel

class LiveWorkoutViewHolder(view: View) : CommonViewHolder(view) {

    private val imgProfile: CommonImageView = view.findViewById(R.id.imgProfile)
    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val lblSubtitle: CommonLabel = view.findViewById(R.id.lblSubtitle)
    private val lblSubtitle2: CommonLabel = view.findViewById(R.id.lblSubtitle2)
    private val btnBook: CommonButton = view.findViewById(R.id.btnBook)
    private val btnSelect: CommonButton = view.findViewById(R.id.btnSelect)

    fun setup(model: LiveWorkoutModel, listener: LiveWorkoutListener) {
        imgProfile.load(model.trainer.profilePhoto)
        lblTitle.text(model.title)
        lblSubtitle.text(model.duration)
        lblSubtitle2.text(model.dateTime)
        btnBook.text(model.status.buttonTitle)

        btnBook.apply {
            text(context.getString(model.status.buttonTitle))
            action { listener.doTapBook(model) }
        }
        btnSelect.action { listener.doTapSelect(model) }
    }
}

package com.trx.consumer.screens.virtualworkouttable

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton
import com.trx.consumer.common.CommonImageView
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonView
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.extensions.load
import com.trx.consumer.extensions.px
import com.trx.consumer.models.common.VirtualWorkoutModel

class VirtualWorkoutTableViewHolder(view: View) : CommonViewHolder(view) {

    private val viewMain: CommonView = view.findViewById(R.id.viewMain)
    private val imgProfile: CommonImageView = view.findViewById(R.id.imgProfile)
    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val lblSubtitle: CommonLabel = view.findViewById(R.id.lblSubtitle)
    private val btnPrimary: CommonButton = view.findViewById(R.id.btnPrimary)
    private val btnSelect: CommonButton = view.findViewById(R.id.btnSelect)

    fun setup(model: VirtualWorkoutModel, listener: VirtualWorkoutTableListener) {
        model.status.apply {
            viewMain.bgColor(backgroundColor)
            lblTitle.textColor(titleColor)
            lblSubtitle.textColor(subtitleColor)

            btnPrimary.apply {
                text = context.getString(buttonTitle)
                textColor(buttonTitleColor)
                bgColor(buttonBackgroundColor)
                layoutParams.width = buttonWidth.toInt().px
                requestLayout()
            }
        }

        imgProfile.load(model.imageUrl)
        lblTitle.text = model.title
        lblSubtitle.text = model.subtitle

        btnPrimary.action { listener.doTapPrimary(model) }
        btnSelect.action { listener.doTapSelect(model) }
    }
}

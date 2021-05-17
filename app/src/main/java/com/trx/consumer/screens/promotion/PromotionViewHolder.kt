package com.trx.consumer.screens.promotion

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton
import com.trx.consumer.common.CommonImageView
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.extensions.load
import com.trx.consumer.extensions.upperCased
import com.trx.consumer.models.common.PromoModel

class PromotionViewHolder(view: View) : CommonViewHolder(view) {

    private val imgBg: CommonImageView = view.findViewById(R.id.imgBg)
    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val btnSelect: CommonButton = view.findViewById(R.id.btnSelect)

    fun setup(model: PromoModel, listener: PromotionListener) {
        imgBg.load(model.imageUrl)
        lblTitle.text(model.title.upperCased())
        btnSelect.action { listener.doTapPromo(model) }
    }
}

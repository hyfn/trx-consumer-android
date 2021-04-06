package com.trx.consumer.screens.plans.list

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.models.common.PlansListModel

class PlansListViewHolder(view: View) : CommonViewHolder(view) {

    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val lblPrice: CommonLabel = view.findViewById(R.id.lblPrice)
    private val lblDescription: CommonLabel = view.findViewById(R.id.lblDescription)
    private val btnPlan: CommonButton = view.findViewById(R.id.btnPlan)

    fun setup(item: PlansListModel, listener: PlansListListener) {
        lblPrice.text(item.priceText)
        lblDescription.text(item.description)
        btnPlan.apply {
            text = itemView.context.getString(item.state.buttonText)
            textColor(item.state.buttonTextColor)
            bgColor(item.state.buttonBgColor)
            action { listener.doTapPlan(item) }
        }
    }
}

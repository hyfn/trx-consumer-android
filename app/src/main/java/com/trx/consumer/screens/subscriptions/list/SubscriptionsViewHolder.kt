package com.trx.consumer.screens.subscriptions.list

import android.view.View
import androidx.core.view.isVisible
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.models.common.SubscriptionModel

class SubscriptionsViewHolder(view: View) : CommonViewHolder(view) {

    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val lblPrice: CommonLabel = view.findViewById(R.id.lblPrice)
    private val lblDescription: CommonLabel = view.findViewById(R.id.lblDescription)
    private val btnPrimary: CommonButton = view.findViewById(R.id.btnPrimary)

    fun setup(item: SubscriptionModel, listener: SubscriptionsListener) {
        lblTitle.text = item.title

        if (item.primaryState == SubscriptionsViewState.CURRENT)
            lblPrice.isVisible = false
        else
            lblPrice.text = item.cost

        lblDescription.text = item.description

        btnPrimary.apply {
            text = itemView.context.getString(item.primaryState.buttonText)
            textColor(item.primaryState.buttonTextColor)
            bgColor(item.primaryState.buttonBgColor)
            action { listener.doTapSubscription(item) }
        }
    }
}

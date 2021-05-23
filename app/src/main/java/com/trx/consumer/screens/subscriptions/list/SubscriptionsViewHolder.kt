package com.trx.consumer.screens.subscriptions.list

import android.view.View
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

    fun setup(
        item: SubscriptionModel,
        subscriptionsViewState: SubscriptionsViewState,
        listener: SubscriptionsListener
    ) {
        lblTitle.text = item.title
        lblPrice.text = item.cost
        lblDescription.text = item.description
        btnPrimary.apply {
            text = itemView.context.getString(subscriptionsViewState.buttonText)
            textColor(subscriptionsViewState.buttonTextColor)
            bgColor(subscriptionsViewState.buttonBgColor)
            action { listener.doTapSubscription(item) }
        }
    }
}

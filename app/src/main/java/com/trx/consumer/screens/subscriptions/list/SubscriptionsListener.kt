package com.trx.consumer.screens.subscriptions.list

import com.trx.consumer.models.common.SubscriptionModel

interface SubscriptionsListener {
    fun doTapSubscription(model: SubscriptionModel)
}

package com.trx.consumer.screens.subscriptions.list

import com.trx.consumer.models.common.iap.SubscriptionModel

interface SubscriptionsListener {
    fun doTapSubscription(model: SubscriptionModel)
}

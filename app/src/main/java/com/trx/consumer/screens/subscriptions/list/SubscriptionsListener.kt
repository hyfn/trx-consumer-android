package com.trx.consumer.screens.subscriptions.list

import com.trx.consumer.models.common.PlanModel

interface SubscriptionsListener {
    fun doTapSubscription(model: PlanModel)
}

package com.trx.consumer.screens.subscriptions

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.SubscriptionModel
import com.trx.consumer.models.common.SubscriptionsModel
import com.trx.consumer.screens.subscriptions.list.SubscriptionsListener

class SubscriptionsViewModel : BaseViewModel(), SubscriptionsListener {

    var model: SubscriptionsModel = SubscriptionsModel()

    val eventLoadView = CommonLiveEvent<SubscriptionsModel>()
    val eventTapBack = CommonLiveEvent<Void>()

    fun doLoadView() {
        eventLoadView.postValue(model)
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    override fun doTapSubscription(model: SubscriptionModel) {
    }
}

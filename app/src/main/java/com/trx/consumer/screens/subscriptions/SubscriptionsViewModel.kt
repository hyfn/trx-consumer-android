package com.trx.consumer.screens.subscriptions

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.SubscriptionModel
import com.trx.consumer.screens.subscriptions.list.SubscriptionsListener

class SubscriptionsViewModel : BaseViewModel(), SubscriptionsListener {

    val eventLoadView = CommonLiveEvent<List<SubscriptionModel>>()
    val eventTapBack = CommonLiveEvent<Void>()

    fun doLoadView() {
        eventLoadView.postValue(SubscriptionModel.testList(1))
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    override fun doTapSubscription(model: SubscriptionModel) {
    }
}

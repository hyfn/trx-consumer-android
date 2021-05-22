package com.trx.consumer.screens.subscriptions

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.PlanModel
import com.trx.consumer.screens.subscriptions.list.SubscriptionsListener

class SubscriptionsViewModel : BaseViewModel(), SubscriptionsListener {

    val eventLoadView = CommonLiveEvent<List<PlanModel>>()
    val eventTapBack = CommonLiveEvent<Void>()

    fun doLoadView() {
        eventLoadView.postValue(PlanModel.testList(5))
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    override fun doTapSubscription(model: PlanModel) {
    }
}

package com.trx.consumer.screens.subscriptions

import androidx.hilt.lifecycle.ViewModelInject
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.models.common.AnalyticsEventModel
import com.trx.consumer.models.common.SubscriptionModel
import com.trx.consumer.models.common.SubscriptionsModel
import com.trx.consumer.screens.subscriptions.list.SubscriptionsListener

class SubscriptionsViewModel @ViewModelInject constructor(
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(), SubscriptionsListener {

    var model: SubscriptionsModel = SubscriptionsModel()

    val eventLoadView = CommonLiveEvent<SubscriptionsModel>()
    val eventTapBack = CommonLiveEvent<Void>()

    fun doLoadView() {
        analyticsManager.trackAmplitude(
            AnalyticsEventModel.PAGE_VIEW,
            this.javaClass.simpleName.replace("ViewModel", "")
        )
        eventLoadView.postValue(model)
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    override fun doTapSubscription(model: SubscriptionModel) {
    }
}

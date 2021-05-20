package com.trx.consumer.screens.plans

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.PlanModel
import com.trx.consumer.screens.plans.list.PlansListener

class PlansViewModel : BaseViewModel(), PlansListener {

    val eventLoadView = CommonLiveEvent<List<PlanModel>>()
    val eventTapBack = CommonLiveEvent<Void>()

    fun doLoadView() {
        eventLoadView.postValue(PlanModel.testList(5))
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    override fun doTapPlan(model: PlanModel) {
    }
}

package com.trx.consumer.screens.plans

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.models.common.PlanModel
import com.trx.consumer.screens.plans.list.PlansListener
import kotlinx.coroutines.launch

class PlansViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val cacheManager: CacheManager
) : BaseViewModel(), PlansListener {

    //region Events

    val eventLoadCanCancel = CommonLiveEvent<Boolean>()
    val eventLoadCancelSubscription = CommonLiveEvent<String?>()
    val eventLoadConfirmSubscription = CommonLiveEvent<PlanModel>()
    val eventLoadError = CommonLiveEvent<String>()
    val eventLoadNextBillDate = CommonLiveEvent<String>()
    val eventLoadView = CommonLiveEvent<Void>()
    val eventLoadPlans = CommonLiveEvent<List<PlanModel>>()

    val eventTapBack = CommonLiveEvent<Void>()

    //endregion

    //region Actions

    fun doLoadView() {
        eventLoadView.call()
    }

    fun doLoadPlans() {
    }

    fun doCallSubscribe() {
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapUnSubscribe() {
    }

    override fun doTapChoosePlan(model: PlanModel) {
        viewModelScope.launch {
            cacheManager.user()?.let { user ->
            }
        }
    }

    //endregion
}

package com.trx.consumer.screens.alert

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.AlertModel

class AlertViewModel : BaseViewModel() {

    lateinit var model: AlertModel

    val eventLoadView = CommonLiveEvent<AlertModel>()

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapPrimary = CommonLiveEvent<(() -> Unit)>()
    val eventTapSecondary = CommonLiveEvent<(() -> Unit)>()

    fun doLoadView(model: AlertModel) {
        this.model = model
        eventLoadView.postValue(model)
    }

    fun doTapPrimary() {
        model.primaryMethod?.let { eventTapPrimary.postValue(it) } ?: eventTapBack.call()
    }

    fun doTapSecondary() {
        model.secondaryMethod?.let { eventTapSecondary.postValue(it) } ?: eventTapBack.call()
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun onBackPressed() {
        when (model.backPressAction) {
            AlertBackAction.PRIMARY -> model.primaryMethod?.let { eventTapPrimary.postValue(it) }
            AlertBackAction.SECONDARY -> model.secondaryMethod?.let {
                eventTapSecondary.postValue(it)
            }
            else -> eventTapBack.call()
        }
    }
}

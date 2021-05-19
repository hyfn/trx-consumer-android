package com.trx.consumer.screens.settings

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.UserModel
import com.trx.consumer.screens.settings.option.SettingsOptionListener

class SettingsViewModel : BaseViewModel(), SettingsOptionListener {

    val eventTapBack = CommonLiveEvent<Void>()
    val eventLoadView = CommonLiveEvent<List<Any>>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        eventLoadView.postValue(SettingsModel.list(UserModel.test()))
    }

    override fun doTapSettings(model: SettingsModel) {
    }
}

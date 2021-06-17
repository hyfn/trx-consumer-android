package com.trx.consumer.screens.liveplayer

import androidx.hilt.lifecycle.ViewModelInject
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.WorkoutModel

class LivePlayerViewModel @ViewModelInject constructor (
    private val backendManager: BackendManager
) : BaseViewModel(), LivePlayerListener {

    val eventLoadError = CommonLiveEvent<String>()
    val eventLoadVideo = CommonLiveEvent<WorkoutModel>()
    val eventTapClose = CommonLiveEvent<Void>()

    fun doLoadVideo() {
        LogManager.log("doLoadVideo")
    }

    fun doTapClose() {
        eventTapClose.call()
    }

    override fun doReceiveMessage(clientId: String, message: String) {
        LogManager.log("doReceiveMessage")
    }

    override fun doReportError(message: String) {
        LogManager.log("doReportError")
    }
}

package com.trx.consumer.screens.maintenance

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent

class MaintenanceViewModel : BaseViewModel() {

    var state = MaintenanceViewState.MAINTENANCE

    var eventLoadView = CommonLiveEvent<MaintenanceViewState>()
    var eventTapButton = CommonLiveEvent<Unit>()

    fun doLoadView() {
        eventLoadView.postValue(state)
    }

    fun doTapButton() {
        eventTapButton.call()
    }
}

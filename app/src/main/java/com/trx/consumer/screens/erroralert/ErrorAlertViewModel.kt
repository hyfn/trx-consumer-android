package com.trx.consumer.screens.erroralert

import androidx.lifecycle.MutableLiveData
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent

class ErrorAlertViewModel : BaseViewModel() {

    //region Objects
    var model: ErrorAlertModel = ErrorAlertModel()

    //endregion

    //region Events
    var eventLoadView = MutableLiveData<ErrorAlertModel>()
    var eventTapOutside = CommonLiveEvent<Void>()
    var eventTapDismiss = CommonLiveEvent<Void>()

    //endregion

    //region Functions
    fun doLoadView(model: ErrorAlertModel) {
        this.model = model
        eventLoadView.postValue(model)
    }

    fun doTapDismiss() {
        eventTapDismiss.call()
    }

    fun doTapOutside() {
        eventTapOutside.call()
    }

    //endregion
}

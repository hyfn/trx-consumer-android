package com.trx.consumer.screens.addcard

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.PurchaseModel

class AddCardViewModel : BaseViewModel() {

    //region Objects

    var purchase: PurchaseModel? = null

    //endregion

    //region Variables

    var cardNumber: String = ""
    var expirationDate: String = ""
    var cvcNumber: String = ""
    var zipCode: String = ""
    var checked = false

    //endregion

    //region Events

    var eventLoadButton = CommonLiveEvent<Boolean>()
    var eventLoadNavBar = CommonLiveEvent<Boolean>()

    var eventTapBack = CommonLiveEvent<Void>()
    var eventTapClose = CommonLiveEvent<Void>()

    var eventSaveSuccess = CommonLiveEvent<Void>()
    var eventSaveError = CommonLiveEvent<Void>()
    var eventValidateError = CommonLiveEvent<String>()
    var eventShowPurchase = CommonLiveEvent<PurchaseModel>()
    var eventDismissKeyboard = CommonLiveEvent<Void>()

    //endregion

    //region Actions

    fun doTapBack() {
        eventTapBack.call()
    }

    //endregion
}

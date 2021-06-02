package com.trx.consumer.screens.addcard

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.pageTitle
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.CardModel
import com.trx.consumer.models.common.PurchaseModel
import com.trx.consumer.stripe.StripeBackendManager
import com.trx.consumer.views.input.InputViewListener
import com.trx.consumer.views.input.InputViewState
import kotlinx.coroutines.launch

class AddCardViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val stripeBackendManager: StripeBackendManager,
    private val cacheManager: CacheManager,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(), InputViewListener {

    //region Objects

    private var purchase: PurchaseModel? = null

    //endregion

    //region Variables

    private var cardNumber: String = ""
    private var expirationDate: String = ""
    private var cvcNumber: String = ""
    private var zipCode: String = ""

    private val card: CardModel?
        get() {
            if (expirationDate.isEmpty()) return null
            val expMonth = expirationDate.substringBefore("/").replaceFirst("0", "")
            val expYear = expirationDate.substringAfter("/")
            return CardModel(
                number = cardNumber,
                securityCode = cvcNumber,
                expMonth = expMonth,
                expYear = expYear,
                zip = zipCode
            )
        }

    private var paymentMethodId: String? = null

    //endregion

    //region Events

    val eventLoadNavBar = CommonLiveEvent<Boolean>()
    val eventLoadButton = CommonLiveEvent<Boolean>()

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapClose = CommonLiveEvent<Void>()

    val eventSaveSuccess = CommonLiveEvent<Void>()
    val eventSaveError = CommonLiveEvent<String>()
    val eventValidateError = CommonLiveEvent<String>()
    val eventShowPurchase = CommonLiveEvent<PurchaseModel>()
    val eventDismissKeyboard = CommonLiveEvent<Void>()

    val eventShowHud = CommonLiveEvent<Boolean>()

    //endregion

    //region Actions

    fun doLoadView() {
        viewModelScope.launch {
            analyticsManager.trackPageView(pageTitle)
            eventLoadNavBar.postValue(purchase != null)
            eventLoadButton.postValue(false)
            eventShowHud.postValue(true)
            backendManager.user().let {
                paymentMethodId = cacheManager.user()?.cardPaymentMethodId
            }
            eventShowHud.postValue(false)
        }
    }

    private fun doCallAddPayment(id: String) {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.paymentAdd(id)
            eventShowHud.postValue(false)
            if (response.isSuccess) eventSaveSuccess.call()
            else eventSaveError.postValue("There was an error saving your card.")
        }
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapClose() {
        eventTapClose.call()
    }

    fun doTapSave() {
        viewModelScope.launch {
            if (!InputViewState.EXPIRATION.expirationValidation(expirationDate)) {
                eventValidateError.postValue(
                    "It looks like your card is expired. Check your information and try again."
                )
            } else {
                eventShowHud.postValue(true)
                card?.let {
                    try {
                        val response = stripeBackendManager.createPaymentMethod(it)
                        response?.let { id ->
                            paymentMethodId?.let { currentId ->
                                val deleteResponse = backendManager.paymentDelete(currentId)
                                if (deleteResponse.isSuccess) {
                                    doCallAddPayment(currentId)
                                } else {
                                    eventSaveError.postValue("There was an error updating your card")
                                }
                            } ?: run {
                                doCallAddPayment(id)
                            }
                        } ?: run {
                            eventSaveError.postValue("There was an error updating your card")
                        }
                    } catch (e: Exception) {
                        LogManager.log(e)
                        e.message?.let { error -> eventSaveError.postValue(error) }
                    }
                }
                eventShowHud.postValue(false)
            }
        }
    }

    override fun doUpdateText(
        userInput: String,
        isValidInput: Boolean,
        identifier: InputViewState
    ) {
        when (identifier) {
            InputViewState.CARD_NUMBER -> cardNumber = if (isValidInput) userInput else ""
            InputViewState.EXPIRATION -> expirationDate = if (isValidInput) userInput else ""
            InputViewState.CVV -> cvcNumber = if (isValidInput) userInput else ""
            InputViewState.ZIPCODE -> zipCode = if (isValidInput) userInput else ""
            else -> { }
        }
        validate()
    }

    private fun validate() {
        val enabled: Boolean = InputViewState.CARD_NUMBER.validate(cardNumber) &&
            InputViewState.EXPIRATION.validate(expirationDate) &&
            InputViewState.CVV.validate(cvcNumber) &&
            InputViewState.ZIPCODE.validate(zipCode)
        eventLoadButton.postValue(enabled)
    }

    fun doDismissKeyboard() {
        eventDismissKeyboard.call()
    }

    //endregion
}

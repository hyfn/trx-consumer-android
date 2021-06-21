package com.trx.consumer.screens.subscriptions

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.managers.IAPManager
import com.trx.consumer.models.common.IAPModel.Companion.ENTITLEMENT
import com.trx.consumer.models.common.PurchaseEntitlementModel
import com.trx.consumer.models.common.SubscriptionModel
import com.trx.consumer.models.core.ResponseModel
import com.trx.consumer.models.responses.PurchasesResponseModel
import com.trx.consumer.screens.subscriptions.list.SubscriptionsListener
import com.trx.consumer.screens.subscriptions.list.SubscriptionsViewState
import kotlinx.coroutines.launch

class SubscriptionsViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val cacheManager: CacheManager
) : BaseViewModel(), SubscriptionsListener {

    // TODO: Need to handle the following scenarios:
    //   - Play Store purchase was successful but the purchase was never successfully sent to backend
    //   - Check if a purchase was cancelled before displaying as current subscription
    //   - Test functionality once backend returns purchases

    val eventLoadCanCancel = CommonLiveEvent<Boolean>()
    val eventLoadError = CommonLiveEvent<String>()
    val eventLoadView = CommonLiveEvent<Void>()
    val eventLoadSubscriptions = CommonLiveEvent<List<SubscriptionModel>>()

    val eventLoadLastBillDate = CommonLiveEvent<String>()
    val eventLoadNextBillDate = CommonLiveEvent<String>()

    val eventTapChooseSubscription = CommonLiveEvent<SubscriptionModel>()
    val eventTapCancel = CommonLiveEvent<Void>()
    val eventTapSettings = CommonLiveEvent<Void>()
    val eventTapBack = CommonLiveEvent<Void>()

    val eventShowHud = CommonLiveEvent<Boolean>()

    fun doLoadView() {
        eventLoadView.call()
        doLoadSubscriptions()
    }

    private fun doLoadSubscriptions() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.purchases()
            if (!response.isSuccess) {
                eventLoadError.postValue("Could not load purchases")
                return@launch
            }

            val model = PurchasesResponseModel.parse(response.responseString)
            val packages = IAPManager.shared.packages()
            val entitlement = model.onDemandSubscription()

            entitlement?.let {
                val match = packages.find {
                    it.iapPackage.product.sku == entitlement.productIdentifier
                }
                match?.let {
                    doReloadView(subscription = match, entitlement = entitlement)
                } ?: eventLoadError.postValue("No matching package")
            } ?: run {
                eventLoadSubscriptions.postValue(packages)
                eventLoadCanCancel.postValue(false)

                cacheManager.user()?.let { user ->
                    user.iap = ""
                    cacheManager.user(user)
                }
            }
        }.invokeOnCompletion { eventShowHud.postValue(false) }
    }

    fun doCallSubscribe(activity: Activity, value: SubscriptionModel) {
//        viewModelScope.launch {
//            eventShowHud.postValue(true)
//            val model = IAPManager.shared.purchase(activity, value)
//            if (model.error != null) {
//                model.purchaserInfo
//                eventLoadError.postValue("There was a Play Store error")
//                return@launch
//            }
//
//            val params = model.paramsPurchase(value)
//            if (params.isEmpty()) {
//                eventLoadError.postValue("There was a purchase information error")
//            }
//
//            val response = backendManager.purchase(params)
//            doReloadView(response)
//        }.invokeOnCompletion { eventShowHud.postValue(false) }
    }

    private fun doReloadView(response: ResponseModel) {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            if (response.isSuccess) {
                val model = PurchasesResponseModel.parse(response.responseString)
                val productId = model.purchase.entitlements[ENTITLEMENT]?.productIdentifier
                productId?.let {
                    val packages = IAPManager.shared.packages()
                    val match = packages.find {
                        it.iapPackage.product.sku == productId
                    }
                    val entitlement = model.purchase.entitlements[ENTITLEMENT]
                    if (match == null || entitlement == null) {
                        eventLoadError.postValue("No matching package for $productId")
                    } else {
                        doReloadView(subscription = match, entitlement = entitlement)
                    }
                }
            } else {
                eventLoadError.postValue("There was an error with purchasing")
            }
        }.invokeOnCompletion { eventShowHud.postValue(false) }
    }

    private fun doReloadView(subscription: SubscriptionModel, entitlement: PurchaseEntitlementModel) {
        subscription.primaryState = SubscriptionsViewState.CURRENT
        eventLoadSubscriptions.postValue(listOf(subscription))
        eventLoadLastBillDate.postValue(entitlement.purchaseDisplay)
        eventLoadNextBillDate.postValue(entitlement.expiresDisplay)
        eventLoadCanCancel.postValue(true)

        viewModelScope.launch {
            cacheManager.user()?.let { user ->
                user.iap = subscription.title
                cacheManager.user(user)
            }
        }
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapCancel() {
        eventTapCancel.call()
    }

    override fun doTapSubscription(model: SubscriptionModel) {
        if (model.primaryState != SubscriptionsViewState.CURRENT) {
            eventTapChooseSubscription.postValue(model)
        }
    }

    fun doTapRestore() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val model = IAPManager.shared.restore()
            if (model.hasOnDemandSubscription) {
                val params = model.paramsRestore()
                if (params.isEmpty()) {
                    eventLoadError.postValue("There was a purchase information error")
                    return@launch
                }
                val response = backendManager.purchase(params = params)
                doReloadView(response)
            } else {
                eventLoadError.postValue("Restore failed (did not match entitlement)")
            }
        }.invokeOnCompletion { eventShowHud.postValue(false) }
    }

    fun doTapUnsubscribe() {
        eventTapSettings.call()
    }
}

package com.trx.consumer.screens.subscriptions

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.managers.IAPManager
import com.trx.consumer.models.common.SubscriptionModel
import com.trx.consumer.models.common.SubscriptionsModel
import com.trx.consumer.models.common.iap.IAPModel.Companion.ENTITLEMENT
import com.trx.consumer.models.common.iap.PurchaseEntitlementModel
import com.trx.consumer.models.core.ResponseModel
import com.trx.consumer.models.responses.PurchasesResponseModel
import com.trx.consumer.screens.plans.list.PlansViewState
import com.trx.consumer.screens.subscriptions.list.SubscriptionsListener
import kotlinx.coroutines.launch

class SubscriptionsViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val cacheManager: CacheManager,
) : BaseViewModel(), SubscriptionsListener {

    var model: SubscriptionsModel = SubscriptionsModel()

    val eventLoadCanCancel = CommonLiveEvent<Boolean>()
    val eventLoadCancel = CommonLiveEvent<Void>()
    val eventLoadConfirm = CommonLiveEvent<SubscriptionModel>()
    val eventLoadError = CommonLiveEvent<String>()
    val eventLoadView = CommonLiveEvent<Void>()
    val eventLoadSubscriptions = CommonLiveEvent<List<SubscriptionModel>>()

    val eventLoadLastBillDate = CommonLiveEvent<String>()
    val eventLoadNextBillDate = CommonLiveEvent<String>()

    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapSettings = CommonLiveEvent<Void>()

    val eventShowHud = CommonLiveEvent<Boolean>()

    fun doLoadView() {
        eventLoadView.call()
    }

    fun doLoadSubscriptions() {
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
        }
    }

    fun doCallSubscribe(activity: Activity, value: SubscriptionModel) {
        viewModelScope.launch {
            val model = IAPManager.shared.purchase(activity, value)
            if (model.error != null) {
                model.purchaserInfo
                eventLoadError.postValue("There was a Play Store error")
                return@launch
            }

            val params = model.paramsPurchase(value)
            if (params.isEmpty()) {
                eventLoadError.postValue("There was a receipt or purchase information error")
            }

            val response = backendManager.purchase(params)
            doReloadView(response)
        }
    }

    fun doReloadView(response: ResponseModel) {
        viewModelScope.launch {
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
        }
    }

    fun doReloadView(subscription: SubscriptionModel, entitlement: PurchaseEntitlementModel) {
        subscription.primaryState = PlansViewState.CURRENT
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
        eventLoadCancel.call()
    }

    override fun doTapSubscription(model: SubscriptionModel) {
        eventLoadConfirm.postValue(model)
    }

    fun doTapRestore() {
        viewModelScope.launch {
            val model = IAPManager.shared.restore()
            if (model.hasOnDemandSubscription) {
                val params = model.paramsRestore()
                if (params.isEmpty()) {
                    eventLoadError.postValue("There was a receipt or purchase information error")
                    return@launch
                }
                val response = backendManager.purchase(params = params)
                doReloadView(response)
            } else {
                eventLoadError.postValue("Restore failed (did not match entitlement)")
            }
        }
    }

    fun doTapUnsubscribe() {
        eventTapSettings.call()
    }
}

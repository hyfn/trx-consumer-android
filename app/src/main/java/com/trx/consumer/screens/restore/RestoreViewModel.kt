package com.trx.consumer.screens.restore

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.params
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.managers.IAPManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NativePurchaseManager
import com.trx.consumer.models.common.AnalyticsPageModel
import com.trx.consumer.models.common.IAPErrorModel
import com.trx.consumer.models.core.ResponseModel
import com.trx.consumer.models.responses.MembershipsResponseModel
import com.trx.consumer.models.responses.UserResponseModel
import com.trx.consumer.screens.welcome.WelcomeState
import kotlinx.coroutines.launch

class RestoreViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val cacheManager: CacheManager,
    private val nativePurchaseManager: NativePurchaseManager,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel() {

    var isFromOnboarding = true

    val eventLoadView = CommonLiveEvent<WelcomeState>()
    val eventTapClose = CommonLiveEvent<Void>()
    val eventShowAlertError = CommonLiveEvent<String>()
    val eventShowAlertSuccess = CommonLiveEvent<Void>()
    val eventShowHome = CommonLiveEvent<String>()
    val eventShowHud = CommonLiveEvent<Boolean>()

    fun doLoadView() {
        viewModelScope.launch {
            cacheManager.didShowRestore(true)
        }
    }

    fun doTapRestore() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val membershipResponse = backendManager.memberships()
            if (!membershipResponse.isSuccess) {
                eventShowAlertError.postValue(IAPErrorModel.RESTORE.display)
                return@launch
            }
            val userResponse = backendManager.user()
            if (!userResponse.isSuccess) {
                eventShowAlertError.postValue(IAPErrorModel.RESTORE.display)
                return@launch
            }
            try {
                val membershipsResponseModel = MembershipsResponseModel.parse(
                    membershipResponse.responseString
                )
                val userMemberships = UserResponseModel
                    .parse(userResponse.responseString)
                    .user
                    .activeMemberships
                val packages = IAPManager.shared.offerings().packages

                val trxProducts = membershipsResponseModel.memberships(userMemberships)
                val trxUserProducts = trxProducts.filter { it.isActive }.map { it.revcatProductId }
                val nativeProducts = nativePurchaseManager.purchases().map { it.sku }
                val rcProducts = IAPManager.shared.purchases().purchaserInfo?.activeSubscriptions
                    ?: emptyList()

                val needsUpdateProducts = nativeProducts - trxUserProducts
                val needsRestoreProducts = nativeProducts - rcProducts

                if (needsUpdateProducts.isNotEmpty()) {
                    if (needsRestoreProducts.isNotEmpty()) {
                        val result = IAPManager.shared.restore()
                        if (result.error != null) {
                            eventShowAlertError.postValue(IAPErrorModel.RESTORE.display)
                            return@launch
                        }
                    }
                    var backendError = ""

                    needsUpdateProducts.forEach { productId ->
                        val matchingPackage = packages.firstOrNull { it.product.sku == productId }
                        val membership = trxProducts.firstOrNull { it.revcatProductId == productId }
                        if (matchingPackage != null && membership != null) {
                            val params = matchingPackage.params(membership.key)
                            val response = backendManager.membershipAdd(params)
                            if (!response.isSuccess) {
                                backendError = IAPErrorModel.MEMBERSHIP_ADD_RESTORE.display
                            }
                        }
                    }
                    backendManager.user()
                    if (backendError.isNotEmpty()) {
                        eventShowAlertError.postValue(backendError)
                    } else {
                        if (isFromOnboarding) {
                            eventShowHome.postValue("Purchases restored successfully")
                        } else {
                            eventShowAlertSuccess.call()
                        }
                    }
                } else {
                    eventShowAlertError.postValue(IAPErrorModel.NEEDS_UPDATE.display)
                }
            } catch (e: Exception) {
                LogManager.log(e)
                eventShowAlertError.postValue(ResponseModel.parseErrorMessage)
            }
        }.invokeOnCompletion { eventShowHud.postValue(false) }
    }

    fun doTapBack() {
        doTapClose()
    }

    fun doTapClose() {
        if (isFromOnboarding) {
            eventShowHome.postValue("You will be taken to Home")
        } else {
            eventTapClose.call()
        }
    }

    fun doTrackPageView() {
        analyticsManager.trackPageView(AnalyticsPageModel.RESTORE)
    }
}

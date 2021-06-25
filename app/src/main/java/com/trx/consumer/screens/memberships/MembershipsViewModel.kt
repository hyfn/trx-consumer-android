package com.trx.consumer.screens.memberships

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.params
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.IAPManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NativePurchaseManager
import com.trx.consumer.models.common.AnalyticsPageModel.MEMBERSHIPS
import com.trx.consumer.models.common.IAPErrorModel
import com.trx.consumer.models.common.MembershipModel
import com.trx.consumer.models.core.ResponseModel
import com.trx.consumer.models.responses.MembershipsResponseModel
import com.trx.consumer.models.responses.UserResponseModel
import com.trx.consumer.screens.memberships.list.MembershipListener
import kotlinx.coroutines.launch

class MembershipsViewModel @ViewModelInject constructor(
    private val analyticsManager: AnalyticsManager,
    private val backendManager: BackendManager,
    private val nativePurchaseManager: NativePurchaseManager,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(), MembershipListener {

    val eventLoadView = CommonLiveEvent<List<MembershipModel>>()
    val eventLoadError = CommonLiveEvent<String>()
    val eventTapChooseMembership = CommonLiveEvent<MembershipModel>()
    val eventShowHud = CommonLiveEvent<Boolean>()
    val eventShowCancelActive = CommonLiveEvent<Void>()
    val eventShowCancelMobile = CommonLiveEvent<Void>()
    val eventShowCancelWeb = CommonLiveEvent<Void>()
    val eventShowRestore = CommonLiveEvent<Void>()
    val eventTapBack = CommonLiveEvent<Void>()

    private var memberships: List<MembershipModel> = emptyList()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val membershipResponse = backendManager.memberships()
            if (!membershipResponse.isSuccess) {
                eventLoadError.postValue(IAPErrorModel.MEMBERSHIPS.display)
                return@launch
            }
            val userResponse = backendManager.user()
            if (!userResponse.isSuccess) {
                eventLoadError.postValue(IAPErrorModel.USER.display)
                return@launch
            }
            try {
                val membershipsResponseModel = MembershipsResponseModel.parse(
                    membershipResponse.responseString
                )
                val user = UserResponseModel.parse(userResponse.responseString).user
                memberships = membershipsResponseModel.memberships(user.activeMemberships)
                eventLoadView.postValue(memberships)
            } catch (e: Exception) {
                LogManager.log(e)
                eventLoadError.postValue(ResponseModel.parseErrorMessage)
            }
        }.invokeOnCompletion {
            eventShowHud.postValue(false)
        }
    }

    fun doCallSubscribe(activity: Activity, model: MembershipModel) {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val packages = IAPManager.shared.offerings().packages
            val matchingPackage = packages.firstOrNull {
                it.product.sku == model.revcatProductId
            } ?: run {
                eventShowHud.postValue(false)
                eventLoadError.postValue(IAPErrorModel.NO_PRODUCT_ID_MATCH.display)
                return@launch
            }

            val purchase = IAPManager.shared.purchase(activity, matchingPackage)
            if (purchase.error != null) {
                eventShowHud.postValue(false)
                eventLoadError.postValue(IAPErrorModel.PURCHASE.display)
                return@launch
            }
            analyticsManager.trackPurchaseSubscription(model)

            val params = matchingPackage.params(model.key)
            val response = backendManager.membershipAdd(params)
            if (response.isSuccess) {
                doLoadView()
            } else {
                eventLoadError.postValue(IAPErrorModel.MEMBERSHIP_ADD.display)
                eventShowHud.postValue(false)
            }
        }
    }

    fun doTapRestore() {
        eventShowRestore.call()
    }

    override fun doTapChoose(model: MembershipModel) {
        if (memberships.any { it.isActive }) {
            eventShowCancelActive.call()
        } else {
            eventTapChooseMembership.postValue(model)
        }
    }

    override fun doTapCancel(model: MembershipModel) {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val nativePurchaseIds = nativePurchaseManager.purchases().map { it.sku }
            val isNativePurchase = nativePurchaseIds.contains(model.revcatProductId)
            if (isNativePurchase) {
                eventShowCancelMobile.call()
            } else {
                eventShowCancelWeb.call()
            }
            eventShowHud.postValue(false)
        }
    }

    fun doTrackPageView() {
        analyticsManager.trackPageView(MEMBERSHIPS)
    }
}

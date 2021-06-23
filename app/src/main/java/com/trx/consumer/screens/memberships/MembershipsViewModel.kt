package com.trx.consumer.screens.memberships

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.Package
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.managers.IAPManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.MembershipModel
import com.trx.consumer.models.core.ResponseModel
import com.trx.consumer.models.responses.MembershipsResponseModel
import com.trx.consumer.models.responses.UserResponseModel
import com.trx.consumer.screens.memberships.list.MembershipListener
import kotlinx.coroutines.launch

class MembershipsViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val cacheManager: CacheManager
) : BaseViewModel(), MembershipListener {

    val eventLoadView = CommonLiveEvent<List<MembershipModel>>()
    val eventLoadError = CommonLiveEvent<String>()
    val eventTapChooseMembership = CommonLiveEvent<MembershipModel>()
    val eventTapCancelMembership = CommonLiveEvent<MembershipModel>()
    val eventShowHud = CommonLiveEvent<Boolean>()
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
                eventLoadError.postValue("Could not load memberships")
                return@launch
            }
            val userResponse = backendManager.user()
            if (!userResponse.isSuccess) {
                eventLoadError.postValue("Could not load your memberships")
                return@launch
            }
            try {
                val membershipsResponseModel = MembershipsResponseModel.parse(
                    membershipResponse.responseString
                )
                val user = UserResponseModel.parse(userResponse.responseString).user
                cacheManager.user(user)
                memberships = membershipsResponseModel.memberships(user.memberships)
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
                eventLoadError.postValue("Could not find a matching product identifier")
                return@launch
            }

            val purchase = IAPManager.shared.purchase(activity, matchingPackage)
            if (purchase.error != null) {
                eventShowHud.postValue(false)
                eventLoadError.postValue("There was an Play Store error")
                return@launch
            }

            val params = params(matchingPackage, model)
            val response = backendManager.membershipAdd(params)
            if (response.isSuccess) {
                doLoadView()
            } else {
                eventLoadError.postValue("There was a purchase error")
                eventShowHud.postValue(false)
            }
        }
    }

    fun doTapRestore() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val packages = IAPManager.shared.offerings().packages
            val model = IAPManager.shared.restore()
            if (model.error != null) {
                eventLoadError.postValue("There was a restore error")
                eventShowHud.postValue(false)
                return@launch
            }

            model.purchaserInfo?.activeSubscriptions?.forEach { productId ->
                val matchingPackage = packages.firstOrNull { it.product.sku == productId }
                val membership = memberships.firstOrNull { it.revcatProductId == productId }
                if (matchingPackage != null && membership != null) {
                    val params = params(matchingPackage, membership)
                    backendManager.membershipAdd(params)
                }
            }
            doLoadView()
        }
    }

    override fun doTapChoose(model: MembershipModel) {
        eventTapChooseMembership.postValue(model)
    }

    override fun doTapCancel(model: MembershipModel) {
        eventTapCancelMembership.postValue(model)
    }

    fun params(matchingPackage: Package, membership: MembershipModel): HashMap<String, Any> {
        return hashMapOf(
            "subscriptionType" to membership.key,
            "currency" to matchingPackage.product.priceCurrencyCode,
            "subscriptionId" to matchingPackage.product.sku,
            "paymentProcessor" to "REVCAT",
            "priceInCents" to (matchingPackage.product.priceAmountMicros / 10000)
        )
    }
}

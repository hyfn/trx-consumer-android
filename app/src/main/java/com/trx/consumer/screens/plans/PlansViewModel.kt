package com.trx.consumer.screens.plans

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.pageTitle
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.PlanModel
import com.trx.consumer.models.common.UserModel
import com.trx.consumer.models.core.ResponseModel
import com.trx.consumer.models.responses.PlansResponseModel
import com.trx.consumer.screens.plans.list.PlansListener
import kotlinx.coroutines.launch

class PlansViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val cacheManager: CacheManager,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(), PlansListener {

    //region Events

    val eventLoadPlans = CommonLiveEvent<List<PlanModel>>()
    val eventLoadBottom = CommonLiveEvent<Boolean>()
    val eventLoadError = CommonLiveEvent<String>()
    val eventLoadNextBillDate = CommonLiveEvent<String?>()
    val eventLoadLastBillDate = CommonLiveEvent<String?>()

    val eventTapBack = CommonLiveEvent<Void>()

    val eventShowCancel = CommonLiveEvent<String?>()
    val eventShowConfirm = CommonLiveEvent<PlanModel>()
    val eventShowHud = CommonLiveEvent<Boolean>()

    //endregion

    //region Actions

    fun doLoadPlans() {
        viewModelScope.launch {
            analyticsManager.trackPageView(pageTitle)
            var user: UserModel? = null
            eventShowHud.postValue(true)
            backendManager.user()
            cacheManager.user()?.let { safeUser ->
                user = safeUser
                eventLoadBottom.postValue(safeUser.plan != null)
                eventLoadNextBillDate.postValue(safeUser.planRenewsDateDisplay)
                eventLoadLastBillDate.postValue(safeUser.planStartDateDisplay)
            }
            val response = backendManager.plans()
            if (response.isSuccess) {
                try {
                    val model = PlansResponseModel.parse(response.responseString)
                    user?.planText?.let { text ->
                        eventLoadPlans.postValue(model.plans(text))
                    }
                } catch (e: Exception) {
                    LogManager.log(e)
                }
            }
            eventShowHud.postValue(false)
        }
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    override fun doTapChoosePlan(model: PlanModel) {
        viewModelScope.launch {
            cacheManager.user()?.let { safeUser ->
                if (safeUser.planText != UserModel.kPlanNamePay) {
                    eventShowCancel.postValue(safeUser.plan)
                    return@launch
                }
            }
            eventShowConfirm.postValue(model)
        }
    }

    fun doTapCancel() {
        viewModelScope.launch {
            cacheManager.user()?.let { safeUser ->
                if (safeUser.planText != UserModel.kPlanNamePay) {
                    eventShowCancel.postValue(safeUser.plan)
                }
            }
        }
    }

    fun doCallPlanAdd(id: String) {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.planAdd(id)
            eventShowHud.postValue(false)
            if (response.isSuccess) {
                doLoadPlans()
            } else {
                eventLoadError.postValue(ResponseModel.genericErrorMessage)
            }
        }
    }

    fun doCallPlanDelete() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            cacheManager.user()?.plan?.let { safePlan ->
                val response = backendManager.planDelete(safePlan)
                eventShowHud.postValue(false)
                if (response.isSuccess) {
                    doLoadPlans()
                } else {
                    eventLoadError.postValue(ResponseModel.genericErrorMessage)
                }
            }
        }
    }

    //endregion
}

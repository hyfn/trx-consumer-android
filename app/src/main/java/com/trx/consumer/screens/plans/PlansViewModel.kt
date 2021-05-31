package com.trx.consumer.screens.plans

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
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
    private val cacheManager: CacheManager
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
            var userModel: UserModel? = null
            eventShowHud.postValue(true)
            backendManager.user()
            cacheManager.user()?.let { user ->
                userModel = user
                eventLoadBottom.postValue(user.plan != null)
                eventLoadNextBillDate.postValue(user.planRenewsDateDisplay)
                eventLoadLastBillDate.postValue(user.planStartDateDisplay)
            }
            val response = backendManager.plans()
            if (response.isSuccess) {
                try {
                    val model = PlansResponseModel.parse(response.responseString)
                    userModel?.planText?.let {
                        eventLoadPlans.postValue(model.plans(it))
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

    fun doCallAddPlan(id: String) {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            val response = backendManager.planAdd(id)
            if (response.isSuccess) {
                doLoadPlans()
            } else {
                eventLoadError.postValue(ResponseModel.genericErrorMessage)
            }
            eventShowHud.postValue(false)
        }
    }

    fun doCallPlanDelete() {
        viewModelScope.launch {
            eventShowHud.postValue(true)
            cacheManager.user()?.plan?.let {
                backendManager.planDelete(it).let { response ->
                    if (response.isSuccess) {
                        doLoadPlans()
                    } else {
                        eventLoadError.postValue(ResponseModel.genericErrorMessage)
                    }
                }
            }
            eventShowHud.postValue(false)
        }
    }

    //endregion
}

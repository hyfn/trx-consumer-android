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

    val eventLoadCanCancel = CommonLiveEvent<Boolean>()
    val eventLoadCancelPlan = CommonLiveEvent<String?>()
    val eventLoadConfirmPlan = CommonLiveEvent<PlanModel>()
    val eventLoadError = CommonLiveEvent<String>()
    val eventLoadNextBillDate = CommonLiveEvent<String?>()
    val eventLoadView = CommonLiveEvent<Void>()
    val eventLoadPlans = CommonLiveEvent<List<PlanModel>>()

    val eventTapBack = CommonLiveEvent<Void>()

    val eventShowHud = CommonLiveEvent<Boolean>()

    //endregion

    //region Actions

    fun doLoadView() {
        eventLoadView.call()
    }

    fun doLoadPlans() {
        viewModelScope.launch {
            var userModel: UserModel? = null
            eventShowHud.postValue(true)
            backendManager.user()
            cacheManager.user()?.let { user ->
                userModel = user
                eventLoadCanCancel.postValue(user.plan != null)
                eventLoadNextBillDate.postValue(user.planRenewsDateDisplay)
            }
            val response = backendManager.plans()
            if (response.isSuccess) {
                try {
                    val model = PlansResponseModel.parse(response.responseString)
                    userModel?.planText.let {
                        eventLoadPlans.postValue(model.plans(it))
                    }
                } catch (e: Exception) {
                    LogManager.log(e)
                }
            }
            eventShowHud.postValue(false)
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

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapDeletePlan() {
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

    override fun doTapChoosePlan(model: PlanModel) {
        viewModelScope.launch {
            cacheManager.user()?.let { safeUser ->
                if (safeUser.planText != UserModel.kPlanNamePay) {
                    eventLoadCancelPlan.postValue(safeUser.plan)
                    return@launch
                }
            }
            eventLoadConfirmPlan.postValue(model)
        }
    }

    //endregion
}

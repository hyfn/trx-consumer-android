package com.trx.consumer.screens.filteroptions

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.common.FilterOptionsModel
import com.trx.consumer.models.params.FilterParamsModel

class FilterOptionViewModel : BaseViewModel(), FilterOptionsListener {

    var model: FilterModel? = null
    var param: FilterParamsModel? = null

    val eventLoadView = CommonLiveEvent<FilterModel>()

    val eventTapBack = CommonLiveEvent<FilterParamsModel>()
    val eventTapFilterValue = CommonLiveEvent<FilterModel>()
    val eventTapSelectAll = CommonLiveEvent<FilterModel>()

    fun doLoadView() {
        model?.let { safeParams ->
            eventLoadView.postValue(safeParams)
        }
    }

    fun doTapBack() {
        eventTapBack.postValue(param)
    }

    fun onBackPressed() {
        eventTapBack.postValue(param)
    }

    fun doTapSelectAll() {
        model?.values?.forEach { model -> model.isSelected = true }
        eventTapSelectAll.postValue(model)
    }

    override fun doTapFilterValue(model: FilterOptionsModel) {
        this.model?.values?.find { model == it }?.isSelected = model.isSelected
        eventTapFilterValue.postValue(this.model)
    }
}

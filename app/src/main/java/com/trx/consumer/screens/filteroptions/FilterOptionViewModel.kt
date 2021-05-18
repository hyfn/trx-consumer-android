package com.trx.consumer.screens.filteroptions

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.common.FilterOptionsModel


class FilterOptionViewModel : BaseViewModel(), FilterOptionsListener {

    var params: FilterModel? = null

    val eventLoadView = CommonLiveEvent<FilterModel>()

    val eventTapBack = CommonLiveEvent<FilterModel>()
    val eventTapFilterValue = CommonLiveEvent<FilterModel>()
    val eventTapSelectAll = CommonLiveEvent<FilterModel>()

    fun doLoadView() {
        params?.let { safeParams ->
            eventLoadView.postValue(safeParams)
        }
    }

    fun doTapBack() {
        eventTapBack.postValue(params)
    }

    fun onBackPressed() {
        eventTapBack.call()
    }

    fun doTapSelectAll() {
        params?.values?.forEach { model -> model.isSelected = true  }
        eventTapSelectAll.postValue(params)
    }

    override fun doTapFilterValue(model: FilterOptionsModel) {
        params?.values?.find { model == it }?.isSelected = model.isSelected
        eventTapFilterValue.postValue(params)
    }
}
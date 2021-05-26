package com.trx.consumer.screens.discover.filters.filteroptions

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.common.FilterOptionsModel
import com.trx.consumer.models.params.FilterParamsModel

class FilterOptionsViewModel : BaseViewModel(), FilterOptionsListener {

    var filter: FilterModel = FilterModel()
    var params: FilterParamsModel = FilterParamsModel()

    val eventLoadView = CommonLiveEvent<FilterModel>()

    val eventTapBack = CommonLiveEvent<FilterParamsModel>()
    val eventTapFilterOption = CommonLiveEvent<FilterModel>()
    val eventTapReset = CommonLiveEvent<FilterModel>()

    fun doLoadView() {
        filter.let { safeParams ->
            eventLoadView.postValue(safeParams)
        }
    }

    fun doTapBack() {
        eventTapBack.postValue(params)
    }

    fun onBackPressed() {
        eventTapBack.postValue(params)
    }

    fun doTapReset() {
        reset()
        eventTapReset.postValue(filter)
    }

    override fun doTapFilterOption(model: FilterOptionsModel) {
        reset()
        filter.values.find { model == it }?.isSelected = true
        eventTapFilterOption.postValue(this.filter)
    }

    private fun reset() {
        filter.values.forEach { model -> model.isSelected = false }
    }
}

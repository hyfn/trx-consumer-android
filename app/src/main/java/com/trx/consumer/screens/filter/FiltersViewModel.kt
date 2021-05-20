package com.trx.consumer.screens.filter

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.params.FilterParamsModel

class FiltersViewModel : BaseViewModel(), FiltersListener {

    var params: FilterParamsModel? = null

    val eventLoadView = CommonLiveEvent<FilterParamsModel>()

    val eventTapApply = CommonLiveEvent<FilterParamsModel>()
    val eventTapClose = CommonLiveEvent<Void>()
    val eventTapReset = CommonLiveEvent<FilterParamsModel>()
    val eventTapFilter = CommonLiveEvent<FilterParamsModel>()

    fun doLoadView() {
        params?.let { safeParams ->
            eventLoadView.postValue(safeParams)
        }
    }

    fun doTapClose() {
        eventTapClose.call()
    }

    fun onBackPressed() {
        eventTapClose.call()
    }

    fun doTapReset() {
        params?.list?.forEach { filter ->
            filter.values.forEach { option ->
                option.isSelected = true
            }
        }
        eventTapReset.postValue(params)
    }

    fun doTapApply() {
        params?.let { safeParams ->
            eventTapApply.postValue(safeParams)
        }
    }

    override fun doTapFilter(model: FilterModel) {
        params?.selectedModel = model
        eventTapFilter.postValue(params)
    }
}

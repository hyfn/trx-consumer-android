package com.trx.consumer.screens.discover.filters

import androidx.hilt.lifecycle.ViewModelInject
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.pageTitle
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.params.FilterParamsModel

class FiltersViewModel @ViewModelInject constructor(
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(), FiltersListener {

    var params: FilterParamsModel? = null

    val eventLoadView = CommonLiveEvent<FilterParamsModel>()

    val eventTapApply = CommonLiveEvent<FilterParamsModel>()
    val eventTapClose = CommonLiveEvent<Void>()
    val eventTapFilter = CommonLiveEvent<FilterParamsModel>()

    fun doLoadView() {
        analyticsManager.trackPageView(pageTitle)
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
        params?.lstFilters?.forEach { filter ->
            filter.values.forEach { option ->
                option.isSelected = false
            }
        }
        doTapApply()
    }

    fun doTapApply() {
        params?.let { safeParams ->
            eventTapApply.postValue(safeParams)
            safeParams.lstFilters.forEach { filter ->
                filter.values.forEach { option ->
                    if(option.isSelected) analyticsManager.trackFilterOnDemand(option)
                }
            }
        }
    }

    override fun doTapFilter(model: FilterModel) {
        params?.selectedModel = model
        eventTapFilter.postValue(params)
    }
}

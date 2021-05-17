package com.trx.consumer.screens.filters

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.common.FilterValueModel
import com.trx.consumer.models.common.VideoFilterModel

class FiltersViewModel : BaseViewModel(), FiltersListener {

    var params: VideoFilterModel? = null

    val eventLoadView = CommonLiveEvent<VideoFilterModel>()

    val eventTapBack = CommonLiveEvent<VideoFilterModel>()
    val eventTapFilterValue = CommonLiveEvent<VideoFilterModel>()
    val eventTapSelectAll = CommonLiveEvent<VideoFilterModel>()

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

    override fun doTapFilterValue(model: FilterValueModel) {
        params?.values?.find { model == it }?.isSelected = model.isSelected
        eventTapFilterValue.postValue(params)
    }
}
package com.trx.consumer.screens.filteroptions

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.FilterOptionsModel
import com.trx.consumer.models.common.VideoFilterModel

class FilterOptionViewModel : BaseViewModel(), FilterOptionsListener {

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

    override fun doTapFilterValue(model: FilterOptionsModel) {
        params?.values?.find { model == it }?.isSelected = model.isSelected
        eventTapFilterValue.postValue(params)
    }
}
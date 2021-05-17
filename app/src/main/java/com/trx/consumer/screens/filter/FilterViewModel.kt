package com.trx.consumer.screens.filter

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.VideoFilterModel
import com.trx.consumer.models.params.VideoFilterParamsModel

class FilterViewModel : BaseViewModel(), VideoFilterListener {

    var params: VideoFilterParamsModel? = null

    val eventLoadView = CommonLiveEvent<VideoFilterParamsModel>()

    val eventTapApply = CommonLiveEvent<VideoFilterParamsModel>()
    val eventTapClose = CommonLiveEvent<Void>()
    val eventTapReset = CommonLiveEvent<Void>()
    val eventTapFilter = CommonLiveEvent<VideoFilterModel>()

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
        eventTapReset.call()
    }

    fun doTapApply() {
        params?.let { safeParams ->
            eventTapApply.postValue(safeParams)
        }
    }

    override fun doTapFilter(model: VideoFilterModel) {
        eventTapFilter.postValue(model)
    }
}

package com.trx.consumer.screens.content

import androidx.hilt.lifecycle.ViewModelInject
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.extensions.pageTitle
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.models.common.ClassModel
import com.trx.consumer.models.common.ContentModel
import com.trx.consumer.models.params.ContentParamsModel

class ContentViewModel @ViewModelInject constructor(
    private val analyticsManager: AnalyticsManager
) : BaseViewModel() {

    var model = ContentModel()
    var state = ContentViewState.PLAIN
    var classModel = ClassModel()

    var eventLoadView = CommonLiveEvent<ContentModel>()
    var eventTapBack = CommonLiveEvent<Void>()
    var eventTapBtnPrimary = CommonLiveEvent<ClassModel>()

    fun doLoadView(contentParamsModel: ContentParamsModel) {
        model = contentParamsModel.model
        state = contentParamsModel.state
        classModel = contentParamsModel.classModel
        eventLoadView.postValue(model)
    }

    fun doTapBtnPrimary() {
        eventTapBtnPrimary.postValue(classModel)
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun onBackPressed() {
        eventTapBack.call()
    }
}

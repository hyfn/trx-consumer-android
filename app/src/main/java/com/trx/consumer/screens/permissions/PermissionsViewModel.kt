package com.trx.consumer.screens.permissions

import androidx.hilt.lifecycle.ViewModelInject
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.PermissionModel

class PermissionsViewModel @ViewModelInject constructor() : BaseViewModel(), PermissionsListener {

    val eventLoadView = CommonLiveEvent<List<PermissionModel>>()
    val eventTapBack = CommonLiveEvent<Void>()
    val permissionList = PermissionModel.testList()

    fun doLoadView() {
        eventLoadView.postValue(permissionList)
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    override fun doPermissionEnabled(permissionId: String) {
        // Add permissions operation
    }
}

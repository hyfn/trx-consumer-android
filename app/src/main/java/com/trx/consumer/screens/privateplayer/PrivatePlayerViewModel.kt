package com.trx.consumer.screens.privateplayer

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.BuildConfig.kFMDevLiveAccessKey
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.frozenmountain.LiveSwitchPlayerListener
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.responses.LiveResponseModel
import kotlinx.coroutines.launch

class PrivatePlayerViewModel @ViewModelInject constructor (
    private val backendManager: BackendManager
) : BaseViewModel(), LiveSwitchPlayerListener {

    //region Objects

    var model: WorkoutModel? = null

    //endregion

    //region Events

    val eventLoadError = CommonLiveEvent<String>()
    val eventLoadVideo = CommonLiveEvent<WorkoutModel>()
    val eventTapClose = CommonLiveEvent<Void>()

    val eventShowHud = CommonLiveEvent<Boolean>()

    //endregion

    //region Actions

    fun doLoadVideo() {
        LogManager.log("doLoadVideo")

        viewModelScope.launch {
            model?.let { workout ->

                //  TODO: Bring back in after testing.
                // eventShowHud.postValue(true)
                // val joinResponse = backendManager.join(workout.identifier)
                // eventShowHud.postValue(false)
                // if (joinResponse.isSuccess) {
                // }

                eventShowHud.postValue(true)
                // TODO: Change to valid key after testing.
                val liveAccessKey = kFMDevLiveAccessKey
                val liveResponse = backendManager.live(liveAccessKey)
                eventShowHud.postValue(false)

                if (liveResponse.isSuccess) {
                    val liveModel = LiveResponseModel.parse(liveResponse.responseString)
                    if (liveModel.isValidType) {
                        workout.live = liveModel
                        eventLoadVideo.postValue(workout)
                    }
                }
            } ?: run {
                eventLoadError.postValue("There was an error")
            }
        }
    }

    fun doTapClose() {
        eventTapClose.call()
    }

    override fun doReceiveMessage(clientId: String, message: String) {
        LogManager.log("doReceiveMessage")
    }

    override fun doReportError(message: String) {
        LogManager.log("doReportError")
    }
}

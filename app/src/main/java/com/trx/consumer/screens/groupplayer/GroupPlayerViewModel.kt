package com.trx.consumer.screens.groupplayer

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.BuildConfig.kFMDevLiveAccessKey
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.responses.LiveResponseModel
import kotlinx.coroutines.launch

class GroupPlayerViewModel @ViewModelInject constructor (
    private val backendManager: BackendManager
) : BaseViewModel(), GroupPlayerListener {

    //region Objects

    var model: WorkoutModel? = null

    //endregion

    //region Events

    val eventLoadVideo = CommonLiveEvent<WorkoutModel>()
    val eventLoadError = CommonLiveEvent<String>()
    val eventTapCamera = CommonLiveEvent<Boolean>()
    val eventTapMic = CommonLiveEvent<Boolean>()
    val eventTapClock = CommonLiveEvent<Boolean>()
    val eventTapCast = CommonLiveEvent<Boolean>()
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

    fun doTapCamera(value: Boolean) {
        eventTapCamera.postValue(value)
    }

    fun doTapMic(value: Boolean) {
        eventTapMic.postValue(value)
    }

    fun doTapClock(value: Boolean) {
        eventTapClock.postValue(value)
    }

    fun doTapCast(value: Boolean) {
        eventTapCast.postValue(value)
    }

    fun doTapClose() {
        eventTapClose.call()
    }

    override fun doReceiveMessage(clientId: String, message: String) {
        LogManager.log("doReceiveMessage: $clientId $message")
    }

    override fun doReportError(message: String) {
        LogManager.log("doReportError")
        eventLoadError.postValue(message)
    }

    //endregion
}

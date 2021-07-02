package com.trx.consumer.screens.privateplayer

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.BuildConfig.kFMDevLiveAccessKey
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.AnalyticsPageModel.PRIVATE_PLAYER
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.responses.LiveResponseModel
import kotlinx.coroutines.launch

class PrivatePlayerViewModel @ViewModelInject constructor (
    private val backendManager: BackendManager,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(), PrivatePlayerListener {

    //region Objects

    var model: WorkoutModel? = null

    private var isCameraChecked: Boolean = false
    private var isMicChecked: Boolean = false
    private var isClockChecked: Boolean = false
    private var isCastChecked: Boolean = false

    var localMediaStarted: Boolean = false

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
        isCameraChecked = value
        eventTapCamera.postValue(value)
    }

    fun doTapMic(value: Boolean) {
        isMicChecked = value
        eventTapMic.postValue(value)
    }

    fun doTapClock(value: Boolean) {
        isClockChecked = value
        eventTapClock.postValue(value)
    }

    fun doTapCast(value: Boolean) {
        isCastChecked = value
        eventTapCast.postValue(value)
    }

    fun doTapClose() {
        if (localMediaStarted) {
            eventTapClose.call()
            localMediaStarted = false
        }
    }

    override fun doReceiveMessage(clientId: String, message: String) {
        LogManager.log("doReceiveMessage: $clientId $message")
    }

    override fun doReportError(message: String) {
        LogManager.log("doReportError")
        eventLoadError.postValue(message)
    }

    fun doTrackPageView() {
        analyticsManager.trackPageView(PRIVATE_PLAYER)
    }

    //endregion
}

package com.trx.consumer.screens.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.screens.settings.option.SettingsOptionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val cacheManager: CacheManager
) : BaseViewModel(), SettingsOptionListener {

    val eventTapSubscriptions = CommonLiveEvent<Void>()
    val eventTapTermsAndConditions = CommonLiveEvent<Void>()
    val eventTapContactSupport = CommonLiveEvent<Void>()
    val eventTapGettingStarted = CommonLiveEvent<Void>()
    val eventTapShop = CommonLiveEvent<Void>()
    val eventTapLogout = CommonLiveEvent<Void>()
    val eventTapBack = CommonLiveEvent<Void>()
    val eventLoadView = CommonLiveEvent<List<Any>>()

    val eventLogOut = CommonLiveEvent<Void>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doLoadView() {
        viewModelScope.launch {
            cacheManager.user()?.let { user ->
                eventLoadView.postValue(SettingsModel.list(user))
            }
        }
    }

    fun updateBeforeLogout() {
        CoroutineScope(Dispatchers.IO).launch {
            backendManager.logout()
        }
        eventLogOut.call()
    }

    override fun doTapSettings(model: SettingsModel) {
        when (model.type) {
            SettingsType.SUBSCRIPTIONS -> eventTapSubscriptions.call()
            SettingsType.SHOP -> eventTapShop.call()
            SettingsType.GETTING_STARTED -> eventTapGettingStarted.call()
            SettingsType.CONTACT_SUPPORT -> eventTapContactSupport.call()
            SettingsType.TERMS_AND_CONDITIONS -> eventTapTermsAndConditions.call()
            SettingsType.RESTORE -> {
            }
            SettingsType.LOGOUT -> eventTapLogout.call()
        }
    }
}

package com.trx.consumer.screens.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.models.common.AnalyticsPageModel.SETTINGS
import com.trx.consumer.models.common.SettingsModel
import com.trx.consumer.models.common.SettingsType
import com.trx.consumer.screens.settings.option.SettingsOptionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel @ViewModelInject constructor(
    private val backendManager: BackendManager,
    private val cacheManager: CacheManager,
    private val analyticsManager: AnalyticsManager
) : BaseViewModel(), SettingsOptionListener {

    //region Events

    val eventLoadView = CommonLiveEvent<List<Any>>()
    val eventTapMemberships = CommonLiveEvent<Void>()
    val eventTapShop = CommonLiveEvent<Void>()
    val eventTapGettingStarted = CommonLiveEvent<Void>()
    val eventTapContactSupport = CommonLiveEvent<Void>()
    val eventTapTermsAndConditions = CommonLiveEvent<Void>()
    val eventTapLogout = CommonLiveEvent<Void>()
    val eventTapBack = CommonLiveEvent<Void>()
    val eventTapTest = CommonLiveEvent<Void>()
    val eventTapMaintenance = CommonLiveEvent<Unit>()
    val eventTapRestore = CommonLiveEvent<Unit>()

    val eventLogOut = CommonLiveEvent<Void>()

    //endregion

    //region Actions

    fun doLoadView() {
        viewModelScope.launch {
            cacheManager.user()?.let { user ->
                eventLoadView.postValue(SettingsModel.list(user))
            }
        }
    }

    override fun doTapSettings(model: SettingsModel) {
        when (model.type) {
            SettingsType.SHOP -> eventTapShop.call()
            SettingsType.GETTING_STARTED -> eventTapGettingStarted.call()
            SettingsType.CONTACT_SUPPORT -> eventTapContactSupport.call()
            SettingsType.TERMS_AND_CONDITIONS -> eventTapTermsAndConditions.call()
            SettingsType.RESTORE -> eventTapRestore.call()
            SettingsType.LOGOUT -> eventTapLogout.call()
            SettingsType.TEST_SCREENS -> eventTapTest.call()
            SettingsType.MEMBERSHIPS -> eventTapMemberships.call()
            SettingsType.SHOW_MAINTENANCE -> eventTapMaintenance.call()
        }
    }

    fun doTapBack() {
        eventTapBack.call()
    }

    fun updateBeforeLogout() {
        CoroutineScope(Dispatchers.IO).launch {
            backendManager.logout()
        }
        eventLogOut.call()
    }

    fun doTrackPageView() {
        analyticsManager.trackPageView(SETTINGS)
    }
    //endregion
}

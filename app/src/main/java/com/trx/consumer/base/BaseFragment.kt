package com.trx.consumer.base

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.trx.consumer.BuildConfig
import com.trx.consumer.R
import com.trx.consumer.managers.ConfigManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.managers.UtilityManager
import com.trx.consumer.screens.maintenance.MaintenanceViewState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class BaseFragment : Fragment {

    @Inject
    lateinit var configManager: ConfigManager

    private val listMaintenanceNotEnabled = listOf(R.id.maintenance_fragment)

    private val name = this.javaClass.simpleName

    constructor() : super()

    constructor(layoutResId: Int) : super(layoutResId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NavigationManager.shared.currentFragment(requireActivity())?.let { destinationId ->
            if (!listMaintenanceNotEnabled.contains(destinationId)) {
                checkMaintenance()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackPressed()
        }
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }

    open fun bind() {}

    override fun onResume() {
        super.onResume()
        print("fragmentName: $name")
    }

    private fun checkMaintenance() {
        val maintenance = configManager.remoteConfig.getBoolean(BuildConfig.isMaintenanceMode)
        val minBuild = configManager.remoteConfig.getLong(BuildConfig.minimumBuildNumber)
        val currentBuild = UtilityManager.shared.buildVersion().toInt()
        if (maintenance) {
            LogManager.log("maintenance")
            NavigationManager.shared.present(
                this,
                R.id.maintenance_fragment,
                MaintenanceViewState.MAINTENANCE
            )
        } else if (minBuild > currentBuild) {
            LogManager.log("update")
            NavigationManager.shared.present(
                this,
                R.id.maintenance_fragment,
                MaintenanceViewState.UPDATE
            )
        }
    }

    open fun onBackPressed() {}
}

package com.trx.consumer.screens.settings

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.BuildConfig.kContactSupportUrl
import com.trx.consumer.BuildConfig.kGettingStartedVideosUrl
import com.trx.consumer.BuildConfig.kProductsUrl
import com.trx.consumer.BuildConfig.kTermsConditionsUrl
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentSettingsBinding
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.managers.UtilityManager
import com.trx.consumer.models.common.AlertModel
import com.trx.consumer.screens.alert.AlertViewState
import com.trx.consumer.screens.settings.option.SettingsOptionAdapter

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentSettingsBinding::bind)

    private lateinit var adapter: SettingsOptionAdapter

    override fun bind() {
        adapter = SettingsOptionAdapter(viewModel) { lifecycleScope }

        viewBinding.rvSettings.adapter = adapter

        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapSubscriptions.observe(viewLifecycleOwner, handleTapSubscriptions)
            eventTapContactSupport.observe(viewLifecycleOwner, handleTapContactSupport)
            eventTapGettingStarted.observe(viewLifecycleOwner, handleTapGetStarted)
            eventTapTermsAndConditions.observe(viewLifecycleOwner, handleTapTermsAndConditions)
            eventTapShop.observe(viewLifecycleOwner, handleTapShop)
            eventTapLogout.observe(viewLifecycleOwner, handleTapLogout)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLogOut.observe(viewLifecycleOwner, handleLogOut)

            doLoadView()
        }
    }

    private val handleTapBack = Observer<Void> {
        LogManager.log("handleTapBack")
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadView = Observer<List<Any>> { list ->
        LogManager.log("handleLoadView")
        viewBinding.lblVersion.text = UtilityManager.shared.versionDisplay()
        adapter.update(list)
    }

    private val handleTapSubscriptions = Observer<Void> {
        LogManager.log("handleTapSubscriptions")
        NavigationManager.shared.present(this, R.id.subscriptions_fragment)
    }

    private val handleTapContactSupport = Observer<Void> {
        LogManager.log("handleTapContactSupport")
        UtilityManager.shared.openUrl(requireContext(), kContactSupportUrl)
    }

    private val handleTapGetStarted = Observer<Void> {
        LogManager.log("handleTapGetStarted")
        UtilityManager.shared.openUrl(requireContext(), kGettingStartedVideosUrl)
    }

    private val handleTapTermsAndConditions = Observer<Void> {
        LogManager.log("handleTapTermsAndConditions")
        UtilityManager.shared.openUrl(requireContext(), kTermsConditionsUrl)
    }

    private val handleTapShop = Observer<Void> {
        LogManager.log("handleTapShop")
        UtilityManager.shared.openUrl(requireContext(), kProductsUrl)
    }

    private val handleTapLogout = Observer<Void> {
        LogManager.log("handleTapLogout")
        val context = requireContext()
        val model = AlertModel.create(
            title = context.getString(R.string.alert_confirm_message),
            message = context.getString(R.string.alert_logout_message)
        )
        model.setPrimaryButton(R.string.alert_nevermind_label, AlertViewState.NEUTRAL)
        model.setSecondaryButton(R.string.alert_logout_confirm_label, AlertViewState.NEGATIVE) {
            viewModel.updateBeforeLogout()
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleLogOut = Observer<Void> {
        LogManager.log("handleLogOut")
        NavigationManager.shared.notLoggedInLaunchSequence(this)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}

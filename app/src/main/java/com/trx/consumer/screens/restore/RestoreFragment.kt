package com.trx.consumer.screens.restore

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.BuildConfig
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentRestoreBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.managers.UtilityManager
import com.trx.consumer.models.common.AlertModel
import com.trx.consumer.screens.alert.AlertViewState

class RestoreFragment : BaseFragment(R.layout.fragment_restore) {

    private val viewModel: RestoreViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentRestoreBinding::bind)

    override fun bind() {
        viewModel.isFromOnboarding = NavigationManager.shared.previousFragment(
            requireActivity()
        ) == R.id.onboarding_fragment

        viewBinding.apply {
            btnClose.action { viewModel.doTapClose() }
            btnRestore.action { viewModel.doTapRestore() }
        }

        viewModel.apply {
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)
            eventShowHome.observe(viewLifecycleOwner, handleShowHome)
            eventShowAlertSuccess.observe(viewLifecycleOwner, handleShowAlertSuccess)
            eventShowAlertError.observe(viewLifecycleOwner, handleShowAlertError)
            eventTapClose.observe(viewLifecycleOwner, handleTapClose)

            doTrackPageView()
            doLoadView()
        }
    }

    private val handleTapClose = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleShowHud = Observer<Boolean> { show ->
        LogManager.log("handleShowHud")
        viewBinding.hudView.isVisible = show
    }

    private val handleShowAlertError = Observer<String> { error ->
        LogManager.log("handleShowError")
        val model = AlertModel.create(title = "", message = error).apply {
            setPrimaryButton(
                R.string.restore_alert_error_primary_label,
                state = AlertViewState.POSITIVE
            ) {
                UtilityManager.shared.openUrl(
                    requireContext(),
                    BuildConfig.kGooglePlaySubscriptionsUrl
                )
            }
            setSecondaryButton(R.string.restore_alert_error_secondary_label)
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleShowHome = Observer<String> { message ->
        LogManager.log("handleShowConfirmHome")
        val model = AlertModel.create(title = "", message = message).apply {
            setPrimaryButton(
                R.string.restore_home_alert_primary_label,
                state = AlertViewState.POSITIVE
            ) { NavigationManager.shared.loggedInLaunchSequence(this@RestoreFragment) }
            setSecondaryButton(R.string.restore_alert_error_secondary_label)
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleShowAlertSuccess = Observer<Void> {
        LogManager.log("handleShowConfirmHome")
        val model = AlertModel.create(
            title = "",
            message = requireContext().getString(R.string.restore_alert_success_message)
        ).apply {
            setPrimaryButton(
                R.string.restore_alert_success_primary_label,
                state = AlertViewState.POSITIVE
            ) {
                NavigationManager.shared.dismiss(this@RestoreFragment)
                NavigationManager.shared.dismiss(this@RestoreFragment)
            }
            setSecondaryButton(R.string.restore_alert_error_secondary_label)
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}

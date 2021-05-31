package com.trx.consumer.screens.subscriptions

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.BuildConfig
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentSubscriptionsBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.managers.UtilityManager
import com.trx.consumer.models.common.AlertModel
import com.trx.consumer.models.common.SubscriptionModel
import com.trx.consumer.screens.alert.AlertViewState
import com.trx.consumer.screens.erroralert.ErrorAlertModel
import com.trx.consumer.screens.subscriptions.list.SubscriptionsAdapter

class SubscriptionsFragment : BaseFragment(R.layout.fragment_subscriptions) {

    private val viewModel: SubscriptionsViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentSubscriptionsBinding::bind)

    private lateinit var adapter: SubscriptionsAdapter

    override fun bind() {
        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventLoadCanCancel.observe(viewLifecycleOwner, handleLoadCanCancel)
            eventLoadError.observe(viewLifecycleOwner, handleLoadError)
            eventLoadNextBillDate.observe(viewLifecycleOwner, handleLoadNextBillDate)
            eventLoadLastBillDate.observe(viewLifecycleOwner, handleLoadLastBillDate)
            eventLoadSubscriptions.observe(viewLifecycleOwner, handleLoadSubscriptions)

            eventTapCancel.observe(viewLifecycleOwner, handleTapCancel)
            eventTapChooseSubscription.observe(viewLifecycleOwner, handleTapChooseSubscription)
            eventTapSettings.observe(viewLifecycleOwner, handleTapSettings)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)

            eventShowHud.observe(viewLifecycleOwner, handleShowHud)
        }

        viewBinding.apply {
            adapter = SubscriptionsAdapter(viewModel) { lifecycleScope }
            rvSubscriptions.adapter = adapter

            btnBack.action { viewModel.doTapBack() }
            btnCancel.action { viewModel.doTapCancel() }
            btnRestore.action { viewModel.doTapRestore() }
        }

        viewModel.doLoadView()
    }

    private val handleLoadView = Observer<Void> {
        LogManager.log("handleLoadView")
        viewBinding.apply {
            viewLastBill.isHidden = true
            viewNextBill.isHidden = true
            btnCancel.isHidden = true
            btnRestore.isHidden = true
        }
    }

    override fun onResume() {
        super.onResume()

        if (!viewBinding.hudView.isVisible) viewModel.doLoadView()
    }

    private val handleLoadCanCancel = Observer<Boolean> { value ->
        LogManager.log("handleLoadCanCancel")
        viewBinding.apply {
            btnCancel.isHidden = !value
            btnRestore.isHidden = value
        }
    }

    private val handleTapCancel = Observer<Void> {
        LogManager.log("handleTapCancel")
        val model = AlertModel.create(
            title = "",
            message = requireContext().getString(R.string.subscriptions_cancel_alert_message)
        ).apply {
            setPrimaryButton(
                title = R.string.subscriptions_cancel_alert_primary_label,
                state = AlertViewState.POSITIVE
            )
            setSecondaryButton(
                R.string.subscriptions_cancel_alert_secondary_label,
                state = AlertViewState.NEGATIVE
            ) {
                viewModel.doTapUnsubscribe()
            }
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleTapChooseSubscription = Observer<SubscriptionModel> { value ->
        LogManager.log("handleTapChooseSubscription")
        val model = AlertModel.create(
            title = "",
            message = requireContext().getString(R.string.subscriptions_confirm_alert_message)
        ).apply {
            setPrimaryButton(
                R.string.subscriptions_confirm_alert_primary_label,
                state = AlertViewState.POSITIVE
            ) {
                viewModel.doCallSubscribe(requireActivity(), value)
            }
            setSecondaryButton(R.string.alert_button_title_dismiss)
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleLoadError = Observer<String> { value ->
        LogManager.log("handleLoadError")
        val model = ErrorAlertModel.error(value)
        NavigationManager.shared.present(this, R.id.error_fragment, model)
    }

    private val handleLoadLastBillDate = Observer<String> { value ->
        LogManager.log("handleLoadLastBillDate")
        viewBinding.apply {
            viewLastBill.isHidden = false
            lblLastBillDate.text = value
        }
    }

    private val handleLoadNextBillDate = Observer<String> { value ->
        LogManager.log("handleLoadNextBilDate")
        viewBinding.apply {
            viewNextBill.isHidden = false
            lblNextBillDate.text = value
        }
    }

    private val handleLoadSubscriptions = Observer<List<SubscriptionModel>> { subscriptions ->
        LogManager.log("handleLoadSubscriptions")
        adapter.update(subscriptions)
    }

    private val handleTapSettings = Observer<Void> {
        LogManager.log("handleTapSettings")
        UtilityManager.shared.openUrl(requireContext(), BuildConfig.kGooglePlaySubscriptionsUrl)
    }

    private val handleShowHud = Observer<Boolean> { show ->
        LogManager.log("handleShowHud")
        viewBinding.hudView.isVisible = show
    }

    private val handleTapBack = Observer<Void> {
        LogManager.log("handleTapBack")
        NavigationManager.shared.dismiss(this)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}

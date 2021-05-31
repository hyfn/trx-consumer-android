package com.trx.consumer.screens.subscriptions

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentSubscriptionsBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.AlertModel
import com.trx.consumer.models.common.SubscriptionModel
import com.trx.consumer.screens.alert.AlertViewState
import com.trx.consumer.screens.subscriptions.list.SubscriptionsAdapter

class SubscriptionsFragment : BaseFragment(R.layout.fragment_subscriptions) {

    private val viewModel: SubscriptionsViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentSubscriptionsBinding::bind)

    private lateinit var adapter: SubscriptionsAdapter

    override fun bind() {
        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventLoadCanCancel.observe(viewLifecycleOwner, handleLoadCanCancel)
            eventLoadCancel.observe(viewLifecycleOwner, handleLoadCancel)
            eventLoadConfirm.observe(viewLifecycleOwner, handleLoadConfirm)
            eventLoadError.observe(viewLifecycleOwner, handleLoadError)
            eventLoadNextBillDate.observe(viewLifecycleOwner, handleLoadNextBillDate)
            eventLoadLastBillDate.observe(viewLifecycleOwner, handleLoadLastBillDate)
            eventLoadSubscriptions.observe(viewLifecycleOwner, handleLoadSubscriptions)

            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapSettings.observe(viewLifecycleOwner, handleTapSettings)
        }

        viewBinding.apply {
            adapter = SubscriptionsAdapter(viewModel) { lifecycleScope }
            rvSubscriptions.adapter = adapter

            btnBack.action { viewModel.doTapBack() }
        }

        viewModel.doLoadView()
    }

    private val handleLoadView = Observer<Void> {
        LogManager.log("handleLoadView")
    }

    private val handleLoadCanCancel = Observer<Boolean> {
        LogManager.log("handleLoadCanCancel")
    }

    private val handleLoadCancel = Observer<Void> {
        LogManager.log("handleLoadCancel")
    }

    private val handleLoadConfirm = Observer<SubscriptionModel> { value ->
        LogManager.log("handleLoadConfirm")
        val model = AlertModel.create(title = "", message = "Are yo sure you want to buy this subscription?")
        model.setPrimaryButton(R.string.subscriptions_confirm_alert_primary_label, state = AlertViewState.POSITIVE) {
            viewModel.doCallSubscribe(requireActivity(), value)
        }
        model.setSecondaryButton(R.string.alert_button_title_dismiss)
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleLoadError = Observer<String> { value ->
        LogManager.log("handleLoadError")
    }

    private val handleLoadLastBillDate = Observer<String> {
        LogManager.log("handleLoadLastBillDate")
    }

    private val handleLoadNextBillDate = Observer<String> { value ->
        LogManager.log("handleLoadNextBilDate")
    }

    private val handleLoadSubscriptions = Observer<List<SubscriptionModel>> { subscriptions ->
        LogManager.log("handleLoadSubscriptions")
        adapter.update(subscriptions)
    }

    private val handleTapSettings = Observer<Void> {
        LogManager.log("handleTapSettings")
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}

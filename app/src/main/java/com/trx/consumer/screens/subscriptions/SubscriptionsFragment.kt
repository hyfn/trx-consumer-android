package com.trx.consumer.screens.subscriptions

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentSubscriptionsBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.SubscriptionsModel
import com.trx.consumer.screens.subscriptions.list.SubscriptionsAdapter
import com.trx.consumer.screens.subscriptions.list.SubscriptionsViewState

class SubscriptionsFragment : BaseFragment(R.layout.fragment_subscriptions) {

    private val viewModel: SubscriptionsViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentSubscriptionsBinding::bind)

    private lateinit var adapter: SubscriptionsAdapter

    override fun bind() {
        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
        }

        viewBinding.apply {
            adapter = SubscriptionsAdapter(viewModel) { lifecycleScope }
            rvSubscriptions.adapter = adapter

            btnBack.action { viewModel.doTapBack() }
        }

        viewModel.doLoadView()
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadView = Observer<SubscriptionsModel> { model ->
        val isActive = model.state == SubscriptionsViewState.CURRENT
        viewBinding.viewBottom.isVisible = isActive
        adapter.update(model)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}

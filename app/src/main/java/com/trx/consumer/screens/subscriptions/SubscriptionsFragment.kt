package com.trx.consumer.screens.subscriptions

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentSubscriptionsBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.PlanModel
import com.trx.consumer.screens.subscriptions.list.SubscriptionsAdapter

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

    private val handleLoadView = Observer<List<PlanModel>> {
        adapter.updateSubscriptions(it)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}
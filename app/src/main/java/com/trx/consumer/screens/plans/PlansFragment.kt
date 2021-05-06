package com.trx.consumer.screens.plans

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentPlansBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.PlanModel
import com.trx.consumer.screens.plans.list.PlansAdapter

class PlansFragment : BaseFragment(R.layout.fragment_plans) {

    private val viewModel: PlansViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentPlansBinding::bind)

    private lateinit var adapter: PlansAdapter

    override fun bind() {
        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
        }

        viewBinding.apply {
            adapter = PlansAdapter(viewModel) { lifecycleScope }
            rvPlans.adapter = adapter

            btnBack.action { viewModel.doTapBack() }
        }

        viewModel.doLoadView()
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadView = Observer<List<PlanModel>> {
        adapter.updatePlans(it)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}

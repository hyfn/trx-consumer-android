package com.trx.consumer.screens.plans

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentPlansBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.PlanModel
import com.trx.consumer.screens.plans.list.PlansAdapter

class PlansFragment : BaseFragment(R.layout.fragment_plans) {

    //region Objects

    private val viewModel: PlansViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentPlansBinding::bind)

    private lateinit var adapter: PlansAdapter

    //endregion

    //region Initializers

    override fun bind() {
        viewBinding.apply {
            adapter = PlansAdapter(viewModel) { lifecycleScope }
            rvPlans.adapter = adapter

            btnBack.action { viewModel.doTapBack() }
        }

        viewModel.apply {
            eventLoadCanCancel.observe(viewLifecycleOwner, handleLoadCanCancel)
            eventLoadCancelSubscription.observe(viewLifecycleOwner, handleLoadCancelSubscription)
            eventLoadConfirmSubscription.observe(viewLifecycleOwner, handleLoadConfirmSubscription)
            eventLoadError.observe(viewLifecycleOwner, handleLoadError)
            eventLoadNextBillDate.observe(viewLifecycleOwner, handleLoadNextBillDate)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventLoadPlans.observe(viewLifecycleOwner, handleLoadPlans)

            eventTapBack.observe(viewLifecycleOwner, handleTapBack)

            eventShowHud.observe(viewLifecycleOwner, handleShowHud)
        }

        viewModel.doLoadView()
        viewModel.doLoadPlans()
    }

    //endregion

    //region Handlers

    private val handleLoadCanCancel = Observer<Boolean> {
        LogManager.log("handleLoadCanCancel")
    }

    private val handleLoadCancelSubscription = Observer<String?> {
        LogManager.log("handleLoadCancelSubscription")
    }

    private val handleLoadConfirmSubscription = Observer<PlanModel> {
        LogManager.log("handleLoadConfirmSubscription")
    }

    private val handleLoadError = Observer<String> {
        LogManager.log("handleLoadError")
    }

    private val handleLoadNextBillDate = Observer<String> {
        LogManager.log("handleLoadNextBillDate")
    }

    private val handleLoadView = Observer<Void> {
        LogManager.log("handleLoadView")
    }

    private val handleLoadPlans = Observer<List<PlanModel>> {
        LogManager.log("handleLoadPlans")
        adapter.updatePlans(it)
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.hudView.isVisible = show
    }
    //endregion
}

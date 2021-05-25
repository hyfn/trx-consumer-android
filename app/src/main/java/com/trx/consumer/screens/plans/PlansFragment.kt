package com.trx.consumer.screens.plans

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentPlansBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.AlertModel
import com.trx.consumer.models.common.PlanModel
import com.trx.consumer.screens.erroralert.ErrorAlertModel
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
            viewPlans.adapter = adapter
            btnBack.action { viewModel.doTapBack() }
        }

        viewModel.apply {
            eventLoadCanCancel.observe(viewLifecycleOwner, handleLoadCanCancel)
            eventLoadCancelPlan.observe(viewLifecycleOwner, handleLoadCancelPlan)
            eventLoadConfirmPlan.observe(viewLifecycleOwner, handleLoadConfirmPlan)
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

    private val handleLoadCanCancel = Observer<Boolean> { value ->
        LogManager.log("handleLoadCanCancel")
        viewBinding.apply {
            btnCancel.isHidden = !value
            viewNextBill.isHidden = !value
        }
    }

    private val handleLoadCancelPlan = Observer<String?> { value ->
        LogManager.log("handleLoadCancelPlan")
        val message = getString(R.string.plans_cancel_message, value)

        //  Not handled like iOS. setSecondaryButton accepts String ints
        //  so doing below and applying that to setSecondaryButton won't work.
        //  val cancelTitle = value?.let { "Cancel $value" } ?: "Cancel"

        val model = AlertModel.create(title = "", message = message)
        model.setPrimaryButton(title = R.string.alert_primary_keep_plan)
        model.setSecondaryButton(title = R.string.alert_secondary_cancel) {
            viewModel.doTapDeletePlan()
        }

        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleLoadConfirmPlan = Observer<PlanModel> { value ->
        LogManager.log("handleLoadConfirmPlan")
        val message = if (value.cost.isNotEmpty()) {
            "Are you sure you want to subscribe for ${value.cost}?"
        } else {
            "Are you sure you want to subscribe?"
        }

        val model = AlertModel.create(title = "", message = message)
        model.setPrimaryButton(title = R.string.alert_primary_submit_payment) {
            viewModel.doCallAddPlan(value.key)
        }
        model.setSecondaryButton(title = R.string.alert_secondary_cancel)

        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleLoadError = Observer<String> { error ->
        LogManager.log("handleLoadError")
        val model = ErrorAlertModel.error(message = error)
        NavigationManager.shared.present(this, R.id.error_fragment, model)
    }

    private val handleLoadNextBillDate = Observer<String?> { value ->
        LogManager.log("handleLoadNextBillDate")
        value?.let {
            viewBinding.apply {
                viewNextBill.isHidden = false
                lblNextBillDate.text = value
            }
        }
    }

    private val handleLoadView = Observer<Void> {
        LogManager.log("handleLoadView")
        //  Empty in iOS
    }

    private val handleLoadPlans = Observer<List<PlanModel>> {
        LogManager.log("handleLoadPlans")
        adapter.updatePlans(it)
    }

    private val handleTapBack = Observer<Void> {
        LogManager.log("handleTapBack")
        NavigationManager.shared.dismiss(this)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleShowHud = Observer<Boolean> { show ->
        LogManager.log("handleShowHud")
        viewBinding.hudView.isVisible = show
    }
    //endregion
}

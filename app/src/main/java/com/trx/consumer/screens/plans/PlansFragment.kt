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
            eventLoadPlans.observe(viewLifecycleOwner, handleLoadPlans)
            eventLoadBottom.observe(viewLifecycleOwner, handleLoadBottom)
            eventLoadError.observe(viewLifecycleOwner, handleLoadError)
            eventLoadNextBillDate.observe(viewLifecycleOwner, handleLoadNextBillDate)
            eventLoadLastBillDate.observe(viewLifecycleOwner, handleLoadLastBillDate)

            eventTapBack.observe(viewLifecycleOwner, handleTapBack)

            eventShowCancel.observe(viewLifecycleOwner, handleShowCancel)
            eventShowConfirm.observe(viewLifecycleOwner, handleShowConfirm)
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)
        }

        viewModel.doLoadPlans()
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    //endregion

    //region Handlers

    private val handleLoadPlans = Observer<List<PlanModel>> {
        LogManager.log("handleLoadPlans")
        adapter.updatePlans(it)
    }

    private val handleLoadBottom = Observer<Boolean> { value ->
        LogManager.log("handleLoadBottom")
        viewBinding.apply {
            btnCancel.isHidden = !value
            btnCancel.action { viewModel.doTapCancel() }
            viewNextBill.isHidden = !value
        }
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

    private val handleLoadLastBillDate = Observer<String?> { value ->
        LogManager.log("handleLoadLastBillDate")
        value?.let {
            viewBinding.apply {
                viewLastBill.isHidden = false
                lblLastBillDate.text = value
            }
        }
    }

    private val handleTapBack = Observer<Void> {
        LogManager.log("handleTapBack")
        NavigationManager.shared.dismiss(this)
    }

    private val handleShowCancel = Observer<String?> {
        LogManager.log("handleShowCancel")

        val message = getString(R.string.plans_cancel_message)
        val model = AlertModel.create(title = "", message = message)

        model.setPrimaryButton(title = R.string.alert_primary_keep_plan)
        model.setSecondaryButton(title = R.string.alert_secondary_cancel_plan) {
            viewModel.doCallPlanDelete()
        }

        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleShowConfirm = Observer<PlanModel> { value ->
        LogManager.log("handleShowConfirm")

        val message = getString(R.string.plans_confirm_message)
        val model = AlertModel.create(title = "", message = message)

        model.setPrimaryButton(title = R.string.alert_primary_submit_payment) {
            viewModel.doCallPlanAdd(value.key)
        }
        model.setSecondaryButton(title = R.string.alert_secondary_cancel)

        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleShowHud = Observer<Boolean> { show ->
        LogManager.log("handleShowHud")
        viewBinding.hudView.isVisible = show
    }

    //endregion
}

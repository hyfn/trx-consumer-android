package com.trx.consumer.screens.memberships

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentMembershipsBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.AlertModel
import com.trx.consumer.models.common.MembershipModel
import com.trx.consumer.screens.alert.AlertViewState
import com.trx.consumer.screens.memberships.list.MembershipViewState
import com.trx.consumer.screens.memberships.list.MembershipsAdapter

class MembershipsFragment : BaseFragment(R.layout.fragment_memberships) {

    private val viewModel: MembershipsViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentMembershipsBinding::bind)

    private lateinit var adapter: MembershipsAdapter

    override fun bind() {
        viewBinding.apply {
            adapter = MembershipsAdapter(viewModel) { lifecycleScope }
            rvMemberships.adapter = adapter

            btnBack.action { viewModel.doTapBack() }
            btnCancel.action { viewModel.doTapCancelMembership() }
        }

        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapChooseMembership.observe(viewLifecycleOwner, handleTapChooseMembership)
            eventTapCancelMembership.observe(viewLifecycleOwner, handleTapCancelMembership)

            doLoadView()
        }
    }

    private val handleTapBack = Observer<Void> {
        LogManager.log("handleTapBack")
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadView = Observer<List<MembershipModel>> {
        viewBinding.viewBottom.isVisible = viewModel.state == MembershipViewState.ACTIVE
        adapter.update(it)
    }

    private val handleTapChooseMembership = Observer<MembershipModel> {
        LogManager.log("handleTapChooseMembership")
        val model = AlertModel.create(
            title = "",
            message = requireContext().getString(R.string.memberships_choose_membership_alert_title)
        ).apply {
            setPrimaryButton(
                R.string.memberships_choose_membership_alert_primary_label,
                state = AlertViewState.POSITIVE
            ) {
            }
            setSecondaryButton(R.string.memberships_choose_membership_alert_secondary_label)
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleTapCancelMembership = Observer<MembershipModel> {
        LogManager.log("handleTapCancelMembership")
        val model = AlertModel.create(
            title = "",
            message = requireContext().getString(R.string.memberships_cancel_membership_alert_title)
        ).apply {
            setPrimaryButton(
                R.string.memberships_cancel_membership_alert_primary_label,
                state = AlertViewState.POSITIVE
            )
            setSecondaryButton(R.string.memberships_cancel_membership_alert_secondary_label) {
            }
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}

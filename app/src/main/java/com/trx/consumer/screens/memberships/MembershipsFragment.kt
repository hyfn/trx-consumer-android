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
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.AlertModel
import com.trx.consumer.models.common.MembershipModel
import com.trx.consumer.screens.alert.AlertViewState
import com.trx.consumer.screens.erroralert.ErrorAlertModel
import com.trx.consumer.screens.memberships.list.MembershipAdapter

class MembershipsFragment : BaseFragment(R.layout.fragment_memberships) {

    private val viewModel: MembershipsViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentMembershipsBinding::bind)

    private lateinit var adapter: MembershipAdapter

    override fun bind() {
        viewBinding.apply {
            adapter = MembershipAdapter(viewModel) { lifecycleScope }
            rvMemberships.adapter = adapter

            btnBack.action { viewModel.doTapBack() }
            btnRestore.action { viewModel.doTapRestore() }
        }

        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventLoadError.observe(viewLifecycleOwner, handleLoadError)
            eventTapChooseMembership.observe(viewLifecycleOwner, handleTapChooseMembership)
            eventTapCancelMembership.observe(viewLifecycleOwner, handleTapCancelMembership)
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)

            doLoadView()
        }
    }

    private val handleLoadView = Observer<List<MembershipModel>> { memberships ->
        LogManager.log("handleLoadView")
        adapter.update(memberships)
        viewBinding.btnRestore.isHidden = memberships.isEmpty()
    }

    private val handleTapBack = Observer<Void> {
        LogManager.log("handleTapBack")
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadError = Observer<String> { error ->
        LogManager.log("handleLoadError")
        val model = ErrorAlertModel.error(error)
        NavigationManager.shared.present(this, R.id.error_fragment, model)
    }

    private val handleTapChooseMembership = Observer<MembershipModel> { membership ->
        LogManager.log("handleTapChooseMembership")
        val model = AlertModel.create(
            title = "",
            message = requireContext()
                .getString(R.string.memberships_choose_membership_alert_message)
        ).apply {
            setPrimaryButton(
                R.string.memberships_choose_membership_alert_primary_label,
                state = AlertViewState.POSITIVE
            ) { viewModel.doCallSubscribe(requireActivity(), membership) }
            setSecondaryButton(R.string.memberships_choose_membership_alert_secondary_label)
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleTapCancelMembership = Observer<MembershipModel> {
        LogManager.log("handleTapCancelMembership")
        val context = requireContext()
        val model = AlertModel.create(
            title = "",
            message = context.getString(R.string.memberships_cancel_membership_alert_message)
        ).apply {
            setPrimaryButton(
                R.string.memberships_cancel_membership_alert_primary_label,
                state = AlertViewState.POSITIVE
            )
            setSecondaryButton(R.string.memberships_cancel_membership_alert_secondary_label) {
                val model = ErrorAlertModel.error(
                    context.getString(R.string.memberships_cancel_wip_label)
                )
                NavigationManager.shared.present(
                    this@MembershipsFragment,
                    R.id.error_fragment,
                    model
                )
            }
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.hudView.isVisible = show
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}

package com.trx.consumer.screens.memberships

import android.text.SpannableString
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.BuildConfig
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentMembershipsBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.extensions.spannableString
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.managers.UtilityManager
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
            eventShowCancelActive.observe(viewLifecycleOwner, handleShowCancelActive)
            eventShowCancelMobile.observe(viewLifecycleOwner, handleShowCancelMobile)
            eventShowCancelWeb.observe(viewLifecycleOwner, handleShowCancelWeb)
            eventShowRestore.observe(viewLifecycleOwner, handleShowRestore)
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)

            doTrackPageView()
            doLoadView()
        }
    }

    private val handleLoadView = Observer<List<MembershipModel>> { memberships ->
        LogManager.log("handleLoadView")
        adapter.update(memberships)
        viewBinding.apply {
            btnRestore.isHidden = memberships.isEmpty()
            lblTerm.text(loadTerm())
        }
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
            setClearButton(loadTerm())
            //setSecondaryButton(R.string.memberships_alert_secondary_label)
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleShowCancelActive = Observer<Void> {
        LogManager.log("handleShowCancelActive")
        val model = AlertModel.create(
            title = "",
            message = requireContext()
                .getString(R.string.memberships_show_cancel_active_alert_message)
        ).apply {
            setPrimaryButton(
                R.string.memberships_show_cancel_active_primary_label,
                state = AlertViewState.POSITIVE
            )
            setSecondaryButton(R.string.memberships_alert_secondary_label)
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleShowCancelMobile = Observer<Void> {
        LogManager.log("handleShowCancelMobile")
        val context = requireContext()
        val model = AlertModel.create(
            title = "",
            message = context.getString(R.string.memberships_show_cancel_mobile_alert_message)
        ).apply {
            setPrimaryButton(
                R.string.memberships_show_cancel_mobile_alert_primary_label,
                state = AlertViewState.POSITIVE
            ) { UtilityManager.shared.openUrl(context, BuildConfig.kGooglePlaySubscriptionsUrl) }
            setSecondaryButton(R.string.memberships_alert_secondary_label)
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleShowCancelWeb = Observer<Void> {
        LogManager.log("handleShowCancelWeb")
        val context = requireContext()
        val model = AlertModel.create(
            title = "",
            message = context.getString(R.string.memberships_show_cancel_web_alert_message)
        ).apply {
            setPrimaryButton(
                R.string.memberships_show_cancel_web_primary_label,
                state = AlertViewState.POSITIVE
            ) {
                UtilityManager.shared.openUrl(context, BuildConfig.trxUrl)
            }
            setSecondaryButton(R.string.memberships_alert_secondary_label)
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleShowRestore = Observer<Void> {
        LogManager.log("handleShowRestore")
        NavigationManager.shared.present(this, R.id.restore_fragment)
    }

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.hudView.isVisible = show
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private fun loadTerm(): SpannableString {
        val context = requireContext()
        return context.spannableString(
            context.getString(R.string.memberships_terms_privacy),
            highlightedColor = ContextCompat.getColor(context, R.color.grey),
            highlightedStrings = listOf(
                context.getString(R.string.memberships_privacy),
                context.getString(R.string.memberships_terms)
            ),
            clickableSpans = listOf(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        UtilityManager.shared.openUrl(context, BuildConfig.kPrivatePolicy)
                    }
                },
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        UtilityManager.shared.openUrl(context, BuildConfig.kTermsConditionsUrl)
                    }
                }
            )
        )
    }
}

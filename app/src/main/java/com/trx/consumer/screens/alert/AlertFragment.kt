package com.trx.consumer.screens.alert

import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseDialogFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentAlertBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.AlertModel

class AlertFragment : BaseDialogFragment(R.layout.fragment_alert) {

    private val viewModel: AlertViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentAlertBinding::bind)

    override fun bind() {
        val model = NavigationManager.shared.params(this) as AlertModel

        viewBinding.apply {
            btnPrimary.action { viewModel.doTapPrimary() }
            btnSecondary.action { viewModel.doTapSecondary() }
            btnClose.action { viewModel.doTapBack() }
        }

        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapPrimary.observe(viewLifecycleOwner, handleTapPrimary)
            eventTapSecondary.observe(viewLifecycleOwner, handleTapSecondary)
        }

        viewModel.doLoadView(model)
    }

    private val handleLoadView = Observer<AlertModel> { model ->
        viewBinding.apply {
            model.apply {
                lblTitle.text = title
                lblBody.text = message
                btnPrimary.apply {
                    text = getString(primaryTitle)
                    textColor(primaryState.titleColor)
                    bgColor(primaryState.bgColor)
                }
                btnSecondary.apply {
                    if (secondaryState == AlertViewState.CLEAR) {
                        isHidden = clearTitle == null
                        clearTitle?.let { text(it) }
                        typeface = ResourcesCompat.getFont(context, secondaryState.fontFamily)
                    } else {
                        val hide = secondaryTitle == 0
                        if (!hide) text = getString(secondaryTitle)
                        isHidden = hide
                    }
                    textColor(secondaryState.titleColor)
                    bgColor(secondaryState.bgColor)
                }
            }
            viewContent.startAnimation(bottomInAnimation(requireContext()))
        }
    }

    private val handleTapBack = Observer<Void> {
        actionOnDismiss()
        LogManager.log("handleTapBack: Tapped Back")
    }

    private val handleTapPrimary = Observer<(() -> Unit)> { method ->
        LogManager.log("handleTapPrimary: Tapped Primary")
        actionOnDismiss(method)
    }

    private val handleTapSecondary = Observer<(() -> Unit)> { method ->
        LogManager.log("handleTapSecondary: Tapped Secondary")
        actionOnDismiss(method)
    }

    private fun actionOnDismiss(method: (() -> Unit)? = null) {
        viewBinding.viewContent.startOutAnimation(bottomOutAnimation(requireContext())) {
            method?.invoke()
            super.dismiss()
        }
    }

    override fun dismiss() {
        LogManager.log("dismiss dialog")
        viewModel.onBackPressed()
    }
}

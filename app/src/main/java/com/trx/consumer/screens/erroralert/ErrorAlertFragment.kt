package com.trx.consumer.screens.erroralert

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseDialogFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentErrorAlertBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager

class ErrorAlertFragment : BaseDialogFragment(R.layout.fragment_error_alert) {

    // region Objects
    private val viewModel: ErrorAlertViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentErrorAlertBinding::bind)

    //endregion

    //region Setup
    override fun bind() {

        val model = NavigationManager.shared.params(this) as ErrorAlertModel

        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapOutside.observe(viewLifecycleOwner, handleTapOutside)
            eventTapDismiss.observe(viewLifecycleOwner, handleTapDismiss)
        }

        viewModel.doLoadView(model)
    }

    //endregion

    //region Handlers
    private val handleLoadView = Observer<ErrorAlertModel> { errorModel ->
        LogManager.log("handleLoadView")
        viewBinding.apply {
            lblErrorText.text(errorModel.message)
            btnDismiss.action { viewModel.doTapDismiss() }
            viewMain.action { viewModel.doTapOutside() }
            viewContent.startInAnimation(topInAnimation(requireContext()))
        }
    }

    private val handleTapOutside = Observer<Void> {
        LogManager.log("handleTapOutside")
        dismiss()
    }

    private val handleTapDismiss = Observer<Void> {
        LogManager.log("handleTapDismiss")
        dismiss()
    }

    override fun dismiss() {
        viewBinding.viewContent.startOutAnimation(topOutAnimation(requireContext())) {
            super.dismiss()
        }
    }

    //endregion
}

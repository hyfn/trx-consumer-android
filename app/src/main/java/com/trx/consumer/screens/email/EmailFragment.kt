package com.trx.consumer.screens.email

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentEmailBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.setPrimaryEnabled
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.managers.UtilityManager
import com.trx.consumer.models.common.AlertModel
import com.trx.consumer.screens.alert.AlertViewState

class EmailFragment : BaseFragment(R.layout.fragment_email) {

    //region Objects

    private val viewModel: EmailViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentEmailBinding::bind)

    //endregion

    //region Initializers

    override fun bind() {
        (NavigationManager.shared.params(this) as? EmailViewState)?.let { emailViewState ->
            viewModel.state = emailViewState
        }

        viewBinding.apply {
            btnBack.action { viewModel.doTapBack() }
            txtSendEmail.setInputViewListener(viewModel)
            btnSendEmail.action {
                viewModel.doDismissKeyboard()
                viewModel.doTapSendEmail()
            }
        }

        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventLoadState.observe(viewLifecycleOwner, handleLoadState)
            eventLoadButton.observe(viewLifecycleOwner, handleLoadButton)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventSendEmailSuccess.observe(viewLifecycleOwner, handleSendEmailSuccess)
            eventSendEmailError.observe(viewLifecycleOwner, handleSendEmailError)
            eventDismissKeyboard.observe(viewLifecycleOwner, handleDismissKeyboard)
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)

            doTrackPageView()
        }

        viewModel.doLoadView()
    }

    //endregion

    //region Handlers

    private val handleLoadView = Observer<Void> {
        LogManager.log("handleLoadView")
        //  Empty on iOS but in code
    }

    private val handleLoadState = Observer<EmailViewState> { state ->
        LogManager.log("handleLoadState")
        loadEmailViewState(state)
    }

    private val handleLoadButton = Observer<Boolean> { enabled ->
        viewBinding.btnSendEmail.setPrimaryEnabled(enabled)
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleSendEmailSuccess = Observer<Int> { stringInt ->
        LogManager.log("handleSendEmailSuccess")
        val message = getString(stringInt)
        val model = AlertModel.create("SUCCESS", message)

        model.setPrimaryButton(R.string.alert_primary_cool) {
            viewBinding.txtSendEmail.text = ""
        }
        model.setSecondaryButton(0)
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleSendEmailError = Observer<String> { message ->
        LogManager.log("handleSendEmailError: $message")
        val model = AlertModel.create("ERROR", message)
        model.setPrimaryButton(
            title = R.string.alert_primary_back,
            state = AlertViewState.NEUTRAL
        ) {
            NavigationManager.shared.dismiss(this, null)
        }
        model.setSecondaryButton(
            title = R.string.alert_secondary_report,
            state = AlertViewState.NEGATIVE
        ) {
            UtilityManager.shared.showSupportEmail(this)
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleDismissKeyboard = Observer<Void> {
        viewBinding.txtSendEmail.dismiss()
    }

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.emailHudView.isVisible = show
    }

    //endregion

    //region Helper Functions

    private fun loadEmailViewState(state: EmailViewState) {
        viewBinding.apply {
            imgHeader.setImageResource(state.headerImage)
            lblHeader.setText(state.headerTitle)
            txtSendEmail.setInputViewState(state.inputViewState)
            lblDescription.setText(state.description)
            btnSendEmail.text = getString(state.buttonTitle)
        }
    }

    //endregion
}

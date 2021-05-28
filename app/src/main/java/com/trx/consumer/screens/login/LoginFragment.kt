package com.trx.consumer.screens.login

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentLoginBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.setPrimaryEnabled
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.screens.email.EmailViewState
import com.trx.consumer.screens.erroralert.ErrorAlertModel

class LoginFragment : BaseFragment(R.layout.fragment_login) {

    //region Objects

    private val viewModel: LoginViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentLoginBinding::bind)

    //endregion

    //region Initializers

    override fun bind() {
        viewBinding.apply {
            txtEmail.setInputViewListener(viewModel)
            txtPassword.setInputViewListener(viewModel)

            btnBack.action { viewModel.doTapBack() }
            btnForgotPassword.action { viewModel.doTapForgotPassword() }
            btnLogin.action {
                viewModel.doDismissKeyboard()
                viewModel.doTapLogin()
            }
            lblSignUp.action { viewModel.doTapSignUp() }
        }

        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventLoadButton.observe(viewLifecycleOwner, handleLoadButton)

            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapForgotPassword.observe(viewLifecycleOwner, handleTapForgotPassword)
            eventTapLogin.observe(viewLifecycleOwner, handleTapLogin)
            eventTapSignUp.observe(viewLifecycleOwner, handleTapSignUp)

            eventShowError.observe(viewLifecycleOwner, handleShowError)
            eventValidateError.observe(viewLifecycleOwner, handleValidateError)

            eventShowOnboarding.observe(viewLifecycleOwner, handleShowOnboarding)
            eventDismissKeyboard.observe(viewLifecycleOwner, handleDismissKeyboard)
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)
        }
    }

    //endregion

    //region Handlers

    private val handleLoadView = Observer<Void> {
        LogManager.log("handleLoadView")
    }

    private val handleLoadButton = Observer<Boolean> { enabled ->
        viewBinding.btnLogin.setPrimaryEnabled(enabled)
    }

    private val handleShowError = Observer<String> { error ->
        LogManager.log("handleShowError")
        val model = ErrorAlertModel.error(message = error)
        NavigationManager.shared.present(this, R.id.error_fragment, model)
    }

    private val handleValidateError = Observer<Int> { error ->
        LogManager.log("handleValidateError")
        val model = ErrorAlertModel.error(message = getString(error))
        NavigationManager.shared.present(this, R.id.error_fragment, model)
    }

    private val handleTapSignUp = Observer<Void> {
        NavigationManager.shared.present(this, R.id.register_fragment)
    }

    private val handleTapLogin = Observer<Void> {
        NavigationManager.shared.loggedInLaunchSequence(this)
    }

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.hudView.isVisible = show
    }

    private val handleTapForgotPassword = Observer<Void> {
        NavigationManager.shared.present(
            this,
            R.id.email_fragment,
            EmailViewState.FORGOT
        )
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    override fun onBackPressed() {
        NavigationManager.shared.dismiss(this)
    }

    private val handleShowOnboarding = Observer<Void> {
        LogManager.log("handleShowOnboarding")
        NavigationManager.shared.present(this, R.id.onboarding_fragment)
    }

    private val handleDismissKeyboard = Observer<Void> {
        dismissKeyboard()
    }

    //endregion

    //region Helper Functions

    private fun dismissKeyboard() {
        viewBinding.apply {
            txtEmail.dismiss()
            txtPassword.dismiss()
        }
    }

    //endregion
}

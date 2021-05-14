package com.trx.consumer.screens.login

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentLoginBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.params.EmailParamsModel
import com.trx.consumer.screens.email.EmailViewState
import com.trx.consumer.screens.erroralert.ErrorAlertModel

class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentLoginBinding::bind)

    override fun bind() {
        viewBinding.apply {
            ivEmail.setInputViewListener(viewModel)
            ivPassword.setInputViewListener(viewModel)

            btnBack.action { viewModel.doTapBack() }
            lblSignUp.action { viewModel.doTapSignUp() }
            btnLogin.action {
                viewModel.doDismissKeyboard()
                viewModel.doTapLogin()
            }
            btnForgotPassword.action { viewModel.doTapForgotPassword() }
        }

        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapSignUp.observe(viewLifecycleOwner, handleTapSignUp)
            eventTapForgotPassword.observe(viewLifecycleOwner, handleTapForgotPassword)
            eventShowError.observe(viewLifecycleOwner, handleShowError)
            eventValidateError.observe(viewLifecycleOwner, handleValidateError)
            eventTapLogin.observe(viewLifecycleOwner, handleTapLogin)
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)
            eventDismissKeyboard.observe(viewLifecycleOwner, handleDismissKeyboard)
        }
    }

    private val handleLoadView = Observer<Void> {
        LogManager.log("handleLoadView")
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
            EmailParamsModel(EmailViewState.FORGOT)
        )
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleDismissKeyboard = Observer<Void> {
        dismissKeyboard()
    }

    private fun dismissKeyboard() {
        viewBinding.apply {
            ivEmail.dismiss()
            ivPassword.dismiss()
        }
    }

    override fun onBackPressed() {
        NavigationManager.shared.dismiss(this)
    }
}

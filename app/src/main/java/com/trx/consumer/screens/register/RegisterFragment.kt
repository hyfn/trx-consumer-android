package com.trx.consumer.screens.register

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.BuildConfig.kTermsConditionsUrl
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentRegisterBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.setPrimaryEnabled
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.managers.UtilityManager
import com.trx.consumer.screens.erroralert.ErrorAlertModel

class RegisterFragment : BaseFragment(R.layout.fragment_register) {

    //region Objects

    private val viewModel: RegisterViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentRegisterBinding::bind)

    //endregion

    //region Initializers

    override fun bind() {
        viewBinding.apply {
            txtFirstName.setInputViewListener(viewModel)
            txtLastName.setInputViewListener(viewModel)
            txtEmail.setInputViewListener(viewModel)
            txtPassword.setInputViewListener(viewModel)
            txtConfirmPassword.setInputViewListener(viewModel)
            btnLogin.action { viewModel.doTapLogin() }
            btnCreateAccount.action {
                viewModel.doDismissKeyboard()
                viewModel.doCreateAccount()
            }
            btnBack.action { viewModel.doTapBack() }
            cbTerm.setOnCheckedChangeListener { _, isChecked ->
                viewModel.doTapCheckbox(isChecked)
            }
        }

        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventLoadButton.observe(viewLifecycleOwner, handleLoadButton)
            eventShowOnboarding.observe(viewLifecycleOwner, handleShowOnboarding)
            eventShowError.observe(viewLifecycleOwner, handleShowError)
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)
            eventTapLogin.observe(viewLifecycleOwner, handleTapLogin)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapTermsConditions.observe(viewLifecycleOwner, handleTapTermsConditions)
            eventValidateError.observe(viewLifecycleOwner, handleValidateError)
            eventDismissKeyboard.observe(viewLifecycleOwner, handleDismissKeyboard)

            doTrackPageView()
            doLoadView()
        }
    }

    //endregion

    //region Handlers

    private val handleLoadView = Observer<Void> {
        viewBinding.apply {
            val termsClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    viewModel.doTapTermsConditions()
                }
            }
            val termsFull = getString(R.string.register_terms)
            val termsConditions = getString(R.string.register_terms_conditions)

            val builder = SpannableStringBuilder(termsFull)

            // TODO: Investigate making highlighted span bold
            builder.setSpan(
                termsClickableSpan,
                termsFull.indexOf(termsConditions),
                termsFull.indexOf(termsConditions) + termsConditions.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            lblTerm.movementMethod = LinkMovementMethod()
            lblTerm.text = builder
        }
    }

    private val handleLoadButton = Observer<Boolean> { enabled ->
        viewBinding.btnCreateAccount.setPrimaryEnabled(enabled)
    }

    private val handleShowOnboarding = Observer<Void> {
        NavigationManager.shared.dismiss(this, R.id.onboarding_fragment)
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleTapLogin = Observer<Void> {
        NavigationManager.shared.dismiss(this, R.id.login_fragment)
    }

    private val handleTapTermsConditions = Observer<Void> {
        LogManager.log("handleTapTermsAndConditions")
        UtilityManager.shared.openUrl(requireContext(), kTermsConditionsUrl)
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

    private val handleDismissKeyboard = Observer<Void> {
        dismissKeyboard()
    }

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.hudView.isVisible = show
    }

    //endregion

    //region Helper Functions

    private fun dismissKeyboard() {
        viewBinding.apply {
            txtFirstName.dismiss()
            txtLastName.dismiss()
            txtEmail.dismiss()
            txtPassword.dismiss()
            txtConfirmPassword.dismiss()
        }
    }

    //endregion
}

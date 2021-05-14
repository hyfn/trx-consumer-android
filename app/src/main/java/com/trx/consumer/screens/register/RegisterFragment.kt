package com.trx.consumer.screens.register

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentRegisterBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.screens.erroralert.ErrorAlertModel

class RegisterFragment : BaseFragment(R.layout.fragment_register) {

    private val viewModel: RegisterViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentRegisterBinding::bind)

    override fun bind() {
        viewBinding.apply {
            ivEmail.setInputViewListener(viewModel)
            ivPassword.setInputViewListener(viewModel)
            ivConfirmPassword.setInputViewListener(viewModel)
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
            eventLoadProfile.observe(viewLifecycleOwner, handleLoadProfile)
            eventLoadError.observe(viewLifecycleOwner, handleLoadError)
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)
            eventTapLogin.observe(viewLifecycleOwner, handleTapLogin)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapTermsConditions.observe(viewLifecycleOwner, handleTapTermsConditions)
            eventValidateError.observe(viewLifecycleOwner, handleValidateError)
            eventDismissKeyboard.observe(viewLifecycleOwner, handleDismissKeyboard)

            doLoadView()
        }
    }

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

    private val handleLoadError = Observer<String> { error ->
        // TODO: Display Alert once implemented
        with(requireContext()) {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }

    private val handleValidateError = Observer<Int> { error ->
        LogManager.log("handleValidateError")
        val model = ErrorAlertModel.error(message = getString(error))
        NavigationManager.shared.present(this, R.id.error_fragment, model)
    }

    private val handleLoadProfile = Observer<Void> {
        // TODO: Navigate user to UpdateFragment
        NavigationManager.shared.loggedInLaunchSequence(this)
    }

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.hudView.isVisible = show
    }

    private val handleTapTermsConditions = Observer<Void> {
        // TODO: Handle displaying Content with terms and conditions
    }

    private val handleTapLogin = Observer<Void> {
        NavigationManager.shared.dismiss(this, R.id.login_fragment)
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
            ivConfirmPassword.dismiss()
        }
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}

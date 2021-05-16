package com.trx.consumer.screens.update

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentUpdateBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.params.UpdateParamsModel

class UpdateFragment : BaseFragment(R.layout.fragment_update) {

    private val viewModel: UpdateViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentUpdateBinding::bind)

    override fun bind() {
        val model = NavigationManager.shared.params(this) as UpdateParamsModel

        viewBinding.apply {
            ivFirstName.setInputViewListener(viewModel)
            ivLastName.setInputViewListener(viewModel)
            ivZipCode.setInputViewListener(viewModel)
            ivBirthDate.showDatePicker(this@UpdateFragment)
            ivBirthDate.setInputViewListener(viewModel)
            cbAgreement.setOnCheckedChangeListener { _, isChecked ->
                viewModel.doTapCheckbox(isChecked)
            }
            btnBack.action { viewModel.doTapBack() }
            btnSave.action { viewModel.doTapSave() }
        }

        viewModel.apply {
            state = model.state

            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventUpdateDate.observe(viewLifecycleOwner, handleUpdateDate)
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)
            eventLoadButton.observe(viewLifecycleOwner, handleLoadButton)
            eventLoadSuccess.observe(viewLifecycleOwner, handleLoadSuccess)
            eventLoadError.observe(viewLifecycleOwner, handleLoadError)
            eventShowOnboarding.observe(viewLifecycleOwner, handleShowOnboarding)
            eventShowVerification.observe(viewLifecycleOwner, handleShowVerification)

            doLoadView()
        }
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadView = Observer<UpdateViewState> {
        viewBinding.apply {
            btnSave.text = getString(it.buttonTitle)
            if (it == UpdateViewState.CREATE) {
                lblAgreement.isVisible = true
                cbAgreement.isVisible = true
            }
        }
    }

    private val handleLoadButton = Observer<Boolean> {
        viewBinding.btnSave.isEnabled = it
    }

    private val handleLoadSuccess = Observer<String> {
        LogManager.log("handleLoadSuccess: $it")
        // TODO: AlertModel modal presentation
    }

    private val handleLoadError = Observer<String> {
        LogManager.log("handleLoadError: $it")
        // TODO: ErrorAlertModel modal presentation
    }

    private val handleShowOnboarding = Observer<Void> {
        LogManager.log("handleShowOnboarding")
        // TODO: OnboardingFragment presentation
        //  Temporarily just heading to home
        NavigationManager.shared.loggedInLaunchSequence(this)
    }

    private val handleShowVerification = Observer<Void> {
        LogManager.log("handleShowVerification")
        // TODO: EmailFragment CODE state presentation
    }

    private val handleUpdateDate = Observer<String> {
        viewBinding.ivBirthDate.text = it
    }

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.hudView.isVisible = show
    }
}

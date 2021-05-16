package com.trx.consumer.screens.update

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentUpdateBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.UserModel
import com.trx.consumer.models.params.UpdateParamsModel

class UpdateFragment : BaseFragment(R.layout.fragment_update) {

    //region Objects

    private val viewModel: UpdateViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentUpdateBinding::bind)

    //endregion

    //region Initializers

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
            btnContinue.action { viewModel.doTapContinue() }
        }

        viewModel.apply {
            state = model.state

            eventLoadState.observe(viewLifecycleOwner, handleLoadState)
            eventLoadUser.observe(viewLifecycleOwner, handleLoadUser)
            eventLoadButton.observe(viewLifecycleOwner, handleLoadButton)
            eventLoadSuccess.observe(viewLifecycleOwner, handleLoadSuccess)
            eventLoadError.observe(viewLifecycleOwner, handleLoadError)

            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapTermsAndConditions.observe(viewLifecycleOwner, handleTapTermsAndConditions)
            eventTapWaivers.observe(viewLifecycleOwner, handleTapWaiver)

            eventUpdateDate.observe(viewLifecycleOwner, handleUpdateDate)

            eventShowOnboarding.observe(viewLifecycleOwner, handleShowOnboarding)
            eventShowVerification.observe(viewLifecycleOwner, handleShowVerification)
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)

            doLoadView()
        }
    }

    //endregion

    //region Handlers

    private val handleLoadState = Observer<UpdateViewState> {
        viewBinding.apply {
            btnContinue.text = getString(it.buttonTitle)
            viewAgreement.isHidden = (it == UpdateViewState.EDIT)
        }
    }

    private val handleLoadUser = Observer<UserModel> { user ->
        viewBinding.apply {
            ivFirstName.text = user.firstName
            ivLastName.text = user.lastName
            ivBirthDate.text = user.birthday
            ivZipCode.text = user.zipCode
        }
    }

    private val handleLoadButton = Observer<Boolean> {
        viewBinding.btnContinue.isEnabled = it
    }

    private val handleLoadSuccess = Observer<String> {
        LogManager.log("handleLoadSuccess: $it")
        // TODO: AlertModel modal presentation
    }

    private val handleLoadError = Observer<String> {
        LogManager.log("handleLoadError: $it")
        // TODO: ErrorAlertModel modal presentation
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleTapTermsAndConditions = Observer<Void> {
        LogManager.log("handleTapTermsAndConditions")
        //  TODO: Add call to showTermWaivers()
    }

    private val handleTapWaiver = Observer<Void> {
        LogManager.log("handleTapWaiver")
        //  TODO: Add call to showTermWaivers()
    }

    private val handleUpdateDate = Observer<String> {
        viewBinding.ivBirthDate.text = it
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

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.hudView.isVisible = show
    }

    //endregion
}

package com.trx.consumer.screens.addcard

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentAddCardBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.AlertModel
import com.trx.consumer.models.common.PurchaseModel
import com.trx.consumer.screens.alert.AlertViewState
import com.trx.consumer.screens.erroralert.ErrorAlertModel

class AddCardFragment : BaseFragment(R.layout.fragment_add_card) {

    //region Objects

    private val viewModel: AddCardViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentAddCardBinding::bind)

    //endregion

    //region Initializers

    override fun bind() {
        viewBinding.apply {
            btnBack.action { viewModel.doTapBack() }
            btnClose.action { viewModel.doTapClose() }
            btnSave.action {
                viewModel.doDismissKeyboard()
                viewModel.doTapSave()
            }

            txtNumber.setInputViewListener(viewModel)
            txtExpiration.setInputViewListener(viewModel)
            txtCVC.setInputViewListener(viewModel)
            txtZip.setInputViewListener(viewModel)
        }

        viewModel.apply {
            eventLoadNavBar.observe(viewLifecycleOwner, handleLoadNavBar)
            eventLoadButton.observe(viewLifecycleOwner, handleLoadButton)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapClose.observe(viewLifecycleOwner, handleTapClose)
            eventSaveSuccess.observe(viewLifecycleOwner, handleSaveSuccess)
            eventSaveError.observe(viewLifecycleOwner, handleSaveError)
            eventValidateError.observe(viewLifecycleOwner, handleValidateError)
            eventShowPurchase.observe(viewLifecycleOwner, handleShowPurchase)
            eventDismissKeyboard.observe(viewLifecycleOwner, handleDismissKeyboard)
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)
        }

        viewModel.doLoadView()
    }

    //endregion

    //region Handlers

    private val handleLoadNavBar = Observer<Boolean> { showClose ->
        loadNavBar(showClose)
    }

    private val handleLoadButton = Observer<Boolean> { enabled ->
        viewBinding.apply {
            btnSave.apply {
                isEnabled = enabled
                bgColor(if (enabled) R.color.black else R.color.greyDark)
            }
        }
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleTapClose = Observer<Void> {
        LogManager.log("handleTapClose")
        NavigationManager.shared.dismiss(this)
    }

    private val handleSaveSuccess = Observer<Void> {
        LogManager.log("handleSaveSuccess")
        val model = AlertModel.create("SUCCESS", "Card successfully added")
        //  TODO: Rework logic in later flow. 
        // val previousFragment = NavigationManager.shared.previousFragment(requireActivity())
        model.setPrimaryButton(R.string.alert_primary_cool) {
            //  TODO: Rework logic in later flow. 
            // previousFragment?.let { safePreviousFragment ->
            // if (safePreviousFragment in listOf(
            // R.id.cards_fragment,
            //  TODO: Add PurchaseFragment when imported
            // R.id.purchase_fragment,
            // R.id.plans_fragment
            // )
            // ) {
            //     NavigationManager.shared.dismiss(this, safePreviousFragment)
            // } else {
            NavigationManager.shared.dismiss(this, null)
            // }
            // }
        }
        model.setSecondaryButton(0)
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleSaveError = Observer<String> { value ->
        LogManager.log("handleSaveError")
        val model = AlertModel.create("ERROR", value)
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
            //  TODO: Add when showSupportEmail() is imported.
            //  UtilityManager.shared.showSupportEmail()
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleValidateError = Observer<String> { message ->
        LogManager.log("handleValidateError: $message")
        val model = ErrorAlertModel.error(message)
        NavigationManager.shared.present(this, R.id.error_fragment, model)
    }

    private val handleShowPurchase = Observer<PurchaseModel> { purchaseModel ->
        LogManager.log("handleShowPurchase: $purchaseModel")
        /* TODO: Not yet implemented in iOS
        NavigationManager.shared.dismiss(
            this,
            R.id.purchase_fragment,
            PurchaseModel
        )
        */
    }

    private val handleDismissKeyboard = Observer<Void> {
        dismissKeyboard()
    }

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.hudView.isVisible = show
    }

    //endregion

    //region Helper Functions

    private fun loadNavBar(showClose: Boolean) {
        viewBinding.apply {
            btnBack.isHidden = showClose
            btnClose.isHidden = !showClose
        }
    }

    private fun dismissKeyboard() {
        viewBinding.apply {
            txtNumber.dismiss()
            txtExpiration.dismiss()
            txtCVC.dismiss()
            txtZip.dismiss()
        }
    }

    //endregion
}

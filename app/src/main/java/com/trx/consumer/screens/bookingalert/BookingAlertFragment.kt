package com.trx.consumer.screens.bookingalert

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.BuildConfig.kTermsSubscriptions
import com.trx.consumer.R
import com.trx.consumer.base.BaseDialogFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentBookingAlertBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.managers.UtilityManager
import com.trx.consumer.models.common.AlertModel
import com.trx.consumer.models.common.BookingAlertModel
import com.trx.consumer.models.common.CardModel
import com.trx.consumer.models.states.BookingState
import com.trx.consumer.screens.alert.AlertViewState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingAlertFragment : BaseDialogFragment(R.layout.fragment_booking_alert) {

    //region Objects

    private val viewModel: BookingAlertViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentBookingAlertBinding::bind)

    //endregion

    //region Initializers

    override fun bind() {
        val model = NavigationManager.shared.params(this) as BookingAlertModel
        viewModel.model = model

        viewBinding.viewContent.isInvisible = true

        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventLoadAddCard.observe(viewLifecycleOwner, handleLoadAddCard)

            eventShowPolicy.observe(viewLifecycleOwner, handleShowPolicy)
            eventShowSuccess.observe(viewLifecycleOwner, handleShowSuccess)
            eventShowError.observe(viewLifecycleOwner, handleShowError)

            eventTapClose.observe(viewLifecycleOwner, handleTapClose)

            eventShowHud.observe(viewLifecycleOwner, handleShowHud)
        }

        viewModel.doLoadView()
    }

    //endregion

    //region Handlers

    private val handleLoadView = Observer<BookingAlertModel> { model ->
        LogManager.log("handleLoadView: $model")
        viewBinding.viewContent.isHidden = false
        loadView(model)
        viewBinding.viewContent.startInAnimation(bottomInAnimation(requireContext()))
    }

    private val handleLoadAddCard = Observer<Void> {
        LogManager.log("handleLoadAddCard")
        NavigationManager.shared.present(this, R.id.add_card_fragment)
    }

    private val handleShowPolicy = Observer<Void> {
        LogManager.log("handleShowPolicy")
        UtilityManager.shared.openUrl(requireContext(), kTermsSubscriptions)
    }

    private val handleShowSuccess = Observer<String> { value ->
        LogManager.log("handleShowSuccess")
        val model = AlertModel.create("BOOK SESSION", value)
        model.setPrimaryButton(R.string.alert_primary_done, AlertViewState.NEUTRAL) {
            dismiss()
        }
        model.setSecondaryButton(0)
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleShowError = Observer<String> { value ->
        LogManager.log("handleShowError")
        val model = AlertModel.create("ERROR", value)
        model.setPrimaryButton(R.string.alert_primary_back, AlertViewState.NEUTRAL) {
            dismiss()
        }
        model.setSecondaryButton(R.string.alert_secondary_report, AlertViewState.NEGATIVE) {
            UtilityManager.shared.showSupportEmail(this)
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleTapClose = Observer<Void> {
        LogManager.log("handleTapClose")
        NavigationManager.shared.dismiss(this)
    }

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.hudView.isVisible = show
    }

    override fun dismiss() {
        if (viewBinding.viewContent.isVisible) {
            viewBinding.viewContent.startOutAnimation(bottomOutAnimation(requireContext())) {
                super.dismiss()
            }
        } else {
            super.dismiss()
        }
    }

    //endregion

    //region Helper Functions

    private fun loadView(model: BookingAlertModel) {
        viewBinding.apply {
            // Views that are always present.
            lblMainTitle.text = getString(model.title)
            btnPolicy.action { viewModel.doTapPolicy() }
            btnClose.action { viewModel.doTapClose() }

            // Conditional view loading
            when (model.workout.state) {
                BookingState.BOOKED -> loadBookedState()
                else -> {
                    btnPrimary.text = model.buttonTitle
                    loadDefaultState(model.card)
                }
            }

            // Remeasure layout so all the margins are the same.
            viewContent.requestLayout()
        }
    }

    private fun loadBookedState() {
        viewBinding.apply {
            viewCard.isHidden = true

            //  Styles since Button previously black bg / white text, adds action
            btnPrimary.apply {
                text = getString(R.string.booking_alert_cancel_no_button_label)
                bgColor(R.color.greyLight)
                textColor(R.color.black)
                action { viewModel.doTapClose() }
            }

            //  Button just changes visibility and adds action
            btnSecondary.apply {
                isVisible = true
                action { viewModel.doTapCancelYes() }
            }
        }
    }

    //  btnAddPayment already styled in xml
    //  btnPrimary restyled and given action based on enabled state. 
    private fun loadDefaultState(card: CardModel?) {
        viewBinding.apply {
            card?.let {
                //  Populates child views of viewCardBg with card data
                viewCardBg.isHidden = false
                imgCard.setImageResource(it.imageName)
                lblType.text = getString(card.typeText)
                lblNumber.text(it.number)

                btnAddPayment.isHidden = true
                btnPrimary.action { viewModel.doTapBook() }
            } ?: run {
                viewCardBg.isHidden = true

                btnAddPayment.apply {
                    isHidden = false
                    action { viewModel.doTapAddPayment() }
                }

                btnPrimary.apply {
                    isEnabled = false
                    btnPrimary.bgColor(R.color.grey)
                }
            }
        }
    }

    //endregion
}

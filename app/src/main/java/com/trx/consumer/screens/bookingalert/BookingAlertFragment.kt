package com.trx.consumer.screens.bookingalert

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseDialogFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentBookingAlertBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.BookingAlertModel
import com.trx.consumer.models.common.BookingState.BOOKED
import com.trx.consumer.models.common.CardModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingAlertFragment : BaseDialogFragment(R.layout.fragment_booking_alert) {

    private val viewModel: BookingAlertViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentBookingAlertBinding::bind)

    override fun bind() {

        val model = NavigationManager.shared.params(this) as BookingAlertModel
        viewModel.model = model

        viewBinding.apply {
            btnPrimary.action {
                viewContent.isInvisible = true
                viewModel.doTapPrimary()
            }
            btnSecondary.action { viewModel.doTapSecondary() }
        }

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

    private val handleLoadView = Observer<BookingAlertModel> { model ->
        LogManager.log("handleLoadView: $model")
        loadView(model)
        viewBinding.viewContent.startInAnimation(bottomInAnimation(requireContext()))
        // viewBinding.apply {
        //     viewContent.isHidden = false
        //     if (it.state == ClassViewState.BOOK) {
        //         lblMainTitle.text = SpannableStringBuilder(
        //             lblMainTitle.context.getString(R.string.booking_alert_main_title_book)
        //         ).color(
        //             ContextCompat.getColor(lblMainTitle.context, R.color.blue)
        //         ) { append(it.pointDisplay) }
        //
        //         btnPrimary.apply {
        //             bgColor(R.color.blueDark)
        //             textColor(R.color.white)
        //         }
        //     } else {
        //         lblMainTitle.text = lblMainTitle.context.getString(
        //             R.string.booking_alert_main_title_else
        //         )
        //
        //         btnPrimary.apply {
        //             bgColor(R.color.white)
        //             textColor(R.color.red)
        //             border(R.color.red, 1.px)
        //         }
        //     }
        //
        //     imgClass.load(it.bannerImageName)
        //     lblTitle.text = it.title
        //     lblSubtitle.text = it.subtitle
        //     lblClassInfo.text = it.classInfo
        //     btnPrimary.text = btnPrimary.context.getString(it.state.primaryModalButtonTitle)
        //     btnSecondary.text = btnSecondary.context.getString(it.state.secondaryModalButtonTitle)
        //
        //     // dialog animation
    }

    private val handleLoadAddCard = Observer<Void> {
        LogManager.log("handleLoad_")
    }

    private val handleShowPolicy = Observer<Void> { }

    private val handleShowSuccess = Observer<String> {
        LogManager.log("handleShowSuccess")
        // val model = AlertModel.success(it)
        // model.setPrimaryButton(R.string.alert_primary_continue) {
        //     NavigationManager.shared.dismiss(this, R.id.home_fragment)
        // }
        // model.setSecondaryButton(0)
        // model.setBackAction(AlertBackAction.SECONDARY)
        // NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleShowError = Observer<String> {
        LogManager.log("handleShowError")
        // val model = AlertModel.error(it)
        // model.setPrimaryButton(R.string.alert_primary_back) {
        //     dismiss()
        // }
        // model.setSecondaryButton(R.string.alert_secondary_report) {
        //     dismiss()
        //     UtilityManager.shared.showSupportEmail(this)
        // }
        // model.setBackAction(AlertBackAction.PRIMARY)
        // NavigationManager.shared.present(this, R.id.alert_fragment, model)
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

    //region Helper Functions

    //  TODO: Complete logic
    private fun loadView(model: BookingAlertModel) {
        viewBinding.apply {
            lblMainTitle.text(model.title)

            when (model.workout.state) {
                BOOKED -> TODO()
                else -> loadViewCard(model.card)
            }
        }
    }

    //  TODO: Complete logic
    private fun loadViewCard(card: CardModel?) {
        viewBinding.apply {
            card?.let {
                imgCard.setImageResource(it.imageName)
                lblType.text(card.typeText)
                lblNumber.text(it.number)
                viewCard.isHidden = false
            } ?: run {
                viewCard.isHidden = false
            }
        }
    }

    //endregion
}

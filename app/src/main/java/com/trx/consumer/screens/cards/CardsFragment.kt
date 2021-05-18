package com.trx.consumer.screens.cards

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentCardsBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.AlertModel
import com.trx.consumer.models.common.CardModel
import com.trx.consumer.screens.alert.AlertViewState

class CardsFragment : BaseFragment(R.layout.fragment_cards) {

    //region Objects

    private val viewModel: CardsViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentCardsBinding::bind)

    //endregion

    //region Initializers

    override fun bind() {
        viewBinding.apply {
            btnBack.action { viewModel.doTapBack() }
            btnEditCard.action { viewModel.doTapAdd() }
        }
        viewModel.apply {
            eventLoadViewSuccess.observe(viewLifecycleOwner, handleLoadViewSuccess)
            eventLoadViewFailure.observe(viewLifecycleOwner, handleLoadViewFailure)
            eventTapAdd.observe(viewLifecycleOwner, handleTapAdd)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapReplace.observe(viewLifecycleOwner, handleTapReplace)
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)
            doLoadView()
        }
    }

    //endregion

    //region Handlers

    private val handleLoadViewSuccess = Observer<CardModel?> {
        it?.let { model ->
            viewBinding.apply {
                viewCard.isHidden = false
                lblEmptyTitle.isHidden = true
                imgCard.setImageResource(model.imageName)
                lblType.text = lblType.context.getString(model.typeText)
                lblNumber.text = model.number
                btnEditCard.text = getString(R.string.cards_update_payment_method_label)
                btnEditCard.action { viewModel.doTapReplace() }
            }
        }
    }

    private val handleLoadViewFailure = Observer<Void> {
        LogManager.log("handleLoadViewFailure")
        val model = AlertModel.create("CARDS", "Could not load cards")
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
            //  TODO: Add when showContactSupport is imported.
            //  UtilityManager.shared.showContactSupport()
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleTapAdd = Observer<Void> {
        LogManager.log("handleTapAdd")
        NavigationManager.shared.present(this, R.id.add_card_fragment)
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    private val handleTapReplace = Observer<Void> {
        LogManager.log("handleTapReplace")
        NavigationManager.shared.present(this, R.id.add_card_fragment)
    }

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.hudView.isVisible = show
    }

    //endregion
}

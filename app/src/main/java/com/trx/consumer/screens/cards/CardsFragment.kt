package com.trx.consumer.screens.cards

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentCardsBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.CardModel

class CardsFragment : BaseFragment(R.layout.fragment_cards) {

    //region Objects

    private val viewModel: CardsViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentCardsBinding::bind)

    //endregion

    //region Initializers

    override fun bind() {
        viewBinding.apply {
            btnBack.action { viewModel.doTapBack() }
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
                viewCard.isVisible = true
                lblEmpty.isVisible = false
                ivIcon.setImageResource(model.imageName)
                lblType.text = lblType.context.getString(model.typeText)
                lblNumber.text = model.number
                btnPayment.text = getString(R.string.cards_update_payment_method_label)
            }
        } ?: run {
            viewBinding.apply {
                viewCard.isVisible = false
                lblEmpty.isVisible = true
                btnPayment.text = getString(R.string.cards_add_payment_method_label)
            }
        }

        // viewBinding.apply {
        //     if (it != null) {
        //         viewCard.isVisible = true
        //         lblEmpty.isVisible = false
        //         ivIcon.setImageResource(it.imageName)
        //         lblType.text = lblType.context.getString(it.typeText)
        //         lblNumber.text = it.number
        //         btnPayment.text = getString(R.string.cards_update_payment_method_label)
        //     } else {
        //         viewCard.isVisible = false
        //         lblEmpty.isVisible = true
        //         btnPayment.text = getString(R.string.cards_add_payment_method_label)
        //     }
        // }
    }

    private val handleLoadViewFailure = Observer<Void> {
        LogManager.log("handleLoadViewFailure")
        //  TODO: Add AlertModel
/*        val model =
            AlertModel.error("Could not load cards")
        model.setPrimaryButton(R.string.alert_primary_back) {
            NavigationManager.shared.dismiss(this, null)
        }
        model.setPrimaryButton(R.string.alert_secondary_report) {
            UtilityManager.shared.showContactSupport()
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)*/
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

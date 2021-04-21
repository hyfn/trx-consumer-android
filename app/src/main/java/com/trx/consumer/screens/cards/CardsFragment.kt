package com.trx.consumer.screens.cards

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentCardsBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.CardModel

class CardsFragment : BaseFragment(R.layout.fragment_cards) {

    private val viewModel: CardsViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentCardsBinding::bind)

    override fun bind() {
        viewBinding.btnBack.action { viewModel.doTapBack() }

        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            doLoadView()
        }
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    private val handleLoadView = Observer<CardModel?> {
        viewBinding.apply {
            if (it != null) {
                viewCard.isVisible = true
                lblEmpty.isVisible = false
                ivIcon.setImageResource(it.imageName)
                lblType.text = lblType.context.getString(it.typeText)
                lblNumber.text = it.number
                btnPayment.text = getString(R.string.cards_update_payment_method_label)
            } else {
                viewCard.isVisible = false
                lblEmpty.isVisible = true
                btnPayment.text = getString(R.string.cards_add_payment_method_label)
            }
        }
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }
}

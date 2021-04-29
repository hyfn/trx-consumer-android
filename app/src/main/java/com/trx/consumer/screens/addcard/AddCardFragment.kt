package com.trx.consumer.screens.addcard

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentAddCardBinding
import com.trx.consumer.managers.NavigationManager

class AddCardFragment : BaseFragment(R.layout.fragment_add_card) {

    private val viewModel: AddCardViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentAddCardBinding::bind)

    override fun bind() {
        viewModel.eventTapBack.observe(viewLifecycleOwner, handleTapBack)
        viewBinding.btnBack.setOnClickListener { viewModel.doTapBack() }
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }
}

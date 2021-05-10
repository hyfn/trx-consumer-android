package com.trx.consumer.screens.trainer

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentTrainerBinding
import com.trx.consumer.managers.NavigationManager

class TrainerFragment : BaseFragment(R.layout.fragment_trainer) {

    private val viewModel: TrainerViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentTrainerBinding::bind)

    override fun bind() {

        viewBinding.btnBack.setOnClickListener { viewModel.doTapBack() }

        viewModel.eventTapBack.observe(viewLifecycleOwner, handleTapBack)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }
}

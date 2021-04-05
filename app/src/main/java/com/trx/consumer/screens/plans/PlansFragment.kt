package com.trx.consumer.screens.plans

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentPlansBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager

class PlansFragment : BaseFragment(R.layout.fragment_plans) {

    private val viewModel: PlansViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentPlansBinding::bind)

    override fun bind() {
        viewModel.eventTapBack.observe(viewLifecycleOwner, handleTapBack)
        viewBinding.btnBack.action { viewModel.doTapBack() }
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}

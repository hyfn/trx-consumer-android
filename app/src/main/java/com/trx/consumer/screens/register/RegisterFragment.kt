package com.trx.consumer.screens.register

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentRegisterBinding
import com.trx.consumer.managers.NavigationManager

class RegisterFragment : BaseFragment(R.layout.fragment_register) {

    private val viewModel: RegisterViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentRegisterBinding::bind)

    override fun bind() {
        viewModel.eventTapBack.observe(viewLifecycleOwner, handleTapBack)
        viewBinding.btnBack.setOnClickListener { viewModel.doTapBack() }
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}

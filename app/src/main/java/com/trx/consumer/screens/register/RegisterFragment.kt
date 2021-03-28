package com.trx.consumer.screens.register

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentRegisterBinding
import com.trx.consumer.managers.NavigationManager

class RegisterFragment : BaseFragment(R.layout.fragment_register) {

    //region Objects
    private val viewModel: RegisterViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentRegisterBinding::bind)
    //endregion

    //region Setup
    override fun bind() {
        viewModel.eventTapBack.observe(viewLifecycleOwner, handleTapBack)
        viewBinding.btnBack.setOnClickListener { viewModel.doTapBack() }
    }

    //endregion

    //region Handlers
    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    //endregion
}

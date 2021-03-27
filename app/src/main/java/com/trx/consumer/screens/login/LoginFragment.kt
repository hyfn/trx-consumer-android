package com.trx.consumer.screens.login

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentLoginBinding
import com.trx.consumer.managers.NavigationManager

class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentLoginBinding::bind)

    override fun bind() {
        viewBinding.btnBack.setOnClickListener { viewModel.doTapBack() }
        viewModel.eventTapBack.observe(viewLifecycleOwner, handleTapBack)
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}

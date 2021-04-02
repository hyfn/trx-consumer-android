package com.trx.consumer.screens.login

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentLoginBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager

class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentLoginBinding::bind)

    override fun bind() {
        viewBinding.apply {
            btnBack.setOnClickListener { viewModel.doTapBack() }
            lblSignUp.action { viewModel.doTapSignUp() }
        }

        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapSignUp.observe(viewLifecycleOwner, handleTapSignUp)
        }
    }

    override fun onBackPressed() {
        NavigationManager.shared.dismiss(this)
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleTapSignUp = Observer<Void> {
        NavigationManager.shared.present(this, R.id.register_fragment)
    }
}
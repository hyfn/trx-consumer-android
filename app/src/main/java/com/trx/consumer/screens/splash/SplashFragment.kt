package com.trx.consumer.screens.splash

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentSplashBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager

class SplashFragment : BaseFragment(R.layout.fragment_splash) {

    //region Objects
    private val viewModel: SplashViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentSplashBinding::bind)
    //endregion

    //region Setup
    override fun bind() {
        viewBinding.apply {
            btnEmail.action { viewModel.doTapEmail() }
            lblSignUp.action { viewModel.doTapSignUp() }
        }

        viewModel.apply {
            eventTapEmail.observe(viewLifecycleOwner, handleTapEmail)
            eventTapSignUp.observe(viewLifecycleOwner, handleTapSignUp)
        }
    }
    //endregion

    //region Handlers

    private val handleTapEmail = Observer<Void> {
        NavigationManager.shared.present(this, R.id.login_fragment)
    }

    private val handleTapSignUp = Observer<Void> {
        NavigationManager.shared.present(this, R.id.register_fragment)
    }

    //endregion
}

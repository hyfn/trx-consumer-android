package com.trx.consumer.screens.splash

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentSplashBinding
import com.trx.consumer.extensions.action

class SplashFragment : BaseFragment(R.layout.fragment_splash) {

    //region Objects
    private val viewModel: SplashViewModel by activityViewModels()
    private val viewBinding by viewBinding(FragmentSplashBinding::bind)
    //endregion

    override fun bind() {
        viewBinding.apply {
            btnFacebook.action { viewModel.doTapFacebook() }
            btnGoogle.action { viewModel.doTapGoogle() }
            btnApple.action { viewModel.doTapApple() }
            btnEmail.action { viewModel.doTapEmail() }
        }

        viewModel.apply {
            eventTapFacebook.observe(viewLifecycleOwner, handleTapFacebook)
            eventTapGoogle.observe(viewLifecycleOwner, handleTapGoogle)
            eventTapApple.observe(viewLifecycleOwner, handleTapApple)
            eventTapEmail.observe(viewLifecycleOwner, handleTapEmail)
        }

        viewModel.doLoadView()
    }
    //endregion

    //region Handlers
    private val handleLoadView = Observer<Void> {
    }

    private val handleTapFacebook = Observer<Void> {
    }

    private val handleTapGoogle = Observer<Void> {
    }

    private val handleTapApple = Observer<Void> {
    }

    private val handleTapEmail = Observer<Void> {
    }

    //endregion
}

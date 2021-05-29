package com.trx.consumer.screens.onboarding

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentOnboardingBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager

class OnBoardingFragment : BaseFragment(R.layout.fragment_onboarding) {

    private val viewModel: OnBoardingViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentOnboardingBinding::bind)

    override fun bind() {
        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapClose.observe(viewLifecycleOwner, handleTapClose)
            eventTapNext.observe(viewLifecycleOwner, handleTapNext)
        }

        viewBinding.apply {
            btnContinue.action { viewModel.doTapNext() }
            btnClose.action { viewModel.doTapClose() }
        }

        viewModel.doLoadView()
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    private val handleLoadView = Observer<OnBoardingViewState> { state ->
        viewBinding.apply {
            lblTitle.text = getString(state.title)
            ivHeader.setImageResource(state.image)
            lblDescription.text = getString(state.description)
            lblItemOne.text = getString(state.listItemOne)
            lblItemTwo.text = getString(state.listItemTwo)
            lblItemThree.text = getString(state.listItemThree)
        }
    }

    private val handleTapClose = Observer<Void> {
        NavigationManager.shared.loggedInLaunchSequence(this)
    }

    private val handleTapNext = Observer<Void> {
        NavigationManager.shared.loggedInLaunchSequence(this)
    }
}

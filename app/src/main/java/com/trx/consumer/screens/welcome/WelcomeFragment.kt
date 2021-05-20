package com.trx.consumer.screens.welcome

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentWelcomeBinding
import com.trx.consumer.managers.NavigationManager

class WelcomeFragment : BaseFragment(R.layout.fragment_welcome) {

    private val viewModel: WelcomeViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentWelcomeBinding::bind)

    override fun bind() {

        val state = NavigationManager.shared.params(this) as WelcomeState
        viewModel.state = state

        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapClose.observe(viewLifecycleOwner, handleTapClose)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            doLoadView()
        }

        viewBinding.apply {
            btnBack.setOnClickListener { viewModel.doTapBack() }
            btnClose.setOnClickListener { viewModel.doTapClose() }
        }
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleTapClose = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadView = Observer<WelcomeState> { state ->
        viewBinding.apply {
            lblTitle.apply { text = context.getString(state.title, "Carly") }
            lblDescription.apply { text = context.getString(state.description) }
            btnContinue.apply { text = context.getString(state.buttonTitle) }
        }
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}

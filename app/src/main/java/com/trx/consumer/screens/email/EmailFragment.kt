package com.trx.consumer.screens.email

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentEmailBinding
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager

class EmailFragment : BaseFragment(R.layout.fragment_email) {

    private val viewModel: EmailViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentEmailBinding::bind)

    override fun bind() {
        val state = NavigationManager.shared.params(this) as EmailViewState
        viewModel.state = state

        viewBinding.btnBack.setOnClickListener { viewModel.doTapBack() }

        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventLoadState.observe(viewLifecycleOwner, handleLoadState)
            eventLoadButton.observe(viewLifecycleOwner, handleLoadButton)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            doLoadView()
        }
    }

    private val handleLoadView = Observer<Void> {
        LogManager.log("handleLoadView")
        //  Empty on iOS but in code
    }

    private val handleLoadState = Observer<EmailViewState> { state ->
        LogManager.log("handleLoadState")
        viewBinding.apply {
            imgHeader.setImageResource(state.headerImage)
            lblHeader.setText(state.headerTitle)
            txtEmail.setEmailInputViewState(state)
            lblDescription.setText(state.description)
            btnSendEmail.text = getString(state.buttonTitle)
        }
    }

    private val handleLoadButton = Observer<Boolean> { enabled ->
        //  eventLoadButton could be removed all together. States not apparent in figma or iOS. 
        viewBinding.btnSendEmail.apply {
            isEnabled = enabled
            bgColor(if (enabled) R.color.black else R.color.greyDark)
        }
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}

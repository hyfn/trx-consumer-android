package com.trx.consumer.screens.email

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentEmailBinding
import com.trx.consumer.managers.NavigationManager

class EmailFragment : BaseFragment(R.layout.fragment_email) {

    private val viewModel: EmailViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentEmailBinding::bind)

    override fun bind() {
        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            doLoadView()
        }
        viewBinding.btnBack.setOnClickListener { viewModel.doTapBack() }
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadView = Observer<EmailViewState> {
        load(it)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private fun load(state: EmailViewState) {
        viewBinding.lblTitle.setText(state.title)
    }
}

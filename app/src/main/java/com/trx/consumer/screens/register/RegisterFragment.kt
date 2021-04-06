package com.trx.consumer.screens.register

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.managers.NavigationManager

class RegisterFragment : BaseFragment(R.layout.fragment_register) {

    //region Objects
    private val viewModel: RegisterViewModel by viewModels()

    //endregion

    //region Setup
    override fun bind() {
        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
        }
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

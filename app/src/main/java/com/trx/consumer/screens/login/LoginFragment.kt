package com.trx.consumer.screens.login

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.managers.NavigationManager

class LoginFragment : BaseFragment(R.layout.fragment_login) {

    //region Objects
    private val viewModel: LoginViewModel by viewModels()

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

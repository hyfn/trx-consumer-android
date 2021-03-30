package com.trx.consumer.screens.home

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentHomeBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    //region Objects
    private val viewModel: HomeViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentHomeBinding::bind)

    //endregion

    //region Setup
    override fun bind() {
        viewBinding.btnTest.action { viewModel.doTapTest() }

        viewModel.eventTapTest.observe(viewLifecycleOwner, handleTapTest)
    }
    //endregion

    //region Handlers

    private val handleTapTest = Observer<Void> {
        LogManager.log("handleTapTest")
        NavigationManager.shared.present(this, R.id.test_utility_fragment)
    }

    //endregion
}

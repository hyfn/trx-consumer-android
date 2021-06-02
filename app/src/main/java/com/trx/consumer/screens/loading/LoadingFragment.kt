package com.trx.consumer.screens.loading

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentLoadingBinding
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager

class LoadingFragment : BaseFragment(R.layout.fragment_loading) {

    //region Objects
    private val viewBinding by viewBinding(FragmentLoadingBinding::bind)
    private val viewModel: LoadingViewModel by viewModels()

    //endregion

    //region Setup
    override fun bind() {
        val model = NavigationManager.shared.params(this) as LoadingViewState

        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)
        }

        viewModel.doLoadView(model)
    }

    //endregion

    //region Handlers
    private val handleShowHud = Observer<Boolean> { show ->
        LogManager.log("handleShowHud")
        viewBinding.hudView.isVisible = show
    }

    private val handleLoadView = Observer<LoadingViewState> { state ->
        LogManager.log("handleLoadView")
        viewBinding.lblTitle.apply {
            text = context.getString(state.message)
        }
    }

    //endregion
}

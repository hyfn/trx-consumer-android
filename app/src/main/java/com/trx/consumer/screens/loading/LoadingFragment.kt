package com.trx.consumer.screens.loading

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentLoadingBinding
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.AlertModel
import com.trx.consumer.screens.alert.AlertBackAction

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
            eventLoadAuth.observe(viewLifecycleOwner, handleLoadAuth)
            eventLoadAuthSuccess.observe(viewLifecycleOwner, handleLoadAuthSuccess)
            eventLoadAuthFailure.observe(viewLifecycleOwner, handleLoadAuthFailure)
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

    private val handleLoadAuth = Observer<Void> {
        LogManager.log("handleLoadAuth")
        viewModel.doLoadAuth()
    }

    private val handleLoadAuthSuccess = Observer<Void> {
        LogManager.log("handleLoadAuthSuccess")
        NavigationManager.shared.loggedInLaunchSequence(this)
    }

    private val handleLoadAuthFailure = Observer<String> { message ->
        LogManager.log("handleLoadAuthFailure")
        val model = AlertModel.create(title = "", message = message)
        model.apply {
            setPrimaryButton(R.string.alert_primary_back) {
                NavigationManager.shared.notLoggedInLaunchSequence(this@LoadingFragment)
            }
            setSecondaryButton(0)
            setBackAction(AlertBackAction.PRIMARY)
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    //endregion
}

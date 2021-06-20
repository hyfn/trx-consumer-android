package com.trx.consumer.screens.maintenance

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentMaintenanceBinding
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.managers.UtilityManager

class MaintenanceFragment : BaseFragment(R.layout.fragment_maintenance) {

    private val viewModel: MaintenanceViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentMaintenanceBinding::bind)
    private val params by lazy {
        NavigationManager.shared.params(this) as MaintenanceViewState
    }

    override fun bind() {
        super.bind()

        viewModel.state = params
        observeViewModel()
        viewModel.doLoadView()
    }

    private fun observeViewModel() = with(viewModel) {
        eventLoadView.observe(viewLifecycleOwner, handleLoadView)
        eventTapButton.observe(viewLifecycleOwner, handleTapButton)
    }

    private val handleLoadView = Observer<MaintenanceViewState> { viewState ->
        LogManager.log("handleLoadView")
        with(viewState) {
            viewBinding.apply {
                imgLogo.setImageResource(imageLogo)
                tvTitle.text = tvTitle.context.getString(title)
                tvMessage.text = tvMessage.context.getString(message)
                btnPrimary.apply {
                    text = btnPrimary.context.getString(btnTitle)
                    isVisible = viewState == MaintenanceViewState.UPDATE
                }
            }
        }
    }

    private val handleTapButton = Observer<Unit> {
        LogManager.log("handleTapButton")
        // TODO: add play store Url
        val url = ""
        if (url.isNotEmpty())
            UtilityManager.shared.openUrl(requireContext(), url)
        else
            NavigationManager.shared.dismiss(this)
    }

    override fun onBackPressed() {
        NavigationManager.shared.dismiss(this)
    }
}

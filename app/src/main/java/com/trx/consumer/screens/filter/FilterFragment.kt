package com.trx.consumer.screens.filter

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentFiltersBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.VideoFilterModel
import com.trx.consumer.models.params.VideoFilterParamsModel

class FilterFragment : BaseFragment(R.layout.fragment_filters) {

    //region Objects
    private val viewModel: FilterViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentFiltersBinding::bind)

    private var adapter: VideoFilterAdapter? = null

    //endregion

    //region Setuo
    override fun bind() {

        val params = NavigationManager.shared.params(this) as VideoFilterParamsModel
        viewModel.params = params
        adapter = VideoFilterAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            rvFilters.adapter = adapter

            btnApply.action { viewModel.doTapApply() }
            btnClear.action { viewModel.doTapReset() }
            btnClose.action { viewModel.doTapClose() }
        }

        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapApply.observe(viewLifecycleOwner, handleTapApply)
            eventTapClose.observe(viewLifecycleOwner, handleTapClose)
            eventTapReset.observe(viewLifecycleOwner, handleTapReset)
            eventTapFilter.observe(viewLifecycleOwner, handleTapFilter)
        }

        viewModel.doLoadView()
    }

    //region - Handlers
    private val handleLoadView = Observer<VideoFilterParamsModel> { model ->
        LogManager.log("handleLoad")
        if (model.list.isNotEmpty()) loadView(model)
        else {
            viewBinding.apply {
                rvFilters.isVisible = false
                lblEmptyList.isVisible = true
            }
        }
    }

    private val handleTapApply = Observer<VideoFilterParamsModel> { model ->
        LogManager.log("handlerTapApply")
        NavigationManager.shared.dismiss(this) // remove after TESTING
        // NavigationManager.shared.dismiss(this,R.id.video_fragment, model ) screen should go back to previous screen with videoFilterParamsModel
    }

    private val handleTapClose = Observer<Void> {
        LogManager.log("handleTapClose")
        NavigationManager.shared.dismiss(this)
    }

    private val handleTapReset = Observer<Void> {
        LogManager.log("handleTapReset")
    }

    private val handleTapFilter = Observer<VideoFilterModel> { model ->
        LogManager.log("handleTapFilter: ${model.title}")
        //NavigationManager.shared.dismiss(this,R.id.filter_option_fragment, model ) move to new screen to populate the filter values
    }
    //endregion

    //region - Functions
    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    private fun loadView(model: VideoFilterParamsModel) {
        adapter?.update(model.list)
    }

    //endregion
}

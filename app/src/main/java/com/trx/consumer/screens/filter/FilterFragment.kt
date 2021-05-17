package com.trx.consumer.screens.filter

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.databinding.FragmentFiltersBinding
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.VideoFilterModel
import com.trx.consumer.models.params.VideoFilterParamsModel

class FilterFragment: BaseFragment(R.layout.fragment_filters) {

    //region Objects
    private val viewModel: FilterViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentFiltersBinding::bind)

    private var filterAdapter: FilterAdapter? = null

    //endregion

    //region Setuo

    override fun bind() {

       val params = NavigationManager.shared.params(this) as VideoFilterParamsModel
        viewModel.params = params
        filterAdapter = FilterAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {

        }

        viewModel.apply {
            eventTapApply.observe(viewLifecycleOwner, handleTapApply)
            eventTapClose.observe(viewLifecycleOwner, handleTapClose)
            eventTapReset.observe(viewLifecycleOwner, handleTapReset)
            eventTapFilter.observe(viewLifecycleOwner, handleTapFilter)
        }
    }

    //region - Handlers

    private val handlerTapApply = Observer<VideoFilterParamsModel> { model ->


    }

    private val handleTapClose = Observer<Void> {
        LogManager.log("handleTapClose")
        NavigationManager.shared.dismiss(this)
    }

    private val handleTapApply = Observer<VideoFilterParamsModel> { params ->
        LogManager.log("handleTapApply")
    }

    private val handleTapReset = Observer<Void> {
        LogManager.log("handleTapReset")
    }

    private val handleTapFilter = Observer<VideoFilterModel> { model ->
        LogManager.log("handleTapFilter: ${model.title}")
    }
    //endregion

    //region - Functions
    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    //endregion
}
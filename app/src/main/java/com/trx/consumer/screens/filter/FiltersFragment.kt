package com.trx.consumer.screens.filter

import android.os.Bundle
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
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.params.FilterParamsModel

class FiltersFragment : BaseFragment(R.layout.fragment_filters) {

    //region Objects
    private val viewModel: FiltersViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentFiltersBinding::bind)

    private var adapter: FiltersAdapter? = null

    //endregion

    //region Setuo
    override fun bind() {

        val model = NavigationManager.shared.params(this) as FilterParamsModel
        viewModel.params = model

        adapter = FiltersAdapter(viewModel) { lifecycleScope }

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
            eventSetSelected.observe(viewLifecycleOwner, handleSetSelected)
        }

        viewModel.doLoadView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("Hello", viewModel.params)
    }

    //region - Handlers
    private val handleLoadView = Observer<FilterParamsModel> { model ->
        LogManager.log("handleLoad")
        if (model.list.isNotEmpty()) loadView(model)
        else {
            viewBinding.apply {
                rvFilters.isVisible = false
                lblEmptyList.isVisible = true
            }
        }
    }

    private val handleTapApply = Observer<FilterParamsModel> { model ->
        LogManager.log("handlerTapApply")
        NavigationManager.shared.dismiss(this) // remove after TESTING
        // NavigationManager.shared.dismiss(this,R.id.video_fragment, model ) screen should go back to previous screen with videoFilterParamsModel
    }

    private val handleTapClose = Observer<Void> {
        LogManager.log("handleTapClose")
        NavigationManager.shared.dismiss(this)
    }

    private val handleTapReset = Observer<FilterParamsModel> { model ->
        LogManager.log("handleTapReset")
        adapter?.update(model.list)
    }

    private val handleTapFilter = Observer<FilterParamsModel> { model ->
        LogManager.log("handleTapFilter")
        NavigationManager.shared.present(this, R.id.filter_options_fragment, model)
    }

    private val handleSetSelected = Observer<FilterParamsModel?> { model ->
        model?.let {  safeModel -> adapter?.update(safeModel.list) }
    }
    //endregion

    //region - Functions
    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    private fun loadView(model: FilterParamsModel) {
        adapter?.update(model.list)
    }

    //endregion
}

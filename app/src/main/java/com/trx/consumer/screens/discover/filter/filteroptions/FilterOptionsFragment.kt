package com.trx.consumer.screens.discover.filter.filteroptions

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentFilterOptionsBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.params.FilterParamsModel

class FilterOptionsFragment : BaseFragment(R.layout.fragment_filter_options) {

    //region Objects
    private val viewModel: FilterOptionsViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentFilterOptionsBinding::bind)

    private var adapter: FilterOptionsAdapter? = null
    //endregion

    //region Setup
    override fun bind() {

        val params = NavigationManager.shared.params(this) as FilterParamsModel
        viewModel.filter = params.selectedModel
        viewModel.params = params
        adapter = FilterOptionsAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            rvFilters.adapter = adapter
            btnSelectAll.action { viewModel.doTapSelectAll() }
            btnBack.action { viewModel.doTapBack() }
        }

        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapFilterOption.observe(viewLifecycleOwner, handleTapFilterOption)
            eventTapSelectAll.observe(viewLifecycleOwner, handleTapSelectAll)
        }

        viewModel.doLoadView()
    }

    //endregion

    //region - Handlers
    private val handleLoadView = Observer<FilterModel> { model ->
        LogManager.log("handleLoadView")
        viewBinding.lblFilterTitle.text = model.title
        if (model.values.isNotEmpty()) adapter?.update(model.values)
        else {
            viewBinding.apply {
                rvFilters.isVisible = false
                lblEmptyList.isVisible = true
            }
        }
    }

    private val handleTapSelectAll = Observer<FilterModel> { model ->
        adapter?.update(model.values)
    }

    private val handleTapBack = Observer<FilterParamsModel> { model ->
        LogManager.log("handleTapBack")
        NavigationManager.shared.dismiss(this, R.id.filter_fragment, model)
    }

    private val handleTapFilterOption = Observer<FilterModel> { model ->
        LogManager.log("handleTapBack")
        adapter?.update(model.values)
    }

    //endregion

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
    //endregion
}

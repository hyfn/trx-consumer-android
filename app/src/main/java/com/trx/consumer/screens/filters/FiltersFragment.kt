package com.trx.consumer.screens.filters

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

class FiltersFragment : BaseFragment(R.layout.fragment_filters) {

    //region Objects
    private val viewModel: FiltersViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentFiltersBinding::bind)

    private var adapter: FiltersAdapter? = null
    //endregion

    //region Setuo
    override fun bind() {

        val params = NavigationManager.shared.params(this) as VideoFilterModel
        viewModel.params = params
        adapter = FiltersAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            rvFilters.adapter = adapter
            btnSelectAll.action { viewModel.doTapSelectAll() }
            btnBack.action { viewModel.doTapBack() }
        }

        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapFilterValue.observe(viewLifecycleOwner, handleTapFilterValue)
            eventTapSelectAll.observe(viewLifecycleOwner, handleTapSelectAll)
        }

        viewModel.doLoadView()
    }

    //endregion

    //region - Handlers
    private val handleLoadView = Observer<VideoFilterModel> { model ->
        LogManager.log("handleLoadView")
        viewBinding.lblTitle.text = model.title
        if (model.values.isNotEmpty()) adapter?.update(model.values)
        else {
            viewBinding.apply {
                rvFilters.isVisible = false
                lblEmptyList.isVisible = true
            }
        }
    }

    private val handleTapSelectAll = Observer<VideoFilterModel> { model ->
        adapter?.update(model.values)
    }

    private val handleTapBack = Observer<VideoFilterModel> { model ->
        LogManager.log("handleTapBack")
        NavigationManager.shared.dismiss(this)
        //NavigationManager.shared.dismiss(this, R.id.filter_fragment, model)
    }

    private val handleTapFilterValue = Observer<VideoFilterModel> { model ->
        LogManager.log("handleTapBack")
        adapter?.update(model.values)
    }

    //endregion

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
    //endregion
}
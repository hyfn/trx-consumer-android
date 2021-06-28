package com.trx.consumer.screens.discover

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentDiscoverBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.DiscoverModel
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel
import com.trx.consumer.models.params.FilterParamsModel
import com.trx.consumer.screens.discover.discoverfilter.DiscoverFilterAdapter
import com.trx.consumer.screens.discover.list.DiscoverAdapter

class DiscoverFragment : BaseFragment(R.layout.fragment_discover) {

    //region Objects
    private val viewModel: DiscoverViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentDiscoverBinding::bind)

    private lateinit var adapter: DiscoverAdapter
    private lateinit var filterAdapter: DiscoverFilterAdapter

    //endregion

    //region Initializers
    override fun bind() {
        NavigationManager.shared.params(this)?.let { params ->
            if (params is FilterParamsModel) viewModel.params = params
        }

        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapVideo.observe(viewLifecycleOwner, handleTapVideo)
            eventTapVideos.observe(viewLifecycleOwner, handleTapVideos)
            eventTapFilter.observe(viewLifecycleOwner, handleTapFilter)
            eventLoadFilters.observe(viewLifecycleOwner, handleLoadFilters)
            eventTapDiscoverFilter.observe(viewLifecycleOwner, handlerTapDiscoverFilter)

            doTrackPageView()
        }

        viewBinding.apply {
            adapter = DiscoverAdapter(viewModel) { lifecycleScope }
            filterAdapter = DiscoverFilterAdapter(viewModel) { lifecycleScope }
            rvVideo.adapter = adapter
            rvFilters.adapter = filterAdapter

            btnWorkouts.action { viewModel.doTapWorkouts() }
            btnCollections.action { viewModel.doTapCollections() }
            btnPrograms.action { viewModel.doTapPrograms() }
            btnFilter.action { viewModel.doTapFilter() }
        }

        viewModel.doLoadView()
    }
    //endregion

    //region Handlers

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleLoadView = Observer<DiscoverModel> { model ->
        loadTabs(model.state)
        adapter.update(model)
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleTapVideo = Observer<VideoModel> {
        NavigationManager.shared.present(this, R.id.workout_fragment, it)
    }

    private val handleTapVideos = Observer<VideosModel> {
        NavigationManager.shared.present(this, R.id.videos_fragment, it)
    }

    private val handleLoadFilters = Observer<List<FilterModel>> { list ->
        setFilterEnabled(list.isNotEmpty())
        filterAdapter.update(list)
    }

    private val handlerTapDiscoverFilter = Observer<FilterParamsModel> { params ->
        NavigationManager.shared.present(this, R.id.filter_fragment, params)
    }

    private val handleTapFilter = Observer<FilterParamsModel> { params ->
        NavigationManager.shared.present(this, R.id.filter_fragment, params)
    }

    //endregion

    //region Helper Functions

    private fun loadTabs(newState: DiscoverViewState) {
        viewBinding.apply {
            btnWorkouts.textColor(R.color.grey)
            btnCollections.textColor(R.color.grey)
            btnPrograms.textColor(R.color.grey)
            indicatorWorkouts.isVisible = false
            indicatorCollections.isVisible = false
            indicatorPrograms.isVisible = false

            when (newState) {
                DiscoverViewState.WORKOUTS -> {
                    setFilterEnabled(true)
                    btnWorkouts.textColor(R.color.black)
                    indicatorWorkouts.isVisible = true
                }
                DiscoverViewState.COLLECTIONS -> {
                    setFilterEnabled(false)
                    btnCollections.textColor(R.color.black)
                    indicatorCollections.isVisible = true
                }
                DiscoverViewState.PROGRAMS -> {
                    setFilterEnabled(false)
                    btnPrograms.textColor(R.color.black)
                    indicatorPrograms.isVisible = true
                }
            }
        }
    }

    private fun setFilterEnabled(enabled: Boolean) {
        viewBinding.apply {
            viewFilter.alpha = if (enabled) 1f else 0.3f
            btnFilter.isEnabled = enabled
            rvFilters.isUserInteractionEnabled = enabled
        }
    }

    //endregion
}

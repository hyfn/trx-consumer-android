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

    private val viewModel: DiscoverViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentDiscoverBinding::bind)

    private lateinit var adapter: DiscoverAdapter
    private lateinit var discoverAdapter: DiscoverFilterAdapter

    override fun bind() {
        NavigationManager.shared.params(this)?.let { params ->
            if (params is FilterParamsModel) viewModel.params = params
        }

        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadWorkouts.observe(viewLifecycleOwner, handleLoadWorkouts)
            eventLoadCollections.observe(viewLifecycleOwner, handleLoadCollections)
            eventLoadPrograms.observe(viewLifecycleOwner, handleLoadPrograms)
            eventTapVideo.observe(viewLifecycleOwner, handleTapVideo)
            eventTapVideos.observe(viewLifecycleOwner, handleTapVideos)
            eventTapFilter.observe(viewLifecycleOwner, handleTapFilter)
            eventLoadFilters.observe(viewLifecycleOwner, handleLoadFilters)
            eventTapDiscoverFilter.observe(viewLifecycleOwner, handlerTapDiscoverFilter)
        }

        viewBinding.apply {
            adapter = DiscoverAdapter(viewModel) { lifecycleScope }
            discoverAdapter = DiscoverFilterAdapter(viewModel) { lifecycleScope }
            rvVideo.adapter = adapter
            rvFilters.adapter = discoverAdapter

            btnWorkouts.action { viewModel.doLoadVideos() }
            btnCollections.action { viewModel.doLoadCollections() }
            btnPrograms.action { viewModel.doLoadPrograms() }
            btnFilter.action { viewModel.doTapFilter() }
        }

        viewModel.doLoadVideos()
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
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

    private val handleLoadWorkouts = Observer<List<VideoModel>> { workouts ->
        handleFilterClick(true)
        loadWorkouts(workouts)
    }

    private val handleLoadCollections = Observer<List<VideosModel>> { collections ->
        handleFilterClick(false)
        loadCollections(collections)
    }

    private val handleLoadPrograms = Observer<List<VideosModel>> { programs ->
        handleFilterClick(false)
        loadPrograms(programs)
    }

    private val handleLoadFilters = Observer<List<FilterModel>> { list ->
        discoverAdapter.update(list)
    }

    private val handlerTapDiscoverFilter = Observer<FilterParamsModel> { params ->
        NavigationManager.shared.present(this, R.id.filter_fragment, params.copyModel())
    }

    private val handleTapFilter = Observer<FilterParamsModel> { params ->
        NavigationManager.shared.present(this, R.id.filter_fragment, params.copyModel())
    }

    private fun loadWorkouts(workouts: List<VideoModel>) {
        val state = DiscoverViewState.WORKOUTS
        loadTabs(state)
        adapter.update(DiscoverModel(state = state, workouts = workouts))
    }

    private fun loadCollections(videos: List<VideosModel>) {
        val state = DiscoverViewState.COLLECTIONS
        loadTabs(state)
        adapter.update(DiscoverModel(state = state, videos = videos))
    }

    private fun loadPrograms(videos: List<VideosModel>) {
        val state = DiscoverViewState.PROGRAMS
        loadTabs(state)
        adapter.update(DiscoverModel(state = state, videos = videos))
    }

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
                    btnWorkouts.textColor(R.color.black)
                    indicatorWorkouts.isVisible = true
                }
                DiscoverViewState.COLLECTIONS -> {
                    btnCollections.textColor(R.color.black)
                    indicatorCollections.isVisible = true
                }
                DiscoverViewState.PROGRAMS -> {
                    btnPrograms.textColor(R.color.black)
                    indicatorPrograms.isVisible = true
                }
            }
        }
    }

    private fun handleFilterClick(isClickable: Boolean) {
        viewBinding.viewFilter.alpha = if (!isClickable) 0.3f else 1f
        viewModel.setFilterClick(isClickable)
    }
}

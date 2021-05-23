package com.trx.consumer.screens.discover

import androidx.core.content.ContextCompat
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
    private var currentState = DiscoverViewState.WORKOUT

    override fun bind() {

        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadWorkouts.observe(viewLifecycleOwner, handleLoadWorkouts)
            eventLoadCollections.observe(viewLifecycleOwner, handleLoadCollections)
            eventLoadPrograms.observe(viewLifecycleOwner, handleLoadPrograms)
            eventTapVideo.observe(viewLifecycleOwner, handleTapVideo)
            eventTapVideos.observe(viewLifecycleOwner, handleTapVideos)
            eventTapFilter.observe(viewLifecycleOwner, handleTapFilter)
            eventLoadFilters.observe(viewLifecycleOwner, handleLoadFilters)
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)
            eventTapDiscoverFilter.observe(viewLifecycleOwner, handlerTapDiscoverFilter)
        }

        viewBinding.apply {
            adapter = DiscoverAdapter(viewModel) { lifecycleScope }
            discoverAdapter = DiscoverFilterAdapter(viewModel) { lifecycleScope }
            rvVideo.adapter = adapter
            rvFilters.adapter = discoverAdapter

            btnWorkouts.action { changeState(DiscoverViewState.WORKOUT) }
            btnCollections.action { changeState(DiscoverViewState.COLLECTIONS) }
            btnPrograms.action { changeState(DiscoverViewState.PROGRAMS) }
            btnFilter.action { viewModel.doTapFilter() }
        }

        viewModel.doLoadView()
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.hudView.isVisible = show
    }

    private val handleTapVideo = Observer<VideoModel> {
        NavigationManager.shared.present(this, R.id.workout_fragment, it)
    }

    private val handleTapVideos = Observer<VideosModel> {
        NavigationManager.shared.present(this, R.id.videos_fragment, it)
    }

    private val handleLoadWorkouts = Observer<List<VideoModel>> {
        adapter.updateVideos(it)
    }

    private val handleLoadCollections = Observer<List<VideosModel>> {
        adapter.updateVideos(it)
    }

    private val handleLoadPrograms = Observer<List<VideosModel>> {
        adapter.updateVideos(it)
    }

    private val handleLoadFilters = Observer<List<FilterModel>> { list ->
        discoverAdapter.update(list)
    }

    private val handlerTapDiscoverFilter = Observer<FilterParamsModel> { params ->
        NavigationManager.shared.present(this, R.id.filter_fragment, params)
    }

    private val handleTapFilter = Observer<FilterParamsModel> { params ->
        NavigationManager.shared.present(this, R.id.filter_fragment, params)
    }

    private fun changeState(newState: DiscoverViewState) {
        currentState = newState

        viewBinding.apply {
            btnWorkouts.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyLight))
            btnCollections.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyLight))
            btnPrograms.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyLight))
            indicatorWorkouts.isVisible = false
            indicatorCollections.isVisible = false
            indicatorPrograms.isVisible = false

            when (newState) {
                DiscoverViewState.WORKOUT -> {
                    btnWorkouts.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    indicatorWorkouts.isVisible = true
                    viewModel.doLoadWorkouts()
                }
                DiscoverViewState.COLLECTIONS -> {
                    btnCollections.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    indicatorCollections.isVisible = true
                    viewModel.doLoadCollections()
                }
                else -> {
                    btnPrograms.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    indicatorPrograms.isVisible = true
                    viewModel.doLoadPrograms()
                }
            }
        }
    }
}

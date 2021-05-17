package com.trx.consumer.screens.video

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentVideoBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.video.list.VideoAdapter

class VideoFragment : BaseFragment(R.layout.fragment_video) {

    private val viewModel: VideoViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentVideoBinding::bind)

    private lateinit var adapter: VideoAdapter
    private var currentState = VideoViewState.WORKOUT

    override fun bind() {

        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadWorkouts.observe(viewLifecycleOwner, handleLoadWorkouts)
            eventLoadCollections.observe(viewLifecycleOwner, handleLoadCollections)
            eventLoadPrograms.observe(viewLifecycleOwner, handleLoadPrograms)
        }

        viewBinding.apply {
            adapter = VideoAdapter(viewModel) { lifecycleScope }
            rvVideo.adapter = adapter

            btnWorkouts.action { changeState(VideoViewState.WORKOUT) }
            btnCollections.action { changeState(VideoViewState.COLLECTIONS) }
            btnPrograms.action { changeState(VideoViewState.PROGRAMS) }
        }

        viewModel.doLoadWorkouts()
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadWorkouts = Observer<List<WorkoutModel>> {
        adapter.updateVideos(it)
    }

    private val handleLoadCollections = Observer<List<WorkoutModel>> {
        adapter.updateVideos(it)
    }

    private val handleLoadPrograms = Observer<List<WorkoutModel>> {
        adapter.updateVideos(it)
    }

    private fun changeState(newState: VideoViewState) {
        currentState = newState

        viewBinding.apply {
            btnWorkouts.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyLight))
            btnCollections.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyLight))
            btnPrograms.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyLight))
            indicatorWorkouts.isVisible = false
            indicatorCollections.isVisible = false
            indicatorPrograms.isVisible = false

            when (newState) {
                VideoViewState.WORKOUT -> {
                    btnWorkouts.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    indicatorWorkouts.isVisible = true
                    viewModel.doLoadWorkouts()
                }
                VideoViewState.COLLECTIONS -> {
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

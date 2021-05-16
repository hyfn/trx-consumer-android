package com.trx.consumer.screens.video

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentVideoBinding
import com.trx.consumer.extensions.action

class VideoFragment : BaseFragment(R.layout.fragment_video) {

    private val viewModel: VideoViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentVideoBinding::bind)

    private var currentState = VideoViewState.WORKOUT

    override fun bind() {
        viewBinding.apply {
            btnWorkouts.action { changeState(VideoViewState.WORKOUT) }
            btnCollections.action { changeState(VideoViewState.COLLECTIONS) }
            btnPrograms.action { changeState(VideoViewState.PROGRAMS) }
        }
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
                    btnWorkouts.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    indicatorWorkouts.isVisible = true
                }
                VideoViewState.COLLECTIONS -> {
                    btnCollections.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    indicatorCollections.isVisible = true
                }
                else -> {
                    btnPrograms.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    indicatorPrograms.isVisible = true
                }
            }
        }
    }
}

package com.trx.consumer.screens.trainer

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentTrainerDetailBinding
import com.trx.consumer.extensions.load
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutAdapter
import com.trx.consumer.screens.videoworkout.VideoWorkoutAdapter

class TrainerDetailFragment : BaseFragment(R.layout.fragment_trainer_detail) {

    private val viewModel: TrainerDetailViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentTrainerDetailBinding::bind)

    private lateinit var upComingClassesAdapter: LiveWorkoutAdapter
    private lateinit var onDemandClassesAdapter: VideoWorkoutAdapter

    override fun bind() {
        upComingClassesAdapter = LiveWorkoutAdapter(viewModel) { lifecycleScope }
        onDemandClassesAdapter = VideoWorkoutAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            rvUpcomingClasses.adapter = upComingClassesAdapter
            rvOnDemandClasses.adapter = onDemandClassesAdapter

            btnBack.setOnClickListener { viewModel.doTapBack() }
        }

        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)

            doLoadView()
            loadUpComingClasses(WorkoutModel.testListLive(5))
            loadOnDemandClasses(VideoModel.testList(5))
        }
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadView = Observer<Void> {
//        viewBinding.apply {
//            imgHeaderTrainer.load(it.profilePhoto)
//            lblTrainerName.text = it.firstNameAndLastInitial
//            lblAboutmeDetails.text = it.bio
//        }
    }

    private fun loadUpComingClasses(workouts: List<WorkoutModel>) {
        upComingClassesAdapter.update(workouts)
    }

    private fun loadOnDemandClasses(videos: List<VideoModel>) {
        onDemandClassesAdapter.update(videos)
    }
}

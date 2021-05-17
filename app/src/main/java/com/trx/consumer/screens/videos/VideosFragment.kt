package com.trx.consumer.screens.videos

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentVideosBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.load
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.params.VideoParamsModel
import com.trx.consumer.screens.video.list.VideoAdapter

class VideosFragment : BaseFragment(R.layout.fragment_videos) {

    private val viewModel: VideosViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentVideosBinding::bind)

    private lateinit var adapter: VideoAdapter

    override fun bind() {
        val model = NavigationManager.shared.params(this) as VideoParamsModel

        viewModel.apply {
            workoutModel = model.workoutModel

            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)

            doLoadView()
        }

        viewBinding.apply {
            adapter = VideoAdapter(viewModel) { lifecycleScope }
            rvRelatedVideos.adapter = adapter

            btnBack.action { viewModel.doTapBack() }
        }
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadView = Observer<WorkoutModel> {
        viewBinding.apply {
            imgHeader.load(it.imageUrl)
            lblTitle.text = it.video.name
            lblSubtitle.text = it.duration
            lblRelatedItems.text = "Related Workouts"
            btnPrimary.text = "Start Workout"
            lblTrainerName.text = it.trainer.fullName
            lblSummary.text = it.trainer.bio
            imgTrainerProfile.load(it.trainer.profilePhoto)
            adapter.updateVideos(WorkoutModel.testList(5))
        }
    }
}

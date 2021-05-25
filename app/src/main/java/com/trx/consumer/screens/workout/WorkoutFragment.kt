package com.trx.consumer.screens.workout

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentWorkoutBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.extensions.load
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.states.BookingState
import com.trx.consumer.screens.player.PlayerActivity

class WorkoutFragment : BaseFragment(R.layout.fragment_workout) {

    private val viewModel: WorkoutViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentWorkoutBinding::bind)

    override fun bind() {
        val model = NavigationManager.shared.params(this)
        viewModel.model = if (model is VideoModel) {
            WorkoutModel().apply {
                mode = WorkoutViewState.VIDEO_
                state = BookingState.VIDEO
                video = model
            }
        } else model as WorkoutModel

        viewBinding.apply {
            btnBack.action { viewModel.doTapBack() }
            btnTrainerProfile.action { viewModel.doTapProfile() }
            btnPrimary.action { viewModel.doTapPrimary() }
        }

        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapProfile.observe(viewLifecycleOwner, handleTapProfile)
            eventTapBookLive.observe(viewLifecycleOwner, handleTapBookLive)
            eventTapStartWorkout.observe(viewLifecycleOwner, handleTapStartWorkout)

            eventLoadWorkoutView.observe(viewLifecycleOwner, handleLoadWorkoutView)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            doLoadView()
        }
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadWorkoutView = Observer<WorkoutModel> { model ->
        LogManager.log("handleLoadVideoView")
        loadView(model)
        viewBinding.apply {
            btnPrimary.apply {
                text = context.getString(model.bookViewStatus.buttonTitle)
                textColor(model.bookViewStatus.buttonTitleColor)
                bgColor(model.bookViewStatus.buttonBackgroundColor)
            }
        }
    }

    private val handleLoadView = Observer<WorkoutModel> { model ->
        LogManager.log("handleLoadView")
        loadView(model)
        viewBinding.apply {
            btnPrimary.apply {
                text = context.getString(model.state.buttonTitle)
                bgColor(model.state.buttonBackgroundColor)
                textColor(model.state.buttonTitleColor)
            }
        }
    }

    private fun loadView(model: WorkoutModel) {
        viewBinding.apply {
            imgHeader.load(model.video.poster)
            lblTitle.text = model.video.name
            lblSummary.text = model.video.description
            lblSubtitle.text = model.video.videoDuration

            viewTrainer.isHidden = false
            imgTrainerPhoto.load(model.video.trainer.profilePhoto)
            lblTrainerName.text = model.video.trainer.fullName
        }
    }

    private val handleTapStartWorkout = Observer<WorkoutModel> { model ->
        LogManager.log("handleTapStartWorkout")
        if (model.workoutState == WorkoutViewState.VIDEO) {
            NavigationManager.shared.presentActivity(
                requireActivity(),
                PlayerActivity::class.java,
                model.video
            )
        }

        if (model.workoutState == WorkoutViewState.LIVE) {
            // TODO: Should go to LivePlayer NavigationManager.shared.present(this, R.id.live_fragment)
        }
    }

    private val handleTapBookLive = Observer<WorkoutModel> { model ->
        LogManager.log("handleTapBookLive")
        // TODO: Needs to implement booking alert model
    }

    private val handleTapProfile = Observer<TrainerModel> { model ->
        LogManager.log("handleTapProfile")
        NavigationManager.shared.present(this, R.id.trainer_fragment, model)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}

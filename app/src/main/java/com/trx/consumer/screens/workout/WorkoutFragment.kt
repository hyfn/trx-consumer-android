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
import com.trx.consumer.models.common.BookingState
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.WorkoutModel

class WorkoutFragment : BaseFragment(R.layout.fragment_workout) {

    private val viewModel: WorkoutViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentWorkoutBinding::bind)

    override fun bind() {
        val model = NavigationManager.shared.params(this)
        viewModel.model = if (model is VideoModel) {
            WorkoutModel().apply {
                mode = WorkoutViewState.video
                state = BookingState.DISABLED
                video = model
            }
        } else model as WorkoutModel

        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapProfile.observe(viewLifecycleOwner, handleTapProfile)
            eventTapBookLive.observe(viewLifecycleOwner, handleTapBookLive)
            eventTapStartWorkout.observe(viewLifecycleOwner, handleTapStartWorkout)

            eventLoadVideoView.observe(viewLifecycleOwner, handleLoadVideoView)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            doLoadView()
        }

        viewBinding.apply {
            btnBack.action { viewModel.doTapBack() }
            btnTrainerProfile.action { viewModel.doTapProfile() }
            btnPrimary.action { viewModel.doTapPrimary() }
        }
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadVideoView = Observer<WorkoutModel> { model ->
        LogManager.log("handleLoadVideoView")
        viewBinding.apply {
            imgHeader.load(model.video.poster)

            lblTitle.text(model.video.name)
            lblSummary.text(model.video.description)
            lblSubtitle.text(model.video.videoDuration)

            viewTrainer.isHidden = false
            imgTrainerPhoto.load(model.video.trainer.profilePhoto)
            lblTrainerName.text(model.video.trainer.fullName)

            btnPrimary.apply { text = context.getString(model.bookViewStatus.buttonTitle) }
            btnPrimary.textColor(model.bookViewStatus.buttonTitleColor)
            btnPrimary.bgColor(model.bookViewStatus.buttonBackgroundColor)
        }
    }

    private val handleLoadView = Observer<WorkoutModel> { model ->

        viewBinding.apply {
            imgHeader.load(model.video.poster)
            lblTitle.text = model.video.name
            lblSummary.text = model.video.description
            lblSubtitle.text = model.video.videoDuration

            viewTrainer.isHidden = false
            imgTrainerPhoto.load(model.video.trainer.profilePhoto)
            lblTrainerName.text = model.video.trainer.fullName
            btnPrimary.bgColor(model.state.buttonBackgroundColor)
            btnPrimary.textColor(model.state.buttonTitleColor)
            btnPrimary.apply { text = context.getString(model.state.buttonTitle) }
        }
    }

    private val handleTapStartWorkout = Observer<WorkoutModel> { model ->
        LogManager.log("handleTapStartWorkout")
        if (model.workoutState == WorkoutViewState.VIDEO) {
            // NavigationManager.shared.present(this, R.id.player_view) TODO: needs to play video
        }

        if (model.workoutState == WorkoutViewState.LIVE) {
            NavigationManager.shared.present(this, R.id.live_fragment)
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

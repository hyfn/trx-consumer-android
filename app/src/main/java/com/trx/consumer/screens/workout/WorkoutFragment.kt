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
import com.trx.consumer.models.common.AlertModel
import com.trx.consumer.models.common.BookingAlertModel
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.states.BookingState
import com.trx.consumer.models.states.WorkoutViewState
import com.trx.consumer.screens.alert.AlertViewState
import com.trx.consumer.screens.liveplayer.LivePlayerActivity
import com.trx.consumer.screens.video.VideoPlayerActivity
import java.util.Locale

class WorkoutFragment : BaseFragment(R.layout.fragment_workout) {

    private val viewModel: WorkoutViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentWorkoutBinding::bind)

    override fun bind() {
        val model = NavigationManager.shared.params(this)
        viewModel.model = if (model is VideoModel) {
            WorkoutModel().apply {
                mode = WorkoutViewState.VIDEO_MODE
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
            eventLoadVideoView.observe(viewLifecycleOwner, handleLoadVideoView)
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)
            eventShowPermissionAlert.observe(viewLifecycleOwner, handleShowPermissionAlert)

            doTrackPageView()
            doLoadView()
        }
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.hudView.isVisible = show
    }

    private val handleShowPermissionAlert = Observer<Void> {
        LogManager.log("handleShowPermissionAlert")
        val model = AlertModel.create(
            title = "",
            message = requireContext().getString(R.string.workout_permission_alert_message)
        ).apply {
            setPrimaryButton(
                R.string.workout_permission_alert_primary_label,
                state = AlertViewState.POSITIVE
            ) {
                NavigationManager.shared.present(this@WorkoutFragment, R.id.memberships_fragment)
            }
            setSecondaryButton(R.string.workout_permission_alert_secondary_label)
        }
        NavigationManager.shared.present(this, R.id.alert_fragment, model)
    }

    private val handleLoadWorkoutView = Observer<WorkoutModel> { model ->
        LogManager.log("handleLoadVideoView")
        load(model)
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
        load(model)
        viewBinding.apply {
            btnPrimary.apply {
                text = context.getString(model.state.buttonTitle)
                bgColor(model.state.buttonBackgroundColor)
                textColor(model.state.buttonTitleColor)
            }
        }
    }

    private val handleLoadVideoView = Observer<WorkoutModel> { model ->
        LogManager.log("handleLoadVideoView")
        loadVideoView(model)
        viewBinding.apply {
            btnPrimary.apply {
                text = context.getString(model.state.buttonTitle)
                bgColor(model.state.buttonBackgroundColor)
                textColor(model.state.buttonTitleColor)
            }
        }
    }

    private fun load(model: WorkoutModel) {
        viewBinding.apply {
            imgHeader.load(model.imageUrl)
            lblTitle.text = model.title
            lblSummary.text = model.summary
            lblSubtitle.text = model.subtitle

            viewTrainer.isHidden = false
            imgTrainerPhoto.load(model.trainer.profilePhoto)
            lblTrainerName.text = model.trainer.fullName
        }
    }

    private fun loadVideoView(model: WorkoutModel) {
        viewBinding.apply {
            imgHeader.load(model.video.poster)
            lblTitle.text = model.video.name
            lblSummary.text = model.video.description
            lblSubtitle.text = model.video.videoDuration

            viewTrainer.isHidden = false
            imgTrainerPhoto.load(model.video.trainer.profilePhoto)
            lblTrainerName.text = model.video.trainer.fullName

            val equipments = model.video.equipment
            if (equipments.isNotEmpty()) {
                viewEquipment.isHidden = false
                lblEquipment.text = equipments.joinToString { it.capitalize(Locale.ROOT) }
            }
        }
    }

    private val handleTapStartWorkout = Observer<WorkoutModel> { model ->
        LogManager.log("handleTapStartWorkout")
        if (model.workoutState == WorkoutViewState.VIDEO) {
            NavigationManager.shared.presentActivity(
                requireActivity(),
                VideoPlayerActivity::class.java,
                model.video
            )
        }

        if (model.workoutState == WorkoutViewState.LIVE) {
            LogManager.log("handleTapStartWorkout | model.workoutState == WorkoutViewState.LIVE")
            NavigationManager.shared.presentActivity(
                requireActivity(),
                LivePlayerActivity::class.java,
                model.video
            )
        }
    }

    private val handleTapBookLive = Observer<BookingAlertModel> { model ->
        LogManager.log("handleTapBookLive")
        NavigationManager.shared.present(this, R.id.booking_alert_fragment, model)
    }

    private val handleTapProfile = Observer<TrainerModel> { model ->
        LogManager.log("handleTapProfile")
        NavigationManager.shared.present(this, R.id.trainer_fragment, model)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}
